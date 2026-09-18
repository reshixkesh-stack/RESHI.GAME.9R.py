package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundHapticsManager
import com.example.data.GameDatabase
import com.example.data.GameRepository
import com.example.data.LevelRecordEntity
import com.example.data.PlayerStatsEntity
import com.example.generator.CrossgridData
import com.example.generator.DailyBlitzGenerator
import com.example.generator.MathFlowData
import com.example.generator.Target24Data
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CrossgridUIState(
    val puzzle: CrossgridPuzzle,
    val currentCells: Map<Pair<Int, Int>, NumberCell>,
    val selectedCell: Pair<Int, Int>? = null,
    val bankNumbers: List<Int> = emptyList(),
    val isSolved: Boolean = false,
    val elapsedSeconds: Int = 0,
    val movesCount: Int = 0,
    val hintsUsed: Int = 0,
    val rowValidation: Map<Int, Pair<EquationStatus, Int?>> = emptyMap(),
    val colValidation: Map<Int, Pair<EquationStatus, Int?>> = emptyMap()
)

data class Target24UIState(
    val puzzle: Target24Puzzle,
    val activeTiles: List<NumberTile>,
    val selectedTile1: NumberTile? = null,
    val selectedOp: MathOp? = null,
    val stepHistory: List<Target24Step> = emptyList(),
    val isSolved: Boolean = false,
    val elapsedSeconds: Int = 0,
    val hintRevealed: Boolean = false,
    val errorMessage: String? = null
)

data class MathFlowUIState(
    val puzzle: MathFlowPuzzle,
    val currentPath: List<Pair<Int, Int>> = emptyList(),
    val currentScore: Int? = null,
    val currentExpression: String = "",
    val isSolved: Boolean = false,
    val elapsedSeconds: Int = 0
)

data class DailyBlitzUIState(
    val isRunning: Boolean = false,
    val isGameOver: Boolean = false,
    val timeLeftSeconds: Int = 60,
    val score: Int = 0,
    val streak: Int = 0,
    val bestStreakInRun: Int = 0,
    val totalAnswered: Int = 0,
    val correctCount: Int = 0,
    val currentQuestion: BlitzQuestion? = null,
    val selectedOptionIndex: Int? = null,
    val isOptionCorrect: Boolean? = null
)

class MainGameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    val soundManager = SoundHapticsManager(application)

    init {
        val db = GameDatabase.getInstance(application)
        repository = GameRepository(db.gameDao())
    }

    val playerStats: StateFlow<PlayerStatsEntity> = repository.playerStats
        .map { it ?: PlayerStatsEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerStatsEntity())

    val allRecords: StateFlow<List<LevelRecordEntity>> = repository.allRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Tab/Mode
    private val _activeMode = MutableStateFlow(GameMode.CROSSGRID)
    val activeMode: StateFlow<GameMode> = _activeMode.asStateFlow()

    // Dialog flags
    val showLevelSelect = MutableStateFlow(false)
    val showStatsDialog = MutableStateFlow(false)
    val showHowToPlay = MutableStateFlow(false)

    // Crossgrid State
    private var crossgridTimerJob: Job? = null
    private val _crossgridState = MutableStateFlow(createInitialCrossgridState(1))
    val crossgridState: StateFlow<CrossgridUIState> = _crossgridState.asStateFlow()

    // Target 24 State
    private var target24TimerJob: Job? = null
    private val _target24State = MutableStateFlow(createInitialTarget24State(1))
    val target24State: StateFlow<Target24UIState> = _target24State.asStateFlow()

    // Math Flow State
    private var mathFlowTimerJob: Job? = null
    private val _mathFlowState = MutableStateFlow(createInitialMathFlowState(1))
    val mathFlowState: StateFlow<MathFlowUIState> = _mathFlowState.asStateFlow()

    // Daily Blitz State
    private var blitzTimerJob: Job? = null
    private val _blitzState = MutableStateFlow(DailyBlitzUIState())
    val blitzState: StateFlow<DailyBlitzUIState> = _blitzState.asStateFlow()

    init {
        startCrossgridTimer()
    }

    fun setMode(mode: GameMode) {
        if (_activeMode.value == mode) return
        _activeMode.value = mode
        soundManager.playTileTap(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
        when (mode) {
            GameMode.CROSSGRID -> startCrossgridTimer()
            GameMode.TARGET24 -> startTarget24Timer()
            GameMode.MATH_FLOW -> startMathFlowTimer()
            GameMode.DAILY_BLITZ -> { /* Wait for user to tap start */ }
        }
    }

    fun toggleSound() {
        val current = playerStats.value
        val updated = current.copy(soundEnabled = !current.soundEnabled)
        viewModelScope.launch { repository.saveStats(updated) }
    }

    fun toggleHaptics() {
        val current = playerStats.value
        val updated = current.copy(hapticsEnabled = !current.hapticsEnabled)
        viewModelScope.launch { repository.saveStats(updated) }
    }

    // ==========================================
    // CROSSGRID LOGIC
    // ==========================================

    private fun createInitialCrossgridState(levelNumber: Int): CrossgridUIState {
        val puzzle = CrossgridData.getLevel(levelNumber)
        val cells = puzzle.cells.toMutableMap()
        val bank = puzzle.availableBankNumbers.toMutableList()
        val rowVal = puzzle.rowEquations.associate { it.index to puzzle.evaluateEquation(it, cells) }
        val colVal = puzzle.colEquations.associate { it.index to puzzle.evaluateEquation(it, cells) }

        return CrossgridUIState(
            puzzle = puzzle,
            currentCells = cells,
            selectedCell = null,
            bankNumbers = bank,
            isSolved = false,
            elapsedSeconds = 0,
            movesCount = 0,
            hintsUsed = 0,
            rowValidation = rowVal,
            colValidation = colVal
        )
    }

    fun startCrossgridTimer() {
        crossgridTimerJob?.cancel()
        crossgridTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_crossgridState.value.isSolved) {
                    _crossgridState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                }
            }
        }
    }

    fun selectCrossgridCell(row: Int, col: Int) {
        val cell = _crossgridState.value.currentCells[row to col] ?: return
        if (cell.isPreFilled) return

        soundManager.playTileTap(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
        _crossgridState.update {
            if (it.selectedCell == (row to col)) {
                it.copy(selectedCell = null)
            } else {
                it.copy(selectedCell = row to col)
            }
        }
    }

    fun placeNumberInCrossgrid(number: Int) {
        val state = _crossgridState.value
        val selected = state.selectedCell ?: return
        val cell = state.currentCells[selected] ?: return
        if (cell.isPreFilled) return

        soundManager.playTileTap(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)

        val newBank = state.bankNumbers.toMutableList()
        // If cell had a previous value, put it back into the bank
        val oldVal = cell.currentValue
        if (oldVal != null) {
            newBank.add(oldVal)
        }
        // Remove one instance of number from bank
        newBank.remove(number)

        val updatedCells = state.currentCells.toMutableMap()
        updatedCells[selected] = cell.copy(currentValue = number)

        val rowVal = state.puzzle.rowEquations.associate { it.index to state.puzzle.evaluateEquation(it, updatedCells) }
        val colVal = state.puzzle.colEquations.associate { it.index to state.puzzle.evaluateEquation(it, updatedCells) }
        val solved = state.puzzle.isAllSolved(updatedCells)

        _crossgridState.update {
            it.copy(
                currentCells = updatedCells,
                bankNumbers = newBank.sorted(),
                selectedCell = null,
                movesCount = it.movesCount + 1,
                rowValidation = rowVal,
                colValidation = colVal,
                isSolved = solved
            )
        }

        if (solved) {
            handleCrossgridVictory()
        } else {
            // Check if this placement just solved any row or col
            val justCompletedRow = rowVal.values.any { it.first == EquationStatus.CORRECT }
            if (justCompletedRow) {
                soundManager.playEquationCorrect(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
            }
        }
    }

    fun clearSelectedCrossgridCell() {
        val state = _crossgridState.value
        val selected = state.selectedCell ?: return
        val cell = state.currentCells[selected] ?: return
        if (cell.isPreFilled || cell.currentValue == null) return

        val oldVal = cell.currentValue
        val newBank = state.bankNumbers.toMutableList()
        newBank.add(oldVal)

        val updatedCells = state.currentCells.toMutableMap()
        updatedCells[selected] = cell.copy(currentValue = null)

        val rowVal = state.puzzle.rowEquations.associate { it.index to state.puzzle.evaluateEquation(it, updatedCells) }
        val colVal = state.puzzle.colEquations.associate { it.index to state.puzzle.evaluateEquation(it, updatedCells) }

        _crossgridState.update {
            it.copy(
                currentCells = updatedCells,
                bankNumbers = newBank.sorted(),
                rowValidation = rowVal,
                colValidation = colVal
            )
        }
    }

    fun useCrossgridHint() {
        val state = _crossgridState.value
        if (state.isSolved) return

        // Find first unfilled or incorrectly filled cell
        val target = state.currentCells.values.firstOrNull {
            !it.isPreFilled && it.currentValue != it.correctValue
        } ?: return

        soundManager.playOperatorSelect(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)

        val newBank = state.bankNumbers.toMutableList()
        val oldVal = target.currentValue
        if (oldVal != null) {
            newBank.add(oldVal)
        }
        newBank.remove(target.correctValue)

        val updatedCells = state.currentCells.toMutableMap()
        updatedCells[target.row to target.col] = target.copy(currentValue = target.correctValue)

        val rowVal = state.puzzle.rowEquations.associate { it.index to state.puzzle.evaluateEquation(it, updatedCells) }
        val colVal = state.puzzle.colEquations.associate { it.index to state.puzzle.evaluateEquation(it, updatedCells) }
        val solved = state.puzzle.isAllSolved(updatedCells)

        _crossgridState.update {
            it.copy(
                currentCells = updatedCells,
                bankNumbers = newBank.sorted(),
                hintsUsed = it.hintsUsed + 1,
                rowValidation = rowVal,
                colValidation = colVal,
                isSolved = solved
            )
        }

        if (solved) {
            handleCrossgridVictory()
        }
    }

    fun resetCrossgrid() {
        val currentLevel = _crossgridState.value.puzzle.levelNumber
        _crossgridState.value = createInitialCrossgridState(currentLevel)
        startCrossgridTimer()
    }

    fun loadCrossgridLevel(level: Int) {
        _crossgridState.value = createInitialCrossgridState(level)
        startCrossgridTimer()
    }

    fun nextCrossgridLevel() {
        val next = _crossgridState.value.puzzle.levelNumber + 1
        loadCrossgridLevel(next)
    }

    private fun handleCrossgridVictory() {
        val state = _crossgridState.value
        soundManager.playVictory(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
        val stars = calculateStars(state.elapsedSeconds, state.hintsUsed)

        viewModelScope.launch {
            val record = LevelRecordEntity(
                id = "CROSSGRID_${state.puzzle.levelNumber}",
                gameMode = GameMode.CROSSGRID.name,
                difficulty = state.puzzle.difficulty.name,
                levelIndex = state.puzzle.levelNumber,
                stars = stars,
                bestTimeSeconds = state.elapsedSeconds
            )
            repository.saveRecord(record)

            val current = playerStats.value
            val newStats = current.copy(
                puzzlesSolved = current.puzzlesSolved + 1,
                currentStreak = current.currentStreak + 1,
                bestStreak = maxOf(current.bestStreak, current.currentStreak + 1),
                totalStars = current.totalStars + stars
            )
            repository.saveStats(newStats)
        }
    }

    // ==========================================
    // TARGET 24 LOGIC
    // ==========================================

    private fun createInitialTarget24State(levelNumber: Int): Target24UIState {
        val puzzle = Target24Data.getLevel(levelNumber)
        val tiles = puzzle.initialNumbers.mapIndexed { idx, v ->
            NumberTile(id = "tile_${idx}_${System.currentTimeMillis()}", value = v)
        }
        return Target24UIState(
            puzzle = puzzle,
            activeTiles = tiles,
            selectedTile1 = null,
            selectedOp = null,
            stepHistory = emptyList(),
            isSolved = false,
            elapsedSeconds = 0,
            hintRevealed = false
        )
    }

    fun startTarget24Timer() {
        target24TimerJob?.cancel()
        target24TimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_target24State.value.isSolved) {
                    _target24State.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                }
            }
        }
    }

    fun selectTarget24Tile(tile: NumberTile) {
        val state = _target24State.value
        if (state.isSolved) return

        soundManager.playTileTap(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)

        if (state.selectedTile1 == null) {
            // First tile selected
            _target24State.update { it.copy(selectedTile1 = tile, errorMessage = null) }
        } else if (state.selectedTile1.id == tile.id) {
            // Deselect
            _target24State.update { it.copy(selectedTile1 = null, selectedOp = null) }
        } else if (state.selectedOp == null) {
            // Swapping selection to another tile
            _target24State.update { it.copy(selectedTile1 = tile) }
        } else {
            // Second tile selected! Perform operation
            performTarget24Operation(state.selectedTile1, state.selectedOp, tile)
        }
    }

    fun selectTarget24Op(op: MathOp) {
        val state = _target24State.value
        if (state.isSolved) return
        soundManager.playOperatorSelect(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
        _target24State.update { it.copy(selectedOp = op, errorMessage = null) }
    }

    private fun performTarget24Operation(t1: NumberTile, op: MathOp, t2: NumberTile) {
        // Validate integer division
        if (op == MathOp.DIV) {
            if (t2.value == 0) {
                soundManager.playError(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
                _target24State.update { it.copy(errorMessage = "Cannot divide by 0") }
                return
            }
            if (t1.value % t2.value != 0) {
                soundManager.playError(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
                _target24State.update { it.copy(errorMessage = "${t1.value} ÷ ${t2.value} is not a whole number") }
                return
            }
        }

        val resultVal = op.apply(t1.value, t2.value)
        val resultTile = NumberTile(
            id = "tile_${System.currentTimeMillis()}_${resultVal}",
            value = resultVal,
            expression = "(${t1.expression} ${op.symbol} ${t2.expression})"
        )

        val state = _target24State.value
        val remaining = state.activeTiles.filter { it.id != t1.id && it.id != t2.id }
        val newTiles = remaining + listOf(resultTile)

        val step = Target24Step(
            tile1 = t1,
            operator = op,
            tile2 = t2,
            resultTile = resultTile,
            remainingTiles = state.activeTiles
        )

        val solved = newTiles.size == 1 && newTiles[0].value == state.puzzle.target

        _target24State.update {
            it.copy(
                activeTiles = newTiles,
                selectedTile1 = null,
                selectedOp = null,
                stepHistory = it.stepHistory + listOf(step),
                isSolved = solved,
                errorMessage = null
            )
        }

        if (solved) {
            handleTarget24Victory()
        } else {
            soundManager.playEquationCorrect(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
        }
    }

    fun undoTarget24Step() {
        val state = _target24State.value
        if (state.stepHistory.isEmpty()) return

        soundManager.playOperatorSelect(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
        val lastStep = state.stepHistory.last()
        val newHistory = state.stepHistory.dropLast(1)

        _target24State.update {
            it.copy(
                activeTiles = lastStep.remainingTiles,
                selectedTile1 = null,
                selectedOp = null,
                stepHistory = newHistory,
                isSolved = false,
                errorMessage = null
            )
        }
    }

    fun revealTarget24Hint() {
        soundManager.playOperatorSelect(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
        _target24State.update { it.copy(hintRevealed = true) }
    }

    fun resetTarget24() {
        val level = _target24State.value.puzzle.levelNumber
        _target24State.value = createInitialTarget24State(level)
        startTarget24Timer()
    }

    fun loadTarget24Level(level: Int) {
        _target24State.value = createInitialTarget24State(level)
        startTarget24Timer()
    }

    fun nextTarget24Level() {
        val next = _target24State.value.puzzle.levelNumber + 1
        loadTarget24Level(next)
    }

    private fun handleTarget24Victory() {
        val state = _target24State.value
        soundManager.playVictory(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
        val stars = if (state.hintRevealed) 2 else if (state.elapsedSeconds < 45) 3 else 2

        viewModelScope.launch {
            val record = LevelRecordEntity(
                id = "TARGET24_${state.puzzle.levelNumber}",
                gameMode = GameMode.TARGET24.name,
                difficulty = "STANDARD",
                levelIndex = state.puzzle.levelNumber,
                stars = stars,
                bestTimeSeconds = state.elapsedSeconds
            )
            repository.saveRecord(record)

            val current = playerStats.value
            val newStats = current.copy(
                puzzlesSolved = current.puzzlesSolved + 1,
                currentStreak = current.currentStreak + 1,
                bestStreak = maxOf(current.bestStreak, current.currentStreak + 1),
                totalStars = current.totalStars + stars
            )
            repository.saveStats(newStats)
        }
    }

    // ==========================================
    // MATH FLOW LOGIC
    // ==========================================

    private fun createInitialMathFlowState(levelNumber: Int): MathFlowUIState {
        val puzzle = MathFlowData.getLevel(levelNumber)
        val startCell = puzzle.grid[puzzle.startCoord.first][puzzle.startCoord.second] as FlowCell.Number
        return MathFlowUIState(
            puzzle = puzzle,
            currentPath = listOf(puzzle.startCoord),
            currentScore = startCell.value,
            currentExpression = "${startCell.value}",
            isSolved = false,
            elapsedSeconds = 0
        )
    }

    fun startMathFlowTimer() {
        mathFlowTimerJob?.cancel()
        mathFlowTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_mathFlowState.value.isSolved) {
                    _mathFlowState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                }
            }
        }
    }

    fun tapMathFlowCell(row: Int, col: Int) {
        val state = _mathFlowState.value
        if (state.isSolved) return

        val coord = row to col
        val path = state.currentPath

        // If clicking last cell, undo
        if (path.size > 1 && path.last() == coord) {
            undoMathFlow()
            return
        }

        // Must be adjacent to last cell in path
        val last = path.last()
        val isAdjacent = (Math.abs(last.first - row) + Math.abs(last.second - col)) == 1
        if (!isAdjacent) return
        if (coord in path) return // No cycles

        val targetCell = state.puzzle.grid[row][col]

        // Ensure alternating Number and Operator
        val lastCell = state.puzzle.grid[last.first][last.second]
        if (lastCell is FlowCell.Number && targetCell !is FlowCell.Operator) return
        if (lastCell is FlowCell.Operator && targetCell !is FlowCell.Number) return

        soundManager.playTileTap(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)

        val newPath = path + listOf(coord)
        val (score, expr) = evaluateFlowPath(state.puzzle, newPath)

        val solved = coord == state.puzzle.goalCoord && score == state.puzzle.targetScore

        _mathFlowState.update {
            it.copy(
                currentPath = newPath,
                currentScore = score,
                currentExpression = expr,
                isSolved = solved
            )
        }

        if (solved) {
            soundManager.playVictory(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
            viewModelScope.launch {
                val record = LevelRecordEntity(
                    id = "FLOW_${state.puzzle.levelNumber}",
                    gameMode = GameMode.MATH_FLOW.name,
                    difficulty = "STANDARD",
                    levelIndex = state.puzzle.levelNumber,
                    stars = 3,
                    bestTimeSeconds = state.elapsedSeconds
                )
                repository.saveRecord(record)
            }
        }
    }

    fun undoMathFlow() {
        val state = _mathFlowState.value
        if (state.currentPath.size <= 1) return
        soundManager.playOperatorSelect(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
        val newPath = state.currentPath.dropLast(1)
        val (score, expr) = evaluateFlowPath(state.puzzle, newPath)
        _mathFlowState.update {
            it.copy(
                currentPath = newPath,
                currentScore = score,
                currentExpression = expr,
                isSolved = false
            )
        }
    }

    fun resetMathFlow() {
        val level = _mathFlowState.value.puzzle.levelNumber
        _mathFlowState.value = createInitialMathFlowState(level)
        startMathFlowTimer()
    }

    fun loadMathFlowLevel(level: Int) {
        _mathFlowState.value = createInitialMathFlowState(level)
        startMathFlowTimer()
    }

    fun nextMathFlowLevel() {
        val next = _mathFlowState.value.puzzle.levelNumber + 1
        _mathFlowState.value = createInitialMathFlowState(next)
        startMathFlowTimer()
    }

    private fun evaluateFlowPath(puzzle: MathFlowPuzzle, path: List<Pair<Int, Int>>): Pair<Int?, String> {
        val startCell = puzzle.grid[path[0].first][path[0].second] as FlowCell.Number
        var current = startCell.value
        val sb = StringBuilder("${startCell.value}")

        var i = 1
        while (i < path.size) {
            val opCell = puzzle.grid[path[i].first][path[i].second] as FlowCell.Operator
            sb.append(" ${opCell.op.symbol} ")
            if (i + 1 < path.size) {
                val numCell = puzzle.grid[path[i + 1].first][path[i + 1].second] as FlowCell.Number
                sb.append(numCell.value)
                current = opCell.op.apply(current, numCell.value)
                i += 2
            } else {
                return Pair(null, sb.toString())
            }
        }
        return Pair(current, sb.toString())
    }

    // ==========================================
    // DAILY BLITZ LOGIC
    // ==========================================

    fun startDailyBlitz() {
        soundManager.playVictory(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
        val firstQ = DailyBlitzGenerator.generateQuestion(1)
        _blitzState.value = DailyBlitzUIState(
            isRunning = true,
            isGameOver = false,
            timeLeftSeconds = 60,
            score = 0,
            streak = 0,
            bestStreakInRun = 0,
            totalAnswered = 0,
            correctCount = 0,
            currentQuestion = firstQ
        )

        blitzTimerJob?.cancel()
        blitzTimerJob = viewModelScope.launch {
            while (_blitzState.value.timeLeftSeconds > 0 && _blitzState.value.isRunning) {
                delay(1000)
                val remaining = _blitzState.value.timeLeftSeconds - 1
                if (remaining <= 0) {
                    endDailyBlitz()
                } else {
                    _blitzState.update { it.copy(timeLeftSeconds = remaining) }
                }
            }
        }
    }

    fun answerBlitz(optionIndex: Int) {
        val state = _blitzState.value
        if (!state.isRunning || state.isGameOver || state.selectedOptionIndex != null) return

        val q = state.currentQuestion ?: return
        val isCorrect = optionIndex == q.correctIndex

        if (isCorrect) {
            soundManager.playEquationCorrect(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
        } else {
            soundManager.playError(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
        }

        val newStreak = if (isCorrect) state.streak + 1 else 0
        val multiplier = 1 + (newStreak / 3)
        val points = if (isCorrect) 100 * multiplier else 0

        _blitzState.update {
            it.copy(
                selectedOptionIndex = optionIndex,
                isOptionCorrect = isCorrect,
                score = it.score + points,
                streak = newStreak,
                bestStreakInRun = maxOf(it.bestStreakInRun, newStreak),
                totalAnswered = it.totalAnswered + 1,
                correctCount = if (isCorrect) it.correctCount + 1 else it.correctCount
            )
        }

        // Brief delay before showing next question
        viewModelScope.launch {
            delay(400)
            if (_blitzState.value.isRunning && !_blitzState.value.isGameOver) {
                val nextQ = DailyBlitzGenerator.generateQuestion(_blitzState.value.totalAnswered)
                _blitzState.update {
                    it.copy(
                        currentQuestion = nextQ,
                        selectedOptionIndex = null,
                        isOptionCorrect = null
                    )
                }
            }
        }
    }

    private fun endDailyBlitz() {
        blitzTimerJob?.cancel()
        soundManager.playVictory(playerStats.value.soundEnabled, playerStats.value.hapticsEnabled)
        _blitzState.update { it.copy(isRunning = false, isGameOver = true, timeLeftSeconds = 0) }

        val finalScore = _blitzState.value.score
        viewModelScope.launch {
            val current = playerStats.value
            val newStats = current.copy(
                blitzHighScore = maxOf(current.blitzHighScore, finalScore),
                puzzlesSolved = current.puzzlesSolved + _blitzState.value.correctCount
            )
            repository.saveStats(newStats)
        }
    }

    private fun calculateStars(elapsedSeconds: Int, hintsUsed: Int): Int {
        if (hintsUsed > 0) return 2
        return if (elapsedSeconds < 60) 3 else 2
    }
}
