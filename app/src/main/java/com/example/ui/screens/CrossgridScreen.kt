package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CrossgridPuzzle
import com.example.model.EquationStatus
import com.example.model.NumberCell
import com.example.ui.CrossgridUIState
import com.example.ui.components.*

@Composable
fun CrossgridScreen(
    state: CrossgridUIState,
    onCellSelected: (Int, Int) -> Unit,
    onNumberPlaced: (Int) -> Unit,
    onClearSelected: () -> Unit,
    onHint: () -> Unit,
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
            // Stats bar (Timer, Moves, Difficulty)
            CrossgridTopStatus(state = state, onReset = onReset, onHint = onHint)

            // The Math Crossgrid Board
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CrossgridBoard(
                    puzzle = state.puzzle,
                    currentCells = state.currentCells,
                    selectedCell = state.selectedCell,
                    rowValidation = state.rowValidation,
                    colValidation = state.colValidation,
                    onCellClick = onCellSelected
                )
            }

            // Number Bank Tray & Actions at the bottom
            CrossgridBottomControls(
                bankNumbers = state.bankNumbers,
                hasSelectedCell = state.selectedCell != null,
                onNumberClick = onNumberPlaced,
                onClearClick = onClearSelected
            )
        }

        if (state.isSolved) {
            val stars = if (state.hintsUsed > 0) 2 else if (state.elapsedSeconds < 60) 3 else 2
            VictoryCelebrationDialog(
                stars = stars,
                timeSeconds = state.elapsedSeconds,
                levelNumber = state.puzzle.levelNumber,
                onNextLevel = onNextLevel,
                onReplay = onReset
            )
        }
    }
}

@Composable
fun CrossgridTopStatus(
    state: CrossgridUIState,
    onReset: () -> Unit,
    onHint: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = Slate800.copy(alpha = 0.6f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Slate700.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Difficulty tag
            Surface(
                color = when (state.puzzle.difficulty.label) {
                    "Easy" -> Emerald600.copy(alpha = 0.2f)
                    "Medium" -> Indigo600.copy(alpha = 0.2f)
                    "Hard" -> Amber500.copy(alpha = 0.2f)
                    else -> Rose500.copy(alpha = 0.2f)
                },
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Slate700)
            ) {
                Text(
                    text = state.puzzle.difficulty.label.uppercase(),
                    color = when (state.puzzle.difficulty.label) {
                        "Easy" -> Emerald500
                        "Medium" -> Indigo500
                        "Hard" -> Amber500
                        else -> Rose500
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            // Timer & Moves
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Timer, contentDescription = "Timer", tint = Color.LightGray, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "%02d:%02d".format(state.elapsedSeconds / 60, state.elapsedSeconds % 60),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.TouchApp, contentDescription = "Moves", tint = Color.LightGray, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${state.movesCount}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Quick actions
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = onHint,
                    modifier = Modifier.size(32.dp).testTag("hint_button")
                ) {
                    Icon(
                        Icons.Filled.Lightbulb,
                        contentDescription = "Hint",
                        tint = Amber500,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onReset,
                    modifier = Modifier.size(32.dp).testTag("reset_button")
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        contentDescription = "Reset",
                        tint = Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CrossgridBoard(
    puzzle: CrossgridPuzzle,
    currentCells: Map<Pair<Int, Int>, NumberCell>,
    selectedCell: Pair<Int, Int>?,
    rowValidation: Map<Int, Pair<EquationStatus, Int?>>,
    colValidation: Map<Int, Pair<EquationStatus, Int?>>,
    onCellClick: (Int, Int) -> Unit
) {
    val rows = puzzle.numberRows
    val cols = puzzle.numberCols

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Slate800,
        border = BorderStroke(1.5.dp, Slate700),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Rows
            for (r in 0 until rows) {
                val rowEq = puzzle.rowEquations.firstOrNull { it.index == r }
                val rowStatus = rowValidation[r]?.first ?: EquationStatus.INCOMPLETE

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (c in 0 until cols) {
                        val cell = currentCells[r to c]
                        val isSelected = selectedCell == (r to c)

                        NumberSlotView(
                            cell = cell,
                            isSelected = isSelected,
                            onClick = { onCellClick(r, c) }
                        )

                        // Operator between cols
                        if (c < cols - 1 && rowEq != null && c < rowEq.operators.size) {
                            Text(
                                text = rowEq.operators[c].symbol,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.width(20.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Equals and Target on right
                    Text(
                        text = "=",
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.width(16.dp),
                        textAlign = TextAlign.Center
                    )

                    TargetBadgeView(
                        target = rowEq?.target ?: 0,
                        status = rowStatus
                    )
                }

                // Vertical operators row between rows
                if (r < rows - 1) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        for (c in 0 until cols) {
                            val colEq = puzzle.colEquations.firstOrNull { it.index == c }
                            val op = colEq?.operators?.getOrNull(r)

                            Box(
                                modifier = Modifier.size(width = 54.dp, height = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = op?.symbol ?: "",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }

                            if (c < cols - 1) {
                                Spacer(modifier = Modifier.width(20.dp))
                            }
                        }
                        // Spacer for right side
                        Spacer(modifier = Modifier.width(16.dp))
                        Spacer(modifier = Modifier.size(46.dp, 24.dp))
                    }
                }
            }

            // Divider row for column equations
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                for (c in 0 until cols) {
                    Box(
                        modifier = Modifier.size(width = 54.dp, height = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "=",
                            color = Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    if (c < cols - 1) {
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Spacer(modifier = Modifier.size(46.dp, 20.dp))
            }

            // Column targets row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (c in 0 until cols) {
                    val colEq = puzzle.colEquations.firstOrNull { it.index == c }
                    val colStatus = colValidation[c]?.first ?: EquationStatus.INCOMPLETE

                    Box(
                        modifier = Modifier.width(54.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TargetBadgeView(
                            target = colEq?.target ?: 0,
                            status = colStatus
                        )
                    }

                    if (c < cols - 1) {
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Spacer(modifier = Modifier.size(46.dp, 40.dp))
            }
        }
    }
}

@Composable
fun NumberSlotView(
    cell: NumberCell?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isPreFilled = cell?.isPreFilled == true
    val hasValue = cell?.currentValue != null

    val borderColor by animateColorAsState(
        targetValue = when {
            isSelected -> Indigo500
            isPreFilled -> Slate700
            hasValue -> Color.White.copy(alpha = 0.3f)
            else -> Slate700
        },
        animationSpec = tween(150),
        label = "border_anim"
    )

    val bgColor = when {
        isSelected -> Indigo600.copy(alpha = 0.25f)
        isPreFilled -> Slate900.copy(alpha = 0.9f)
        hasValue -> Slate900
        else -> Slate700.copy(alpha = 0.3f)
    }

    Surface(
        modifier = Modifier
            .size(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = !isPreFilled) { onClick() }
            .testTag("cell_${cell?.row}_${cell?.col}"),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(if (isSelected) 2.5.dp else 1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (cell?.currentValue != null) {
                Text(
                    text = "${cell.currentValue}",
                    color = if (isPreFilled) Color(0xFF93C5FD) else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            } else {
                Text(
                    text = "•",
                    color = Color.Gray.copy(alpha = 0.4f),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun TargetBadgeView(
    target: Int,
    status: EquationStatus
) {
    val bgColor by animateColorAsState(
        targetValue = when (status) {
            EquationStatus.CORRECT -> Emerald600
            EquationStatus.WRONG -> Rose500.copy(alpha = 0.25f)
            EquationStatus.INCOMPLETE -> Slate700.copy(alpha = 0.5f)
        },
        animationSpec = tween(200),
        label = "target_bg"
    )

    val borderColor = when (status) {
        EquationStatus.CORRECT -> Emerald500
        EquationStatus.WRONG -> Rose500
        EquationStatus.INCOMPLETE -> Slate700
    }

    Surface(
        modifier = Modifier.size(width = 46.dp, height = 40.dp),
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$target",
                color = if (status == EquationStatus.CORRECT) Color.White else Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Black,
                fontSize = 17.sp
            )
        }
    }
}

@Composable
fun CrossgridBottomControls(
    bankNumbers: List<Int>,
    hasSelectedCell: Boolean,
    onNumberClick: (Int) -> Unit,
    onClearClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        color = Slate800,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Slate700)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (hasSelectedCell) "TAP A NUMBER TO PLACE:" else "TAP AN EMPTY CELL FIRST",
                    color = if (hasSelectedCell) Amber500 else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )

                if (hasSelectedCell) {
                    TextButton(
                        onClick = onClearClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Clear Cell", color = Rose500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Number Tiles Tray
            if (bankNumbers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("All tiles placed! Verify equations above.", color = Emerald500, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(bankNumbers) { number ->
                        NumberBankChip(
                            number = number,
                            enabled = hasSelectedCell,
                            onClick = { onNumberClick(number) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NumberBankChip(
    number: Int,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(width = 50.dp, height = 52.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = enabled) { onClick() }
            .testTag("bank_chip_$number"),
        shape = RoundedCornerShape(14.dp),
        color = if (enabled) Indigo600 else Slate700.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, if (enabled) Indigo500 else Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$number",
                color = if (enabled) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}
