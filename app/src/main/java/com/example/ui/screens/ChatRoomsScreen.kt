package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ChatMessage
import com.example.data.models.StudyRoom
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
import com.example.viewmodel.StudyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatRoomsScreen(viewModel: StudyViewModel) {
    val channels by viewModel.channels.collectAsState()
    val channelMessages by viewModel.channelMessages.collectAsState()
    val studyRooms by viewModel.studyRooms.collectAsState()
    val roomMessages by viewModel.roomMessages.collectAsState()
    val selectedChannelId by viewModel.selectedChannelId.collectAsState()
    val selectedRoom by viewModel.selectedRoom.collectAsState()
    val currentPack by viewModel.currentStudyPack.collectAsState()

    var inputMsg by remember { mutableStateOf("") }
    var attachNote by remember { mutableStateOf(false) }

    val activeMessages: List<ChatMessage> = if (selectedRoom != null) {
        roomMessages[selectedRoom?.id].orEmpty()
    } else {
        channelMessages[selectedChannelId].orEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightDark)
    ) {
        // Top Section: Channels & Private Room Management Bar
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Rooms Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedRoom != null) "🔒 Private Study Lounge" else "🌐 Global Student Hub",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.setShowJoinRoomDialog(true) },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("join_room_button")
                        ) {
                            Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Join PIN", fontSize = 11.sp, color = CyanGlow)
                        }

                        Button(
                            onClick = { viewModel.setShowCreateRoomDialog(true) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("create_room_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Room", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Public Channels Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(channels) { ch ->
                        val isSelected = selectedRoom == null && selectedChannelId == ch.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) PrimaryIndigo else CardNavy)
                                .border(1.dp, if (isSelected) CyanGlow else BorderNavy, RoundedCornerShape(20.dp))
                                .clickable { viewModel.selectChannel(ch.id) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "${ch.icon} ${ch.name}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(EmeraldSuccess)
                                        .size(6.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${ch.onlineCount}", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Active Rooms Carousel
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(studyRooms) { room ->
                        val isSelected = selectedRoom?.id == room.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SecondaryCyan.copy(alpha = 0.25f) else CardNavy)
                                .border(1.dp, if (isSelected) SecondaryCyan else BorderNavy, RoundedCornerShape(12.dp))
                                .clickable { viewModel.selectRoom(room) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (room.isPrivate) Icons.Default.Lock else Icons.Default.MeetingRoom,
                                    contentDescription = null,
                                    tint = if (room.isPrivate) TertiaryAmber else SecondaryCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(text = room.name, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "${room.code} • ${room.activeMembersCount}/${room.maxMembers} students",
                                        fontSize = 9.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Room Banner if in a private room
        selectedRoom?.let { room ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryIndigoDark)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Room Code: ${room.code}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        if (room.pin != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "(PIN: ${room.pin})", color = TertiaryAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = "Exit Room",
                        color = CyanGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.selectChannel("general") }
                    )
                }
            }
        }

        // Live Message Feed
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(activeMessages) { msg ->
                MessageBubble(message = msg)
            }
        }

        // Attached Note Banner if active
        if (attachNote && currentPack != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardNavy)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AttachFile, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Attaching: ${currentPack?.note?.title}",
                            fontSize = 11.sp,
                            color = CyanGlow,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "Remove",
                        color = RoseError,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { attachNote = false }
                    )
                }
            }
        }

        // Input & Note Attachment Bar
        Surface(
            color = DeepNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Note Attachment Toggle Button
                IconButton(
                    onClick = { attachNote = !attachNote },
                    modifier = Modifier.testTag("attach_note_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach Current Note",
                        tint = if (attachNote) CyanGlow else Color(0xFF94A3B8)
                    )
                }

                OutlinedTextField(
                    value = inputMsg,
                    onValueChange = { inputMsg = it },
                    placeholder = {
                        Text(
                            if (selectedRoom != null) "Message ${selectedRoom?.name}..."
                            else "Share insights in #${channels.find { it.id == selectedChannelId }?.name ?: "channel"}..."
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        unfocusedBorderColor = BorderNavy,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field")
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        if (inputMsg.isNotBlank()) {
                            viewModel.sendCommunityMessage(inputMsg, attachCurrentNote = attachNote)
                            inputMsg = ""
                            attachNote = false
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(PrimaryIndigo)
                        .testTag("send_chat_message_button")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val timeFormatted = remember(message.timestamp) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date(message.timestamp))
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .clip(
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (message.isFromMe) 14.dp else 2.dp,
                        bottomEnd = if (message.isFromMe) 2.dp else 14.dp
                    )
                )
                .background(if (message.isFromMe) PrimaryIndigoDark else CardNavy)
                .border(1.dp, if (message.isFromMe) CyanGlow else BorderNavy, RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column {
                // Header (Sender info)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = message.senderAvatar, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = message.senderUsername,
                            fontWeight = FontWeight.Bold,
                            color = if (message.isFromMe) Color.White else CyanGlow,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = message.senderCountry, fontSize = 11.sp)
                    }
                    Text(text = timeFormatted, fontSize = 9.sp, color = Color(0xFF94A3B8))
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Message Text
                Text(
                    text = message.text,
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                // Attached Study Note Card
                if (!message.attachedNoteTitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DeepNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyanGlow.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📄 Shared AI Study Note", fontSize = 10.sp, color = CyanGlow, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = message.attachedNoteTitle,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            if (!message.attachedNoteSnippet.isNullOrBlank()) {
                                Text(
                                    text = message.attachedNoteSnippet,
                                    fontSize = 10.sp,
                                    color = Color(0xFFCBD5E1),
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
