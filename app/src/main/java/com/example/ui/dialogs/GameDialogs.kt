package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.LevelRecordEntity
import com.example.data.PlayerStatsEntity
import com.example.model.GameMode
import com.example.ui.components.*

@Composable
fun LevelSelectDialog(
    gameMode: GameMode,
    currentLevel: Int,
    records: List<LevelRecordEntity>,
    onSelectLevel: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val maxLevels = when (gameMode) {
        GameMode.CROSSGRID -> 9
        GameMode.TARGET24 -> 12
        GameMode.MATH_FLOW -> 4
        GameMode.DAILY_BLITZ -> 1
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Slate900,
            border = BorderStroke(1.5.dp, Slate700),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("level_select_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SELECT LEVEL",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items((1..maxLevels).toList()) { lvl ->
                        val recordId = when (gameMode) {
                            GameMode.CROSSGRID -> "CROSSGRID_$lvl"
                            GameMode.TARGET24 -> "TARGET24_$lvl"
                            GameMode.MATH_FLOW -> "FLOW_$lvl"
                            else -> "BLITZ_$lvl"
                        }
                        val rec = records.firstOrNull { it.id == recordId }
                        val isCurrent = lvl == currentLevel
                        val stars = rec?.stars ?: 0

                        Surface(
                            modifier = Modifier
                                .height(80.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    onSelectLevel(lvl)
                                    onDismiss()
                                }
                                .testTag("level_item_$lvl"),
                            shape = RoundedCornerShape(14.dp),
                            color = if (isCurrent) Indigo600 else Slate800,
                            border = BorderStroke(1.5.dp, if (isCurrent) Color.White else Slate700)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "$lvl",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    for (i in 1..3) {
                                        Icon(
                                            imageVector = if (i <= stars) Icons.Filled.Star else Icons.Outlined.Star,
                                            contentDescription = null,
                                            tint = if (i <= stars) Amber500 else Slate700,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerStatsDialog(
    stats: PlayerStatsEntity,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Slate900,
            border = BorderStroke(1.5.dp, Slate700),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("stats_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Banner Image
                Image(
                    painter = painterResource(id = R.drawable.hero_math_puzzle),
                    contentDescription = "Math Puzzle Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PLAYER STATS",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Cards Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Solved",
                        value = "${stats.puzzlesSolved}",
                        icon = Icons.Filled.CheckCircle,
                        iconTint = Emerald500
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Streak",
                        value = "${stats.currentStreak}",
                        icon = Icons.Filled.LocalFireDepartment,
                        iconTint = Amber500
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Stars",
                        value = "${stats.totalStars}",
                        icon = Icons.Filled.Star,
                        iconTint = Amber500
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Blitz Best",
                        value = "${stats.blitzHighScore}",
                        icon = Icons.Filled.FlashOn,
                        iconTint = Indigo500
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Settings toggles
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Slate800,
                    border = BorderStroke(1.dp, Slate700),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Sound Effects", color = Color.White, fontSize = 14.sp)
                            Switch(
                                checked = stats.soundEnabled,
                                onCheckedChange = { onToggleSound() },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Indigo600)
                            )
                        }

                        Divider(color = Slate700.copy(alpha = 0.5f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Haptic Feedback", color = Color.White, fontSize = 14.sp)
                            Switch(
                                checked = stats.hapticsEnabled,
                                onCheckedChange = { onToggleHaptics() },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Indigo600)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Slate800,
        border = BorderStroke(1.dp, Slate700)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text(title, color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun HowToPlayDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Slate900,
            border = BorderStroke(1.5.dp, Slate700),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("how_to_play_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HOW TO PLAY",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                RuleExplanationItem(
                    title = "Crossgrid",
                    description = "Place numbers into the empty slots so every horizontal and vertical math equation matches its target number at the edge.",
                    badgeColor = Indigo600
                )

                Spacer(modifier = Modifier.height(10.dp))

                RuleExplanationItem(
                    title = "Target 24",
                    description = "Combine all 4 number cards using +, -, ×, ÷. Select a card, an operation, and a second card to merge them until you hit 24!",
                    badgeColor = Emerald600
                )

                Spacer(modifier = Modifier.height(10.dp))

                RuleExplanationItem(
                    title = "Math Flow",
                    description = "Step along adjacent grid cells from START to GOAL, alternating between numbers and operators to hit the exact target score.",
                    badgeColor = Amber500
                )

                Spacer(modifier = Modifier.height(10.dp))

                RuleExplanationItem(
                    title = "Daily Blitz",
                    description = "A rapid 60-second time rush! Solve arithmetic equations as fast as possible to build combo multipliers and set new high scores.",
                    badgeColor = Rose500
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("GOT IT!", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun RuleExplanationItem(
    title: String,
    description: String,
    badgeColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Slate800,
        border = BorderStroke(1.dp, Slate700),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Surface(
                color = badgeColor,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                color = Color.LightGray,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}
