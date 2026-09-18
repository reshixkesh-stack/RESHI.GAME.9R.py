package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MathOp
import com.example.model.NumberTile
import com.example.ui.Target24UIState
import com.example.ui.components.*

@Composable
fun Target24Screen(
    state: Target24UIState,
    onTileClick: (NumberTile) -> Unit,
    onOpClick: (MathOp) -> Unit,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    onHint: () -> Unit,
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
            // Target Header Card
            Target24HeaderCard(
                target = state.puzzle.target,
                elapsedSeconds = state.elapsedSeconds,
                onUndo = onUndo,
                canUndo = state.stepHistory.isNotEmpty(),
                onReset = onReset,
                onHint = onHint
            )

            // Hint Card if revealed
            AnimatedVisibility(visible = state.hintRevealed, enter = fadeIn(), exit = fadeOut()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Amber500.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, Amber500.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Lightbulb, contentDescription = "Hint", tint = Amber500, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.puzzle.hintText,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Error message if non-integer or divide by 0
            AnimatedVisibility(visible = state.errorMessage != null, enter = fadeIn(), exit = fadeOut()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Rose500.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Rose500),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = state.errorMessage ?: "",
                        color = Rose500,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Active Number Tiles (Cards)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = when {
                            state.selectedTile1 == null -> "SELECT FIRST NUMBER"
                            state.selectedOp == null -> "SELECT AN OPERATOR"
                            else -> "SELECT SECOND NUMBER"
                        },
                        color = Amber500,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Grid or Row of number tiles
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        state.activeTiles.forEach { tile ->
                            val isSelected = state.selectedTile1?.id == tile.id
                            LargeNumberTileCard(
                                tile = tile,
                                isSelected = isSelected,
                                onClick = { onTileClick(tile) }
                            )
                        }
                    }

                    // Steps trail
                    if (state.stepHistory.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Slate800.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, Slate700)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("STEPS SO FAR:", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                state.stepHistory.forEach { step ->
                                    Text(
                                        text = "${step.tile1.value} ${step.operator.symbol} ${step.tile2.value} = ${step.resultTile.value}",
                                        color = Color.LightGray,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Operator Buttons Bar at bottom
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                color = Slate800,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Slate700)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MathOp.values().forEach { op ->
                        val isOpSelected = state.selectedOp == op
                        OperatorButton(
                            op = op,
                            isSelected = isOpSelected,
                            enabled = state.selectedTile1 != null,
                            onClick = { onOpClick(op) }
                        )
                    }
                }
            }
        }

        if (state.isSolved) {
            val stars = if (state.hintRevealed) 2 else if (state.elapsedSeconds < 45) 3 else 2
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
fun Target24HeaderCard(
    target: Int,
    elapsedSeconds: Int,
    canUndo: Boolean,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    onHint: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = Slate800,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, Slate700)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Target Callout
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "TARGET",
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = Emerald600,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$target",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }
            }

            // Timer
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Timer, contentDescription = "Timer", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "%02d:%02d".format(elapsedSeconds / 60, elapsedSeconds % 60),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Controls
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onHint,
                    modifier = Modifier.size(34.dp).testTag("target24_hint_button")
                ) {
                    Icon(Icons.Filled.Lightbulb, contentDescription = "Hint", tint = Amber500, modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = onUndo,
                    enabled = canUndo,
                    modifier = Modifier.size(34.dp).testTag("target24_undo_button")
                ) {
                    Icon(Icons.Filled.Undo, contentDescription = "Undo", tint = if (canUndo) Color.White else Slate700, modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = onReset,
                    modifier = Modifier.size(34.dp).testTag("target24_reset_button")
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Reset", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun LargeNumberTileCard(
    tile: NumberTile,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(width = 72.dp, height = 80.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("target_tile_${tile.value}"),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Indigo600 else Slate800,
        border = BorderStroke(
            width = if (isSelected) 2.5.dp else 1.5.dp,
            color = if (isSelected) Color.White else Slate700
        ),
        shadowElevation = if (isSelected) 8.dp else 2.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${tile.value}",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp
            )
        }
    }
}

@Composable
fun OperatorButton(
    op: MathOp,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .clickable(enabled = enabled) { onClick() }
            .testTag("op_button_${op.name}"),
        shape = CircleShape,
        color = when {
            isSelected -> Amber500
            enabled -> Slate700
            else -> Slate700.copy(alpha = 0.4f)
        },
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color.White else Slate700
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = op.symbol,
                color = when {
                    isSelected -> Slate900
                    enabled -> Color.White
                    else -> Color.Gray
                },
                fontWeight = FontWeight.Black,
                fontSize = 26.sp
            )
        }
    }
}
