package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GameMode
import com.example.ui.MainGameViewModel
import com.example.ui.theme.DarkBackground
import com.example.ui.components.GameHeaderBar
import com.example.ui.components.ModeNavigationBar
import com.example.ui.dialogs.HowToPlayDialog
import com.example.ui.dialogs.LevelSelectDialog
import com.example.ui.dialogs.PlayerStatsDialog
import com.example.ui.screens.CrossgridScreen
import com.example.ui.screens.DailyBlitzScreen
import com.example.ui.screens.MathFlowScreen
import com.example.ui.screens.Target24Screen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MathPuzzleApp()
            }
        }
    }
}

@Composable
fun MathPuzzleApp(viewModel: MainGameViewModel = viewModel()) {
    val activeMode by viewModel.activeMode.collectAsStateWithLifecycle()
    val stats by viewModel.playerStats.collectAsStateWithLifecycle()
    val records by viewModel.allRecords.collectAsStateWithLifecycle()

    val crossgridState by viewModel.crossgridState.collectAsStateWithLifecycle()
    val target24State by viewModel.target24State.collectAsStateWithLifecycle()
    val mathFlowState by viewModel.mathFlowState.collectAsStateWithLifecycle()
    val blitzState by viewModel.blitzState.collectAsStateWithLifecycle()

    val showLevels by viewModel.showLevelSelect.collectAsStateWithLifecycle()
    val showStats by viewModel.showStatsDialog.collectAsStateWithLifecycle()
    val showHelp by viewModel.showHowToPlay.collectAsStateWithLifecycle()

    val currentLevelNumber = when (activeMode) {
        GameMode.CROSSGRID -> crossgridState.puzzle.levelNumber
        GameMode.TARGET24 -> target24State.puzzle.levelNumber
        GameMode.MATH_FLOW -> mathFlowState.puzzle.levelNumber
        GameMode.DAILY_BLITZ -> null
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        containerColor = DarkBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            GameHeaderBar(
                title = activeMode.displayName,
                levelNumber = currentLevelNumber,
                starsCount = stats.totalStars,
                streakCount = stats.currentStreak,
                soundEnabled = stats.soundEnabled,
                onSoundToggle = { viewModel.toggleSound() },
                onOpenStats = { viewModel.showStatsDialog.value = true },
                onOpenLevels = { viewModel.showLevelSelect.value = true },
                onOpenHelp = { viewModel.showHowToPlay.value = true }
            )
        },
        bottomBar = {
            ModeNavigationBar(
                currentMode = activeMode,
                onSelectMode = { viewModel.setMode(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeMode) {
                GameMode.CROSSGRID -> {
                    CrossgridScreen(
                        state = crossgridState,
                        onCellSelected = { r, c -> viewModel.selectCrossgridCell(r, c) },
                        onNumberPlaced = { num -> viewModel.placeNumberInCrossgrid(num) },
                        onClearSelected = { viewModel.clearSelectedCrossgridCell() },
                        onHint = { viewModel.useCrossgridHint() },
                        onReset = { viewModel.resetCrossgrid() },
                        onNextLevel = { viewModel.nextCrossgridLevel() }
                    )
                }
                GameMode.TARGET24 -> {
                    Target24Screen(
                        state = target24State,
                        onTileClick = { tile -> viewModel.selectTarget24Tile(tile) },
                        onOpClick = { op -> viewModel.selectTarget24Op(op) },
                        onUndo = { viewModel.undoTarget24Step() },
                        onReset = { viewModel.resetTarget24() },
                        onHint = { viewModel.revealTarget24Hint() },
                        onNextLevel = { viewModel.nextTarget24Level() }
                    )
                }
                GameMode.MATH_FLOW -> {
                    MathFlowScreen(
                        state = mathFlowState,
                        onCellTap = { r, c -> viewModel.tapMathFlowCell(r, c) },
                        onUndo = { viewModel.undoMathFlow() },
                        onReset = { viewModel.resetMathFlow() },
                        onNextLevel = { viewModel.nextMathFlowLevel() }
                    )
                }
                GameMode.DAILY_BLITZ -> {
                    DailyBlitzScreen(
                        state = blitzState,
                        highScore = stats.blitzHighScore,
                        onStartBlitz = { viewModel.startDailyBlitz() },
                        onAnswer = { idx -> viewModel.answerBlitz(idx) }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showLevels) {
        LevelSelectDialog(
            gameMode = activeMode,
            currentLevel = currentLevelNumber ?: 1,
            records = records,
            onSelectLevel = { lvl ->
                when (activeMode) {
                    GameMode.CROSSGRID -> viewModel.loadCrossgridLevel(lvl)
                    GameMode.TARGET24 -> viewModel.loadTarget24Level(lvl)
                    GameMode.MATH_FLOW -> viewModel.loadMathFlowLevel(lvl)
                    GameMode.DAILY_BLITZ -> {}
                }
            },
            onDismiss = { viewModel.showLevelSelect.value = false }
        )
    }

    if (showStats) {
        PlayerStatsDialog(
            stats = stats,
            onToggleSound = { viewModel.toggleSound() },
            onToggleHaptics = { viewModel.toggleHaptics() },
            onDismiss = { viewModel.showStatsDialog.value = false }
        )
    }

    if (showHelp) {
        HowToPlayDialog(
            onDismiss = { viewModel.showHowToPlay.value = false }
        )
    }
}
