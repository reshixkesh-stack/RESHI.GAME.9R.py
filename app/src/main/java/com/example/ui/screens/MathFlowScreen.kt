package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FlowCell
import com.example.ui.MathFlowUIState
import com.example.ui.components.*

@Composable
fun MathFlowScreen(
    state: MathFlowUIState,
    onCellTap: (Int, Int) -> Unit,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    onNextLevel: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Flow Header with target and live expression
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                color = Slate800,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Slate700)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("GOAL:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = Emerald600, shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    text = "${state.puzzle.targetScore}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("CURRENT:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = if (state.currentScore == state.puzzle.targetScore) Emerald600 else Slate700,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = state.currentScore?.toString() ?: "...",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = onUndo, enabled = state.currentPath.size > 1, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Filled.Undo, contentDescription = "Undo", tint = if (state.currentPath.size > 1) Color.White else Slate700, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = onReset, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Filled.Refresh, contentDescription = "Reset", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = Slate900,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (state.currentExpression.isNotEmpty()) state.currentExpression else "Tap adjacent cells to connect path to Goal",
                            color = if (state.currentScore == state.puzzle.targetScore) Emerald500 else Color.LightGray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Grid Board
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = Slate800,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.5.dp, Slate700),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (r in 0 until state.puzzle.rows) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                for (c in 0 until state.puzzle.cols) {
                                    val cell = state.puzzle.grid[r][c]
                                    val coord = r to c
                                    val isInPath = coord in state.currentPath
                                    val isPathHead = state.currentPath.lastOrNull() == coord

                                    FlowCellView(
                                        cell = cell,
                                        isInPath = isInPath,
                                        isPathHead = isPathHead,
                                        onClick = { onCellTap(r, c) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Hint footer instructions
            Surface(
                color = Slate800.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = "Rule: Alternate between numbers and operators. Reach GOAL with target score!",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        if (state.isSolved) {
            VictoryCelebrationDialog(
                stars = 3,
                timeSeconds = state.elapsedSeconds,
                levelNumber = state.puzzle.levelNumber,
                onNextLevel = onNextLevel,
                onReplay = onReset
            )
        }
    }
}

@Composable
fun FlowCellView(
    cell: FlowCell,
    isInPath: Boolean,
    isPathHead: Boolean,
    onClick: () -> Unit
) {
    val isNum = cell is FlowCell.Number
    val isStart = isNum && (cell as FlowCell.Number).isStart
    val isGoal = isNum && (cell as FlowCell.Number).isGoal

    val bgColor = when {
        isPathHead -> Amber500
        isInPath -> Indigo600
        isStart -> Indigo600.copy(alpha = 0.3f)
        isGoal -> Emerald600.copy(alpha = 0.3f)
        isNum -> Slate900
        else -> Slate700.copy(alpha = 0.4f)
    }

    val borderColor = when {
        isPathHead -> Color.White
        isInPath -> Indigo500
        isGoal -> Emerald500
        isStart -> Indigo500
        else -> Slate700
    }

    Surface(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("flow_cell_${cell.row}_${cell.col}"),
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(if (isPathHead || isGoal) 2.dp else 1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (cell) {
                is FlowCell.Number -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${cell.value}",
                            color = if (isPathHead) Slate900 else Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                        if (isStart) {
                            Text("START", color = if (isPathHead) Slate900 else Indigo500, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        } else if (isGoal) {
                            Text("GOAL", color = if (isPathHead) Slate900 else Emerald500, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                is FlowCell.Operator -> {
                    Text(
                        text = cell.op.symbol,
                        color = if (isPathHead) Slate900 else Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }
        }
    }
}
