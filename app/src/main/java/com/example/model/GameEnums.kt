package com.example.model

enum class GameMode(val displayName: String, val subtitle: String) {
    CROSSGRID("Crossgrid", "Fill grid to solve all equations"),
    TARGET24("Target 24", "Combine 4 numbers to hit the target"),
    MATH_FLOW("Math Flow", "Navigate the grid path to the goal"),
    DAILY_BLITZ("Daily Blitz", "60s speed challenge & streak rush")
}

enum class Difficulty(val label: String, val starsRequired: Int) {
    EASY("Easy", 0),
    MEDIUM("Medium", 6),
    HARD("Hard", 18),
    MASTER("Master", 36)
}
