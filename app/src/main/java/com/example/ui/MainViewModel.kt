package com.example.ui

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.AppDatabase
import com.example.data.CandidateCategory
import com.example.data.DefaultMockData
import com.example.data.ExamRegistry
import com.example.data.ExamRule
import com.example.data.TypingMockEntity
import com.example.data.TypingResultEntity
import com.example.engine.EvaluationResult
import com.example.engine.TypingEvaluator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class AppScreen {
    object Home : AppScreen()
    data class StartMock(val mockId: String) : AppScreen()
    data class Test(val mockId: String) : AppScreen()
    object Result : AppScreen()
    object Admin : AppScreen()
    object History : AppScreen()
    object Learn : AppScreen()
    object Steno : AppScreen()
}

class MainViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    private val database = AppDatabase.getDatabase(application)
    private val dao = database.typingDao()
    val soundManager = SoundManager(application)

    private var tts: TextToSpeech? = null
    val isTtsReady = MutableStateFlow(false)

    // Current navigation screen
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Home)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Navigation back stack
    private val backStack = mutableListOf<AppScreen>()

    // Mocks list from database
    val allMocks: StateFlow<List<TypingMockEntity>> = dao.getAllMocks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // History results from database
    val allResults: StateFlow<List<TypingResultEntity>> = dao.getAllResults()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected mock for starting/testing
    private val _selectedMock = MutableStateFlow<TypingMockEntity?>(null)
    val selectedMock: StateFlow<TypingMockEntity?> = _selectedMock.asStateFlow()

    // Test configuration
    val selectedExam = MutableStateFlow<ExamRule>(ExamRegistry.SSC_CGL)
    val selectedCategory = MutableStateFlow<CandidateCategory>(CandidateCategory.UR)
    val isPracticeMode = MutableStateFlow(false)
    val practiceMinutes = MutableStateFlow(15)

    // Live Test state
    val typedContent = MutableStateFlow("")
    val keystrokeCount = MutableStateFlow(0)
    val backspaceCount = MutableStateFlow(0)
    val secondsRemaining = MutableStateFlow(900)
    val totalTestSeconds = MutableStateFlow(900)
    val isTestRunning = MutableStateFlow(false)
    private var timerJob: Job? = null

    // Evaluation & Result state
    val latestEvaluation = MutableStateFlow<EvaluationResult?>(null)
    val countUnreachedAsMistakes = MutableStateFlow(true)

    // Filter state on Home
    val selectedFilterTab = MutableStateFlow("all") // all, level_1, level_2, level_3, level_4, ntpc, cgl, chsl, dsssb, kvs_nvs, hindi
    val searchQuery = MutableStateFlow("")

    init {
        tts = TextToSpeech(application, this)
        seedDatabaseIfEmpty()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.ENGLISH
            isTtsReady.value = true
        }
    }

    fun speakText(text: String, rateWpm: Int) {
        if (isTtsReady.value) {
            tts?.stop()
            // Android TTS speed: 1.0 is ~150 WPM normal rate
            val speechRate = (rateWpm / 150.0f).coerceIn(0.4f, 2.0f)
            tts?.setSpeechRate(speechRate)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "steno_utterance")
        }
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    private fun seedDatabaseIfEmpty() {
        viewModelScope.launch {
            val count = dao.getMocksCount()
            if (count == 0) {
                dao.insertMocks(DefaultMockData.ALL_DEFAULT_MOCKS)
            }
        }
    }

    fun resetToFactoryMocks() {
        viewModelScope.launch {
            dao.deleteDefaultMocks()
            dao.insertMocks(DefaultMockData.ALL_DEFAULT_MOCKS)
        }
    }

    fun navigateTo(screen: AppScreen) {
        backStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            val prev = backStack.removeAt(backStack.size - 1)
            _currentScreen.value = prev
            return true
        }
        if (_currentScreen.value !is AppScreen.Home) {
            _currentScreen.value = AppScreen.Home
            return true
        }
        return false
    }

    fun openMockStart(mock: TypingMockEntity) {
        _selectedMock.value = mock
        val matchingExam = when (mock.examTag) {
            "ntpc" -> ExamRegistry.RRB_NTPC
            "chsl", "chsl_ldc" -> ExamRegistry.SSC_CHSL_LDC
            "chsl_deo" -> ExamRegistry.SSC_CHSL_DEO
            "dsssb" -> ExamRegistry.DSSSB
            "kvs", "kvs_nvs" -> ExamRegistry.KVS_NVS
            "hindi" -> ExamRegistry.HINDI
            else -> ExamRegistry.SSC_CGL
        }
        selectedExam.value = matchingExam
        navigateTo(AppScreen.StartMock(mock.id))
    }

    fun startTest() {
        val mock = _selectedMock.value ?: return
        typedContent.value = ""
        keystrokeCount.value = 0
        backspaceCount.value = 0

        val mins = if (isPracticeMode.value) practiceMinutes.value else selectedExam.value.defaultDurationMinutes
        val totalSecs = mins * 60
        totalTestSeconds.value = totalSecs
        secondsRemaining.value = totalSecs
        isTestRunning.value = true

        if (soundManager.isHallNoiseEnabled) {
            soundManager.startHallNoise()
        }

        startTimer()
        navigateTo(AppScreen.Test(mock.id))
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (secondsRemaining.value > 0 && isTestRunning.value) {
                delay(1000)
                secondsRemaining.value = secondsRemaining.value - 1
            }
            if (isTestRunning.value && secondsRemaining.value <= 0) {
                submitTest()
            }
        }
    }

    fun onKeyTyped(newText: String, isBackspace: Boolean = false) {
        if (!isTestRunning.value) return
        val currentKds = keystrokeCount.value
        val targetLimit = selectedExam.value.targetKeystrokes

        // Enforce maximum key depression limit if reached
        if (currentKds >= targetLimit && newText.length >= typedContent.value.length) {
            return
        }

        // Clean text (Enter and Tab disabled per exam rules)
        val sanitized = newText.replace("\n", " ").replace("\r", " ").replace("\t", " ")

        if (isBackspace) {
            backspaceCount.value = backspaceCount.value + 1
        }
        keystrokeCount.value = currentKds + 1
        typedContent.value = sanitized

        soundManager.playKeyClick(0.6f)
    }

    fun submitTest() {
        if (!isTestRunning.value) return
        isTestRunning.value = false
        timerJob?.cancel()
        soundManager.stopHallNoise()

        val mock = _selectedMock.value ?: return
        val usedSecs = totalTestSeconds.value - secondsRemaining.value

        val eval = TypingEvaluator.evaluate(
            originalText = mock.text,
            typedText = typedContent.value,
            timeUsedSeconds = usedSecs,
            totalTimeSeconds = totalTestSeconds.value,
            keystrokes = keystrokeCount.value,
            backspaceCount = backspaceCount.value,
            examRule = selectedExam.value,
            userCategory = selectedCategory.value,
            countUnreachedAsMistakes = countUnreachedAsMistakes.value
        )
        latestEvaluation.value = eval

        // Persist test result to database
        viewModelScope.launch {
            val dateFmt = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
            val categoryQualJson = eval.categoryQualifications.joinToString(";") {
                "${it.category.shortName}:${if (it.isQualified) "PASS" else "FAIL"}:${it.allowedErrorPercent}"
            }
            val resultEntity = TypingResultEntity(
                mockId = mock.id,
                mockTitle = mock.title,
                examName = selectedExam.value.name,
                category = selectedCategory.value.shortName,
                dateFormatted = dateFmt,
                timeUsedSeconds = usedSecs,
                totalTimeSeconds = totalTestSeconds.value,
                keystrokes = keystrokeCount.value,
                backspaceCount = backspaceCount.value,
                wordsTyped = eval.typedWordsCount,
                correctWords = eval.alignment.correctWords,
                wrongWords = eval.alignment.wrongWords,
                omittedWords = eval.alignment.omittedWords,
                extraWords = eval.alignment.extraWords,
                halfMistakes = eval.alignment.halfMistakes,
                unreachedWords = eval.alignment.unreachedOriginalWords,
                totalMistakes = eval.totalMistakes,
                errorPercentage = eval.activeErrorPercent,
                allowedErrorPercentage = eval.allowedErrorPercent,
                netWpm = eval.netWpm,
                grossWpm = eval.grossWpm,
                netKdph = eval.netKdph,
                accuracyPercentage = eval.accuracyPercent,
                isQualified = eval.isUserQualified,
                categoryQualJson = categoryQualJson
            )
            dao.insertResult(resultEntity)
        }

        navigateTo(AppScreen.Result)
    }

    fun toggleCountUnreached(countUnreached: Boolean) {
        countUnreachedAsMistakes.value = countUnreached
        val mock = _selectedMock.value ?: return
        val usedSecs = totalTestSeconds.value - secondsRemaining.value
        latestEvaluation.value = TypingEvaluator.evaluate(
            originalText = mock.text,
            typedText = typedContent.value,
            timeUsedSeconds = usedSecs,
            totalTimeSeconds = totalTestSeconds.value,
            keystrokes = keystrokeCount.value,
            backspaceCount = backspaceCount.value,
            examRule = selectedExam.value,
            userCategory = selectedCategory.value,
            countUnreachedAsMistakes = countUnreached
        )
    }

    // Admin operations
    fun saveCustomMock(mock: TypingMockEntity) {
        viewModelScope.launch {
            dao.insertMock(mock)
        }
    }

    fun updateMock(mock: TypingMockEntity) {
        viewModelScope.launch {
            dao.updateMock(mock)
        }
    }

    fun deleteMock(mock: TypingMockEntity) {
        viewModelScope.launch {
            dao.deleteMock(mock)
        }
    }

    fun clearAllResults() {
        viewModelScope.launch {
            dao.clearAllResults()
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
        tts?.shutdown()
    }
}
