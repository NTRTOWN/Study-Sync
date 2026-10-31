package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.EducationLevel
import com.example.data.models.UserProfile
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryIndigoLight
import com.example.ui.theme.SecondaryCyan

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PermanentProfileDialog(
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (username: String, avatar: String, flag: String, country: String, level: EducationLevel, major: String, dailyGoal: Int) -> Unit
) {
    var username by remember { mutableStateOf(userProfile.username) }
    var selectedAvatar by remember { mutableStateOf(userProfile.avatarEmoji) }
    var selectedFlag by remember { mutableStateOf(userProfile.countryFlag) }
    var selectedCountry by remember { mutableStateOf(userProfile.countryName) }
    var selectedLevel by remember { mutableStateOf(userProfile.educationLevel) }
    var major by remember { mutableStateOf(userProfile.majorOrFocus) }
    var dailyGoalMinutes by remember { mutableIntStateOf(userProfile.dailyGoalMinutes) }
    var isExpandedLevel by remember { mutableStateOf(false) }

    val avatars = listOf("🎓", "🔬", "💻", "📚", "👩‍🔬", "👨‍💻", "🧬", "🔭", "📐", "⚡", "🩺", "🎨")
    val flags = listOf(
        "🌐" to "Global",
        "🇺🇸" to "United States",
        "🇮🇳" to "India",
        "🇬🇧" to "United Kingdom",
        "🇨🇦" to "Canada",
        "🇩🇪" to "Germany",
        "🇯🇵" to "Japan",
        "🇦🇺" to "Australia",
        "🇫🇷" to "France",
        "🇧🇷" to "Brazil"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepNavy,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = CyanGlow
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Permanent Student Profile",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Your permanent identity in global study rooms and peer chat:",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )

                // Username input
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Permanent Username") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        unfocusedBorderColor = BorderNavy,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input_field")
                )

                // Avatar Picker
                Text(
                    text = "Select Avatar Emoji",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFCBD5E1)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    avatars.forEach { emoji ->
                        val isSelected = selectedAvatar == emoji
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) PrimaryIndigo else CardNavy)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) CyanGlow else BorderNavy,
                                    shape = CircleShape
                                )
                                .clickable { selectedAvatar = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }

                // Country Flag
                Text(
                    text = "Country & Region",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFCBD5E1)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    flags.forEach { (flag, country) ->
                        val isSelected = selectedFlag == flag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryIndigo else CardNavy)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) CyanGlow else BorderNavy,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedFlag = flag
                                    selectedCountry = country
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(text = "$flag $country", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }

                // Education Level Dropdown
                ExposedDropdownMenuBox(
                    expanded = isExpandedLevel,
                    onExpandedChange = { isExpandedLevel = !isExpandedLevel }
                ) {
                    OutlinedTextField(
                        value = selectedLevel.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Education Level") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedLevel) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanGlow,
                            unfocusedBorderColor = BorderNavy,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isExpandedLevel,
                        onDismissRequest = { isExpandedLevel = false },
                        modifier = Modifier.background(CardNavy)
                    ) {
                        EducationLevel.entries.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level.label, color = Color.White) },
                                onClick = {
                                    selectedLevel = level
                                    isExpandedLevel = false
                                }
                            )
                        }
                    }
                }

                // Major / Focus field
                OutlinedTextField(
                    value = major,
                    onValueChange = { major = it },
                    label = { Text("Major / Focus Subject") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        unfocusedBorderColor = BorderNavy,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Daily Study Goal
                Column {
                    Text(
                        text = "Daily Study Target: $dailyGoalMinutes min (${dailyGoalMinutes / 60}h ${dailyGoalMinutes % 60}m)",
                        fontSize = 13.sp,
                        color = CyanGlow,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        listOf(30, 60, 120, 180).forEach { mins ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (dailyGoalMinutes == mins) PrimaryIndigo else CardNavy)
                                    .clickable { dailyGoalMinutes = mins }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("${mins}m", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(username, selectedAvatar, selectedFlag, selectedCountry, selectedLevel, major, dailyGoalMinutes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text("Save Profile", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        }
    )
}

@Composable
fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, topic: String, isPrivate: Boolean, pin: String?) -> Unit
) {
    var roomName by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepNavy,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MeetingRoom,
                    contentDescription = null,
                    tint = SecondaryCyan
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Study Room", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Launch a public or private study room for real-time focus & note sharing.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )

                OutlinedTextField(
                    value = roomName,
                    onValueChange = { roomName = it },
                    label = { Text("Room Name (e.g. Physics Sprint)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        unfocusedBorderColor = BorderNavy,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Study Subject / Topic") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        unfocusedBorderColor = BorderNavy,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Private Room",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Require 4-digit PIN to enter",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Switch(
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryIndigo
                        )
                    )
                }

                if (isPrivate) {
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { if (it.length <= 4) pin = it },
                        label = { Text("4-Digit Access PIN") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanGlow,
                            unfocusedBorderColor = BorderNavy,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (roomName.isNotBlank() && topic.isNotBlank()) {
                        onCreate(roomName, topic, isPrivate, if (isPrivate) pin else null)
                    }
                },
                enabled = roomName.isNotBlank() && topic.isNotBlank() && (!isPrivate || pin.length >= 4),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
            ) {
                Text("Create Room", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        }
    )
}

@Composable
fun JoinRoomDialog(
    onDismiss: () -> Unit,
    onJoin: (codeOrPin: String) -> Boolean
) {
    var codeOrPin by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepNavy,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = CyanGlow
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Join Study Room", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Enter Room Code (e.g. MATH-202) or Private PIN provided by the host:",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )

                OutlinedTextField(
                    value = codeOrPin,
                    onValueChange = {
                        codeOrPin = it
                        errorText = null
                    },
                    label = { Text("Room Code or PIN") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        unfocusedBorderColor = BorderNavy,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorText != null) {
                    Text(
                        text = errorText ?: "",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val success = onJoin(codeOrPin)
                    if (!success) {
                        errorText = "No active room found with this code or PIN."
                    }
                },
                enabled = codeOrPin.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
            ) {
                Text("Enter Room", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        }
    )
}

@Composable
fun ExportHtmlDialog(
    htmlContent: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepNavy,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Export Study Website (HTML)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("StudySphere HTML", htmlContent)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Full HTML copied to clipboard! Paste into any browser or file.", Toast.LENGTH_LONG).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy HTML",
                        tint = CyanGlow
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                Text(
                    text = "Working standalone HTML code for mobile phone, laptop, PC. Includes interactive tabs, multi-page notes, hierarchical mind map, and practice quiz with instant grading!",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardNavy)
                        .border(1.dp, BorderNavy, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = htmlContent,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color(0xFF38BDF8)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("StudySphere HTML", htmlContent)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "HTML Copied! Open in any browser.", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
            ) {
                Text("Copy & Close", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF94A3B8))
            }
        }
    )
}
