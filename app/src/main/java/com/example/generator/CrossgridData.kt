package com.example.generator

import com.example.model.*

object CrossgridData {

    // Helper to build 2x2 grid
    // Row 0: c(0,0) opH(0) c(0,1) = r0
    // Row 1: c(1,0) opH(1) c(1,1) = r1
    // Col 0: c(0,0) opV(0) c(1,0) = c0
    // Col 1: c(0,1) opV(1) c(1,1) = c1
    private fun build2x2(
        id: String,
        levelNumber: Int,
        diff: Difficulty,
        v00: Int, v01: Int,
        v10: Int, v11: Int,
        opH0: MathOp, opH1: MathOp,
        opV0: MathOp, opV1: MathOp,
        prefilledCoords: Set<Pair<Int, Int>>
    ): CrossgridPuzzle {
        val cells = mutableMapOf<Pair<Int, Int>, NumberCell>()
        val matrix = arrayOf(intArrayOf(v00, v01), intArrayOf(v10, v11))

        for (r in 0..1) {
            for (c in 0..1) {
                val correct = matrix[r][c]
                val isPre = (r to c) in prefilledCoords
                cells[r to c] = NumberCell(
                    row = r,
                    col = c,
                    correctValue = correct,
                    currentValue = if (isPre) correct else null,
                    isPreFilled = isPre
                )
            }
        }

        val targetR0 = opH0.apply(v00, v01)
        val targetR1 = opH1.apply(v10, v11)
        val targetC0 = opV0.apply(v00, v10)
        val targetC1 = opV1.apply(v01, v11)

        val rowEqs = listOf(
            CrossgridEquation(
                id = "R0",
                isRow = true,
                index = 0,
                target = targetR0,
                cellIndices = listOf(0 to 0, 0 to 1),
                operators = listOf(opH0)
            ),
            CrossgridEquation(
                id = "R1",
                isRow = true,
                index = 1,
                target = targetR1,
                cellIndices = listOf(1 to 0, 1 to 1),
                operators = listOf(opH1)
            )
        )

        val colEqs = listOf(
            CrossgridEquation(
                id = "C0",
                isRow = false,
                index = 0,
                target = targetC0,
                cellIndices = listOf(0 to 0, 1 to 0),
                operators = listOf(opV0)
            ),
            CrossgridEquation(
                id = "C1",
                isRow = false,
                index = 1,
                target = targetC1,
                cellIndices = listOf(0 to 1, 1 to 1),
                operators = listOf(opV1)
            )
        )

        val bank = mutableListOf<Int>()
        for (r in 0..1) {
            for (c in 0..1) {
                if ((r to c) !in prefilledCoords) {
                    bank.add(matrix[r][c])
                }
            }
        }
        bank.shuffle()

        return CrossgridPuzzle(
            id = id,
            levelNumber = levelNumber,
            difficulty = diff,
            numberRows = 2,
            numberCols = 2,
            cells = cells,
            rowEquations = rowEqs,
            colEquations = colEqs,
            availableBankNumbers = bank
        )
    }

    // Helper to build 3x3 grid
    private fun build3x3(
        id: String,
        levelNumber: Int,
        diff: Difficulty,
        vals: Array<IntArray>, // 3x3
        opH: Array<Array<MathOp>>, // 3 rows, 2 ops each
        opV: Array<Array<MathOp>>, // 2 rows, 3 ops each
        prefilledCoords: Set<Pair<Int, Int>>
    ): CrossgridPuzzle {
        val cells = mutableMapOf<Pair<Int, Int>, NumberCell>()
        for (r in 0..2) {
            for (c in 0..2) {
                val correct = vals[r][c]
                val isPre = (r to c) in prefilledCoords
                cells[r to c] = NumberCell(
                    row = r,
                    col = c,
                    correctValue = correct,
                    currentValue = if (isPre) correct else null,
                    isPreFilled = isPre
                )
            }
        }

        val rowEqs = (0..2).map { r ->
            var target = vals[r][0]
            target = opH[r][0].apply(target, vals[r][1])
            target = opH[r][1].apply(target, vals[r][2])
            CrossgridEquation(
                id = "R$r",
                isRow = true,
                index = r,
                target = target,
                cellIndices = listOf(r to 0, r to 1, r to 2),
                operators = listOf(opH[r][0], opH[r][1])
            )
        }

        val colEqs = (0..2).map { c ->
            var target = vals[0][c]
            target = opV[0][c].apply(target, vals[1][c])
            target = opV[1][c].apply(target, vals[2][c])
            CrossgridEquation(
                id = "C$c",
                isRow = false,
                index = c,
                target = target,
                cellIndices = listOf(0 to c, 1 to c, 2 to c),
                operators = listOf(opV[0][c], opV[1][c])
            )
        }

        val bank = mutableListOf<Int>()
        for (r in 0..2) {
            for (c in 0..2) {
                if ((r to c) !in prefilledCoords) {
                    bank.add(vals[r][c])
                }
            }
        }
        bank.shuffle()

        return CrossgridPuzzle(
            id = id,
            levelNumber = levelNumber,
            difficulty = diff,
            numberRows = 3,
            numberCols = 3,
            cells = cells,
            rowEquations = rowEqs,
            colEquations = colEqs,
            availableBankNumbers = bank
        )
    }

    val levels: List<CrossgridPuzzle> = listOf(
        // EASY 1: 2x2
        // [3] + [5] = 8
        //  +     -
        // [4] + [2] = 6
        //  =     =
        //  7     3
        build2x2(
            id = "crossgrid_1",
            levelNumber = 1,
            diff = Difficulty.EASY,
            v00 = 3, v01 = 5,
            v10 = 4, v11 = 2,
            opH0 = MathOp.ADD, opH1 = MathOp.ADD,
            opV0 = MathOp.ADD, opV1 = MathOp.SUB,
            prefilledCoords = setOf(0 to 0)
        ),
        // EASY 2: 2x2
        // [6] - [2] = 4
        //  +     +
        // [3] + [5] = 8
        //  =     =
        //  9     7
        build2x2(
            id = "crossgrid_2",
            levelNumber = 2,
            diff = Difficulty.EASY,
            v00 = 6, v01 = 2,
            v10 = 3, v11 = 5,
            opH0 = MathOp.SUB, opH1 = MathOp.ADD,
            opV0 = MathOp.ADD, opV1 = MathOp.ADD,
            prefilledCoords = setOf(1 to 1)
        ),
        // EASY 3: 2x2
        // [7] + [4] = 11
        //  -     +
        // [2] + [8] = 10
        //  =     =
        //  5    12
        build2x2(
            id = "crossgrid_3",
            levelNumber = 3,
            diff = Difficulty.EASY,
            v00 = 7, v01 = 4,
            v10 = 2, v11 = 8,
            opH0 = MathOp.ADD, opH1 = MathOp.ADD,
            opV0 = MathOp.SUB, opV1 = MathOp.ADD,
            prefilledCoords = setOf(0 to 1)
        ),
        // EASY 4: 2x2
        // [9] - [4] = 5
        //  -     -
        // [3] + [2] = 5
        //  =     =
        //  6     2
        build2x2(
            id = "crossgrid_4",
            levelNumber = 4,
            diff = Difficulty.EASY,
            v00 = 9, v01 = 4,
            v10 = 3, v11 = 2,
            opH0 = MathOp.SUB, opH1 = MathOp.ADD,
            opV0 = MathOp.SUB, opV1 = MathOp.SUB,
            prefilledCoords = setOf()
        ),
        // MEDIUM 1: 3x3
        // 2 + 4 + 3 = 9
        // +   -   +
        // 5 - 1 + 6 = 10
        // +   +   -
        // 1 + 8 - 7 = 2
        // =   =   =
        // 8  11   2
        build3x3(
            id = "crossgrid_5",
            levelNumber = 5,
            diff = Difficulty.MEDIUM,
            vals = arrayOf(
                intArrayOf(2, 4, 3),
                intArrayOf(5, 1, 6),
                intArrayOf(1, 8, 7)
            ),
            opH = arrayOf(
                arrayOf(MathOp.ADD, MathOp.ADD),
                arrayOf(MathOp.SUB, MathOp.ADD),
                arrayOf(MathOp.ADD, MathOp.SUB)
            ),
            opV = arrayOf(
                arrayOf(MathOp.ADD, MathOp.SUB, MathOp.ADD),
                arrayOf(MathOp.ADD, MathOp.ADD, MathOp.SUB)
            ),
            prefilledCoords = setOf(0 to 0, 1 to 1)
        ),
        // MEDIUM 2: 3x3 with multiplication
        // 3 × 2 + 5 = 11
        // +   +   -
        // 4 + 6 - 2 = 8
        // -   ×   +
        // 1 + 3 × 4 = 16
        // =   =   =
        // 6  24   7
        build3x3(
            id = "crossgrid_6",
            levelNumber = 6,
            diff = Difficulty.MEDIUM,
            vals = arrayOf(
                intArrayOf(3, 2, 5),
                intArrayOf(4, 6, 2),
                intArrayOf(1, 3, 4)
            ),
            opH = arrayOf(
                arrayOf(MathOp.MUL, MathOp.ADD),
                arrayOf(MathOp.ADD, MathOp.SUB),
                arrayOf(MathOp.ADD, MathOp.MUL)
            ),
            opV = arrayOf(
                arrayOf(MathOp.ADD, MathOp.ADD, MathOp.SUB),
                arrayOf(MathOp.SUB, MathOp.MUL, MathOp.ADD)
            ),
            prefilledCoords = setOf(0 to 1, 2 to 0)
        ),
        // HARD 1: 3x3 with division
        // 8 ÷ 2 × 5 = 20
        // -   +   -
        // 3 × 4 - 6 = 6
        // +   -   +
        // 5 + 9 ÷ 2 = 7
        // =   =   =
        // 10  -3  3
        build3x3(
            id = "crossgrid_7",
            levelNumber = 7,
            diff = Difficulty.HARD,
            vals = arrayOf(
                intArrayOf(8, 2, 5),
                intArrayOf(3, 4, 6),
                intArrayOf(5, 9, 2)
            ),
            opH = arrayOf(
                arrayOf(MathOp.DIV, MathOp.MUL),
                arrayOf(MathOp.MUL, MathOp.SUB),
                arrayOf(MathOp.ADD, MathOp.DIV)
            ),
            opV = arrayOf(
                arrayOf(MathOp.SUB, MathOp.ADD, MathOp.SUB),
                arrayOf(MathOp.ADD, MathOp.SUB, MathOp.ADD)
            ),
            prefilledCoords = setOf(1 to 2)
        ),
        // HARD 2: 3x3
        // 6 × 3 ÷ 2 = 9
        // ÷   +   +
        // 2 + 7 × 1 = 9
        // +   -   -
        // 4 × 2 - 5 = 3
        // =   =   =
        // 7   8   -2
        build3x3(
            id = "crossgrid_8",
            levelNumber = 8,
            diff = Difficulty.HARD,
            vals = arrayOf(
                intArrayOf(6, 3, 2),
                intArrayOf(2, 7, 1),
                intArrayOf(4, 2, 5)
            ),
            opH = arrayOf(
                arrayOf(MathOp.MUL, MathOp.DIV),
                arrayOf(MathOp.ADD, MathOp.MUL),
                arrayOf(MathOp.MUL, MathOp.SUB)
            ),
            opV = arrayOf(
                arrayOf(MathOp.DIV, MathOp.ADD, MathOp.ADD),
                arrayOf(MathOp.ADD, MathOp.SUB, MathOp.SUB)
            ),
            prefilledCoords = setOf(0 to 0)
        ),
        // MASTER 1: 3x3
        // 9 × 2 - 8 = 10
        // -   ÷   ÷
        // 4 + 2 × 3 = 18
        // ×   +   +
        // 2 × 6 - 9 = 3
        // =   =   =
        // 10  7   7
        build3x3(
            id = "crossgrid_9",
            levelNumber = 9,
            diff = Difficulty.MASTER,
            vals = arrayOf(
                intArrayOf(9, 2, 8),
                intArrayOf(4, 2, 3),
                intArrayOf(2, 6, 9)
            ),
            opH = arrayOf(
                arrayOf(MathOp.MUL, MathOp.SUB),
                arrayOf(MathOp.ADD, MathOp.MUL),
                arrayOf(MathOp.MUL, MathOp.SUB)
            ),
            opV = arrayOf(
                arrayOf(MathOp.SUB, MathOp.DIV, MathOp.DIV),
                arrayOf(MathOp.MUL, MathOp.ADD, MathOp.ADD)
            ),
            prefilledCoords = setOf()
        )
    )

    fun getLevel(levelNumber: Int): CrossgridPuzzle {
        val index = (levelNumber - 1) % levels.size
        return levels[index].copy(
            levelNumber = levelNumber,
            // Fresh copy of cells
            cells = levels[index].cells.mapValues { (_, v) ->
                v.copy(currentValue = if (v.isPreFilled) v.correctValue else null)
            },
            availableBankNumbers = levels[index].availableBankNumbers.shuffled()
        )
    }
}
