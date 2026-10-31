package com.example.data.models

import java.util.UUID

enum class MaterialType(val label: String, val icon: String) {
    TEXT_PROMPT("Topic / Text", "📝"),
    DOCUMENT_PDF("PDF / Book Chapter", "📄"),
    VIDEO_LINK("Video / Link", "🎥"),
    IMAGE_PHOTO("Problem Photo / Diagram", "📷")
}

enum class NotesDepth(val label: String, val pageCountEstimate: Int, val description: String) {
    BRIEF_SUMMARY("Quick Summary", 1, "High-yield executive brief & core equations"),
    DETAILED_NOTES("Detailed Study Notes", 3, "Thorough explanations, diagrams & worked examples"),
    COMPREHENSIVE_CHAPTER("Full Textbook Chapter", 5, "In-depth multi-page breakdown, derivations & glossary")
}

enum class QuestionDifficulty(val label: String, val badgeColorHex: Long) {
    RECALL_FOUNDATION("Level 1: Recall & Basics", 0xFF10B981),
    CONCEPTUAL_APPLICATION("Level 2: Conceptual & Practical", 0xFF06B6D4),
    HOTS_HIGHER_ORDER("Level 3: Higher-Order Thinking (HOTS)", 0xFFF59E0B),
    OLYMPIAD_ADVANCED("Level 4: Olympiad / Complex Problem", 0xFFEF4444)
}

data class MindMapNode(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String? = null,
    val colorHex: Long = 0xFF6366F1,
    val children: List<MindMapNode> = emptyList(),
    val isExpanded: Boolean = true
)

data class NotePage(
    val pageNumber: Int,
    val sectionTitle: String,
    val contentMarkdown: String,
    val keyPoints: List<String> = emptyList(),
    val formulasOrQuotes: List<String> = emptyList()
)

data class StudyNote(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val sourceMaterialTitle: String,
    val materialType: MaterialType,
    val timestamp: Long = System.currentTimeMillis(),
    val pages: List<NotePage>,
    val tags: List<String> = listOf("StudySphere", "AI-Generated")
)

data class QuizQuestion(
    val id: String = UUID.randomUUID().toString(),
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
    val difficulty: QuestionDifficulty,
    val userSelectedIndex: Int? = null
)

data class ProblemSolutionStep(
    val stepNumber: Int,
    val stepTitle: String,
    val explanation: String,
    val mathematicalFormula: String? = null
)

data class ProblemSolution(
    val id: String = UUID.randomUUID().toString(),
    val problemStatement: String,
    val finalAnswer: String,
    val methodUsed: String,
    val steps: List<ProblemSolutionStep>,
    val verificationOrTip: String
)

data class TutorChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
