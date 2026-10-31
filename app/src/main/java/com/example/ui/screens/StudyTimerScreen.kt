package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryIndigoDark
import com.example.ui.theme.PrimaryIndigoLight
import com.example.ui.theme.TertiaryAmber
import com.example.viewmodel.AmbientSound
import com.example.viewmodel.StudyViewModel
import com.example.viewmodel.TimerMode

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudyTimerScreen(viewModel: StudyViewModel) {
    val timerMode by viewModel.timerMode.collectAsState()
    val remainingSeconds by viewModel.timerRemainingSeconds.collectAsState()
    val isRunning by viewModel.isTimerRunning.collectAsState()
    val selectedSound by viewModel.selectedSound.collectAsState()
    val sessionLogs by viewModel.sessionLogs.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val totalSeconds = timerMode.defaultMinutes * 60
    val progressFraction = if (totalSeconds > 0) {
        remainingSeconds.toFloat() / totalSeconds.toFloat()
    } else 0f

    val animatedProgress by animateFloatAsState(targetValue = progressFraction, label = "timerProgress")

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Selector Row
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TimerMode.entries.forEach { mode ->
                val isSelected = timerMode == mode
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) PrimaryIndigo else CardNavy)
                        .border(1.dp, if (isSelected) CyanGlow else BorderNavy, RoundedCornerShape(20.dp))
                        .clickable { viewModel.setTimerMode(mode) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = mode.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Circular Timer Canvas
        Box(
            modifier = Modifier
                .size(240.dp)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val topLeft = Offset((size.width - 2 * radius) / 2, (size.height - 2 * radius) / 2)
                val arcSize = Size(radius * 2, radius * 2)

                // Background track circle
                drawArc(
                    color = Color(0xFF1E293B),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )

                // Progress active arc
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(PrimaryIndigo, CyanGlow, PrimaryIndigoLight, PrimaryIndigo)
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Central Digital Timer Text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeFormatted,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )
                Text(
                    text = if (isRunning) "Deep Focus Active" else "Ready to Focus",
                    fontSize = 12.sp,
                    color = if (isRunning) EmeraldSuccess else Color(0xFF94A3B8),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Timer Play / Pause / Reset Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CardNavy)
                    .border(1.dp, BorderNavy, CircleShape)
                    .testTag("reset_timer_button")
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
            }

            Button(
                onClick = { viewModel.toggleTimer() },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                shape = CircleShape,
                modifier = Modifier
                    .size(70.dp)
                    .testTag("toggle_timer_button")
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pause" else "Start",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // Ambient Focus Audio Synthesizer Box
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.GraphicEq, contentDescription = null, tint = CyanGlow)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ambient Focus Sound Generator",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AmbientSound.entries.forEach { sound ->
                        val isSelected = selectedSound == sound
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryIndigoDark else DeepNavy)
                                .border(1.dp, if (isSelected) CyanGlow else BorderNavy, RoundedCornerShape(10.dp))
                                .clickable { viewModel.setAmbientSound(sound) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(sound.icon, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(sound.label, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }

        // Daily Study Statistics & Logs
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Today's Focus Log", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = "${userProfile.minutesStudiedToday}m / ${userProfile.dailyGoalMinutes}m goal",
                        color = CyanGlow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                sessionLogs.take(4).forEach { session ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = session.subject, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "${session.dateString} • ${session.mode}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldSuccess.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(text = "+${session.minutesStudied} min", color = EmeraldSuccess, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
