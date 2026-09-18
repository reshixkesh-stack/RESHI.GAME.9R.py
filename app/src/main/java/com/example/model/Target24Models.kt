package com.example.model

data class NumberTile(
    val id: String,
    val value: Int,
    val expression: String = "$value"
)

data class Target24Step(
    val tile1: NumberTile,
    val operator: MathOp,
    val tile2: NumberTile,
    val resultTile: NumberTile,
    val remainingTiles: List<NumberTile>
)

data class Target24Puzzle(
    val id: String,
    val levelNumber: Int,
    val initialNumbers: List<Int>,
    val target: Int = 24,
    val solutionExpression: String = "",
    val hintText: String = ""
)
