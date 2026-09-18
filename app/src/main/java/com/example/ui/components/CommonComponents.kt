package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GameMode

val Slate900 = Color(0xFF0F172A)
val Slate800 = Color(0xFF1E293B)
val Slate700 = Color(0xFF334155)
val Indigo600 = Color(0xFF4F46E5)
val Indigo500 = Color(0xFF6366F1)
val Emerald500 = Color(0xFF10B981)
val Emerald600 = Color(0xFF059669)
val Amber500 = Color(0xFFF59E0B)
val Rose500 = Color(0xFFF43F5E)

@Composable
fun GameHeaderBar(
    title: String,
    levelNumber: Int?,
    starsCount: Int,
    streakCount: Int,
    soundEnabled: Boolean,
    onSoundToggle: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenLevels: () -> Unit,
    onOpenHelp: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        color = Slate800.copy(alpha = 0.85f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Slate700)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Level / Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (levelNumber != null) {
                    Surface(
                        modifier = Modifier
                            .clickable { onOpenLevels() }
                            .testTag("level_badge_button"),
                        color = Indigo600,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Lvl $levelNumber",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Level",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            }

            // Stats Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Streak
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate700.copy(alpha = 0.5f))
                        .padding(horizontal = 7.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = Amber500,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "$streakCount",
                        color = Amber500,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Stars
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate700.copy(alpha = 0.5f))
                        .padding(horizontal = 7.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Stars",
                        tint = Amber500,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "$starsCount",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Actions
                IconButton(
                    onClick = onSoundToggle,
                    modifier = Modifier.size(34.dp).testTag("sound_toggle_button")
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                        contentDescription = "Toggle Sound",
                        tint = if (soundEnabled) Indigo500 else Color.Gray,
                        modifier = Modifier.size(19.dp)
                    )
                }

                IconButton(
                    onClick = onOpenStats,
                    modifier = Modifier.size(34.dp).testTag("stats_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Leaderboard,
                        contentDescription = "Stats",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(19.dp)
                    )
                }

                IconButton(
                    onClick = onOpenHelp,
                    modifier = Modifier.size(34.dp).testTag("help_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.HelpOutline,
                        contentDescription = "How to play",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModeNavigationBar(
    currentMode: GameMode,
    onSelectMode: (GameMode) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        color = Slate800,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Slate700)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GameMode.values().forEach { mode ->
                val isSelected = mode == currentMode
                val icon = when (mode) {
                    GameMode.CROSSGRID -> Icons.Filled.GridOn
                    GameMode.TARGET24 -> Icons.Filled.Calculate
                    GameMode.MATH_FLOW -> Icons.Filled.Route
                    GameMode.DAILY_BLITZ -> Icons.Filled.FlashOn
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) Brush.horizontalGradient(
                                listOf(Indigo600, Indigo500)
                            ) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                        )
                        .clickable { onSelectMode(mode) }
                        .padding(vertical = 8.dp)
                        .testTag("mode_tab_${mode.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = mode.displayName,
                            tint = if (isSelected) Color.White else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = mode.displayName,
                            color = if (isSelected) Color.White else Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VictoryCelebrationDialog(
    stars: Int,
    timeSeconds: Int,
    levelNumber: Int,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("victory_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Slate900,
            border = BorderStroke(2.dp, Indigo500)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PUZZLE SOLVED!",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = Emerald500,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stars display
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..3) {
                        val earned = i <= stars
                        Icon(
                            imageVector = if (earned) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Star $i",
                            tint = if (earned) Amber500 else Slate700,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Slate800,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Time", color = Color.Gray, fontSize = 12.sp)
                            Text("${timeSeconds}s", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Level", color = Color.Gray, fontSize = 12.sp)
                            Text("#$levelNumber", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onReplay,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("replay_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Slate700)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Replay", tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Replay", color = Color.White)
                    }

                    Button(
                        onClick = onNextLevel,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("next_level_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Next", fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Filled.ArrowForward, contentDescription = "Next Level", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
