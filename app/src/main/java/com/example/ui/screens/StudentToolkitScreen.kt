package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.models.AssignmentItem
import com.example.data.models.CitationStyle
import com.example.data.models.FormulaItem
import com.example.data.models.PriorityLevel
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
import com.example.ui.theme.TertiaryAmber
import com.example.viewmodel.StudyViewModel

data class CourseGrade(
    val id: String = java.util.UUID.randomUUID().toString(),
    var name: String,
    var gradePoints: Double,
    var credits: Double
)

@Composable
fun StudentToolkitScreen(viewModel: StudyViewModel) {
    var selectedToolTab by remember { mutableIntStateOf(0) }
    // 0: GPA Calculator, 1: Formula Cheatsheet, 2: Citation Generator, 3: Homework Planner, 4: Exam Countdown

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightDark)
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedToolTab,
            containerColor = DeepNavy,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                if (selectedToolTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedToolTab]),
                        color = CyanGlow
                    )
                }
            }
        ) {
            Tab(
                selected = selectedToolTab == 0,
                onClick = { selectedToolTab = 0 },
                text = { Text("📊 GPA Calculator", fontWeight = FontWeight.Bold, color = Color.White) }
            )
            Tab(
                selected = selectedToolTab == 1,
                onClick = { selectedToolTab = 1 },
                text = { Text("📐 Formula Vault", fontWeight = FontWeight.Bold, color = Color.White) }
            )
            Tab(
                selected = selectedToolTab == 2,
                onClick = { selectedToolTab = 2 },
                text = { Text("📖 Citation Builder", fontWeight = FontWeight.Bold, color = Color.White) }
            )
            Tab(
                selected = selectedToolTab == 3,
                onClick = { selectedToolTab = 3 },
                text = { Text("📝 Homework Tasks", fontWeight = FontWeight.Bold, color = Color.White) }
            )
            Tab(
                selected = selectedToolTab == 4,
                onClick = { selectedToolTab = 4 },
                text = { Text("⏳ Exam Countdown", fontWeight = FontWeight.Bold, color = Color.White) }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedToolTab) {
                0 -> GpaCalculatorTab()
                1 -> FormulaLibraryTab(viewModel.formulaLibrary)
                2 -> CitationGeneratorTab(viewModel)
                3 -> HomeworkPlannerTab(viewModel)
                4 -> ExamCountdownTab()
            }
        }
    }
}

@Composable
fun GpaCalculatorTab() {
    val courses = remember {
        mutableStateListOf(
            CourseGrade(name = "Multivariable Calculus", gradePoints = 4.0, credits = 4.0),
            CourseGrade(name = "Data Structures & Algorithms", gradePoints = 4.0, credits = 4.0),
            CourseGrade(name = "University Physics II", gradePoints = 3.7, credits = 4.0),
            CourseGrade(name = "Academic Writing & Ethics", gradePoints = 4.0, credits = 3.0)
        )
    }

    var newCourseName by remember { mutableStateOf("") }
    var newGradePoints by remember { mutableDoubleStateOf(4.0) }
    var newCredits by remember { mutableDoubleStateOf(3.0) }

    val totalPoints = courses.sumOf { it.gradePoints * it.credits }
    val totalCredits = courses.sumOf { it.credits }
    val gpa = if (totalCredits > 0) totalPoints / totalCredits else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // GPA Result Card
        Card(
            colors = CardDefaults.cardColors(containerColor = PrimaryIndigoDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Cumulative Semester GPA", color = CyanGlow, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format("%.2f", gpa),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${courses.size} Courses • $totalCredits Credit Hours",
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp
                )
            }
        }

        // Add Course Form
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Add Course & Grade", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)

                OutlinedTextField(
                    value = newCourseName,
                    onValueChange = { newCourseName = it },
                    label = { Text("Course Name") },
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Grade selector
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Grade (A=4.0, B=3.0, C=2.0)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(4.0 to "A", 3.7 to "A-", 3.3 to "B+", 3.0 to "B").forEach { (pts, label) ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (newGradePoints == pts) PrimaryIndigo else DeepNavy)
                                        .clickable { newGradePoints = pts }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Credits selector
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Credits", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(1.0, 3.0, 4.0).forEach { cr ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (newCredits == cr) PrimaryIndigo else DeepNavy)
                                        .clickable { newCredits = cr }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text("${cr.toInt()}cr", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        if (newCourseName.isNotBlank()) {
                            courses.add(CourseGrade(name = newCourseName, gradePoints = newGradePoints, credits = newCredits))
                            newCourseName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Course")
                }
            }
        }

        // Course List
        courses.forEachIndexed { idx, c ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = c.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text(text = "${c.credits} Credits • Grade Points: ${c.gradePoints}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                    IconButton(onClick = { courses.removeAt(idx) }) {
                        Icon(imageVector = Icons.Default.Calculate, contentDescription = "Delete", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FormulaLibraryTab(formulas: List<FormulaItem>) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Calculus", "Physics", "Chemistry", "Computer Science")

    val filtered = formulas.filter { f ->
        (selectedCategory == "All" || f.category.equals(selectedCategory, ignoreCase = true)) &&
        (searchQuery.isBlank() || f.name.contains(searchQuery, true) || f.description.contains(searchQuery, true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search 100+ formulas, theorems & laws...") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyanGlow,
                unfocusedBorderColor = BorderNavy,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Categories Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) PrimaryIndigo else CardNavy)
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = cat, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = item.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PrimaryIndigoDark)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(text = item.category, fontSize = 10.sp, color = CyanGlow, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DeepNavy)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = item.formulaLatex,
                                fontFamily = FontFamily.Monospace,
                                color = CyanGlow,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = item.description, color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Variables: ${item.variableDefinitions}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitationGeneratorTab(viewModel: StudyViewModel) {
    val context = LocalContext.current
    var style by remember { mutableStateOf(CitationStyle.APA7) }
    var author by remember { mutableStateOf("Feynman, Richard P.") }
    var title by remember { mutableStateOf("The Feynman Lectures on Physics") }
    var year by remember { mutableStateOf("1964") }
    var publisher by remember { mutableStateOf("Addison-Wesley") }
    var url by remember { mutableStateOf("https://feynmanlectures.caltech.edu") }

    val formattedCitation = viewModel.repository.generateCitation(style, author, title, year, publisher, url)

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
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Academic Citation Builder", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)

                // Style selector
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CitationStyle.entries.forEach { st ->
                        val isSelected = style == st
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryIndigo else DeepNavy)
                                .clickable { style = st }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(text = st.name, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Author(s)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title of Paper / Book") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("Year") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = publisher, onValueChange = { publisher = it }, label = { Text("Publisher") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("URL / DOI (Optional)") }, modifier = Modifier.fillMaxWidth())
            }
        }

        // Result Card
        Card(
            colors = CardDefaults.cardColors(containerColor = PrimaryIndigoDark),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Formatted ${style.label}", color = CyanGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Citation", formattedCitation)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Citation copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formattedCitation,
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
fun HomeworkPlannerTab(viewModel: StudyViewModel) {
    val assignments by viewModel.assignments.collectAsState()
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("Tomorrow") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Quick add row
        Card(
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Add Assignment / Homework", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task") }, modifier = Modifier.weight(1.5f))
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") }, modifier = Modifier.weight(1f))
                }
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addAssignment(title, subject.ifBlank { "General" }, dueDate, PriorityLevel.HIGH, "")
                            title = ""
                            subject = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add to Study Schedule (+20 XP)")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(assignments) { task ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.toggleAssignmentCompleted(task.id) }) {
                            Icon(
                                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (task.isCompleted) EmeraldSuccess else Color(0xFF94A3B8)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontWeight = FontWeight.Bold,
                                color = if (task.isCompleted) Color(0xFF94A3B8) else Color.White,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${task.subject} • Due: ${task.dueDate}",
                                color = Color(task.priority.colorHex),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExamCountdownTab() {
    val exams = listOf(
        Triple("Calculus & Mechanics Finals", "12 Days", "Nov 10, 2026"),
        Triple("Algorithms & Machine Learning Sprint", "24 Days", "Nov 22, 2026"),
        Triple("Organic Chemistry Lab Assessment", "36 Days", "Dec 04, 2026")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.HourglassBottom, contentDescription = null, tint = TertiaryAmber)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Exam Countdown & Milestone Tracker", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

        exams.forEach { (name, days, date) ->
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
                        Text(text = name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Text(text = "Target Date: $date", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryIndigo)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = days, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
