package com.example.model

data class NumberCell(
    val row: Int,
    val col: Int,
    val correctValue: Int,
    val currentValue: Int? = null,
    val isPreFilled: Boolean = false
)

enum class MathOp(val symbol: String) {
    ADD("+"),
    SUB("-"),
    MUL("×"),
    DIV("÷");

    fun apply(a: Int, b: Int): Int {
        return when (this) {
            ADD -> a + b
            SUB -> a - b
            MUL -> a * b
            DIV -> if (b != 0 && a % b == 0) a / b else a / (if (b == 0) 1 else b)
        }
    }

    companion object {
        fun fromSymbol(s: String): MathOp = when (s) {
            "+", "ADD" -> ADD
            "-", "SUB" -> SUB
            "×", "*", "MUL" -> MUL
            "÷", "/", "DIV" -> DIV
            else -> ADD
        }
    }
}

enum class EquationStatus {
    INCOMPLETE,
    CORRECT,
    WRONG
}

data class CrossgridEquation(
    val id: String,
    val isRow: Boolean,
    val index: Int,
    val target: Int,
    val cellIndices: List<Pair<Int, Int>>, // (row, col) coordinates of number cells
    val operators: List<MathOp>
)

data class CrossgridPuzzle(
    val id: String,
    val levelNumber: Int,
    val difficulty: Difficulty,
    val numberRows: Int, // e.g. 2 or 3
    val numberCols: Int, // e.g. 2 or 3
    val cells: Map<Pair<Int, Int>, NumberCell>,
    val rowEquations: List<CrossgridEquation>,
    val colEquations: List<CrossgridEquation>,
    val availableBankNumbers: List<Int> // numbers available in tray
) {
    fun evaluateEquation(eq: CrossgridEquation, currentCells: Map<Pair<Int, Int>, NumberCell>): Pair<EquationStatus, Int?> {
        val values = eq.cellIndices.map { currentCells[it]?.currentValue }
        if (values.any { it == null }) {
            return Pair(EquationStatus.INCOMPLETE, null)
        }

        // Sequential left-to-right (or top-to-bottom) evaluation
        var result = values[0]!!
        for (i in eq.operators.indices) {
            val op = eq.operators[i]
            val nextVal = values[i + 1]!!
            if (op == MathOp.DIV && (nextVal == 0 || result % nextVal != 0)) {
                // Not integer division
                return Pair(EquationStatus.WRONG, result / (if (nextVal == 0) 1 else nextVal))
            }
            result = op.apply(result, nextVal)
        }

        return if (result == eq.target) {
            Pair(EquationStatus.CORRECT, result)
        } else {
            Pair(EquationStatus.WRONG, result)
        }
    }

    fun isAllSolved(currentCells: Map<Pair<Int, Int>, NumberCell>): Boolean {
        for (eq in rowEquations) {
            if (evaluateEquation(eq, currentCells).first != EquationStatus.CORRECT) return false
        }
        for (eq in colEquations) {
            if (evaluateEquation(eq, currentCells).first != EquationStatus.CORRECT) return false
        }
        return true
    }
}
