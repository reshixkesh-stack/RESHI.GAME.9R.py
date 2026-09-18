package com.example.model

sealed class FlowCell {
    abstract val row: Int
    abstract val col: Int

    data class Number(
        override val row: Int,
        override val col: Int,
        val value: Int,
        val isStart: Boolean = false,
        val isGoal: Boolean = false
    ) : FlowCell()

    data class Operator(
        override val row: Int,
        override val col: Int,
        val op: MathOp
    ) : FlowCell()
}

data class MathFlowPuzzle(
    val id: String,
    val levelNumber: Int,
    val rows: Int = 4,
    val cols: Int = 4,
    val grid: List<List<FlowCell>>,
    val startCoord: Pair<Int, Int>,
    val goalCoord: Pair<Int, Int>,
    val targetScore: Int,
    val hintPath: List<Pair<Int, Int>> = emptyList()
)

data class BlitzQuestion(
    val id: String,
    val prompt: String,
    val options: List<Int>,
    val correctIndex: Int,
    val explanation: String
)
