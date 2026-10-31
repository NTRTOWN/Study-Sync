package com.example.data.repository

import android.content.Context
import com.example.data.models.AssignmentItem
import com.example.data.models.Channel
import com.example.data.models.ChatMessage
import com.example.data.models.CitationStyle
import com.example.data.models.EducationLevel
import com.example.data.models.Flashcard
import com.example.data.models.FormulaItem
import com.example.data.models.MindMapNode
import com.example.data.models.PriorityLevel
import com.example.data.models.StudyNote
import com.example.data.models.StudyRoom
import com.example.data.models.StudySessionLog
import com.example.data.models.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class StudyRepository(context: Context) {
    private val prefs = context.getSharedPreferences("study_sphere_prefs", Context.MODE_PRIVATE)

    // User Profile
    private val _userProfile = MutableStateFlow(loadUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Public Channels
    private val _channels = MutableStateFlow<List<Channel>>(
        listOf(
            Channel("general", "#general-study", "🌐", "Global student lounge & daily motivation", 428),
            Channel("stem", "#math-and-physics", "📐", "Calculus, mechanics, proofs & problem solving", 312),
            Channel("cs", "#code-and-ai", "💻", "Algorithms, Python, systems & AI projects", 285),
            Channel("med", "#biology-and-chem", "🧬", "Organic chem, anatomy & pre-med prep", 194),
            Channel("exams", "#exam-cram-2026", "🔥", "Finals, AP, SAT, GRE & competitive cramming", 516)
        )
    )
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    // Channel Messages
    private val _channelMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(
        mapOf(
            "general" to listOf(
                ChatMessage(
                    senderUsername = "Elena_R",
                    senderAvatar = "👩‍🔬",
                    senderCountry = "🇨🇦",
                    text = "Welcome everyone! Finished a 50m deep work session on Thermodynamics. The AI tutor mind map feature is insane!",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 18,
                    likesCount = 12
                ),
                ChatMessage(
                    senderUsername = "Kenji_T",
                    senderAvatar = "👨‍💻",
                    senderCountry = "🇯🇵",
                    text = "Studying Discrete Math right now. Anyone want to jump in a private room for Pomodoro intervals?",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 10,
                    likesCount = 8
                ),
                ChatMessage(
                    senderUsername = "Amina_K",
                    senderAvatar = "👩‍🎓",
                    senderCountry = "🇬🇧",
                    text = "I just generated a 5-page chapter breakdown on Neural Networks with Higher-Order Thinking questions. Sharing notes with the room!",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 4,
                    attachedNoteTitle = "Deep Learning & Backpropagation",
                    attachedNoteSnippet = "Core derivations of gradient descent with chain rule equations.",
                    likesCount = 19
                )
            ),
            "stem" to listOf(
                ChatMessage(
                    senderUsername = "Marcus_Phys",
                    senderAvatar = "🔭",
                    senderCountry = "🇩🇪",
                    text = "Who can check step 3 of the relativistic Doppler proof? Used the problem solver mode and verified the velocity shift.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 35,
                    likesCount = 5
                )
            ),
            "cs" to listOf(
                ChatMessage(
                    senderUsername = "DevAlex",
                    senderAvatar = "⚡",
                    senderCountry = "🇺🇸",
                    text = "Practicing Dynamic Programming today. Remember: optimal substructure + overlapping subproblems!",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 50,
                    likesCount = 14
                )
            ),
            "med" to listOf(
                ChatMessage(
                    senderUsername = "DrMaya",
                    senderAvatar = "🩺",
                    senderCountry = "🇮🇳",
                    text = "Generated 20 HOTS flashcards for the Krebs Cycle. Active recall is working wonders.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 25,
                    likesCount = 9
                )
            ),
            "exams" to listOf(
                ChatMessage(
                    senderUsername = "Liam_Study",
                    senderAvatar = "📚",
                    senderCountry = "🇦🇺",
                    text = "Countdown is on! 12 days until finals. 25m Pomodoro starting now!",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 6,
                    likesCount = 7
                )
            )
        )
    )
    val channelMessages: StateFlow<Map<String, List<ChatMessage>>> = _channelMessages.asStateFlow()

    // Study Rooms (Public & Private)
    private val _studyRooms = MutableStateFlow<List<StudyRoom>>(
        listOf(
            StudyRoom(
                id = "room-1",
                code = "MATH-202",
                name = "Advanced Calculus Study Lounge",
                description = "Silent focus & peer question answering on multivariable calculus",
                isPrivate = false,
                topic = "Multivariable Calculus",
                activeMembersCount = 6,
                maxMembers = 12,
                hostUsername = "ProfessorOak"
            ),
            StudyRoom(
                id = "room-2",
                code = "CS-401",
                name = "Algorithms & Data Structures Sprint",
                description = "Leetcode and graph theory practice with room timer",
                isPrivate = false,
                topic = "Computer Science",
                activeMembersCount = 8,
                maxMembers = 10,
                hostUsername = "BinaryGuru"
            ),
            StudyRoom(
                id = "room-3",
                code = "MED-771",
                name = "MCAT & Medical Prep Private Room",
                description = "Invite-only room for high-yield biology and biochemistry drills",
                isPrivate = true,
                topic = "Biochemistry",
                activeMembersCount = 4,
                maxMembers = 6,
                hostUsername = "DrSophia",
                pin = "1234"
            )
        )
    )
    val studyRooms: StateFlow<List<StudyRoom>> = _studyRooms.asStateFlow()

    // Private Room Chat Messages
    private val _roomMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(
        mapOf(
            "room-1" to listOf(
                ChatMessage(
                    senderUsername = "ProfessorOak",
                    senderAvatar = "🎓",
                    senderCountry = "🇺🇸",
                    text = "Welcome to room MATH-202. Synchronized timer set to 25m focus.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 15
                )
            ),
            "room-2" to listOf(
                ChatMessage(
                    senderUsername = "BinaryGuru",
                    senderAvatar = "💻",
                    senderCountry = "🇩🇪",
                    text = "Working on Dijkstra and A* pathfinding. Feel free to drop questions!",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 20
                )
            ),
            "room-3" to listOf(
                ChatMessage(
                    senderUsername = "DrSophia",
                    senderAvatar = "🩺",
                    senderCountry = "🇬🇧",
                    text = "Private Room PIN verified. Reviewing lipid metabolism flashcards now.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 10
                )
            )
        )
    )
    val roomMessages: StateFlow<Map<String, List<ChatMessage>>> = _roomMessages.asStateFlow()

    // Saved Notes Library
    private val _savedNotes = MutableStateFlow<List<StudyNote>>(emptyList())
    val savedNotes: StateFlow<List<StudyNote>> = _savedNotes.asStateFlow()

    // Saved Mind Maps Library
    private val _savedMindMaps = MutableStateFlow<List<Pair<String, MindMapNode>>>(emptyList())
    val savedMindMaps: StateFlow<List<Pair<String, MindMapNode>>> = _savedMindMaps.asStateFlow()

    // Flashcards Deck
    private val _flashcards = MutableStateFlow<List<Flashcard>>(
        listOf(
            Flashcard(
                deckName = "Essential Physics",
                frontPrompt = "What is the physical interpretation of Noether's Theorem?",
                backAnswer = "Every continuous differentiable symmetry of the action of a physical system has a corresponding conservation law (e.g., time symmetry → conservation of energy).",
                subject = "Physics"
            ),
            Flashcard(
                deckName = "Essential Physics",
                frontPrompt = "State the difference between Dirichlet and Neumann boundary conditions.",
                backAnswer = "Dirichlet specifies the exact value of the function on the boundary; Neumann specifies the derivative (flux) normal to the boundary.",
                subject = "Mathematics"
            ),
            Flashcard(
                deckName = "Computer Science Core",
                frontPrompt = "What is the Master Theorem for divide-and-conquer recurrence?",
                backAnswer = "T(n) = aT(n/b) + f(n). Compares f(n) to n^(log_b a) to establish bounds in three cases.",
                subject = "Algorithms"
            ),
            Flashcard(
                deckName = "Computer Science Core",
                frontPrompt = "Explain the difference between P, NP, and NP-Complete.",
                backAnswer = "P: Solvable in polynomial time. NP: Verifiable in polynomial time. NP-Complete: The hardest problems in NP; if any is solved in P, P=NP.",
                subject = "Theoretical CS"
            )
        )
    )
    val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    // Assignments
    private val _assignments = MutableStateFlow<List<AssignmentItem>>(
        listOf(
            AssignmentItem(
                title = "Thermodynamics Problem Set #4",
                subject = "Physics",
                dueDate = "Tomorrow, 11:59 PM",
                priority = PriorityLevel.HIGH,
                notes = "Focus on Carnot cycle efficiency and entropy change."
            ),
            AssignmentItem(
                title = "Data Structures Red-Black Tree Implementation",
                subject = "Computer Science",
                dueDate = "Friday, 5:00 PM",
                priority = PriorityLevel.MEDIUM,
                notes = "Implement rotation algorithms and balancing tests."
            ),
            AssignmentItem(
                title = "Biochemistry Literature Review",
                subject = "Biology",
                dueDate = "Nov 15, 2026",
                priority = PriorityLevel.LOW,
                notes = "Review 3 peer-reviewed articles on enzyme kinetics."
            )
        )
    )
    val assignments: StateFlow<List<AssignmentItem>> = _assignments.asStateFlow()

    // Formula Cheatsheet
    val formulaLibrary: List<FormulaItem> = listOf(
        FormulaItem(
            category = "Calculus",
            name = "Fundamental Theorem of Calculus",
            formulaLatex = "d/dx ∫_a^x f(t) dt = f(x)  and  ∫_a^b f(x) dx = F(b) - F(a)",
            description = "Links differentiation and integration into reciprocal operations.",
            variableDefinitions = "F(x) is any antiderivative of f(x)."
        ),
        FormulaItem(
            category = "Calculus",
            name = "Taylor Series Expansion",
            formulaLatex = "f(x) = Σ_{n=0}^∞ [f^(n)(a) / n!] (x - a)^n",
            description = "Polynomial approximation of an infinitely differentiable function around a.",
            variableDefinitions = "f^(n)(a) is the n-th derivative evaluated at point a."
        ),
        FormulaItem(
            category = "Physics",
            name = "Euler-Lagrange Equation",
            formulaLatex = "d/dt [∂L/∂q̇_i] - ∂L/∂q_i = 0",
            description = "Derives equations of motion via the Principle of Stationary Action.",
            variableDefinitions = "L = T - V (Kinetic minus Potential energy), q_i are generalized coordinates."
        ),
        FormulaItem(
            category = "Physics",
            name = "Maxwell's Equations (Differential Form)",
            formulaLatex = "∇·E = ρ/ε₀,  ∇·B = 0,  ∇×E = -∂B/∂t,  ∇×B = μ₀J + μ₀ε₀ ∂E/∂t",
            description = "Complete classical electrodynamics unifying electricity, magnetism and light.",
            variableDefinitions = "E: electric field, B: magnetic field, ρ: charge density, J: current density."
        ),
        FormulaItem(
            category = "Chemistry",
            name = "Gibbs Free Energy & Spontaneity",
            formulaLatex = "ΔG = ΔH - T ΔS  and  ΔG° = -RT ln(K_eq)",
            description = "Determines whether a chemical transformation is thermodynamically spontaneous.",
            variableDefinitions = "ΔG < 0 indicates spontaneity. R = 8.314 J/mol·K, T in Kelvin."
        ),
        FormulaItem(
            category = "Computer Science",
            name = "Shannon Information Entropy",
            formulaLatex = "H(X) = - Σ P(x_i) log₂ P(x_i)",
            description = "Measures the expected uncertainty or information content produced by a source.",
            variableDefinitions = "P(x_i) is the probability of occurrence of event x_i."
        )
    )

    // Study Sessions
    private val _sessionLogs = MutableStateFlow<List<StudySessionLog>>(
        listOf(
            StudySessionLog(dateString = "Today", subject = "Physics & Mechanics", minutesStudied = 25, mode = "Pomodoro Focus"),
            StudySessionLog(dateString = "Today", subject = "Algorithms", minutesStudied = 20, mode = "Deep Work"),
            StudySessionLog(dateString = "Yesterday", subject = "Multivariable Calculus", minutesStudied = 50, mode = "Deep Work")
        )
    )
    val sessionLogs: StateFlow<List<StudySessionLog>> = _sessionLogs.asStateFlow()

    // Actions
    fun saveUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        prefs.edit()
            .putString("username", profile.username)
            .putString("avatarEmoji", profile.avatarEmoji)
            .putString("countryFlag", profile.countryFlag)
            .putString("countryName", profile.countryName)
            .putString("educationLevel", profile.educationLevel.name)
            .putString("majorOrFocus", profile.majorOrFocus)
            .putInt("studyStreakDays", profile.studyStreakDays)
            .putInt("xpPoints", profile.xpPoints)
            .putInt("dailyGoalMinutes", profile.dailyGoalMinutes)
            .putInt("minutesStudiedToday", profile.minutesStudiedToday)
            .apply()
    }

    private fun loadUserProfile(): UserProfile {
        val username = prefs.getString("username", "GlobalScholar") ?: "GlobalScholar"
        val avatar = prefs.getString("avatarEmoji", "🎓") ?: "🎓"
        val flag = prefs.getString("countryFlag", "🌐") ?: "🌐"
        val country = prefs.getString("countryName", "Global") ?: "Global"
        val eduStr = prefs.getString("educationLevel", EducationLevel.COLLEGE_UNDERGRAD.name)
        val edu = try { EducationLevel.valueOf(eduStr ?: "") } catch (_: Exception) { EducationLevel.COLLEGE_UNDERGRAD }
        val major = prefs.getString("majorOrFocus", "Computer Science & Physics") ?: "Computer Science & Physics"
        val streak = prefs.getInt("studyStreakDays", 14)
        val xp = prefs.getInt("xpPoints", 1850)
        val dailyGoal = prefs.getInt("dailyGoalMinutes", 120)
        val studied = prefs.getInt("minutesStudiedToday", 45)

        return UserProfile(
            username = username,
            avatarEmoji = avatar,
            countryFlag = flag,
            countryName = country,
            educationLevel = edu,
            majorOrFocus = major,
            studyStreakDays = streak,
            xpPoints = xp,
            dailyGoalMinutes = dailyGoal,
            minutesStudiedToday = studied,
            isPermanent = true
        )
    }

    fun addChannelMessage(channelId: String, text: String, noteTitle: String? = null, noteSnippet: String? = null) {
        val user = _userProfile.value
        val msg = ChatMessage(
            senderUsername = user.username,
            senderAvatar = user.avatarEmoji,
            senderCountry = user.countryFlag,
            text = text,
            attachedNoteTitle = noteTitle,
            attachedNoteSnippet = noteSnippet,
            isFromMe = true
        )
        _channelMessages.update { map ->
            val list = map[channelId].orEmpty() + msg
            map + (channelId to list)
        }
        awardXp(15)
    }

    fun addRoomMessage(roomId: String, text: String) {
        val user = _userProfile.value
        val msg = ChatMessage(
            senderUsername = user.username,
            senderAvatar = user.avatarEmoji,
            senderCountry = user.countryFlag,
            text = text,
            isFromMe = true
        )
        _roomMessages.update { map ->
            val list = map[roomId].orEmpty() + msg
            map + (roomId to list)
        }
        awardXp(15)
    }

    fun createStudyRoom(name: String, topic: String, isPrivate: Boolean, pin: String?): StudyRoom {
        val user = _userProfile.value
        val code = "ROOM-" + (1000..9999).random()
        val room = StudyRoom(
            id = UUID.randomUUID().toString(),
            code = code,
            name = name,
            description = "Created by ${user.username} for $topic",
            isPrivate = isPrivate,
            topic = topic,
            activeMembersCount = 1,
            maxMembers = if (isPrivate) 6 else 12,
            hostUsername = user.username,
            pin = pin
        )
        _studyRooms.update { it + room }
        awardXp(50)
        return room
    }

    fun saveStudyNote(note: StudyNote, mindMap: MindMapNode?) {
        _savedNotes.update { listOf(note) + it.filter { n -> n.id != note.id } }
        if (mindMap != null) {
            _savedMindMaps.update { listOf(Pair(note.title, mindMap)) + it }
        }
        awardXp(30)
    }

    fun generateFlashcardsFromNote(note: StudyNote) {
        val newCards = mutableListOf<Flashcard>()
        note.pages.forEach { page ->
            page.keyPoints.forEachIndexed { idx, point ->
                newCards.add(
                    Flashcard(
                        deckName = note.title,
                        frontPrompt = "Regarding ${page.sectionTitle}: What is key concept #${idx + 1}?",
                        backAnswer = point,
                        subject = note.title
                    )
                )
            }
            page.formulasOrQuotes.forEach { formula ->
                newCards.add(
                    Flashcard(
                        deckName = note.title,
                        frontPrompt = "What is the formula / theorem for: ${page.sectionTitle}?",
                        backAnswer = formula,
                        subject = note.title
                    )
                )
            }
        }
        if (newCards.isNotEmpty()) {
            _flashcards.update { newCards + it }
            awardXp(40)
        }
    }

    fun toggleFlashcardMastered(id: String) {
        _flashcards.update { list ->
            list.map {
                if (it.id == id) it.copy(isMastered = !it.isMastered, reviewCount = it.reviewCount + 1)
                else it
            }
        }
        awardXp(10)
    }

    fun toggleAssignmentCompleted(id: String) {
        _assignments.update { list ->
            list.map {
                if (it.id == id) it.copy(isCompleted = !it.isCompleted)
                else it
            }
        }
        awardXp(25)
    }

    fun addAssignment(title: String, subject: String, dueDate: String, priority: PriorityLevel, notes: String) {
        val item = AssignmentItem(
            title = title,
            subject = subject,
            dueDate = dueDate,
            priority = priority,
            notes = notes
        )
        _assignments.update { listOf(item) + it }
        awardXp(20)
    }

    fun logCompletedTimerSession(minutes: Int, subject: String) {
        val session = StudySessionLog(
            dateString = "Today",
            subject = subject,
            minutesStudied = minutes,
            mode = "Pomodoro Focus"
        )
        _sessionLogs.update { listOf(session) + it }
        _userProfile.update {
            val newStudied = it.minutesStudiedToday + minutes
            val newXp = it.xpPoints + (minutes * 3)
            val updated = it.copy(minutesStudiedToday = newStudied, xpPoints = newXp)
            saveUserProfile(updated)
            updated
        }
    }

    fun awardXp(amount: Int) {
        _userProfile.update {
            val updated = it.copy(xpPoints = it.xpPoints + amount)
            saveUserProfile(updated)
            updated
        }
    }

    fun generateCitation(style: CitationStyle, author: String, title: String, year: String, publisher: String, url: String): String {
        val safeAuthor = author.ifBlank { "Anonymous" }
        val safeTitle = title.ifBlank { "Untitled Work" }
        val safeYear = year.ifBlank { "n.d." }
        val safePub = publisher.ifBlank { "Academic Press" }
        return when (style) {
            CitationStyle.APA7 -> "$safeAuthor. ($safeYear). $safeTitle. $safePub.${if (url.isNotBlank()) " $url" else ""}"
            CitationStyle.MLA9 -> "$safeAuthor. \"$safeTitle.\" $safePub, $safeYear.${if (url.isNotBlank()) " $url" else ""}"
            CitationStyle.CHICAGO -> "$safeAuthor. $safeYear. $safeTitle. City: $safePub."
            CitationStyle.IEEE -> "[1] $safeAuthor, \"$safeTitle,\" $safePub, $safeYear."
            CitationStyle.HARVARD -> "$safeAuthor ($safeYear) '$safeTitle', $safePub."
        }
    }
}
