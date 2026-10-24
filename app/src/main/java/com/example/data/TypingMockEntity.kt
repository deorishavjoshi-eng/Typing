package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "typing_mocks")
data class TypingMockEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val level: Int, // 1: Easy, 2: Moderate, 3: Hard, 4: Advanced
    val examTag: String, // "cgl", "chsl", "ntpc", "dsssb", "kvs_nvs", "hindi", "general"
    val durationMinutes: Int,
    val targetKeystrokes: Int,
    val wordCount: Int,
    val text: String,
    val cutoffGeneral: Double,
    val cutoffEws: Double,
    val cutoffObc: Double,
    val cutoffSc: Double,
    val cutoffSt: Double,
    val cutoffPwd: Double,
    val minSpeedWpm: Int,
    val isCustom: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis()
)
