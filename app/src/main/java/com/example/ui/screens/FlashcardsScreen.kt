package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Flashcard
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
import com.example.viewmodel.AiTab
import com.example.viewmodel.ScreenDestination
import com.example.viewmodel.StudyViewModel

@Composable
fun FlashcardsScreen(viewModel: StudyViewModel) {
    val flashcards by viewModel.flashcards.collectAsState()
    val savedNotes by viewModel.savedNotes.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Flashcards Practice, 1: Saved Notes Library

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightDark)
    ) {
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            containerColor = DeepNavy,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                if (activeTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = CyanGlow
                    )
                }
            }
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("🗂️ Active Flashcards (${flashcards.size})", fontWeight = FontWeight.Bold, color = Color.White) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("📚 Saved Notes Library (${savedNotes.size})", fontWeight = FontWeight.Bold, color = Color.White) }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (activeTab == 0) {
                FlashcardsPracticeView(
                    flashcards = flashcards,
                    onToggleMastered = { viewModel.toggleFlashcardMastered(it) }
                )
            } else {
                SavedNotesLibraryView(
                    notes = savedNotes,
                    onOpenNote = { note ->
                        // Switch to AI Tutor screen and set note reader
                        viewModel.setScreen(ScreenDestination.AI_TUTOR)
                        viewModel.setAiTab(AiTab.NOTES_READER)
                    },
                    onExportHtml = { viewModel.triggerHtmlExportModal() }
                )
            }
        }
    }
}

@Composable
fun FlashcardsPracticeView(
    flashcards: List<Flashcard>,
    onToggleMastered: (String) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "cardFlip"
    )

    if (flashcards.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No flashcards in your deck yet. Generate them instantly from any AI note!", color = Color(0xFF94A3B8), textAlign = TextAlign.Center)
        }
        return
    }

    val safeIndex = currentIndex.coerceIn(0, flashcards.size - 1)
    val card = flashcards[safeIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Deck: ${card.deckName}",
                fontWeight = FontWeight.Bold,
                color = CyanGlow,
                fontSize = 13.sp
            )
            Text(
                text = "${safeIndex + 1} of ${flashcards.size}",
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 12.sp
            )
        }

        // 3D Animated Flip Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isFlipped) DeepNavy else CardNavy
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .border(1.dp, if (card.isMastered) EmeraldSuccess else PrimaryIndigo, RoundedCornerShape(20.dp))
                .clickable { isFlipped = !isFlipped }
                .testTag("flashcard_flip_target")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Front vs Back content
                if (rotation <= 90f) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "PROMPT",
                            fontWeight = FontWeight.Bold,
                            color = PrimaryIndigoLight,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = card.frontPrompt,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.FlipCameraAndroid, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tap to reveal answer", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                    }
                } else {
                    // Back side (mirrored graphicsLayer)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.graphicsLayer { rotationY = 180f }
                    ) {
                        Text(
                            text = "ANSWER & CORE INSIGHT",
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = card.backAnswer,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            color = Color(0xFFF8FAFC),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }

        // Action Buttons: Needs Review / Mastered & Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = {
                    isFlipped = false
                    if (currentIndex > 0) currentIndex--
                },
                enabled = safeIndex > 0
            ) {
                Text("Previous")
            }

            Button(
                onClick = {
                    onToggleMastered(card.id)
                    isFlipped = false
                    if (safeIndex < flashcards.size - 1) currentIndex++
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (card.isMastered) EmeraldSuccess else PrimaryIndigo
                ),
                modifier = Modifier.testTag("mark_mastered_button")
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (card.isMastered) "Mastered" else "Mark Mastered (+10 XP)")
            }

            OutlinedButton(
                onClick = {
                    isFlipped = false
                    if (currentIndex < flashcards.size - 1) currentIndex++
                },
                enabled = safeIndex < flashcards.size - 1
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun SavedNotesLibraryView(
    notes: List<StudyNote>,
    onOpenNote: (StudyNote) -> Unit,
    onExportHtml: () -> Unit
) {
    if (notes.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No saved notes found yet. Analyze a topic to build your persistent knowledge library.", color = Color(0xFF94A3B8), textAlign = TextAlign.Center)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(notes) { note ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenNote(note) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = note.title,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryIndigo.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${note.pages.size} Pages",
                                color = CyanGlow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Source: ${note.sourceMaterialTitle} • ${note.materialType.label}",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Open in Reader →",
                            color = CyanGlow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
