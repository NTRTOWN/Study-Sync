@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.MaterialType
import com.example.data.models.MindMapNode
import com.example.data.models.NotePage
import com.example.data.models.NotesDepth
import com.example.data.models.ProblemSolution
import com.example.data.models.QuestionDifficulty
import com.example.data.models.QuizQuestion
import com.example.data.models.StudyNote
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryIndigoDark
import com.example.ui.theme.PrimaryIndigoLight
import com.example.ui.theme.RoseError
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.TertiaryAmber
import com.example.viewmodel.AiProcessingState
import com.example.viewmodel.AiTab
import com.example.viewmodel.StudyViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiTutorScreen(viewModel: StudyViewModel) {
    val currentAiTab by viewModel.currentAiTab.collectAsState()
    val aiState by viewModel.aiState.collectAsState()
    val studyPack by viewModel.currentStudyPack.collectAsState()
    val solverResult by viewModel.solverResult.collectAsState()
    val isSolving by viewModel.isSolvingProblem.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightDark)
    ) {
        // AI Sub-Tabs Header
        ScrollableTabRow(
            selectedTabIndex = currentAiTab.ordinal,
            containerColor = DeepNavy,
            contentColor = Color.White,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                if (currentAiTab.ordinal < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[currentAiTab.ordinal]),
                        color = CyanGlow
                    )
                }
            }
        ) {
            Tab(
                selected = currentAiTab == AiTab.MATERIAL_INPUT,
                onClick = { viewModel.setAiTab(AiTab.MATERIAL_INPUT) },
                text = { Text("📥 Input Material", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = currentAiTab == AiTab.NOTES_READER,
                onClick = { viewModel.setAiTab(AiTab.NOTES_READER) },
                text = {
                    Text(
                        text = "📖 Notes (${studyPack?.note?.pages?.size ?: 0}p)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            Tab(
                selected = currentAiTab == AiTab.MIND_MAP,
                onClick = { viewModel.setAiTab(AiTab.MIND_MAP) },
                text = { Text("🧠 Mind Map", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = currentAiTab == AiTab.QUIZ_PRACTICE,
                onClick = { viewModel.setAiTab(AiTab.QUIZ_PRACTICE) },
                text = {
                    Text(
                        text = "📝 Quiz (${studyPack?.questions?.size ?: 0})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            Tab(
                selected = currentAiTab == AiTab.PROBLEM_SOLVER,
                onClick = { viewModel.setAiTab(AiTab.PROBLEM_SOLVER) },
                text = { Text("💡 Step Solver", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = currentAiTab == AiTab.TUTOR_CHAT,
                onClick = { viewModel.setAiTab(AiTab.TUTOR_CHAT) },
                text = { Text("💬 Socratic Tutor", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        // Active Tab Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentAiTab) {
                AiTab.MATERIAL_INPUT -> MaterialInputContent(
                    aiState = aiState,
                    onProcess = { title, content, type, depth, count, diff ->
                        viewModel.processStudyMaterial(title, content, type, depth, count, diff)
                    }
                )
                AiTab.NOTES_READER -> studyPack?.let {
                    NotesReaderContent(
                        note = it.note,
                        onGenerateFlashcards = { viewModel.createFlashcardsFromCurrentNote() },
                        onExportHtml = { viewModel.triggerHtmlExportModal() }
                    )
                } ?: EmptyStatePrompt("No notes generated yet. Submit material in 'Input Material'.")

                AiTab.MIND_MAP -> studyPack?.let {
                    MindMapVisualizerContent(rootNode = it.mindMap)
                } ?: EmptyStatePrompt("No mind map available. Analyze a topic first.")

                AiTab.QUIZ_PRACTICE -> studyPack?.let { pack ->
                    val userAnswers by viewModel.userQuizAnswers.collectAsState()
                    QuizPracticeContent(
                        questions = pack.questions,
                        userAnswers = userAnswers,
                        onSelectAnswer = { qIdx, optIdx -> viewModel.selectQuizAnswer(qIdx, optIdx) }
                    )
                } ?: EmptyStatePrompt("No quiz loaded. Process study material to generate questions.")

                AiTab.PROBLEM_SOLVER -> ProblemSolverContent(
                    solution = solverResult ?: studyPack?.problemSolution,
                    isLoading = isSolving,
                    onSolve = { problem -> viewModel.solveProblemDirectly(problem) }
                )

                AiTab.TUTOR_CHAT -> {
                    val messages by viewModel.tutorMessages.collectAsState()
                    val isThinking by viewModel.isTutorThinking.collectAsState()
                    TutorChatContent(
                        messages = messages,
                        isThinking = isThinking,
                        onSendMessage = { viewModel.sendTutorMessage(it) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MaterialInputContent(
    aiState: AiProcessingState,
    onProcess: (String, String, MaterialType, NotesDepth, Int, QuestionDifficulty) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MaterialType.DOCUMENT_PDF) }
    var selectedDepth by remember { mutableStateOf(NotesDepth.DETAILED_NOTES) }
    var questionCount by remember { mutableIntStateOf(5) }
    var selectedDifficulty by remember { mutableStateOf(QuestionDifficulty.HOTS_HIGHER_ORDER) }

    // Quick Presets
    val presets = listOf(
        "Quantum Physics & Tunneling" to "Schrödinger wave equation, barrier tunneling, Heisenberg uncertainty and quantum computing qubits.",
        "Calculus: Multi-Variable Vectors" to "Partial derivatives, gradient vectors, Jacobian matrix, Green's Theorem and line integrals.",
        "Cellular Respiration & Krebs" to "Glycolysis, pyruvate oxidation, citric acid cycle, electron transport chain and ATP synthesis.",
        "Algorithms: Dynamic Programming" to "Optimal substructure, memoization, Bellman-Ford, Knapsack problem and asymptotic complexity."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryIndigo),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Universal Material Analyzer",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Extracts multi-page notes, mind maps, and HOTS questions from any source",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Material Type Selector
        Text(
            text = "1. Source Modality",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = CyanGlow
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MaterialType.entries.forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { selectedType = type },
                    label = { Text("${type.icon} ${type.label}") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryIndigo,
                        selectedLabelColor = Color.White,
                        containerColor = CardNavy,
                        labelColor = Color(0xFFCBD5E1)
                    )
                )
            }
        }

        // Material Title & Body Inputs
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Chapter / Material Title") },
            placeholder = { Text("e.g. Chapter 4: Special Relativity & Invariants") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyanGlow,
                unfocusedBorderColor = BorderNavy,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("material_title_input")
        )

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Material Content / Textbook Excerpt / Video Link / Transcript") },
            placeholder = {
                Text(
                    when (selectedType) {
                        MaterialType.DOCUMENT_PDF -> "Paste chapter text, book excerpt, or document content here..."
                        MaterialType.VIDEO_LINK -> "Paste YouTube/lecture link, notes or transcript..."
                        MaterialType.IMAGE_PHOTO -> "Describe problem diagram, equations, or photo notes..."
                        MaterialType.TEXT_PROMPT -> "Enter key learning objectives, topics, or theories..."
                    }
                )
            },
            minLines = 4,
            maxLines = 10,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyanGlow,
                unfocusedBorderColor = BorderNavy,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("material_content_input")
        )

        // Presets quick-fill
        Text(
            text = "⚡ Quick Sample Topics (Tap to test):",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            presets.forEach { (pTitle, pContent) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardNavy)
                        .clickable {
                            title = pTitle
                            content = pContent
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(text = pTitle, fontSize = 11.sp, color = Color(0xFF38BDF8))
                }
            }
        }

        // Configuration: Notes Depth
        Text(
            text = "2. Notes Depth & Page Count",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = CyanGlow
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NotesDepth.entries.forEach { depth ->
                val isSelected = selectedDepth == depth
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) PrimaryIndigo else CardNavy)
                        .border(1.dp, if (isSelected) CyanGlow else BorderNavy, RoundedCornerShape(10.dp))
                        .clickable { selectedDepth = depth }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text(
                            text = "${depth.label} (${depth.pageCountEstimate} Pgs)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = depth.description,
                            color = if (isSelected) Color(0xFFE0E7FF) else Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Configuration: Questions Difficulty
        Text(
            text = "3. Question Cognitive Level",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = CyanGlow
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            QuestionDifficulty.entries.forEach { diff ->
                val isSelected = selectedDifficulty == diff
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(diff.badgeColorHex).copy(alpha = 0.25f) else CardNavy)
                        .border(1.dp, if (isSelected) Color(diff.badgeColorHex) else BorderNavy, RoundedCornerShape(8.dp))
                        .clickable { selectedDifficulty = diff }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = diff.label,
                        color = if (isSelected) Color(diff.badgeColorHex) else Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Questions Count
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Questions Count: $questionCount",
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            listOf(3, 5, 8, 10).forEach { cnt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (questionCount == cnt) PrimaryIndigo else CardNavy)
                        .clickable { questionCount = cnt }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("$cnt Qs", fontSize = 11.sp, color = Color.White)
                }
            }
        }

        // Processing state or action button
        when (aiState) {
            is AiProcessingState.Processing -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(color = CyanGlow, modifier = Modifier.size(32.dp))
                        Text(
                            text = aiState.statusMessage,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        LinearProgressIndicator(
                            color = PrimaryIndigo,
                            trackColor = BorderNavy,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            is AiProcessingState.Error -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = RoseError.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = RoseError)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = aiState.message, color = Color.White, fontSize = 12.sp)
                    }
                }
            }
            else -> {}
        }

        Button(
            onClick = {
                onProcess(
                    title.ifBlank { "Core Study Topic" },
                    content.ifBlank { "Fundamental principles and higher thinking analysis." },
                    selectedType,
                    selectedDepth,
                    questionCount,
                    selectedDifficulty
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("generate_studypack_button")
        ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Generate Notes, Mind Map & Questions",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun NotesReaderContent(
    note: StudyNote,
    onGenerateFlashcards: () -> Unit,
    onExportHtml: () -> Unit
) {
    var selectedPageNum by remember { mutableIntStateOf(1) }
    val currentPage = note.pages.find { it.pageNumber == selectedPageNum } ?: note.pages.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with page selector & quick actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    text = "Source: ${note.sourceMaterialTitle} • ${note.pages.size} Pages",
                    fontSize = 11.sp,
                    color = CyanGlow
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = onGenerateFlashcards,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("generate_flashcards_button")
                ) {
                    Icon(imageVector = Icons.Default.Style, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyanGlow)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Deck", fontSize = 11.sp, color = CyanGlow)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Page Number Chips Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            note.pages.forEach { page ->
                val isSelected = page.pageNumber == selectedPageNum
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) PrimaryIndigo else CardNavy)
                        .border(1.dp, if (isSelected) CyanGlow else BorderNavy, RoundedCornerShape(8.dp))
                        .clickable { selectedPageNum = page.pageNumber }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Page ${page.pageNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Page Content Body
        currentPage?.let { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = page.sectionTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PrimaryIndigoLight
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = page.contentMarkdown,
                            fontSize = 13.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 20.sp
                        )
                    }
                }

                // Key Takeaways Box
                if (page.keyPoints.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EmeraldSuccess.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EmeraldSuccess.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Key Principles & Takeaways", fontWeight = FontWeight.Bold, color = EmeraldSuccess, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            page.keyPoints.forEach { pt ->
                                Text("• $pt", color = Color(0xFFF1F5F9), fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(bottom = 4.dp))
                            }
                        }
                    }
                }

                // Formulas Box
                if (page.formulasOrQuotes.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = TertiaryAmber.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, TertiaryAmber.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Functions, contentDescription = null, tint = TertiaryAmber, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Formulas & Axioms", fontWeight = FontWeight.Bold, color = TertiaryAmber, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            page.formulasOrQuotes.forEach { f ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DeepNavy)
                                        .padding(8.dp)
                                ) {
                                    Text(text = f, fontFamily = FontFamily.Monospace, color = CyanGlow, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MindMapVisualizerContent(rootNode: MindMapNode) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = CyanGlow)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Interactive Concept Mind Map", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Tap branches to expand or collapse sub-concepts", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }
            }
        }

        // Render recursive mind map nodes
        MindMapTreeNodeView(node = rootNode, level = 0)
    }
}

@Composable
fun MindMapTreeNodeView(node: MindMapNode, level: Int) {
    var isExpanded by remember { mutableStateOf(node.isExpanded) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (level * 16).dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = when (level) {
                    0 -> PrimaryIndigoDark
                    1 -> CardNavy
                    else -> DeepNavy
                }
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = when (level) {
                        0 -> CyanGlow
                        1 -> PrimaryIndigo
                        else -> BorderNavy
                    },
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { isExpanded = !isExpanded }
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (node.children.isNotEmpty()) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = CyanGlow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CyanGlow)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = node.title,
                        fontWeight = if (level == 0) FontWeight.Bold else FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = if (level == 0) 15.sp else 13.sp
                    )
                    if (!node.description.isNullOrBlank()) {
                        Text(
                            text = node.description,
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                if (node.children.isNotEmpty()) {
                    Text(
                        text = "${node.children.size} sub",
                        fontSize = 10.sp,
                        color = CyanGlow,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                node.children.forEach { child ->
                    MindMapTreeNodeView(node = child, level = level + 1)
                }
            }
        }
    }
}

@Composable
fun QuizPracticeContent(
    questions: List<QuizQuestion>,
    userAnswers: Map<Int, Int>,
    onSelectAnswer: (Int, Int) -> Unit
) {
    val totalScore = userAnswers.count { (qIdx, optIdx) ->
        qIdx < questions.size && questions[qIdx].correctOptionIndex == optIdx
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Adaptive Practice Quiz", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${questions.size} Questions Generated", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(EmeraldSuccess.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Score: $totalScore / ${questions.size}",
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        itemsIndexed(questions) { qIdx, question ->
            val selectedOption = userAnswers[qIdx]
            val isAnswered = selectedOption != null

            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Question ${qIdx + 1}",
                            fontWeight = FontWeight.Bold,
                            color = CyanGlow,
                            fontSize = 13.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(question.difficulty.badgeColorHex).copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = question.difficulty.label,
                                color = Color(question.difficulty.badgeColorHex),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = question.questionText,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Options
                    question.options.forEachIndexed { optIdx, optText ->
                        val isThisSelected = selectedOption == optIdx
                        val isCorrect = optIdx == question.correctOptionIndex

                        val bgColor = when {
                            !isAnswered -> DeepNavy
                            isCorrect -> EmeraldSuccess.copy(alpha = 0.25f)
                            isThisSelected -> RoseError.copy(alpha = 0.25f)
                            else -> DeepNavy
                        }

                        val borderColor = when {
                            !isAnswered -> BorderNavy
                            isCorrect -> EmeraldSuccess
                            isThisSelected -> RoseError
                            else -> BorderNavy
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(bgColor)
                                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                                .clickable(enabled = !isAnswered) { onSelectAnswer(qIdx, optIdx) }
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${('A' + optIdx)}.",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorrect && isAnswered) EmeraldSuccess else CyanGlow,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = optText,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // Explanation Box
                    if (isAnswered) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DeepNavy)
                                .border(1.dp, CyanGlow.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("💡 Explanation:", fontWeight = FontWeight.Bold, color = CyanGlow, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = question.explanation, color = Color(0xFFCBD5E1), fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProblemSolverContent(
    solution: ProblemSolution?,
    isLoading: Boolean,
    onSolve: (String) -> Unit
) {
    var problemInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = TertiaryAmber)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Step-by-Step Problem Solver", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Solves math, physics, chemistry, and CS questions with detailed derivations", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = problemInput,
                    onValueChange = { problemInput = it },
                    label = { Text("Enter any problem statement or equation") },
                    placeholder = { Text("e.g. Find the projectile range given launch angle 45 deg and v0 = 20 m/s") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        unfocusedBorderColor = BorderNavy,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        if (problemInput.isNotBlank()) onSolve(problemInput)
                    },
                    enabled = !isLoading && problemInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                    } else {
                        Text("Solve Problem Step-by-Step", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Show Solution Steps
        solution?.let { sol ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Method: ${sol.methodUsed}",
                        fontWeight = FontWeight.Bold,
                        color = CyanGlow,
                        fontSize = 14.sp
                    )

                    sol.steps.forEach { step ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DeepNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BorderNavy, RoundedCornerShape(8.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Step ${step.stepNumber}: ${step.stepTitle}",
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryIndigoLight,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = step.explanation,
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                                if (!step.mathematicalFormula.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CardNavy)
                                            .padding(6.dp)
                                    ) {
                                        Text(text = step.mathematicalFormula, fontFamily = FontFamily.Monospace, color = CyanGlow, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Final Answer Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EmeraldSuccess.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EmeraldSuccess, RoundedCornerShape(10.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("✅ Verified Final Answer", fontWeight = FontWeight.Bold, color = EmeraldSuccess, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = sol.finalAnswer, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "💡 ${sol.verificationOrTip}", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TutorChatContent(
    messages: List<com.example.data.models.TutorChatMessage>,
    isThinking: Boolean,
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    val quickChips = listOf(
        "Explain step-by-step",
        "Give me a hint",
        "What formula should I use?",
        "Give me a challenge problem"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Chat History List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp,
                                    bottomStart = if (isUser) 14.dp else 2.dp,
                                    bottomEnd = if (isUser) 2.dp else 14.dp
                                )
                            )
                            .background(if (isUser) PrimaryIndigo else CardNavy)
                            .border(1.dp, if (isUser) CyanGlow else BorderNavy, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = if (isUser) "You" else "Scholar AI Tutor",
                                fontWeight = FontWeight.Bold,
                                color = if (isUser) Color.White else CyanGlow,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.text,
                                color = Color.White,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            if (isThinking) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = CyanGlow, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tutor is thinking...", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                }
            }
        }

        // Quick Suggestion Chips
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            quickChips.forEach { chip ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardNavy)
                        .clickable { onSendMessage(chip) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = chip, fontSize = 11.sp, color = CyanGlow)
                }
            }
        }

        // Input Field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask your personal AI tutor...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanGlow,
                    unfocusedBorderColor = BorderNavy,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PrimaryIndigo)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

@Composable
fun EmptyStatePrompt(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = message, color = Color(0xFF94A3B8), fontSize = 14.sp)
        }
    }
}
