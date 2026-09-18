package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.DailyBlitzUIState
import com.example.ui.components.*

@Composable
fun DailyBlitzScreen(
    state: DailyBlitzUIState,
    highScore: Int,
    onStartBlitz: () -> Unit,
    onAnswer: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (!state.isRunning && !state.isGameOver) {
            // Idle Start Screen
            DailyBlitzStartView(highScore = highScore, onStart = onStartBlitz)
        } else {
            // Active Game Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Timer & Score Status Header
                BlitzHeaderStatus(
                    timeLeft = state.timeLeftSeconds,
                    score = state.score,
                    streak = state.streak
                )

                // The Question Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val q = state.currentQuestion
                    if (q != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            color = Slate800,
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(2.dp, Indigo500)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "SOLVE FOR [ ? ]",
                                    color = Amber500,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = q.prompt,
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // 4 Answer Option Buttons (2x2 Grid)
                val options = state.currentQuestion?.options ?: emptyList()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (row in 0..1) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            for (col in 0..1) {
                                val idx = row * 2 + col
                                if (idx < options.size) {
                                    val optValue = options[idx]
                                    val isSelected = state.selectedOptionIndex == idx
                                    val isCorrect = state.isOptionCorrect

                                    BlitzOptionCard(
                                        modifier = Modifier.weight(1f),
                                        value = optValue,
                                        isSelected = isSelected,
                                        isCorrect = isCorrect,
                                        onClick = { onAnswer(idx) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Game Over Dialog
        if (state.isGameOver) {
            DailyBlitzGameOverDialog(
                score = state.score,
                highScore = highScore,
                correctCount = state.correctCount,
                totalAnswered = state.totalAnswered,
                bestStreak = state.bestStreakInRun,
                onPlayAgain = onStartBlitz
            )
        }
    }
}

@Composable
fun DailyBlitzStartView(
    highScore: Int,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = Amber500.copy(alpha = 0.15f),
            border = BorderStroke(2.dp, Amber500),
            modifier = Modifier.size(90.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.FlashOn, contentDescription = "Blitz", tint = Amber500, modifier = Modifier.size(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "DAILY BLITZ",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 28.sp,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Solve as many rapid math puzzles as you can in 60 seconds! Build combo streaks to multiply points.",
            color = Color.LightGray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Slate800,
            border = BorderStroke(1.dp, Slate700)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.EmojiEvents, contentDescription = "High Score", tint = Amber500, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("HIGH SCORE: ", color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("$highScore", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(52.dp)
                .testTag("start_blitz_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = "Start", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("START 60s RUSH", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
        }
    }
}

@Composable
fun BlitzHeaderStatus(
    timeLeft: Int,
    score: Int,
    streak: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        color = Slate800,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Slate700)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time Left
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Timer,
                    contentDescription = "Timer",
                    tint = if (timeLeft <= 10) Rose500 else Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${timeLeft}s",
                    color = if (timeLeft <= 10) Rose500 else Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
            }

            // Streak Multiplier
            if (streak > 1) {
                Surface(
                    color = Amber500.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Amber500)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.LocalFireDepartment, contentDescription = "Streak", tint = Amber500, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${1 + (streak / 3)}x COMBO",
                            color = Amber500,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Score
            Column(horizontalAlignment = Alignment.End) {
                Text("SCORE", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("$score", color = Emerald500, fontSize = 20.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun BlitzOptionCard(
    modifier: Modifier = Modifier,
    value: Int,
    isSelected: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            isSelected && isCorrect == true -> Emerald600
            isSelected && isCorrect == false -> Rose500
            else -> Slate800
        },
        label = "opt_bg"
    )

    Surface(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("blitz_option_$value"),
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(1.5.dp, if (isSelected) Color.White else Slate700)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$value",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun DailyBlitzGameOverDialog(
    score: Int,
    highScore: Int,
    correctCount: Int,
    totalAnswered: Int,
    bestStreak: Int,
    onPlayAgain: () -> Unit
) {
    val isNewHigh = score > highScore && score > 0

    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Slate900,
            border = BorderStroke(2.dp, if (isNewHigh) Amber500 else Indigo500),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isNewHigh) "NEW HIGH SCORE!" else "TIME'S UP!",
                    color = if (isNewHigh) Amber500 else Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "$score",
                    color = Emerald500,
                    fontWeight = FontWeight.Black,
                    fontSize = 42.sp
                )

                Text("POINTS", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Slate800,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Accuracy", color = Color.Gray, fontSize = 13.sp)
                            val accuracy = if (totalAnswered > 0) (correctCount * 100 / totalAnswered) else 0
                            Text("$accuracy% ($correctCount / $totalAnswered)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Best Streak", color = Color.Gray, fontSize = 13.sp)
                            Text("$bestStreak", color = Amber500, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("play_again_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Play Again", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PLAY AGAIN", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
