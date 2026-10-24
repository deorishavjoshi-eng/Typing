package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TypingMockEntity
import com.example.engine.TypingEvaluator
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.ExamFailRed
import com.example.ui.theme.ExamPassGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StenoScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allMocks by viewModel.allMocks.collectAsState()
    val scrollState = rememberScrollState()

    var selectedMock by remember { mutableStateOf<TypingMockEntity?>(allMocks.firstOrNull()) }
    var mockDropdownExpanded by remember { mutableStateOf(false) }
    var selectedSpeedWpm by remember { mutableIntStateOf(80) }

    var typedTranscription by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    if (selectedMock == null && allMocks.isNotEmpty()) {
        selectedMock = allMocks.first()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    viewModel.stopSpeaking()
                    viewModel.navigateTo(AppScreen.Home)
                },
                modifier = Modifier.testTag("steno_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = "Stenographer Audio Dictation",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🎧 Audio Dictation Controls",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Listen to the passage spoken aloud at your chosen shorthand speed (WPM), write or type what you hear, and check transcription accuracy.",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Select Passage Dropdown
                    Text("Select Dictation Passage:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = mockDropdownExpanded,
                        onExpandedChange = { mockDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedMock?.title ?: "Select Mock",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mockDropdownExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = mockDropdownExpanded,
                            onDismissRequest = { mockDropdownExpanded = false }
                        ) {
                            allMocks.take(15).forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m.title, fontSize = 12.sp) },
                                    onClick = {
                                        selectedMock = m
                                        mockDropdownExpanded = false
                                        viewModel.stopSpeaking()
                                        isPlaying = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dictation Speed Selector
                    Text("Dictation Speed (WPM):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(60, 80, 100, 120).forEach { speed ->
                            Button(
                                onClick = {
                                    selectedSpeedWpm = speed
                                    if (isPlaying && selectedMock != null) {
                                        viewModel.speakText(selectedMock!!.text, speed)
                                    }
                                },
                                modifier = Modifier.weight(1f).height(36.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedSpeedWpm == speed) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (selectedSpeedWpm == speed) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("$speed WPM", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Play / Stop Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                selectedMock?.let {
                                    viewModel.speakText(it.text, selectedSpeedWpm)
                                    isPlaying = true
                                }
                            },
                            modifier = Modifier.weight(1f).height(42.dp).testTag("steno_play_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("▶ Play Audio", fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.stopSpeaking()
                                isPlaying = false
                            },
                            modifier = Modifier.weight(1f).height(42.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ExamFailRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Stop, contentDescription = "Stop", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("■ Stop Audio", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Candidate Transcription Area
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "✍ Candidate Shorthand Transcription",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Type what you hear as the passage is read aloud:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = typedTranscription,
                        onValueChange = { typedTranscription = it },
                        modifier = Modifier.fillMaxWidth().height(160.dp).testTag("steno_typed_input"),
                        placeholder = { Text("Begin typing your shorthand transcription here...", fontSize = 13.sp) },
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val mock = selectedMock ?: return@Button
                            val origWords = mock.text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
                            val mineWords = typedTranscription.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
                            val align = TypingEvaluator.align(origWords, mineWords)
                            val mistakes = align.wrongWords + align.omittedWords + align.extraWords + (align.halfMistakes * 0.5)
                            val errPct = if (origWords.isNotEmpty()) (mistakes / origWords.size) * 100.0 else 0.0
                            val accPct = (100.0 - errPct).coerceAtLeast(0.0)

                            resultText = "Words Typed: ${mineWords.size} of ${origWords.size} · Mistakes: %.1f · Accuracy: %.2f%% · Error: %.2f%%".format(
                                mistakes, accPct, errPct
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp).testTag("steno_check_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = "Check", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Check Transcription Accuracy", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (resultText != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = resultText!!,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}
