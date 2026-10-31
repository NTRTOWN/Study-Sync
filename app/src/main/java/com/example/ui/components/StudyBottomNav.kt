package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryIndigoDark
import com.example.viewmodel.ScreenDestination

@Composable
fun StudyBottomNav(
    currentScreen: ScreenDestination,
    onNavigate: (ScreenDestination) -> Unit
) {
    NavigationBar(
        containerColor = DeepNavy,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("study_bottom_navigation")
    ) {
        NavigationBarItem(
            selected = currentScreen == ScreenDestination.AI_TUTOR,
            onClick = { onNavigate(ScreenDestination.AI_TUTOR) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == ScreenDestination.AI_TUTOR) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                    contentDescription = "AI Tutor"
                )
            },
            label = {
                Text(
                    text = "AI Tutor",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == ScreenDestination.AI_TUTOR) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = CyanGlow,
                indicatorColor = PrimaryIndigoDark,
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            )
        )

        NavigationBarItem(
            selected = currentScreen == ScreenDestination.GLOBAL_COMMUNITY,
            onClick = { onNavigate(ScreenDestination.GLOBAL_COMMUNITY) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == ScreenDestination.GLOBAL_COMMUNITY) Icons.Filled.Forum else Icons.Outlined.Forum,
                    contentDescription = "Community Lounge"
                )
            },
            label = {
                Text(
                    text = "Lounge",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == ScreenDestination.GLOBAL_COMMUNITY) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = CyanGlow,
                indicatorColor = PrimaryIndigoDark,
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            )
        )

        NavigationBarItem(
            selected = currentScreen == ScreenDestination.FOCUS_TIMER,
            onClick = { onNavigate(ScreenDestination.FOCUS_TIMER) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == ScreenDestination.FOCUS_TIMER) Icons.Filled.Timer else Icons.Outlined.Timer,
                    contentDescription = "Focus Timer"
                )
            },
            label = {
                Text(
                    text = "Timer",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == ScreenDestination.FOCUS_TIMER) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = CyanGlow,
                indicatorColor = PrimaryIndigoDark,
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            )
        )

        NavigationBarItem(
            selected = currentScreen == ScreenDestination.FLASHCARDS_LIBRARY,
            onClick = { onNavigate(ScreenDestination.FLASHCARDS_LIBRARY) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == ScreenDestination.FLASHCARDS_LIBRARY) Icons.Filled.Style else Icons.Outlined.Style,
                    contentDescription = "Flashcards & Notes"
                )
            },
            label = {
                Text(
                    text = "Cards",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == ScreenDestination.FLASHCARDS_LIBRARY) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = CyanGlow,
                indicatorColor = PrimaryIndigoDark,
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            )
        )

        NavigationBarItem(
            selected = currentScreen == ScreenDestination.STUDENT_TOOLKIT,
            onClick = { onNavigate(ScreenDestination.STUDENT_TOOLKIT) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == ScreenDestination.STUDENT_TOOLKIT) Icons.Filled.Handyman else Icons.Outlined.Handyman,
                    contentDescription = "Student Toolkit"
                )
            },
            label = {
                Text(
                    text = "Toolkit",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == ScreenDestination.STUDENT_TOOLKIT) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = CyanGlow,
                indicatorColor = PrimaryIndigoDark,
                unselectedIconColor = Color(0xFF94A3B8),
                unselectedTextColor = Color(0xFF94A3B8)
            )
        )
    }
}
