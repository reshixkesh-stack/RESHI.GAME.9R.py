package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {
    val allRecords: Flow<List<LevelRecordEntity>> = gameDao.getAllRecords()
    val playerStats: Flow<PlayerStatsEntity?> = gameDao.getPlayerStats()

    fun getRecordsForMode(mode: String): Flow<List<LevelRecordEntity>> {
        return gameDao.getRecordsForMode(mode)
    }

    suspend fun saveRecord(record: LevelRecordEntity) {
        val existing = gameDao.getRecordById(record.id)
        if (existing == null) {
            gameDao.saveRecord(record)
        } else {
            // Keep the best stars and best time
            val updated = record.copy(
                stars = maxOf(existing.stars, record.stars),
                bestTimeSeconds = if (existing.bestTimeSeconds > 0) minOf(existing.bestTimeSeconds, record.bestTimeSeconds) else record.bestTimeSeconds
            )
            gameDao.saveRecord(updated)
        }
    }

    suspend fun updateStats(transform: (PlayerStatsEntity) -> PlayerStatsEntity) {
        // Collect current stats synchronously or update default
        val current = PlayerStatsEntity()
        gameDao.savePlayerStats(transform(current))
    }

    suspend fun saveStats(stats: PlayerStatsEntity) {
        gameDao.savePlayerStats(stats)
    }
}
