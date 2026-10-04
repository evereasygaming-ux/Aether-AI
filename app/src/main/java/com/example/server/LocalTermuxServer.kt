package com.example.server

import android.util.Log
import com.example.data.repository.AetherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicInteger

class LocalTermuxServer(private val repository: AetherRepository) {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var serverSocket: ServerSocket? = null
    private var serverJob: Job? = null
    private var uptimeJob: Job? = null

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _port = MutableStateFlow(8080)
    val port: StateFlow<Int> = _port.asStateFlow()

    private val _uptimeSeconds = MutableStateFlow(0L)
    val uptimeSeconds: StateFlow<Long> = _uptimeSeconds.asStateFlow()

    private val _requestCount = MutableStateFlow(0)
    val requestCount: StateFlow<Int> = _requestCount.asStateFlow()

    private val _recentLogs = MutableStateFlow<List<String>>(emptyList())
    val recentLogs: StateFlow<List<String>> = _recentLogs.asStateFlow()

    private val connectionCounter = AtomicInteger(0)

    fun start(targetPort: Int = 8080) {
        if (_isRunning.value) return

        serverJob = scope.launch {
            try {
                _port.value = targetPort
                serverSocket = ServerSocket(targetPort, 50, InetAddress.getByName("127.0.0.1"))
                _isRunning.value = true
                addLog("[TERMUX-DAEMON] Server initialized on 127.0.0.1:$targetPort (PID ${android.os.Process.myPid()})")
                repository.logServerActivity("/server/start", "SYSTEM", 200, "Localhost Termux server started on port $targetPort")

                // Start uptime tracker
                startUptimeTracker()

                while (isActive && serverSocket != null && !serverSocket!!.isClosed) {
                    try {
                        val clientSocket = serverSocket!!.accept()
                        scope.launch {
                            handleClient(clientSocket)
                        }
                    } catch (e: Exception) {
                        if (!isActive) break
                    }
                }
            } catch (e: Exception) {
                Log.e("LocalTermuxServer", "Failed to start server on $targetPort: ${e.message}")
                addLog("[TERMUX-ERROR] Port $targetPort busy or permission denied: ${e.message}")
                _isRunning.value = false
            }
        }
    }

    fun stop() {
        try {
            uptimeJob?.cancel()
            serverSocket?.close()
            serverSocket = null
            serverJob?.cancel()
            _isRunning.value = false
            addLog("[TERMUX-DAEMON] Server halted.")
            scope.launch {
                repository.logServerActivity("/server/stop", "SYSTEM", 200, "Localhost Termux server stopped")
            }
        } catch (e: Exception) {
            Log.e("LocalTermuxServer", "Error stopping: ${e.message}")
        }
    }

    fun restart() {
        stop()
        scope.launch {
            delay(500)
            start(_port.value)
        }
    }

    private fun startUptimeTracker() {
        uptimeJob?.cancel()
        _uptimeSeconds.value = 0L
        uptimeJob = scope.launch {
            while (isActive && _isRunning.value) {
                delay(1000)
                _uptimeSeconds.value += 1
            }
        }
    }

    private suspend fun handleClient(socket: Socket) = withContext(Dispatchers.IO) {
        val connId = connectionCounter.incrementAndGet()
        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = PrintWriter(socket.getOutputStream(), true)

            val requestLine = reader.readLine() ?: return@withContext
            val parts = requestLine.split(" ")
            if (parts.size < 2) return@withContext

            val method = parts[0]
            val path = parts[1]

            // Read headers
            var line: String?
            var contentLength = 0
            while (reader.readLine().also { line = it } != null) {
                if (line.isNullOrBlank()) break
                if (line!!.startsWith("Content-Length:", ignoreCase = true)) {
                    contentLength = line!!.substringAfter(":").trim().toIntOrNull() ?: 0
                }
            }

            // Read body if any
            val body = if (contentLength > 0) {
                val buf = CharArray(contentLength)
                reader.read(buf, 0, contentLength)
                String(buf)
            } else ""

            _requestCount.value += 1
            addLog("[REQ #$connId] $method $path ($contentLength bytes)")

            val (statusCode, responseJson) = processApiRequest(method, path, body)

            val responseBytes = responseJson.toByteArray(Charsets.UTF_8)
            writer.print("HTTP/1.1 $statusCode OK\r\n")
            writer.print("Content-Type: application/json; charset=utf-8\r\n")
            writer.print("Content-Length: ${responseBytes.size}\r\n")
            writer.print("Connection: close\r\n")
            writer.print("Access-Control-Allow-Origin: *\r\n")
            writer.print("\r\n")
            writer.flush()
            socket.getOutputStream().write(responseBytes)
            socket.getOutputStream().flush()

            repository.logServerActivity(path, method, statusCode, "Client #${connId} served: ${responseJson.take(80)}")
        } catch (e: Exception) {
            Log.e("LocalTermuxServer", "Client handling error: ${e.message}")
        } finally {
            try {
                socket.close()
            } catch (e: Exception) {}
        }
    }

    private suspend fun processApiRequest(method: String, path: String, body: String): Pair<Int, String> {
        return try {
            when {
                path == "/api/v1/status" || path == "/" -> {
                    val json = JSONObject().apply {
                        put("status", "ONLINE")
                        put("host", "127.0.0.1")
                        put("port", _port.value)
                        put("uptimeSeconds", _uptimeSeconds.value)
                        put("daemon", "Aether-Termux-Bridge/v2.4")
                        put("architecture", "aarch64-linux-android")
                        put("totalRequests", _requestCount.value)
                        put("quantumState", "ENTANGLED")
                    }
                    200 to json.toString()
                }

                path.startsWith("/api/v1/devices") -> {
                    if (method == "POST" && path.contains("/toggle")) {
                        val deviceId = path.substringAfter("/devices/").substringBefore("/toggle")
                        val isOn = if (body.isNotBlank()) {
                            JSONObject(body).optBoolean("isOn", true)
                        } else true
                        repository.toggleDevicePower(deviceId, isOn)
                        200 to JSONObject().apply {
                            put("success", true)
                            put("deviceId", deviceId)
                            put("powerState", isOn)
                        }.toString()
                    } else {
                        // Return status summary
                        200 to JSONObject().apply {
                            put("status", "OK")
                            put("synced", true)
                            put("message", "Smart devices operational")
                        }.toString()
                    }
                }

                path == "/api/v1/execute" -> {
                    val scriptBody = if (body.isNotBlank()) JSONObject(body) else JSONObject()
                    val scriptCode = scriptBody.optString("code", "echo 'Hello from Termux'")
                    val language = scriptBody.optString("language", "BASH")

                    val simulatedOutput = executeMockTerminalCommand(language, scriptCode)
                    200 to JSONObject().apply {
                        put("exitCode", 0)
                        put("language", language)
                        put("output", simulatedOutput)
                        put("timestamp", System.currentTimeMillis())
                    }.toString()
                }

                path == "/api/v1/schedule" -> {
                    200 to JSONObject().apply {
                        put("status", "SCHEDULE_ACTIVE")
                        put("syncedRoutines", 4)
                        put("nextTrigger", "Quantum Morning Awakening")
                    }.toString()
                }

                path == "/api/v1/ping" -> {
                    200 to JSONObject().apply {
                        put("pong", true)
                        put("latencyMs", 1)
                    }.toString()
                }

                else -> {
                    404 to JSONObject().apply {
                        put("error", "Endpoint not found")
                        put("path", path)
                    }.toString()
                }
            }
        } catch (e: Exception) {
            500 to JSONObject().apply {
                put("error", e.message ?: "Internal Error")
            }.toString()
        }
    }

    private fun executeMockTerminalCommand(language: String, code: String): String {
        return buildString {
            append("[TERMUX-$language HOST PID:${android.os.Process.myPid()}]\n")
            append("$ ${code.lines().firstOrNull()?.take(40) ?: "run"}\n")
            append("=> Process spawned in isolated execution jail.\n")
            if (code.contains("requests") || code.contains("curl")) {
                append("=> HTTP socket dispatched to 127.0.0.1:${_port.value}\n")
                append("=> Response Code: 200 OK [JSON Payload Received]\n")
            }
            append("=> CPU Cycles: 0.004s | Mem: 14.2MB | Exit Code: 0 (SUCCESS)")
        }
    }

    private fun addLog(log: String) {
        val current = _recentLogs.value.toMutableList()
        current.add(0, log)
        if (current.size > 80) current.removeAt(current.lastIndex)
        _recentLogs.value = current
    }
}
