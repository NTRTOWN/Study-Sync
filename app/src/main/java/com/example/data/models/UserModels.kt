package com.example.data.models

import java.util.UUID

enum class EducationLevel(val label: String) {
    HIGH_SCHOOL("High School"),
    COLLEGE_UNDERGRAD("Undergraduate / College"),
    GRADUATE_MASTERS("Master's / Graduate"),
    PHD_RESEARCH("PhD / Researcher"),
    SELF_TAUGHT("Lifelong / Self-Taught")
}

data class UserProfile(
    val username: String = "GlobalScholar",
    val avatarEmoji: String = "🎓",
    val countryFlag: String = "🌐",
    val countryName: String = "Global",
    val educationLevel: EducationLevel = EducationLevel.COLLEGE_UNDERGRAD,
    val majorOrFocus: String = "Computer Science & Physics",
    val studyStreakDays: Int = 14,
    val xpPoints: Int = 1850,
    val dailyGoalMinutes: Int = 120,
    val minutesStudiedToday: Int = 45,
    val isPermanent: Boolean = true
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderUsername: String,
    val senderAvatar: String,
    val senderCountry: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val attachedNoteTitle: String? = null,
    val attachedNoteSnippet: String? = null,
    val likesCount: Int = 0,
    val isFromMe: Boolean = false
)

data class StudyRoom(
    val id: String,
    val code: String, // e.g. "STUDY-489"
    val name: String,
    val description: String,
    val isPrivate: Boolean,
    val topic: String,
    val activeMembersCount: Int = 1,
    val maxMembers: Int = 10,
    val hostUsername: String = "GlobalScholar",
    val pin: String? = null
)

data class Channel(
    val id: String,
    val name: String,
    val icon: String,
    val description: String,
    val onlineCount: Int
)
