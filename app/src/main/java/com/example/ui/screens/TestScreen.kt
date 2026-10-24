package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.ExamFailRed

@Composable
fun TestScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val mock = viewModel.selectedMock.collectAsState().value ?: return
    val selectedExam by viewModel.selectedExam.collectAsState()
    val typedText by viewModel.typedContent.collectAsState()
    val keystrokes by viewModel.keystrokeCount.collectAsState()
    val backspaces by viewModel.backspaceCount.collectAsState()
    val secondsLeft by viewModel.secondsRemaining.collectAsState()
    val totalSecs by viewModel.totalTestSeconds.collectAsState()

    var showSubmitDialog by remember { mutableStateOf(false) }
    val passageScrollState = rememberScrollState()

    val mins = secondsLeft / 60
    val secs = secondsLeft % 60
    val formattedTime = "%02d:%02d".format(mins, secs)
    val isTimeLow = secondsLeft <= 60

    val maxKd = selectedExam.targetKeystrokes
    val progress = (keystrokes.toFloat() / maxKd).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9FB))
    ) {
        // Exam Hall Top Blue/Charcoal Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF263238))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedExam.name} – Typing Skill Test",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (viewModel.soundManager.isHallNoiseEnabled) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Hall Noise On",
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hall Noise", fontSize = 10.sp, color = Color(0xFFFFB74D))
                    }
                }
            }
        }

        // Candidate Info & Countdown Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Language: ${if (mock.examTag == "hindi") "Hindi (Inscript)" else "English (US)"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF455A64)
                    )
                    Text(
                        text = "v1.04 · TCS iON Simulated Pattern",
                        fontSize = 10.sp,
                        color = Color(0xFF90A4AE)
                    )
                }

                // Countdown Timer
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            if (isTimeLow) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (isTimeLow) ExamFailRed else MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Time Left",
                        tint = if (isTimeLow) ExamFailRed else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Time Left: $formattedTime",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isTimeLow) ExamFailRed else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("test_timer_display")
                    )
                }

                // Candidate Avatar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCFD8DC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Candidate",
                            tint = Color(0xFF455A64),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Original Passage Viewer Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ORIGINAL PASSAGE (READ & TYPE EXACTLY)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${mock.wordCount} words",
                    fontSize = 11.sp,
                    color = Color(0xFF78909C)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.5.dp, Color(0xFFB0BEC5), RoundedCornerShape(8.dp))
                    .padding(10.dp)
                    .verticalScroll(passageScrollState)
            ) {
                Text(
                    text = mock.text,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = Color(0xFF263238),
                    modifier = Modifier.testTag("original_passage_text")
                )
            }
        }

        // Typing Input Area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CANDIDATE TYPING AREA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Key Depressions: $keystrokes / $maxKd",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (keystrokes >= maxKd) ExamFailRed else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("kd_counter")
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = typedText,
                onValueChange = { newVal ->
                    val isBk = newVal.length < typedText.length
                    viewModel.onKeyTyped(newVal, isBk)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("candidate_typing_input"),
                placeholder = {
                    Text(
                        "Start typing here... Keep your eyes on the original text.",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.None
                ),
                keyboardActions = KeyboardActions.Default,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp,
                    color = Color(0xFF1A237E),
                    fontFamily = FontFamily.SansSerif
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color(0xFFB0BEC5)
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        // Bottom Bar with Live Stats & Submit Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column {
                        Text("Backspace", fontSize = 10.sp, color = Color(0xFF78909C))
                        Text("$backspaces", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    val wordsTyped = typedText.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size
                    Column {
                        Text("Words Typed", fontSize = 10.sp, color = Color(0xFF78909C))
                        Text("$wordsTyped", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { showSubmitDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("submit_test_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Submit Test", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Submit", modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }

    // Submit Confirmation Dialog
    if (showSubmitDialog) {
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text("Submit Typing Test?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "You have $formattedTime remaining. Once submitted, your errors and speed will be calculated and evaluated against the official cutoff criteria."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        viewModel.submitTest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Yes, Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("Continue Typing")
                }
            }
        )
    }
}
