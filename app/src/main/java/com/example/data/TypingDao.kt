package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TypingDao {
    @Query("SELECT * FROM typing_mocks ORDER BY level ASC, title ASC")
    fun getAllMocks(): Flow<List<TypingMockEntity>>

    @Query("SELECT * FROM typing_mocks WHERE level = :level ORDER BY title ASC")
    fun getMocksByLevel(level: Int): Flow<List<TypingMockEntity>>

    @Query("SELECT * FROM typing_mocks WHERE examTag = :examTag OR examTag = 'all' ORDER BY level ASC, title ASC")
    fun getMocksByExam(examTag: String): Flow<List<TypingMockEntity>>

    @Query("SELECT * FROM typing_mocks WHERE id = :id LIMIT 1")
    suspend fun getMockById(id: String): TypingMockEntity?

    @Query("SELECT COUNT(*) FROM typing_mocks")
    suspend fun getMocksCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMock(mock: TypingMockEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMocks(mocks: List<TypingMockEntity>)

    @Update
    suspend fun updateMock(mock: TypingMockEntity)

    @Delete
    suspend fun deleteMock(mock: TypingMockEntity)

    @Query("DELETE FROM typing_mocks WHERE isCustom = 0")
    suspend fun deleteDefaultMocks()

    @Query("DELETE FROM typing_mocks WHERE id = :id")
    suspend fun deleteMockById(id: String)

    // Results queries
    @Query("SELECT * FROM typing_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<TypingResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: TypingResultEntity)

    @Query("DELETE FROM typing_results")
    suspend fun clearAllResults()
}
