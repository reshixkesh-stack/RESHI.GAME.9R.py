package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_records")
data class LevelRecordEntity(
    @PrimaryKey val id: String, // e.g. "CROSSGRID_EASY_1"
    val gameMode: String,
    val difficulty: String,
    val levelIndex: Int,
    val stars: Int,
    val bestTimeSeconds: Int,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "player_stats")
data class PlayerStatsEntity(
    @PrimaryKey val id: Int = 1,
    val puzzlesSolved: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val blitzHighScore: Int = 0,
    val totalStars: Int = 0,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
)
