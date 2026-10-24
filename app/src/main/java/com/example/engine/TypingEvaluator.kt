package com.example.engine

import com.example.data.CandidateCategory
import com.example.data.ExamRule
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class AlignmentStats(
    val correctWords: Int,
    val halfMistakes: Int,
    val wrongWords: Int,
    val omittedWords: Int,
    val extraWords: Int,
    val reachedOriginalWords: Int,
    val unreachedOriginalWords: Int
)

data class CategoryQualification(
    val category: CandidateCategory,
    val allowedErrorPercent: Double,
    val requiredWpm: Int,
    val isQualified: Boolean,
    val reason: String
)

data class EvaluationResult(
    val timeUsedSeconds: Int,
    val totalTimeSeconds: Int,
    val keystrokes: Int,
    val backspaceCount: Int,
    val originalWordsCount: Int,
    val typedWordsCount: Int,
    val typedCharsCount: Int,
    val alignment: AlignmentStats,
    val fullMistakesCount: Int,
    val halfMistakesCount: Int,
    val totalMistakes: Double,
    val errorPercentTypedPart: Double,
    val errorPercentSscOverall: Double,
    val activeErrorPercent: Double,
    val grossWpm: Int,
    val netWpm: Int,
    val grossKdph: Int,
    val netKdph: Int,
    val rrbNetWpm: Int,
    val accuracyPercent: Double,
    val completionPercent: Double,
    val userCategory: CandidateCategory,
    val isUserQualified: Boolean,
    val allowedErrorPercent: Double,
    val categoryQualifications: List<CategoryQualification>,
    val crossExamResults: List<CrossExamStatus>
)

data class CrossExamStatus(
    val examName: String,
    val speedRequirement: String,
    val errorLimit: String,
    val isQualified: Boolean,
    val statusNote: String
)

object TypingEvaluator {

    private fun normalize(w: String): String {
        return w.lowercase().replace(Regex("[^\\p{L}\\p{M}\\p{N}]"), "")
    }

    private fun wordDistanceCost(orig: String, typed: String): Double {
        if (orig == typed) return 0.0
        val normOrig = normalize(orig)
        val normTyped = normalize(typed)
        // If words match letters/numbers ignoring case and punctuation -> Half mistake
        if (normOrig.isNotEmpty() && normOrig == normTyped) {
            return 0.5
        }
        return 1.0
    }

    fun align(originalWords: List<String>, typedWords: List<String>): AlignmentStats {
        val m = originalWords.size
        val n = typedWords.size
        val dp = Array(m + 1) { DoubleArray(n + 1) }

        for (i in 0..m) dp[i][0] = i.toDouble()
        for (j in 0..n) dp[0][j] = j.toDouble()

        for (i in 1..m) {
            for (j in 1..n) {
                val cost = wordDistanceCost(originalWords[i - 1], typedWords[j - 1])
                val replace = dp[i - 1][j - 1] + cost
                val delete = dp[i - 1][j] + 1.0
                val insert = dp[i][j - 1] + 1.0
                dp[i][j] = min(replace, min(delete, insert))
            }
        }

        // Find how many words in the original passage were actually reached
        var bestI = 0
        var bestScore = Double.POSITIVE_INFINITY
        for (i in 0..m) {
            if (dp[i][n] <= bestScore) {
                bestScore = dp[i][n]
                bestI = i
            }
        }

        var i = bestI
        var j = n
        var correct = 0
        var half = 0
        var wrong = 0
        var omit = 0
        var extra = 0

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && abs(dp[i][j] - (dp[i - 1][j - 1] + wordDistanceCost(originalWords[i - 1], typedWords[j - 1]))) < 1e-6) {
                val cost = wordDistanceCost(originalWords[i - 1], typedWords[j - 1])
                when (cost) {
                    0.0 -> correct++
                    0.5 -> half++
                    else -> wrong++
                }
                i--
                j--
            } else if (i > 0 && abs(dp[i][j] - (dp[i - 1][j] + 1.0)) < 1e-6) {
                omit++
                i--
            } else {
                extra++
                j--
            }
        }

        return AlignmentStats(
            correctWords = correct,
            halfMistakes = half,
            wrongWords = wrong,
            omittedWords = omit,
            extraWords = extra,
            reachedOriginalWords = bestI,
            unreachedOriginalWords = m - bestI
        )
    }

    fun evaluate(
        originalText: String,
        typedText: String,
        timeUsedSeconds: Int,
        totalTimeSeconds: Int,
        keystrokes: Int,
        backspaceCount: Int,
        examRule: ExamRule,
        userCategory: CandidateCategory,
        countUnreachedAsMistakes: Boolean
    ): EvaluationResult {
        val originalWords = originalText.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        val typedWords = typedText.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }

        val alignment = align(originalWords, typedWords)
        val fullMistakes = alignment.wrongWords + alignment.omittedWords + alignment.extraWords
        val halfMistakes = alignment.halfMistakes

        val mistakesInTypedPart = fullMistakes + (halfMistakes * 0.5)
        val sscTotalMistakes = mistakesInTypedPart + alignment.unreachedOriginalWords
        val activeTotalMistakes = mistakesInTypedPart + if (countUnreachedAsMistakes) alignment.unreachedOriginalWords else 0

        val reachedCount = max(alignment.reachedOriginalWords, 1)
        val origTotalCount = max(originalWords.size, 1)

        val errorPercentTypedPart = (mistakesInTypedPart / reachedCount) * 100.0
        val errorPercentSscOverall = (sscTotalMistakes / origTotalCount) * 100.0
        val activeErrorPercent = if (countUnreachedAsMistakes) {
            (activeTotalMistakes / origTotalCount) * 100.0
        } else {
            errorPercentTypedPart
        }

        val effectiveSeconds = max(timeUsedSeconds, 1)
        val hours = effectiveSeconds / 3600.0
        val minutes = effectiveSeconds / 60.0

        val grossKdph = (keystrokes / hours).toInt()
        val grossWpm = (grossKdph / 300.0).toInt() // 5 keystrokes per word standard

        val netChars = typedText.length
        val netKdph = (netChars / hours).toInt()
        val netWpm = (netKdph / 300.0).toInt()

        // RRB NTPC evaluation rule:
        // Permissible mistakes: 5% of total words typed
        // Penalty: (Total mistakes - 5% of words typed) * 10
        // Net Speed = (Total words typed - Penalty) / duration_in_minutes
        val permissibleMistakes = typedWords.size * 0.05
        val excessMistakes = max(0.0, mistakesInTypedPart - permissibleMistakes)
        val rrbPenaltyWords = excessMistakes * 10.0
        val durationMins = max(1.0, totalTimeSeconds / 60.0)
        val rrbNetWpm = max(0.0, (typedWords.size - rrbPenaltyWords) / durationMins).toInt()

        val accuracyPercent = max(0.0, 100.0 - errorPercentTypedPart)
        val completionPercent = min(100.0, (keystrokes.toDouble() / examRule.targetKeystrokes.coerceAtLeast(1)) * 100.0)

        // Evaluate for all categories
        val categoryQualifications = CandidateCategory.entries.map { cat ->
            val allowedError = examRule.getCutoffForCategory(cat)
            val speedOk = if (examRule.id == "ntpc") {
                rrbNetWpm >= examRule.requiredWpm
            } else {
                netKdph >= examRule.requiredKdph || examRule.requiredKdph == 0
            }
            val errorOk = activeErrorPercent <= allowedError
            val qualified = typedWords.isNotEmpty() && speedOk && errorOk

            val reason = when {
                typedWords.isEmpty() -> "No words typed"
                !speedOk -> "Speed below required ${examRule.requiredWpm} WPM (${examRule.requiredKdph} KDPH)"
                !errorOk -> "Errors %.2f%% exceeded limit of %.1f%%".format(activeErrorPercent, allowedError)
                else -> "Qualified (Speed & accuracy met)"
            }

            CategoryQualification(
                category = cat,
                allowedErrorPercent = allowedError,
                requiredWpm = examRule.requiredWpm,
                isQualified = qualified,
                reason = reason
            )
        }

        val userCatQual = categoryQualifications.find { it.category == userCategory }
            ?: categoryQualifications.first()

        // Cross-exam comparison
        val crossExamResults = listOf(
            CrossExamStatus(
                examName = "SSC CGL DEST",
                speedRequirement = "8,000 KDPH (~27 WPM)",
                errorLimit = "5.0% (UR)",
                isQualified = typedWords.isNotEmpty() && netKdph >= 8000 && activeErrorPercent <= 5.0,
                statusNote = if (netKdph >= 8000 && activeErrorPercent <= 5.0) "Pass (Cleared)" else "Need 8k KDPH & ≤5% error"
            ),
            CrossExamStatus(
                examName = "SSC CHSL (LDC/JSA)",
                speedRequirement = "10,500 KDPH (35 WPM)",
                errorLimit = "7.0% (UR)",
                isQualified = typedWords.isNotEmpty() && netKdph >= 10500 && activeErrorPercent <= 7.0,
                statusNote = if (netKdph >= 10500 && activeErrorPercent <= 7.0) "Pass (Cleared)" else "Need 35 WPM & ≤7% error"
            ),
            CrossExamStatus(
                examName = "SSC CHSL (DEO)",
                speedRequirement = "8,000 KDPH (~27 WPM)",
                errorLimit = "5.0% (UR)",
                isQualified = typedWords.isNotEmpty() && netKdph >= 8000 && activeErrorPercent <= 5.0,
                statusNote = if (netKdph >= 8000 && activeErrorPercent <= 5.0) "Pass (Cleared)" else "Need 8k KDPH & ≤5% error"
            ),
            CrossExamStatus(
                examName = "RRB NTPC (CBTST)",
                speedRequirement = "30 Net WPM",
                errorLimit = "5% unpenalized + 10x deduction",
                isQualified = typedWords.isNotEmpty() && rrbNetWpm >= 30,
                statusNote = if (rrbNetWpm >= 30) "Pass ($rrbNetWpm WPM Net)" else "Need ≥30 Net WPM (Current: $rrbNetWpm)"
            ),
            CrossExamStatus(
                examName = "DSSSB (JA/LDC)",
                speedRequirement = "35 WPM Net",
                errorLimit = "3.0% (UR) / 5% (OBC/SC/ST)",
                isQualified = typedWords.isNotEmpty() && netWpm >= 35 && activeErrorPercent <= 3.0,
                statusNote = if (netWpm >= 35 && activeErrorPercent <= 3.0) "Pass (Cleared)" else "Need 35 WPM & ≤3% error"
            ),
            CrossExamStatus(
                examName = "KVS / NVS (JSA)",
                speedRequirement = "35 WPM (KVS) / 30 WPM (NVS)",
                errorLimit = "CBSE 5-word penalty rule",
                isQualified = typedWords.isNotEmpty() && netWpm >= 35 && activeErrorPercent <= 7.0,
                statusNote = if (netWpm >= 35 && activeErrorPercent <= 7.0) "Pass (Cleared)" else "Need 35 WPM & ≤7% error"
            )
        )

        return EvaluationResult(
            timeUsedSeconds = timeUsedSeconds,
            totalTimeSeconds = totalTimeSeconds,
            keystrokes = keystrokes,
            backspaceCount = backspaceCount,
            originalWordsCount = originalWords.size,
            typedWordsCount = typedWords.size,
            typedCharsCount = netChars,
            alignment = alignment,
            fullMistakesCount = fullMistakes,
            halfMistakesCount = halfMistakes,
            totalMistakes = activeTotalMistakes,
            errorPercentTypedPart = errorPercentTypedPart,
            errorPercentSscOverall = errorPercentSscOverall,
            activeErrorPercent = activeErrorPercent,
            grossWpm = grossWpm,
            netWpm = netWpm,
            grossKdph = grossKdph,
            netKdph = netKdph,
            rrbNetWpm = rrbNetWpm,
            accuracyPercent = accuracyPercent,
            completionPercent = completionPercent,
            userCategory = userCategory,
            isUserQualified = userCatQual.isQualified,
            allowedErrorPercent = userCatQual.allowedErrorPercent,
            categoryQualifications = categoryQualifications,
            crossExamResults = crossExamResults
        )
    }
}
