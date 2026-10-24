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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CandidateCategory
import com.example.engine.CategoryQualification
import com.example.engine.CrossExamStatus
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.ExamFailRed
import com.example.ui.theme.ExamPassGreen

@Composable
fun ResultScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val eval = viewModel.latestEvaluation.collectAsState().value ?: return
    val mock = viewModel.selectedMock.collectAsState().value ?: return
    val selectedExam by viewModel.selectedExam.collectAsState()
    val countUnreached by viewModel.countUnreachedAsMistakes.collectAsState()

    val scrollState = rememberScrollState()

    val isPass = eval.isUserQualified
    val heroBg = if (isPass) ExamPassGreen else ExamFailRed

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
                onClick = { viewModel.navigateTo(AppScreen.Home) },
                modifier = Modifier.testTag("result_back_to_home")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = "Detailed Test Scorecard",
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
            // Result Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(heroBg, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Circular Error % Ring
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .border(6.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%.2f%%".format(eval.activeErrorPercent),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Cutoff ≤${eval.allowedErrorPercent}%",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 9.sp
                            )
                        }
                    }

                    // Status and Metadata
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isPass) "QUALIFIED 🎉" else "NOT QUALIFIED",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isPass) "Congratulations! You met all speed and accuracy cutoffs."
                            else "Keep practicing! Review your mistakes below to improve speed & accuracy.",
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${selectedExam.name} · ${eval.userCategory.displayName}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 4 Core Stat Cards Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "⚡ NET SPEED",
                    mainValue = "${eval.netKdph} KDPH",
                    subValue = "${eval.netWpm} WPM (Req: ${selectedExam.requiredWpm})",
                    progress = (eval.netKdph.toFloat() / selectedExam.requiredKdph.coerceAtLeast(1)).coerceIn(0f, 1f),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "🎯 ACCURACY",
                    mainValue = "%.1f%%".format(eval.accuracyPercent),
                    subValue = "Error in typed: %.2f%%".format(eval.errorPercentTypedPart),
                    progress = (eval.accuracyPercent / 100.0).toFloat().coerceIn(0f, 1f),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "📄 COMPLETION",
                    mainValue = "${eval.completionPercent.toInt()}%",
                    subValue = "${eval.keystrokes} of ${selectedExam.targetKeystrokes} KDs",
                    progress = (eval.completionPercent / 100.0).toFloat().coerceIn(0f, 1f),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "✍ MISTAKES",
                    mainValue = "%.1f".format(eval.totalMistakes),
                    subValue = "${eval.fullMistakesCount} full · ${eval.halfMistakesCount} half",
                    progress = (eval.activeErrorPercent.toFloat() / 20f).coerceIn(0f, 1f),
                    modifier = Modifier.weight(1f)
                )
            }

            // SSC Count Unreached Toggle Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = countUnreached,
                    onCheckedChange = { viewModel.toggleCountUnreached(it) },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("toggle_count_unreached_checkbox")
                )
                Column {
                    Text(
                        text = "Count unreached words as mistakes (SSC Rule)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "SSC DEST treats all untyped passage words as full mistakes",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Category-Wise Qualification Status Matrix
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🏷 Qualifying Status for Every Category",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Check whether your current typing score clears the cutoff for each student category:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        eval.categoryQualifications.forEach { catQual ->
                            CategoryQualTile(
                                catQual = catQual,
                                isSelectedUserCat = catQual.category == eval.userCategory,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Detailed Typing Metrics Panel
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "📋 Word Alignment & Typing Breakdown",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        MetricRowItem("Total Words Typed", "${eval.typedWordsCount}", Modifier.weight(1f))
                        MetricRowItem("Correct Words", "${eval.alignment.correctWords}", Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        MetricRowItem("Wrong Words", "${eval.alignment.wrongWords}", Modifier.weight(1f))
                        MetricRowItem("Omitted Words", "${eval.alignment.omittedWords}", Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        MetricRowItem("Extra Words", "${eval.alignment.extraWords}", Modifier.weight(1f))
                        MetricRowItem("Half Mistakes", "${eval.alignment.halfMistakes}", Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        MetricRowItem("Words Not Reached", "${eval.alignment.unreachedOriginalWords}", Modifier.weight(1f))
                        MetricRowItem("Backspace Count", "${eval.backspaceCount}", Modifier.weight(1f))
                    }
                }
            }

            // Cross-Exam Compatibility Matrix
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🏛 Your Score in Other Competitive Exams",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Real-time evaluation against patterns of other major government typing exams:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    eval.crossExamResults.forEach { status ->
                        CrossExamRow(status = status)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }

            // Bottom Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.Home) },
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("All Mocks", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.startTest() },
                    modifier = Modifier.weight(1f).height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Retake Test", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    mainValue: String,
    subValue: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(4.dp))
            Text(mainValue, fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(subValue, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun CategoryQualTile(
    catQual: CategoryQualification,
    isSelectedUserCat: Boolean,
    modifier: Modifier = Modifier
) {
    val isPass = catQual.isQualified
    val borderCol = if (isSelectedUserCat) MaterialTheme.colorScheme.primary else Color(0xFFCFD8DC)
    val bgCol = if (isPass) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val textCol = if (isPass) ExamPassGreen else ExamFailRed

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(if (isSelectedUserCat) 2.dp else 1.dp, borderCol, RoundedCornerShape(8.dp))
            .background(bgCol)
            .padding(vertical = 8.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = catQual.category.shortName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF263238)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Icon(
                imageVector = if (isPass) Icons.Default.Check else Icons.Default.Close,
                contentDescription = if (isPass) "Pass" else "Fail",
                tint = textCol,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "≤${catQual.allowedErrorPercent}%",
                fontSize = 9.sp,
                color = Color(0xFF546E7A)
            )
        }
    }
}

@Composable
fun MetricRowItem(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(vertical = 4.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CrossExamRow(status: CrossExamStatus) {
    val isPass = status.isQualified
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isPass) Color(0xFFF1F8E9) else Color(0xFFFAFAFA),
                RoundedCornerShape(8.dp)
            )
            .border(1.dp, if (isPass) Color(0xFFAED581) else Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(status.examName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("${status.speedRequirement} · Limit: ${status.errorLimit}", fontSize = 10.sp, color = Color(0xFF546E7A))
        }

        Box(
            modifier = Modifier
                .background(
                    if (isPass) ExamPassGreen else Color(0xFFE0E0E0),
                    RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = if (isPass) "✓ Qualified" else "Not yet",
                color = if (isPass) Color.White else Color(0xFF424242),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
