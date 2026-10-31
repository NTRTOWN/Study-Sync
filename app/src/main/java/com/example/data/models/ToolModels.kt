package com.example.data.models

import java.util.UUID

data class Flashcard(
    val id: String = UUID.randomUUID().toString(),
    val deckName: String,
    val frontPrompt: String,
    val backAnswer: String,
    val subject: String,
    val reviewCount: Int = 0,
    val isMastered: Boolean = false
)

enum class PriorityLevel(val label: String, val colorHex: Long) {
    LOW("Low Priority", 0xFF10B981),
    MEDIUM("Medium Priority", 0xFFF59E0B),
    HIGH("Urgent / High Priority", 0xFFEF4444)
}

data class AssignmentItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subject: String,
    val dueDate: String,
    val priority: PriorityLevel = PriorityLevel.MEDIUM,
    val isCompleted: Boolean = false,
    val notes: String = ""
)

data class FormulaItem(
    val id: String = UUID.randomUUID().toString(),
    val category: String, // "Calculus", "Physics", "Chemistry", "CS"
    val name: String,
    val formulaLatex: String,
    val description: String,
    val variableDefinitions: String
)

enum class CitationStyle(val label: String) {
    APA7("APA 7th Edition"),
    MLA9("MLA 9th Edition"),
    CHICAGO("Chicago Author-Date"),
    IEEE("IEEE Engineering"),
    HARVARD("Harvard Style")
}

data class StudySessionLog(
    val id: String = UUID.randomUUID().toString(),
    val dateString: String,
    val subject: String,
    val minutesStudied: Int,
    val mode: String // "Pomodoro Focus", "Deep Work", "Quick Review"
)
