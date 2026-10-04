package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectedDeviceNode
import com.example.data.model.ServerLog
import com.example.ui.AetherViewModel
import com.example.ui.theme.HoloAlertRed
import com.example.ui.theme.HoloAmber
import com.example.ui.theme.HoloBorderGlow
import com.example.ui.theme.HoloCardElevated
import com.example.ui.theme.HoloCardSurface
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.HoloDarkSurface
import com.example.ui.theme.HoloGreen
import com.example.ui.theme.HoloTeal
import com.example.ui.theme.HoloViolet
import com.example.ui.theme.HoloVoidBlack
import com.example.ui.theme.TextHoloDim
import com.example.ui.theme.TextHoloPrimary
import com.example.ui.theme.TextHoloSecondary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TermuxDashboardScreen(
    viewModel: AetherViewModel,
    modifier: Modifier = Modifier
) {
    val isRunning by viewModel.termuxServer.isRunning.collectAsState()
    val serverPort by viewModel.termuxServer.port.collectAsState()
    val uptimeSec by viewModel.termuxServer.uptimeSeconds.collectAsState()
    val requestCount by viewModel.termuxServer.requestCount.collectAsState()
    val recentServerLogs by viewModel.serverLogs.collectAsState()
    val connectedNodes by viewModel.connectedNodes.collectAsState()
    val telemetry by viewModel.telemetry.collectAsState()

    var testResultText by remember { mutableStateOf("Ready to send test probe to 127.0.0.1:8080") }

    val formattedUptime = remember(uptimeSec) {
        val h = uptimeSec / 3600
        val m = (uptimeSec % 3600) / 60
        val s = uptimeSec % 60
        String.format("%02d:%02d:%02d", h, m, s)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HoloVoidBlack)
            .testTag("termux_dashboard_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Title Header
            item {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "CENTRALIZED MONITOR & TERMUX SERVER",
                        color = HoloCyan,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "LOCAL BACKGROUND PROCESSES & CROSS-DEVICE SYNC",
                        color = TextHoloSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Localhost Termux Server Control Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(HoloCardElevated)
                        .border(
                            1.dp,
                            if (isRunning) HoloGreen.copy(alpha = 0.5f) else HoloAlertRed.copy(alpha = 0.5f),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(16.dp)
                        .testTag("termux_server_banner")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isRunning) HoloGreen else HoloAlertRed)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "http://127.0.0.1:$serverPort",
                                    color = if (isRunning) HoloGreen else HoloAlertRed,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = if (isRunning) "ONLINE (PID ${android.os.Process.myPid()})" else "OFFLINE",
                                color = if (isRunning) HoloGreen else TextHoloDim,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Server Stats row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("UPTIME", color = TextHoloDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text(formattedUptime, color = TextHoloPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Column {
                                Text("REQUESTS", color = TextHoloDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text("$requestCount", color = HoloCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Column {
                                Text("SOCKET LATENCY", color = TextHoloDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text("${telemetry.networkLatencyMs} ms", color = HoloTeal, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Control Buttons: Start, Stop, Restart
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (isRunning) viewModel.termuxServer.stop() else viewModel.termuxServer.start(8080)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRunning) HoloAlertRed.copy(alpha = 0.8f) else HoloGreen,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isRunning) "STOP SERVER" else "START SERVER", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }

                            Button(
                                onClick = { viewModel.termuxServer.restart() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HoloCardSurface,
                                    contentColor = HoloCyan
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("RESTART", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }

            // Interactive API Request Tester & Curl generator
            item {
                Column {
                    Text(
                        text = "LOCAL API PROBE TESTER",
                        color = TextHoloDim,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val testEndpoints = listOf(
                            "GET /status" to "http://127.0.0.1:8080/api/v1/status",
                            "POST /dev/toggle" to "http://127.0.0.1:8080/api/v1/devices/dev-light-1/toggle",
                            "GET /schedule" to "http://127.0.0.1:8080/api/v1/schedule",
                            "POST /ping" to "http://127.0.0.1:8080/api/v1/ping"
                        )
                        testEndpoints.forEach { (label, url) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(HoloCardSurface)
                                    .border(1.dp, HoloBorderGlow, RoundedCornerShape(6.dp))
                                    .clickable {
                                        viewModel.testProbe(url) {
                                            testResultText = it
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(label, color = HoloCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF050810))
                            .border(1.dp, HoloCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = testResultText,
                            color = TextHoloSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Centralized Cross-Device Synchronization Matrix
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CROSS-DEVICE SYNC TOPOLOGY",
                            color = TextHoloDim,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp
                        )
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(HoloCardSurface)
                                .clickable { viewModel.pingAllNodes() }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = HoloViolet, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PING ALL NODES", color = HoloViolet, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            items(connectedNodes, key = { it.id }) { node ->
                ConnectedNodeCard(node = node)
            }

            // Real-Time Hardware Telemetry Gauges
            item {
                Column {
                    Text(
                        text = "SYSTEM TELEMETRY GAUGES",
                        color = TextHoloDim,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(top = 6.dp, bottom = 6.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(HoloCardSurface)
                            .border(1.dp, HoloBorderGlow, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // CPU Gauge
                            Column {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("CPU PROCESSING LOAD", color = TextHoloDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Text("${telemetry.cpuLoadPercent}%", color = HoloCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { telemetry.cpuLoadPercent / 100f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = if (telemetry.cpuLoadPercent > 70) HoloAlertRed else HoloCyan,
                                    trackColor = HoloDarkSurface
                                )
                            }

                            // RAM Gauge
                            Column {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("RAM FOOTPRINT", color = TextHoloDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Text("${telemetry.ramUsedMb} / ${telemetry.ramTotalMb} MB", color = HoloViolet, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { telemetry.ramUsedMb.toFloat() / telemetry.ramTotalMb },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = HoloViolet,
                                    trackColor = HoloDarkSurface
                                )
                            }
                        }
                    }
                }
            }

            // Real-Time Server Request Logs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE SERVER REQUEST STREAM",
                        color = TextHoloDim,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                    IconButton(
                        onClick = { viewModel.clearServerLogs() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear", tint = TextHoloDim, modifier = Modifier.size(16.dp))
                    }
                }
            }

            items(recentServerLogs.take(8), key = { it.id }) { log ->
                ServerLogCard(log = log)
            }

            item { Spacer(modifier = Modifier.height(70.dp)) }
        }
    }
}

@Composable
fun ConnectedNodeCard(node: ConnectedDeviceNode) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(HoloCardSurface)
            .border(1.dp, HoloBorderGlow, RoundedCornerShape(10.dp))
            .padding(12.dp)
            .testTag("node_card_${node.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(HoloDarkSurface)
                        .border(1.dp, HoloViolet.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (node.type) {
                            "MOBILE_LOCAL" -> Icons.Default.Smartphone
                            "TERMUX_NODE" -> Icons.Default.Terminal
                            "DESKTOP" -> Icons.Default.Computer
                            "WATCH" -> Icons.Default.Watch
                            else -> Icons.Default.Hub
                        },
                        contentDescription = null,
                        tint = HoloViolet,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = node.name,
                        color = TextHoloPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${node.address} • PING: ${node.pingMs}ms",
                        color = TextHoloDim,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(HoloGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = node.syncStatus,
                    color = HoloGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun ServerLogCard(log: ServerLog) {
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val timeStr = remember(log.timestamp) { timeFormat.format(Date(log.timestamp)) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF060A13))
            .border(1.dp, HoloCyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = log.method,
                        color = when (log.method) {
                            "GET" -> HoloCyan
                            "POST" -> HoloGreen
                            "SYSTEM" -> HoloViolet
                            else -> HoloAmber
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = log.endpoint,
                        color = TextHoloPrimary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = log.details,
                    color = TextHoloDim,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${log.status} OK",
                    color = if (log.status in 200..299) HoloGreen else HoloAlertRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = timeStr,
                    color = TextHoloDim,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
