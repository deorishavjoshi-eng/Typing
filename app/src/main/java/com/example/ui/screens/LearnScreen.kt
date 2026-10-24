package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MainViewModel

@Composable
fun LearnScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

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
                modifier = Modifier.testTag("learn_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = "Typing Rules & Examination Patterns",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LearnCard(
                title = "1. Posture & Touch Typing Fundamentals",
                content = """
                    • Rest fingers naturally on the Home Row: Left hand (A, S, D, F) and Right hand (J, K, L, ;).
                    • Keep your eyes glued to the original master passage rather than your keyboard or typed text.
                    • Maintain wrists straight and level with your desk; avoid resting wrists on hard table edges.
                    • Practice smooth, rhythmic keystrokes at a steady tempo instead of rapid bursts followed by long pauses.
                """.trimIndent()
            )

            LearnCard(
                title = "2. SSC CGL & CHSL DEST Official Scoring Scheme",
                content = """
                    Full Mistakes (Deducted 1.0 point each):
                    • Omission of every word figure in the passage.
                    • Substitution of a wrong word or misspelt word.
                    • Addition of every word not found in the original text.
                    • Incomplete or truncated word.

                    Half Mistakes (Deducted 0.5 point each):
                    • Capitalization error: typing small letters where capitals are required or vice versa.
                    • Punctuation error: missing or incorrect punctuation mark.
                    • Spacing error: unnecessary space between letters of a word, or no space between two words.
                    • Transposition error: words placed in inverted order.

                    Error Formula:
                    Percentage of Errors = (Full Mistakes + Half Mistakes ÷ 2) ÷ Total Words Typed × 100.
                """.trimIndent()
            )

            LearnCard(
                title = "3. RRB NTPC (CBTST) Evaluation Formula",
                content = """
                    • Duration: Exactly 10 minutes.
                    • Speed Requirement: Minimum 30 Words Per Minute (Net) in English or 25 WPM in Hindi.
                    • Permissible Mistakes: 5% of total words typed are allowed without any deduction penalty.
                    • Excess Mistakes Penalty: For every mistake exceeding the 5% threshold, 10 words are deducted from your total words typed!
                    • Formula:
                      Excess Mistakes = Total Mistakes - (Total Words Typed × 0.05).
                      Net Speed (WPM) = [Total Words Typed - (Excess Mistakes × 10)] ÷ 10 minutes.
                    • Qualifying Benchmark: Net Speed must be ≥ 30 WPM to qualify.
                """.trimIndent()
            )

            LearnCard(
                title = "4. DSSSB & KVS / NVS JSA Patterns",
                content = """
                    • DSSSB (Junior Assistant / LDC): 10 minutes test at 35 WPM in English. Extremely strict accuracy criteria: UR candidates must maintain errors below 3%, while OBC/SC/ST/EWS must maintain errors below 5%.
                    • KVS & NVS (Junior Secretariat Assistant): 10 minutes test on Computer at 35 WPM (KVS) or 30 WPM (NVS) in English. Evaluated on CBSE pattern where each mistake incurs a 5-word penalty deduction.
                """.trimIndent()
            )

            LearnCard(
                title = "5. Key Rules for Government Typing Tests",
                content = """
                    • Single Paragraph Format: Do NOT press the 'Enter' or 'Tab' key during the test. All exam passages are presented as a single continuous paragraph.
                    • Copy-Paste is Strictly Disabled: In all real TCS iON and government exam portals, Ctrl+C, Ctrl+V, right-click, and cut are locked.
                    • Backspace Strategy: Full backspace is enabled in standard exams. However, do not repeatedly erase entire sentences; correct mistakes within the immediate word, otherwise continue typing to preserve net speed.
                """.trimIndent()
            )
        }
    }
}

@Composable
fun LearnCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
