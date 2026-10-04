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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VoiceScript
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

@Composable
fun ScriptIdeScreen(
    viewModel: AetherViewModel,
    modifier: Modifier = Modifier
) {
    val scripts by viewModel.scripts.collectAsState()
    val selectedId by viewModel.selectedScriptId.collectAsState()
    val editorCode by viewModel.ideEditorCode.collectAsState()
    val consoleOutput by viewModel.ideOutputConsole.collectAsState()
    val isExecuting by viewModel.isScriptExecuting.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()

    var showNewScriptDialog by remember { mutableStateOf(false) }

    val activeScript = scripts.find { it.id == selectedId } ?: scripts.firstOrNull()

    LaunchedEffect(activeScript) {
        if (activeScript != null && editorCode.isBlank()) {
            viewModel.selectScript(activeScript)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HoloVoidBlack)
            .testTag("script_ide_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "TERMUX SCRIPT IDE",
                        color = HoloCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "CUSTOM VOICE COMMAND SCRIPTING & CODE INTEGRATION",
                        color = TextHoloSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Script Selector Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    scripts.forEach { script ->
                        val isSelected = script.id == selectedId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) HoloCyan.copy(alpha = 0.2f) else HoloCardSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) HoloCyan else HoloBorderGlow,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.selectScript(script) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("script_tab_${script.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = script.language,
                                    color = when (script.language) {
                                        "PYTHON" -> HoloAmber
                                        "BASH" -> HoloGreen
                                        else -> HoloViolet
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = script.title,
                                    color = if (isSelected) TextHoloPrimary else TextHoloDim,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // + New Script Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(HoloCardElevated)
                            .border(1.dp, HoloBorderGlow, RoundedCornerShape(8.dp))
                            .clickable { showNewScriptDialog = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("new_script_tab")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = HoloCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("NEW SCRIPT", color = HoloCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            // Active Script Metadata & Voice Trigger
            if (activeScript != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(HoloCardSurface)
                            .border(1.dp, HoloBorderGlow, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = HoloViolet,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "VOICE TRIGGER:",
                                        color = HoloViolet,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "\"${activeScript.triggerPhrase}\"",
                                        color = TextHoloPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = activeScript.lastRunStatus,
                                    color = if (activeScript.lastRunStatus == "SUCCESS") HoloGreen else TextHoloDim,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = activeScript.description,
                                color = TextHoloDim,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Quick Code Insertion Snippets Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val snippets = listOf(
                        "curl 127.0.0.1:8080" to "curl -s http://127.0.0.1:8080/api/v1/status\n",
                        "requests.post" to "res = requests.post('http://127.0.0.1:8080/api/v1/automate', json={})\n",
                        "json.dumps" to "import json\nprint(json.dumps({'status': 'ONLINE'}))\n",
                        "device.toggle" to "curl -X POST http://127.0.0.1:8080/api/v1/devices/dev-light-1/toggle\n"
                    )
                    snippets.forEach { (label, codeSnippet) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(HoloDarkSurface)
                                .border(1.dp, HoloBorderGlow, RoundedCornerShape(6.dp))
                                .clickable {
                                    viewModel.updateEditorCode(editorCode + "\n" + codeSnippet)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(label, color = HoloTeal, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            // Code Editor Box
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF070B14))
                        .border(1.dp, HoloCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                        .testTag("code_editor_box")
                ) {
                    BasicTextField(
                        value = editorCode,
                        onValueChange = { viewModel.updateEditorCode(it) },
                        textStyle = TextStyle(
                            color = TextHoloPrimary,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        ),
                        cursorBrush = SolidColor(HoloCyan),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Action Buttons: Run in Termux, High Thinking Optimize, Save
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Run Script Button
                    Button(
                        onClick = { viewModel.executeCurrentScript() },
                        enabled = !isExecuting,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("run_script_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HoloCyan,
                            contentColor = HoloVoidBlack
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isExecuting) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = HoloVoidBlack, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("RUNNING...")
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("RUN IN TERMUX", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }
                    }

                    // Gemini 3.1 Pro High-Thinking Optimize Button
                    Button(
                        onClick = { viewModel.askAiToOptimizeScript() },
                        enabled = !isAiThinking,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_optimize_script_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HoloViolet,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isAiThinking) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("THINKING...")
                        } else {
                            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("HIGH-THINK AI", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }
                    }

                    // Save Button
                    IconButton(
                        onClick = { viewModel.saveCurrentScript() },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(HoloCardElevated)
                            .border(1.dp, HoloBorderGlow, RoundedCornerShape(8.dp))
                            .testTag("save_script_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = HoloCyan)
                    }
                }
            }

            // Output Console Terminal Window
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Terminal, contentDescription = null, tint = HoloGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TERMUX CONSOLE OUTPUT (STDOUT)",
                                color = HoloGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF04060B))
                            .border(1.dp, HoloGreen.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                            .testTag("console_output_box")
                    ) {
                        Text(
                            text = consoleOutput,
                            color = HoloGreen,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(70.dp)) }
        }
    }

    if (showNewScriptDialog) {
        NewScriptDialog(
            onDismiss = { showNewScriptDialog = false },
            onCreate = { title, lang, trigger ->
                viewModel.createNewScript(title, lang, trigger)
                showNewScriptDialog = false
            }
        )
    }
}

@Composable
fun NewScriptDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, language: String, trigger: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("BASH") }
    var trigger by remember { mutableStateOf("") }

    val languages = listOf("BASH", "PYTHON", "JAVASCRIPT")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "CREATE VOICE AUTOMATION SCRIPT",
                color = HoloCyan,
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Script Title (e.g. Cluster Ping)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HoloCyan,
                        focusedTextColor = TextHoloPrimary,
                        unfocusedTextColor = TextHoloPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = trigger,
                    onValueChange = { trigger = it },
                    label = { Text("Voice Trigger Phrase (e.g. Ping nodes)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HoloCyan,
                        focusedTextColor = TextHoloPrimary,
                        unfocusedTextColor = TextHoloPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "TARGET LANGUAGE:",
                    color = TextHoloDim,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    languages.forEach { lang ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (language == lang) HoloCyan else HoloCardSurface)
                                .clickable { language = lang }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lang,
                                color = if (language == lang) HoloVoidBlack else TextHoloPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(title, language, trigger.ifBlank { "run script $title" })
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = HoloCyan, contentColor = HoloVoidBlack)
            ) {
                Text("INITIALIZE SCRIPT")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextHoloDim)
            }
        },
        containerColor = HoloDarkSurface,
        shape = RoundedCornerShape(14.dp)
    )
}
