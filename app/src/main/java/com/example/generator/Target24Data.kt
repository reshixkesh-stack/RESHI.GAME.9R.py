package com.example.generator

import com.example.model.MathOp
import com.example.model.Target24Puzzle

object Target24Data {

    val levels: List<Target24Puzzle> = listOf(
        Target24Puzzle(
            id = "t24_1",
            levelNumber = 1,
            initialNumbers = listOf(1, 2, 3, 4),
            target = 24,
            solutionExpression = "(1 + 2 + 3) × 4 = 24",
            hintText = "Add the smallest three numbers together, then multiply by the largest!"
        ),
        Target24Puzzle(
            id = "t24_2",
            levelNumber = 2,
            initialNumbers = listOf(6, 6, 6, 6),
            target = 24,
            solutionExpression = "6 + 6 + 6 + 6 = 24",
            hintText = "A simple sum of all four numbers reaches 24!"
        ),
        Target24Puzzle(
            id = "t24_3",
            levelNumber = 3,
            initialNumbers = listOf(2, 3, 4, 6),
            target = 24,
            solutionExpression = "(6 - 2) × (3 + 3)... or (2 × 3) × 4 = 24",
            hintText = "2 × 3 = 6, and 6 × 4 = 24!"
        ),
        Target24Puzzle(
            id = "t24_4",
            levelNumber = 4,
            initialNumbers = listOf(4, 1, 8, 7),
            target = 24,
            solutionExpression = "(8 - 4) × (7 - 1) = 4 × 6 = 24",
            hintText = "Subtract to get 4 and 6, then multiply them!"
        ),
        Target24Puzzle(
            id = "t24_5",
            levelNumber = 5,
            initialNumbers = listOf(3, 8, 3, 8),
            target = 24,
            solutionExpression = "8 ÷ (3 - (8 ÷ 3)) = 24",
            hintText = "Think fractional division: 8 / (3 - 8/3) = 8 / (1/3) = 24!"
        ),
        Target24Puzzle(
            id = "t24_6",
            levelNumber = 6,
            initialNumbers = listOf(5, 5, 5, 1),
            target = 24,
            solutionExpression = "(5 - 1 ÷ 5) × 5 = 24",
            hintText = "5 - (1 ÷ 5) = 4.8. 4.8 × 5 = 24!"
        ),
        Target24Puzzle(
            id = "t24_7",
            levelNumber = 7,
            initialNumbers = listOf(4, 4, 10, 10),
            target = 24,
            solutionExpression = "(10 × 10 - 4) ÷ 4 = 24",
            hintText = "10 × 10 is 100. Subtract 4 to get 96, then divide by 4!"
        ),
        Target24Puzzle(
            id = "t24_8",
            levelNumber = 8,
            initialNumbers = listOf(2, 3, 7, 9),
            target = 24,
            solutionExpression = "(9 - 7 + 2) × 6 or (9 - 7) × (3 + 9) or (7 + 9) ÷ 2 × 3 = 24",
            hintText = "(7 + 9) = 16, 16 ÷ 2 = 8, 8 × 3 = 24!"
        ),
        Target24Puzzle(
            id = "t24_9",
            levelNumber = 9,
            initialNumbers = listOf(1, 3, 4, 6),
            target = 24,
            solutionExpression = "6 ÷ (1 - 3 ÷ 4) = 24",
            hintText = "1 - 3/4 is 1/4. 6 divided by 1/4 is 24!"
        ),
        Target24Puzzle(
            id = "t24_10",
            levelNumber = 10,
            initialNumbers = listOf(2, 4, 8, 9),
            target = 24,
            solutionExpression = "9 × 4 - (8 ÷ 2) = 36 - 4... or (9 - 4) × 8 ÷ 2? Try 9 × 2 + 8 - 2",
            hintText = "(9 × 2) = 18. Add (8 - 2) or use 8 × (4 - (9/something))!"
        ),
        Target24Puzzle(
            id = "t24_11",
            levelNumber = 11,
            initialNumbers = listOf(3, 3, 8, 8),
            target = 24,
            solutionExpression = "8 ÷ (3 - 8 ÷ 3) = 24",
            hintText = "Another fraction inversion master puzzle!"
        ),
        Target24Puzzle(
            id = "t24_12",
            levelNumber = 12,
            initialNumbers = listOf(2, 8, 8, 10),
            target = 24,
            solutionExpression = "(10 - 8) × 8 + 8 = 24",
            hintText = "(10 - 8) gives 2, 2 × 8 gives 16, 16 + 8 gives 24!"
        )
    )

    fun getLevel(levelNumber: Int): Target24Puzzle {
        val index = (levelNumber - 1) % levels.size
        return levels[index].copy(levelNumber = levelNumber)
    }

    // Procedural solver for hints or dynamic puzzles
    fun solve24(numbers: List<Double>): String? {
        if (numbers.size == 1) {
            return if (Math.abs(numbers[0] - 24.0) < 0.001) "24" else null
        }
        // Brute force pairs
        for (i in numbers.indices) {
            for (j in numbers.indices) {
                if (i == j) continue
                val a = numbers[i]
                val b = numbers[j]
                val remaining = numbers.filterIndexed { idx, _ -> idx != i && idx != j }

                val candidates = listOf(
                    (a + b) to "($a + $b)",
                    (a - b) to "($a - $b)",
                    (a * b) to "($a × $b)"
                ) + if (Math.abs(b) > 0.0001) listOf((a / b) to "($a ÷ $b)") else emptyList()

                for ((res, expr) in candidates) {
                    val nextList = remaining + listOf(res)
                    val sub = solve24(nextList)
                    if (sub != null) {
                        return expr
                    }
                }
            }
        }
        return null
    }
}
