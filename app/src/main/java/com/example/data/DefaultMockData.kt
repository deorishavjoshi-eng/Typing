package com.example.data

object DefaultMockData {
    val ALL_DEFAULT_MOCKS: List<TypingMockEntity> by lazy {
        val list = mutableListOf<TypingMockEntity>()
        list.addAll(DefaultMockDataEasy.mocks)
        list.addAll(DefaultMockDataModerate.mocks)
        list.addAll(DefaultMockDataHard.mocks)
        list.addAll(DefaultMockDataAdvanced.mocks)
        list.addAll(DefaultMockDataExams.mocks)
        list
    }
}
