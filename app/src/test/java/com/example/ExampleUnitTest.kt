package com.example

import com.example.data.CandidateCategory
import com.example.data.ExamRegistry
import com.example.engine.TypingEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testWordAlignmentExactMatch() {
        val original = "The quick brown fox jumps over the lazy dog"
        val typed = "The quick brown fox jumps over the lazy dog"
        val origWords = original.split(" ")
        val typedWords = typed.split(" ")

        val stats = TypingEvaluator.align(origWords, typedWords)
        assertEquals(origWords.size, stats.correctWords)
        assertEquals(0, stats.wrongWords)
        assertEquals(0, stats.omittedWords)
        assertEquals(0, stats.extraWords)
        assertEquals(0, stats.halfMistakes)
    }

    @Test
    fun testWordAlignmentWithCapitalizationHalfMistake() {
        val original = "The quick brown fox"
        val typed = "the quick brown fox"
        val origWords = original.split(" ")
        val typedWords = typed.split(" ")

        val stats = TypingEvaluator.align(origWords, typedWords)
        assertEquals(3, stats.correctWords)
        assertEquals(1, stats.halfMistakes) // "the" vs "The" is a half mistake
        assertEquals(0, stats.wrongWords)
    }

    @Test
    fun testEvaluationResultQualified() {
        // 35 words typed in 60 seconds (1 minute) = 35 WPM (>= 30 WPM required for NTPC)
        val text35Words = "The Indian Railways network is undergoing its most comprehensive technological modernization in post-independence history with the introduction of Vande Bharat Express trains running at high speeds across corridors with superior acceleration and passenger comfort."
        val words = text35Words.split(" ").filter { it.isNotEmpty() }
        assertTrue(words.size >= 30)

        val eval = TypingEvaluator.evaluate(
            originalText = text35Words,
            typedText = text35Words,
            timeUsedSeconds = 60,
            totalTimeSeconds = 60,
            keystrokes = text35Words.length,
            backspaceCount = 0,
            examRule = ExamRegistry.RRB_NTPC,
            userCategory = CandidateCategory.UR,
            countUnreachedAsMistakes = false
        )

        assertEquals(0.0, eval.activeErrorPercent, 0.01)
        assertEquals(100.0, eval.accuracyPercent, 0.01)
        assertTrue(eval.rrbNetWpm >= 30)
        assertTrue(eval.isUserQualified)
    }

    @Test
    fun testEvaluationResultFailsDueToLowSpeed() {
        val shortText = "India is a vast country."
        val eval = TypingEvaluator.evaluate(
            originalText = shortText,
            typedText = shortText,
            timeUsedSeconds = 600,
            totalTimeSeconds = 600,
            keystrokes = shortText.length,
            backspaceCount = 0,
            examRule = ExamRegistry.RRB_NTPC,
            userCategory = CandidateCategory.UR,
            countUnreachedAsMistakes = false
        )

        assertEquals(0.0, eval.activeErrorPercent, 0.01)
        assertFalse(eval.isUserQualified) // Only ~5 words in 10 minutes fails speed requirement
    }
}
