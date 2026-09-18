package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM level_records")
    fun getAllRecords(): Flow<List<LevelRecordEntity>>

    @Query("SELECT * FROM level_records WHERE gameMode = :mode")
    fun getRecordsForMode(mode: String): Flow<List<LevelRecordEntity>>

    @Query("SELECT * FROM level_records WHERE id = :id LIMIT 1")
    suspend fun getRecordById(id: String): LevelRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecord(record: LevelRecordEntity)

    @Query("SELECT * FROM player_stats WHERE id = 1 LIMIT 1")
    fun getPlayerStats(): Flow<PlayerStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlayerStats(stats: PlayerStatsEntity)
}
