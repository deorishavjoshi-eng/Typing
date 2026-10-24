package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "typing_results")
data class TypingResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mockId: String,
    val mockTitle: String,
    val examName: String,
    val category: String, // UR, EWS, OBC, SC, ST, PwD
    val dateFormatted: String,
    val timestamp: Long = System.currentTimeMillis(),
    val timeUsedSeconds: Int,
    val totalTimeSeconds: Int,
    val keystrokes: Int,
    val backspaceCount: Int,
    val wordsTyped: Int,
    val correctWords: Int,
    val wrongWords: Int,
    val omittedWords: Int,
    val extraWords: Int,
    val halfMistakes: Int,
    val unreachedWords: Int,
    val totalMistakes: Double,
    val errorPercentage: Double,
    val allowedErrorPercentage: Double,
    val netWpm: Int,
    val grossWpm: Int,
    val netKdph: Int,
    val accuracyPercentage: Double,
    val isQualified: Boolean,
    val categoryQualJson: String
)
