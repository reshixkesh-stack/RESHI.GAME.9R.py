package com.example.generator

import com.example.model.FlowCell
import com.example.model.MathFlowPuzzle
import com.example.model.MathOp

object MathFlowData {

    val levels: List<MathFlowPuzzle> = listOf(
        // LEVEL 1: 3x3 Grid
        // (0,0)[3] (0,1)[+] (0,2)[4]
        // (1,0)[×] (1,1)[2] (1,2)[-]
        // (2,0)[5] (2,1)[+] (2,2)[1] (Goal)
        // Path A: (0,0) -> (0,1)[+] -> (0,2)[4] -> (1,2)[-] -> (2,2)[1] => (3 + 4) - 1 = 6
        // Path B: (0,0) -> (1,0)[×] -> (2,0)[5] -> (2,1)[+] -> (2,2)[1] => (3 × 5) + 1 = 16
        // Target: 16
        MathFlowPuzzle(
            id = "flow_1",
            levelNumber = 1,
            rows = 3,
            cols = 3,
            grid = listOf(
                listOf(
                    FlowCell.Number(0, 0, 3, isStart = true),
                    FlowCell.Operator(0, 1, MathOp.ADD),
                    FlowCell.Number(0, 2, 4)
                ),
                listOf(
                    FlowCell.Operator(1, 0, MathOp.MUL),
                    FlowCell.Number(1, 1, 2),
                    FlowCell.Operator(1, 2, MathOp.SUB)
                ),
                listOf(
                    FlowCell.Number(2, 0, 5),
                    FlowCell.Operator(2, 1, MathOp.ADD),
                    FlowCell.Number(2, 2, 1, isGoal = true)
                )
            ),
            startCoord = 0 to 0,
            goalCoord = 2 to 2,
            targetScore = 16,
            hintPath = listOf(0 to 0, 1 to 0, 2 to 0, 2 to 1, 2 to 2)
        ),
        // LEVEL 2: 3x3 Grid. Target: 14
        // Path: (0,0)[8] -> (0,1)[-] -> (0,2)[1] -> (1,2)[×] -> (2,2)[2] => (8 - 1) × 2 = 14
        MathFlowPuzzle(
            id = "flow_2",
            levelNumber = 2,
            rows = 3,
            cols = 3,
            grid = listOf(
                listOf(
                    FlowCell.Number(0, 0, 8, isStart = true),
                    FlowCell.Operator(0, 1, MathOp.SUB),
                    FlowCell.Number(0, 2, 1)
                ),
                listOf(
                    FlowCell.Operator(1, 0, MathOp.ADD),
                    FlowCell.Number(1, 1, 4),
                    FlowCell.Operator(1, 2, MathOp.MUL)
                ),
                listOf(
                    FlowCell.Number(2, 0, 6),
                    FlowCell.Operator(2, 1, MathOp.SUB),
                    FlowCell.Number(2, 2, 2, isGoal = true)
                )
            ),
            startCoord = 0 to 0,
            goalCoord = 2 to 2,
            targetScore = 14,
            hintPath = listOf(0 to 0, 0 to 1, 0 to 2, 1 to 2, 2 to 2)
        ),
        // LEVEL 3: 4x4 Grid. Target: 21
        // (0,0)[5] -> (0,1)[+] -> (0,2)[2] -> (1,2)[×] -> (2,2)[3] -> (2,3)[-] -> (3,3)[0] => (5+2)×3 - 0 = 21
        MathFlowPuzzle(
            id = "flow_3",
            levelNumber = 3,
            rows = 4,
            cols = 4,
            grid = listOf(
                listOf(
                    FlowCell.Number(0, 0, 5, isStart = true),
                    FlowCell.Operator(0, 1, MathOp.ADD),
                    FlowCell.Number(0, 2, 2),
                    FlowCell.Operator(0, 3, MathOp.MUL)
                ),
                listOf(
                    FlowCell.Operator(1, 0, MathOp.MUL),
                    FlowCell.Number(1, 1, 3),
                    FlowCell.Operator(1, 2, MathOp.MUL),
                    FlowCell.Number(1, 3, 4)
                ),
                listOf(
                    FlowCell.Number(2, 0, 2),
                    FlowCell.Operator(2, 1, MathOp.ADD),
                    FlowCell.Number(2, 2, 3),
                    FlowCell.Operator(2, 3, MathOp.SUB)
                ),
                listOf(
                    FlowCell.Operator(3, 0, MathOp.SUB),
                    FlowCell.Number(3, 1, 1),
                    FlowCell.Operator(3, 2, MathOp.ADD),
                    FlowCell.Number(3, 3, 0, isGoal = true)
                )
            ),
            startCoord = 0 to 0,
            goalCoord = 3 to 3,
            targetScore = 21,
            hintPath = listOf(0 to 0, 0 to 1, 0 to 2, 1 to 2, 2 to 2, 2 to 3, 3 to 3)
        ),
        // LEVEL 4: 4x4 Grid. Target: 30
        // (0,0)[6] -> (1,0)[+] -> (2,0)[4] -> (2,1)[×] -> (2,2)[3] -> (3,2)[+] -> (3,3)[0] => (6+4)×3 + 0 = 30
        MathFlowPuzzle(
            id = "flow_4",
            levelNumber = 4,
            rows = 4,
            cols = 4,
            grid = listOf(
                listOf(
                    FlowCell.Number(0, 0, 6, isStart = true),
                    FlowCell.Operator(0, 1, MathOp.SUB),
                    FlowCell.Number(0, 2, 1),
                    FlowCell.Operator(0, 3, MathOp.ADD)
                ),
                listOf(
                    FlowCell.Operator(1, 0, MathOp.ADD),
                    FlowCell.Number(1, 1, 5),
                    FlowCell.Operator(1, 2, MathOp.MUL),
                    FlowCell.Number(1, 3, 8)
                ),
                listOf(
                    FlowCell.Number(2, 0, 4),
                    FlowCell.Operator(2, 1, MathOp.MUL),
                    FlowCell.Number(2, 2, 3),
                    FlowCell.Operator(2, 3, MathOp.SUB)
                ),
                listOf(
                    FlowCell.Operator(3, 0, MathOp.ADD),
                    FlowCell.Number(3, 1, 2),
                    FlowCell.Operator(3, 2, MathOp.ADD),
                    FlowCell.Number(3, 3, 0, isGoal = true)
                )
            ),
            startCoord = 0 to 0,
            goalCoord = 3 to 3,
            targetScore = 30,
            hintPath = listOf(0 to 0, 1 to 0, 2 to 0, 2 to 1, 2 to 2, 3 to 2, 3 to 3)
        )
    )

    fun getLevel(levelNumber: Int): MathFlowPuzzle {
        val index = (levelNumber - 1) % levels.size
        return levels[index].copy(levelNumber = levelNumber)
    }
}
