package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiStudyService
import com.example.data.ai.HtmlExportService
import com.example.data.ai.StudyPackResult
import com.example.data.models.AssignmentItem
import com.example.data.models.Channel
import com.example.data.models.ChatMessage
import com.example.data.models.CitationStyle
import com.example.data.models.EducationLevel
import com.example.data.models.Flashcard
import com.example.data.models.FormulaItem
import com.example.data.models.MaterialType
import com.example.data.models.MindMapNode
import com.example.data.models.NotesDepth
import com.example.data.models.PriorityLevel
import com.example.data.models.ProblemSolution
import com.example.data.models.QuestionDifficulty
import com.example.data.models.QuizQuestion
import com.example.data.models.StudyNote
import com.example.data.models.StudyRoom
import com.example.data.models.StudySessionLog
import com.example.data.models.TutorChatMessage
import com.example.data.models.UserProfile
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenDestination {
    AI_TUTOR,
    GLOBAL_COMMUNITY,
    FOCUS_TIMER,
    FLASHCARDS_LIBRARY,
    STUDENT_TOOLKIT
}

enum class AiTab {
    MATERIAL_INPUT,
    NOTES_READER,
    MIND_MAP,
    QUIZ_PRACTICE,
    PROBLEM_SOLVER,
    TUTOR_CHAT
}

enum class TimerMode(val title: String, val defaultMinutes: Int) {
    POMODORO_FOCUS("Pomodoro Focus", 25),
    SHORT_BREAK("Short Break", 5),
    LONG_BREAK("Long Break", 15),
    DEEP_WORK("Deep Work Marathon", 50)
}

enum class AmbientSound(val label: String, val icon: String) {
    NONE("Silent Focus", "🔇"),
    RAIN("Rain on Window", "🌧️"),
    LIBRARY("Quiet Library", "📚"),
    LOFI("Lo-Fi Study Beat", "🎧"),
    WHITE_NOISE("Gentle White Noise", "📻")
}

sealed interface AiProcessingState {
    data object Idle : AiProcessingState
    data class Processing(val statusMessage: String) : AiProcessingState
    data class Success(val result: StudyPackResult) : AiProcessingState
    data class Error(val message: String) : AiProcessingState
}

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    val repository = StudyRepository(application)
    private val aiService = GeminiStudyService()

    // Navigation
    private val _currentScreen = MutableStateFlow(ScreenDestination.AI_TUTOR)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _currentAiTab = MutableStateFlow(AiTab.MATERIAL_INPUT)
    val currentAiTab: StateFlow<AiTab> = _currentAiTab.asStateFlow()

    // User Profile
    val userProfile: StateFlow<UserProfile> = repository.userProfile

    // AI Processing State
    private val _aiState = MutableStateFlow<AiProcessingState>(AiProcessingState.Idle)
    val aiState: StateFlow<AiProcessingState> = _aiState.asStateFlow()

    private val _currentStudyPack = MutableStateFlow<StudyPackResult?>(null)
    val currentStudyPack: StateFlow<StudyPackResult?> = _currentStudyPack.asStateFlow()

    // Problem Solver direct query state
    private val _solverResult = MutableStateFlow<ProblemSolution?>(null)
    val solverResult: StateFlow<ProblemSolution?> = _solverResult.asStateFlow()

    private val _isSolvingProblem = MutableStateFlow(false)
    val isSolvingProblem: StateFlow<Boolean> = _isSolvingProblem.asStateFlow()

    // Interactive Tutor Chat
    private val _tutorMessages = MutableStateFlow<List<TutorChatMessage>>(
        listOf(
            TutorChatMessage(
                sender = "ai",
                text = "Hello! I'm your personal AI Tutor. Upload any book chapter, PDF, video link, problem photo, or ask me any question. I'll adapt step-by-step to your exact pace!"
            )
        )
    )
    val tutorMessages: StateFlow<List<TutorChatMessage>> = _tutorMessages.asStateFlow()

    private val _isTutorThinking = MutableStateFlow(false)
    val isTutorThinking: StateFlow<Boolean> = _isTutorThinking.asStateFlow()

    // Active Chat & Rooms
    val channels: StateFlow<List<Channel>> = repository.channels
    val channelMessages: StateFlow<Map<String, List<ChatMessage>>> = repository.channelMessages
    val studyRooms: StateFlow<List<StudyRoom>> = repository.studyRooms
    val roomMessages: StateFlow<Map<String, List<ChatMessage>>> = repository.roomMessages

    private val _selectedChannelId = MutableStateFlow("general")
    val selectedChannelId: StateFlow<String> = _selectedChannelId.asStateFlow()

    private val _selectedRoom = MutableStateFlow<StudyRoom?>(null)
    val selectedRoom: StateFlow<StudyRoom?> = _selectedRoom.asStateFlow()

    // Flashcards & Library
    val flashcards: StateFlow<List<Flashcard>> = repository.flashcards
    val savedNotes: StateFlow<List<StudyNote>> = repository.savedNotes
    val assignments: StateFlow<List<AssignmentItem>> = repository.assignments
    val formulaLibrary: List<FormulaItem> = repository.formulaLibrary
    val sessionLogs: StateFlow<List<StudySessionLog>> = repository.sessionLogs

    // Study Timer State
    private val _timerMode = MutableStateFlow(TimerMode.POMODORO_FOCUS)
    val timerMode: StateFlow<TimerMode> = _timerMode.asStateFlow()

    private val _timerRemainingSeconds = MutableStateFlow(25 * 60)
    val timerRemainingSeconds: StateFlow<Int> = _timerRemainingSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _selectedSound = MutableStateFlow(AmbientSound.NONE)
    val selectedSound: StateFlow<AmbientSound> = _selectedSound.asStateFlow()

    private var timerJob: Job? = null

    // Dialog & UI Visibility states
    private val _showProfileDialog = MutableStateFlow(false)
    val showProfileDialog: StateFlow<Boolean> = _showProfileDialog.asStateFlow()

    private val _showCreateRoomDialog = MutableStateFlow(false)
    val showCreateRoomDialog: StateFlow<Boolean> = _showCreateRoomDialog.asStateFlow()

    private val _showJoinRoomDialog = MutableStateFlow(false)
    val showJoinRoomDialog: StateFlow<Boolean> = _showJoinRoomDialog.asStateFlow()

    private val _showExportHtmlDialog = MutableStateFlow<String?>(null)
    val showExportHtmlDialog: StateFlow<String?> = _showExportHtmlDialog.asStateFlow()

    // Interactive Quiz Score Tracking
    private val _userQuizAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val userQuizAnswers: StateFlow<Map<Int, Int>> = _userQuizAnswers.asStateFlow()

    init {
        // Preload an initial rich sample study pack so the user can immediately experience notes & mind map
        loadDefaultSamplePack()
    }

    private fun loadDefaultSamplePack() {
        viewModelScope.launch {
            val sample = aiService.processMaterial(
                materialTitle = "Quantum Foundations & Wave Mechanics",
                materialContent = "Schrödinger wave equation, de Broglie wavelength, wave-particle duality, Heisenberg uncertainty principle, and quantum tunneling in modern semiconductors.",
                materialType = MaterialType.DOCUMENT_PDF,
                depth = NotesDepth.DETAILED_NOTES,
                questionCount = 4,
                difficulty = QuestionDifficulty.HOTS_HIGHER_ORDER
            )
            _currentStudyPack.value = sample
            repository.saveStudyNote(sample.note, sample.mindMap)
        }
    }

    // Navigation Controls
    fun setScreen(screen: ScreenDestination) {
        _currentScreen.value = screen
    }

    fun setAiTab(tab: AiTab) {
        _currentAiTab.value = tab
    }

    // AI Generation Method
    fun processStudyMaterial(
        title: String,
        content: String,
        materialType: MaterialType,
        depth: NotesDepth,
        questionCount: Int,
        difficulty: QuestionDifficulty
    ) {
        viewModelScope.launch {
            _aiState.value = AiProcessingState.Processing("Reading material & constructing knowledge graph...")
            delay(400)
            _aiState.value = AiProcessingState.Processing("Synthesizing multi-page notes & formulas...")
            delay(400)
            _aiState.value = AiProcessingState.Processing("Generating hierarchical mind map & ${difficulty.label} questions...")

            try {
                val result = aiService.processMaterial(
                    materialTitle = title.ifBlank { "Study Material: ${materialType.label}" },
                    materialContent = content,
                    materialType = materialType,
                    depth = depth,
                    questionCount = questionCount,
                    difficulty = difficulty
                )
                _currentStudyPack.value = result
                _userQuizAnswers.value = emptyMap()
                repository.saveStudyNote(result.note, result.mindMap)
                _aiState.value = AiProcessingState.Success(result)
                _currentAiTab.value = AiTab.NOTES_READER
            } catch (e: Exception) {
                _aiState.value = AiProcessingState.Error(e.message ?: "Failed to analyze study material.")
            }
        }
    }

    fun solveProblemDirectly(problemText: String) {
        viewModelScope.launch {
            _isSolvingProblem.value = true
            try {
                val solution = aiService.solveProblem(problemText)
                _solverResult.value = solution
                _currentAiTab.value = AiTab.PROBLEM_SOLVER
            } catch (_: Exception) {
                // Handled gracefully
            } finally {
                _isSolvingProblem.value = false
            }
        }
    }

    fun sendTutorMessage(query: String) {
        if (query.isBlank()) return
        val userMsg = TutorChatMessage(sender = "user", text = query)
        _tutorMessages.value = _tutorMessages.value + userMsg

        viewModelScope.launch {
            _isTutorThinking.value = true
            val currentTopic = _currentStudyPack.value?.note?.title
            val reply = aiService.sendTutorChat(_tutorMessages.value, query, currentTopic)
            _tutorMessages.value = _tutorMessages.value + TutorChatMessage(sender = "ai", text = reply)
            _isTutorThinking.value = false
        }
    }

    fun selectQuizAnswer(questionIndex: Int, optionIndex: Int) {
        if (_userQuizAnswers.value.containsKey(questionIndex)) return
        _userQuizAnswers.value = _userQuizAnswers.value + (questionIndex to optionIndex)
        repository.awardXp(15)
    }

    // Community & Chat
    fun selectChannel(id: String) {
        _selectedChannelId.value = id
        _selectedRoom.value = null
    }

    fun selectRoom(room: StudyRoom) {
        _selectedRoom.value = room
    }

    fun sendCommunityMessage(text: String, attachCurrentNote: Boolean = false) {
        if (text.isBlank()) return
        val currentNote = if (attachCurrentNote) _currentStudyPack.value?.note else null
        val room = _selectedRoom.value
        if (room != null) {
            repository.addRoomMessage(room.id, text)
        } else {
            repository.addChannelMessage(
                channelId = _selectedChannelId.value,
                text = text,
                noteTitle = currentNote?.title,
                noteSnippet = currentNote?.pages?.firstOrNull()?.keyPoints?.firstOrNull()
            )
        }
    }

    fun createRoom(name: String, topic: String, isPrivate: Boolean, pin: String?) {
        val newRoom = repository.createStudyRoom(name, topic, isPrivate, pin)
        _selectedRoom.value = newRoom
        _showCreateRoomDialog.value = false
    }

    fun joinRoomByCodeOrPin(codeOrPin: String): Boolean {
        val match = studyRooms.value.find {
            it.code.equals(codeOrPin.trim(), ignoreCase = true) || it.pin == codeOrPin.trim()
        }
        return if (match != null) {
            _selectedRoom.value = match
            _showJoinRoomDialog.value = false
            true
        } else false
    }

    // Timer Controls
    fun setTimerMode(mode: TimerMode) {
        timerJob?.cancel()
        _isTimerRunning.value = false
        _timerMode.value = mode
        _timerRemainingSeconds.value = mode.defaultMinutes * 60
    }

    fun toggleTimer() {
        if (_isTimerRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isTimerRunning.value = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value && _timerRemainingSeconds.value > 0) {
                delay(1000)
                _timerRemainingSeconds.value = _timerRemainingSeconds.value - 1
            }
            if (_timerRemainingSeconds.value <= 0) {
                _isTimerRunning.value = false
                repository.logCompletedTimerSession(
                    minutes = _timerMode.value.defaultMinutes,
                    subject = _currentStudyPack.value?.note?.title ?: "Focus Session"
                )
            }
        }
    }

    private fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        pauseTimer()
        _timerRemainingSeconds.value = _timerMode.value.defaultMinutes * 60
    }

    fun setAmbientSound(sound: AmbientSound) {
        _selectedSound.value = sound
    }

    // Flashcard & Library Actions
    fun createFlashcardsFromCurrentNote() {
        val pack = _currentStudyPack.value ?: return
        repository.generateFlashcardsFromNote(pack.note)
        setScreen(ScreenDestination.FLASHCARDS_LIBRARY)
    }

    fun toggleFlashcardMastered(id: String) {
        repository.toggleFlashcardMastered(id)
    }

    fun toggleAssignmentCompleted(id: String) {
        repository.toggleAssignmentCompleted(id)
    }

    fun addAssignment(title: String, subject: String, dueDate: String, priority: PriorityLevel, notes: String) {
        repository.addAssignment(title, subject, dueDate, priority, notes)
    }

    // Export HTML Webpage
    fun generateHtmlExport(): String {
        val pack = _currentStudyPack.value
        return if (pack != null) {
            HtmlExportService.generateStandaloneHtml(
                note = pack.note,
                mindMap = pack.mindMap,
                questions = pack.questions,
                problemSolution = pack.problemSolution ?: _solverResult.value
            )
        } else {
            "<html><body><h1>No active study pack</h1></body></html>"
        }
    }

    fun triggerHtmlExportModal() {
        _showExportHtmlDialog.value = generateHtmlExport()
    }

    fun dismissExportDialog() {
        _showExportHtmlDialog.value = null
    }

    // Dialog controls
    fun setShowProfileDialog(show: Boolean) {
        _showProfileDialog.value = show
    }

    fun setShowCreateRoomDialog(show: Boolean) {
        _showCreateRoomDialog.value = show
    }

    fun setShowJoinRoomDialog(show: Boolean) {
        _showJoinRoomDialog.value = show
    }

    fun updateProfile(
        username: String,
        avatarEmoji: String,
        countryFlag: String,
        countryName: String,
        educationLevel: EducationLevel,
        majorOrFocus: String,
        dailyGoalMinutes: Int
    ) {
        val current = userProfile.value
        val updated = current.copy(
            username = username.ifBlank { "Scholar" },
            avatarEmoji = avatarEmoji,
            countryFlag = countryFlag,
            countryName = countryName,
            educationLevel = educationLevel,
            majorOrFocus = majorOrFocus,
            dailyGoalMinutes = dailyGoalMinutes
        )
        repository.saveUserProfile(updated)
        _showProfileDialog.value = false
    }
}
