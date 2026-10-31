package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.CreateRoomDialog
import com.example.ui.components.ExportHtmlDialog
import com.example.ui.components.JoinRoomDialog
import com.example.ui.components.PermanentProfileDialog
import com.example.ui.components.StudyBottomNav
import com.example.ui.components.StudyTopBar
import com.example.ui.screens.AiTutorScreen
import com.example.ui.screens.ChatRoomsScreen
import com.example.ui.screens.FlashcardsScreen
import com.example.ui.screens.StudentToolkitScreen
import com.example.ui.screens.StudyTimerScreen
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.StudySphereTheme
import com.example.viewmodel.ScreenDestination
import com.example.viewmodel.StudyViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: StudyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StudySphereTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: StudyViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    // Dialogs
    val showProfileDialog by viewModel.showProfileDialog.collectAsState()
    val showCreateRoomDialog by viewModel.showCreateRoomDialog.collectAsState()
    val showJoinRoomDialog by viewModel.showJoinRoomDialog.collectAsState()
    val exportHtmlContent by viewModel.showExportHtmlDialog.collectAsState()

    // BackHandler: if not on Home (AI_TUTOR), navigate back to AI_TUTOR
    BackHandler(enabled = currentScreen != ScreenDestination.AI_TUTOR) {
        viewModel.setScreen(ScreenDestination.AI_TUTOR)
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            StudyTopBar(
                userProfile = userProfile,
                onProfileClick = { viewModel.setShowProfileDialog(true) },
                onExportHtmlClick = { viewModel.triggerHtmlExportModal() }
            )
        },
        bottomBar = {
            StudyBottomNav(
                currentScreen = currentScreen,
                onNavigate = { viewModel.setScreen(it) }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightDark)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                ScreenDestination.AI_TUTOR -> AiTutorScreen(viewModel = viewModel)
                ScreenDestination.GLOBAL_COMMUNITY -> ChatRoomsScreen(viewModel = viewModel)
                ScreenDestination.FOCUS_TIMER -> StudyTimerScreen(viewModel = viewModel)
                ScreenDestination.FLASHCARDS_LIBRARY -> FlashcardsScreen(viewModel = viewModel)
                ScreenDestination.STUDENT_TOOLKIT -> StudentToolkitScreen(viewModel = viewModel)
            }
        }
    }

    // Modal Dialogs
    if (showProfileDialog) {
        PermanentProfileDialog(
            userProfile = userProfile,
            onDismiss = { viewModel.setShowProfileDialog(false) },
            onSave = { username, avatar, flag, country, level, major, dailyGoal ->
                viewModel.updateProfile(username, avatar, flag, country, level, major, dailyGoal)
            }
        )
    }

    if (showCreateRoomDialog) {
        CreateRoomDialog(
            onDismiss = { viewModel.setShowCreateRoomDialog(false) },
            onCreate = { name, topic, isPrivate, pin ->
                viewModel.createRoom(name, topic, isPrivate, pin)
            }
        )
    }

    if (showJoinRoomDialog) {
        JoinRoomDialog(
            onDismiss = { viewModel.setShowJoinRoomDialog(false) },
            onJoin = { codeOrPin ->
                viewModel.joinRoomByCodeOrPin(codeOrPin)
            }
        )
    }

    exportHtmlContent?.let { html ->
        ExportHtmlDialog(
            htmlContent = html,
            onDismiss = { viewModel.dismissExportDialog() }
        )
    }
}
