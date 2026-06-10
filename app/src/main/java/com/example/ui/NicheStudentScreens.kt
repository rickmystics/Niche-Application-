package com.example.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import java.util.Calendar

// --- MAIN WRAPPER SCREEN FOR STUDENTS ---

@Composable
fun StudentMainContainer(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentSubRoute by viewModel.currentRoute.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            // Persistent accessible Bottom Navigation (Themed Sleek White/Blue)
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = Color.White,
                tonalElevation = 0.dp // Elegant flat top-bordered strip
            ) {
                val navColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF2563EB),
                    selectedTextColor = Color(0xFF2563EB),
                    indicatorColor = Color(0xFFEFF6FF), // bg-blue-50
                    unselectedIconColor = Color(0xFF64748B),
                    unselectedTextColor = Color(0xFF94A3B8)
                )

                NavigationBarItem(
                    selected = currentSubRoute == "student_dashboard",
                    onClick = { viewModel.navigateTo("student_dashboard") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = navColors,
                    modifier = Modifier.testTag("nav_home_tab")
                )
                NavigationBarItem(
                    selected = currentSubRoute == "subjects_grid",
                    onClick = { viewModel.navigateTo("subjects_grid") },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Courses") },
                    label = { Text("Courses", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = navColors,
                    modifier = Modifier.testTag("nav_courses_tab")
                )
                NavigationBarItem(
                    selected = currentSubRoute == "community_room",
                    onClick = { viewModel.navigateTo("community_room") },
                    icon = { Icon(Icons.Default.Forum, contentDescription = "Community") },
                    label = { Text("Community", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = navColors,
                    modifier = Modifier.testTag("nav_community_tab")
                )
                NavigationBarItem(
                    selected = currentSubRoute == "downloads_screen",
                    onClick = { viewModel.navigateTo("downloads_screen") },
                    icon = { Icon(Icons.Default.Download, contentDescription = "Downloads") },
                    label = { Text("Downloads", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = navColors,
                    modifier = Modifier.testTag("nav_downloads_tab")
                )
                NavigationBarItem(
                    selected = currentSubRoute == "student_profile",
                    onClick = { viewModel.navigateTo("student_profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = navColors,
                    modifier = Modifier.testTag("nav_profile_tab")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentSubRoute) {
                "student_dashboard" -> StudentDashboard(viewModel)
                "subjects_grid" -> SubjectsGridScreen(viewModel)
                "community_room" -> CommunityRoomScreen(viewModel)
                "downloads_screen" -> DownloadsScreen(viewModel)
                "student_profile" -> StudentProfileScreen(viewModel)

                // Sub-Screen details handled here
                "subject_detail" -> SubjectDetailScreen(viewModel)
                "chapter_detail" -> ChapterDetailScreen(viewModel)
                "video_player" -> VideoLearningScreen(viewModel)
                "notes_viewer" -> NotesReaderScreen(viewModel)
                "quiz_screen" -> QuizScreenLayout(viewModel)
                "doubts_screen" -> DoubtSolvingScreen(viewModel)
            }
        }
    }
}

// --- 1. STUDENT HOMEPAGE ---

@Composable
fun StudentDashboard(viewModel: MainViewModel) {
    val stats by viewModel.studentStats.collectAsStateWithLifecycle()
    val search by viewModel.searchQuery.collectAsStateWithLifecycle()
    val defaultSubjects = viewModel.subjectsList

    // Hourly greeting calculation
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp), // Spacious elegant margins
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Welcoming Title Section (Sleek layout)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$greeting,",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B) // Slate-500
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${viewModel.userName.value} 👋",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A) // Slate-900
                )
            }

            // High-End active bell or Clear Doubt call-to-action button
            Button(
                onClick = { viewModel.navigateTo("doubts_screen") },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp), // Soft sleek shape
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                modifier = Modifier.testTag("home_ask_doubt_btn")
            ) {
                Text(
                    text = "💬 Solve Doubt",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Elegant Search Bar with slate borders and soft shadow
        OutlinedTextField(
            value = search,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { Text("Search lectures, notes, or topics...", color = Color(0xFF94A3B8), fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dashboard_search_input"),
            leadingIcon = { Text("🔍", modifier = Modifier.padding(start = 12.dp)) },
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFFCBD5E1), // slate-300
                unfocusedBorderColor = Color(0xFFE2E8F0), // slate-200
                focusedLabelColor = Color(0xFF0F172A),
                unfocusedLabelColor = Color(0xFF64748B)
            ),
            singleLine = true
        )

        // Continue Learning Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Continue Learning",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "View all",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2563EB),
                modifier = Modifier.clickable { /* action */ }
            )
        }

        // Horizontally scrolling list of elegant row card elements
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(defaultSubjects) { item ->
                val firstChap = item.chapters.firstOrNull()
                if (firstChap != null) {
                    Card(
                        modifier = Modifier
                            .width(280.dp)
                            .clickable {
                                viewModel.selectSubject(item)
                                viewModel.selectChapter(firstChap)
                            },
                        shape = RoundedCornerShape(24.dp), // Sleek 3xl rounded corner
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)) // Slate border-100
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Rounded-2xl white container for the icon
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(item.themeColor).copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = item.icon, fontSize = 28.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name.uppercase(),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    color = Color(item.themeColor),
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = firstChap.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1E293B),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { stats?.completionPercent?.div(100f) ?: 0.65f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = Color(item.themeColor),
                                    trackColor = Color(0xFFF1F5F9)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Upcoming Mock Examinations (Exquisite Dark Slate Theme Card)
        NicheCard(
            backgroundColor = Color(0xFF1E293B), // Navy/Slate-800 theme representation
            borderColor = Color(0xFF334155),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "MOCK TEST",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        color = Color(0xFF60A5FA), // bright blue banner tag
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Countdown: ${viewModel.examGoalSelected.value} Mock Test",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Starts in 2 days, 04 hours • Complete limits formula study.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8) // slate-400
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = { /* keep minimal */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("Register", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Recommended For You section (AI Driven insights)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recommended For You (AI-driven)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "✨ NICHE Pilot",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color(0xFF2563EB)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            RecommendationItem(
                type = "Watch Video Lecture",
                title = "Gauss Law & Shell Potentials (Revise Coulomb's Law Weak Connection)",
                desc = "Created by HC Verma Sir • 31 Mins",
                color = Color(0xFF7C3AED),
                onClick = {
                    val p = defaultSubjects.find { it.id == "subject_phy" }
                    val ch = p?.chapters?.firstOrNull()
                    val v = ch?.videos?.getOrNull(1)
                    if (p != null && ch != null && v != null) {
                        viewModel.selectSubject(p)
                        viewModel.selectChapter(ch)
                        viewModel.selectVideo(v)
                    }
                }
            )

            RecommendationItem(
                type = "Quiz Review",
                title = "Fundamental Limits Assessment",
                desc = "Strengthen your limits definition concept • 2 Qs",
                color = Color(0xFF2563EB),
                onClick = {
                    val m = defaultSubjects.find { it.id == "subject_math" }
                    val ch = m?.chapters?.firstOrNull()
                    if (m != null && ch != null) {
                        viewModel.selectSubject(m)
                        viewModel.selectChapter(ch)
                        viewModel.startQuiz(ch)
                    }
                }
            )
        }

        // Quick Stats overview
        Text("Daily Activities", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F172A))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "Hours learned",
                value = "${stats?.hoursLearned ?: 12.5} hrs",
                icon = "⏱️",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Daily Streak",
                value = "${stats?.streak ?: 5} days",
                icon = "🔥",
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun RecommendationItem(
    type: String,
    title: String,
    desc: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp), // Rounded-3xl
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)) // slate border
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.08f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = type, color = color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Text("🔥 Goal Aligned", fontSize = 11.sp, color = Color(0xFF94A3B8))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
        }
    }
}

// --- 2. SUBJECTS GRID VIEW ---

@Composable
fun SubjectsGridScreen(viewModel: MainViewModel) {
    val list = viewModel.subjectsList

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        NicheHeader(title = "Courses & Subjects", subtitle = "Choose targeted study modules aligned to syllabus")

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(list) { sub ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(165.dp)
                        .clickable { viewModel.selectSubject(sub) }
                        .testTag("subject_grid_card_${sub.id}"),
                    colors = CardDefaults.cardColors(containerColor = Color(sub.themeColor).copy(alpha = 0.06f)),
                    shape = RoundedCornerShape(24.dp), // Sleek 3xl shape
                    border = BorderStroke(1.dp, Color(sub.themeColor).copy(alpha = 0.18f)) // Custom tone borders
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Floating white circle/pill for the icon, matching class="w-10 h-10 bg-white shadow-sm"
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = sub.icon, fontSize = 20.sp)
                        }

                        Column {
                            Text(
                                text = sub.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${sub.chapters.size} Chapters",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- 3. SUBJECT CHAPTERS DETAIL ---

@Composable
fun SubjectDetailScreen(viewModel: MainViewModel) {
    val subject by viewModel.selectedSubject.collectAsStateWithLifecycle()

    if (subject == null) return

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo("student_dashboard") }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = subject!!.name, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(subject!!.chapters) { ch ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectChapter(ch) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ch.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.weight(1f)
                            )
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(text = "🎥 ${ch.videoCount} Videos", fontSize = 12.sp, color = Color.Gray)
                            Text(text = "📝 ${ch.notesCount} Notes", fontSize = 12.sp, color = Color.Gray)
                            if (ch.hasQuiz) {
                                Text(text = "🔥 Mock Quiz Available", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.selectChapter(ch) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(subject!!.themeColor))
                        ) {
                            Text("Start Learning module", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// --- 4. INDIVIDUAL CHAPTER CORE SECTIONS WITH TABS ---

@Composable
fun ChapterDetailScreen(viewModel: MainViewModel) {
    val chapter by viewModel.selectedChapter.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Videos", "Notes Review", "MCQ Practice", "Assignments")

    if (chapter == null) return

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo("subject_detail") }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = chapter!!.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1
            )
        }

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> { // Videos
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(chapter!!.videos) { video ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable { viewModel.selectVideo(video) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.05f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "▶", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = video.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(text = "Duration: ${video.duration} • ${video.teacherName}", fontSize = 12.sp, color = Color.Gray)
                            }
                            IconButton(onClick = { viewModel.downloadChapterContent(chapter!!, "video", video.title) }) {
                                Icon(Icons.Default.Download, contentDescription = "Downloads", tint = Color.Gray)
                            }
                        }
                    }
                }
            }
            1 -> { // Notes
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(chapter!!.notes) { note ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable { viewModel.selectNote(note) }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📄", fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = note.title, fontWeight = FontWeight.SemiBold)
                                    Text(text = "PDF document • ${note.fileSize}", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            Row {
                                IconButton(onClick = { viewModel.downloadChapterContent(chapter!!, "notes", note.title) }) {
                                    Icon(Icons.Default.Download, contentDescription = "Downloads")
                                }
                                IconButton(onClick = { viewModel.selectNote(note) }) {
                                    Icon(Icons.Default.ChevronRight, contentDescription = "Open")
                                }
                            }
                        }
                    }
                }
            }
            2 -> { // MCQs (Practice test launch button)
                if (chapter!!.hasQuiz) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Chapter Mastery Evaluation", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Test includes standard multiple choice questions aligned to JEE metric syllabus criteria. Get AI recommended follow-up studies based on errors.",
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.startQuiz(chapter!!) },
                            modifier = Modifier.height(50.dp).fillMaxWidth(0.8f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Launch Mock Quiz Setup")
                        }
                    }
                } else {
                    EmptyState(message = "Mock quiz is under development for this chap", tip = "Please read study summaries and review notes in the next panel.")
                }
            }
            3 -> { // Assignments
                if (chapter!!.assignments.isEmpty()) {
                    EmptyState(message = "No homework assigned here yet", tip = "Feel free to practice optional limits formula sets.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(chapter!!.assignments) { assign ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "📝 $assign", fontWeight = FontWeight.Medium)
                                StatusBadge(status = "Incomplete")
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 5. VIDEO REVISION PLAYER SCREEN WITH IN-VIDEO annotation ---

@Composable
fun VideoLearningScreen(viewModel: MainViewModel) {
    val video by viewModel.activeVideo.collectAsStateWithLifecycle()
    val chapter by viewModel.selectedChapter.collectAsStateWithLifecycle()

    var isPlaying by remember { mutableStateOf(true) }
    var bookmarkTriggered by remember { mutableStateOf(false) }
    var inVideoNoteStr by remember { mutableStateOf("") }
    var playbackSpeed by remember { mutableStateOf("1.0x") }
    var savedNotesList = remember { mutableStateListOf<String>() }

    var selectedSubTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Notes Detail", "Peer Comments", "Chapter Doubts")

    if (video == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Mock Video Frame at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isPlaying) "Playing Lesson Streams..." else "PAUSED",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    IconButton(onClick = { isPlaying = !isPlaying }) {
                        Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    }
                    IconButton(onClick = { bookmarkTriggered = true }) {
                        Icon(Icons.Default.BookmarkBorder, contentDescription = "Add bookmark", tint = Color.White)
                    }
                }
            }

            // Stream Playback controller indicators
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("12:15 / ${video!!.duration}", color = Color.White, fontSize = 11.sp)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Speed: $playbackSpeed",
                            color = Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .clickable {
                                    playbackSpeed = when (playbackSpeed) {
                                        "1.0x" -> "1.5x"
                                        "1.5x" -> "2.0x"
                                        "2.0x" -> "0.5x"
                                        else -> "1.0x"
                                    }
                                }
                                .padding(4.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Fullscreen, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }

        // Lecture descriptions
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = video!!.title, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.navigateTo("chapter_detail") }) {
                    Icon(Icons.Default.Close, contentDescription = "Close player")
                }
            }
            Text(text = "Instructor: ${video!!.teacherName}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            // Annotation Box
            NicheCard(backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("✍️ Quick In-Video Diary Notes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inVideoNoteStr,
                        onValueChange = { inVideoNoteStr = it },
                        placeholder = { Text("Write down key calculations at 12:15...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (inVideoNoteStr.isNotEmpty()) {
                                savedNotesList.add("[12:15] - $inVideoNoteStr")
                                inVideoNoteStr = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save Note Badge", fontSize = 12.sp)
                    }
                }
            }

            if (savedNotesList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Saved Lecture Bookmarks:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                savedNotesList.forEach { sNote ->
                    Text(text = "• $sNote", fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 2.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sub Tab Sections
            TabRow(selectedTabIndex = selectedSubTab) {
                tabs.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedSubTab == idx,
                        onClick = { selectedSubTab = idx },
                        text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedSubTab) {
                0 -> { // Notes summary
                    val defaultNotes = chapter?.notes?.firstOrNull()
                    if (defaultNotes != null) {
                        Text(text = defaultNotes.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = defaultNotes.contentSnippet, fontSize = 13.sp, color = Color.DarkGray)
                    }
                }
                1 -> { // Comments
                    Text("💬 Study Group Messages:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    listOf(
                        "Ankit S: L'Hopital equation really simplifies limits of infinity/infinity order!",
                        "Priscilla J: The concentric shells derivation at 18:00 was fantastic HC Verma Sir!"
                    ).forEach { comment ->
                        Text(text = comment, fontSize = 13.sp, modifier = Modifier.padding(vertical = 4.dp), color = Color.DarkGray)
                    }
                }
                2 -> { // Doubts Solver shortcut
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Have any studying doubt?", fontWeight = FontWeight.Bold)
                        Button(onClick = { viewModel.navigateTo("doubts_screen") }) {
                            Text("Ask AI / Teacher")
                        }
                    }
                }
            }
        }
    }

    if (bookmarkTriggered) {
        Dialog(onDismissRequest = { bookmarkTriggered = false }) {
            Card(shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Bookmark Logged!", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Timestamp 12:15 saved to your personal dashboard progression log.")
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { bookmarkTriggered = false }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

// --- 6. IN-APP PDF READER WITH DYNAMIC GEMINI SUMMARIZATION ---

@Composable
fun NotesReaderScreen(viewModel: MainViewModel) {
    val note by viewModel.activeNote.collectAsStateWithLifecycle()
    val summary by viewModel.noteSummary.collectAsStateWithLifecycle()
    val loading by viewModel.isSummaryLoading.collectAsStateWithLifecycle()

    if (note == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo("chapter_detail") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = note!!.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(text = "PDF Offline Preview", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            IconButton(onClick = { /* Simulated Highlight text */ }) {
                Icon(Icons.Default.EditNote, contentDescription = "Highlight", tint = MaterialTheme.colorScheme.primary)
            }
        }

        // Mock PDF Paper
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFEFDF7)) // Cozy reading paper tint
                .border(1.dp, Color(0xFFE6E1D3), RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "--- SYLLABUS SECTION HANDOUT ---",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = note!!.contentSnippet,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "A core tip in competitive JEE prep is solving previous year question papers. Under limiting equations, if standard binomial expansions apply, approximate factors early to bypass bulky limit iterations. Check continuity of f(x) via limits matching actual function evaluations.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.DarkGray,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // AI Generated Quick Summary (Direct Gemini API Query!)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✨", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Quick Summary",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (loading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("NICHE AI summarizing notes...", fontSize = 12.sp)
                    }
                } else if (summary.isNotEmpty()) {
                    Text(
                        text = summary,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                } else {
                    Text("Failed to generate AI notes overview. Please make sure internet connection works.")
                }
            }
        }
    }
}

// --- 7. MCQ QUIZ EVALUATOR WITH countdown TIMER & AI INSIGHTS ---

@Composable
fun QuizScreenLayout(viewModel: MainViewModel) {
    val questions by viewModel.activeQuizQuestions.collectAsStateWithLifecycle()
    val activeIdx by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val answers by viewModel.userAnswers.collectAsStateWithLifecycle()
    val seconds by viewModel.quizSecondsRemaining.collectAsStateWithLifecycle()
    val result by viewModel.quizCompletedResult.collectAsStateWithLifecycle()

    val aiLoading by viewModel.isQuizAiLoading.collectAsStateWithLifecycle()
    val aiRecommendation by viewModel.quizAiRecommendations.collectAsStateWithLifecycle()

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No questions loaded. Please choose another chapter.")
        }
        return
    }

    val currentQ = questions[activeIdx]

    if (result != null) {
        // Result Screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Quiz Finished Report", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("YOUR MASTERY SCORE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("${result!!.score} / ${result!!.totalQuestions}", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Accuracy rate: ${result!!.percentage.toInt()}%", fontSize = 14.sp)
                }
            }

            // Flagged Weak Areas
            Text("Concepts Needing Attention:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = result!!.weakAreas,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red.copy(alpha = 0.8f)
                    )
                }
            }

            // Gemini AI Recommendations
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✨", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("NICHE AI Remediation Plan", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (aiLoading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Generating tailored study recommendations...")
                        }
                    } else {
                        Text(text = aiRecommendation, fontSize = 13.sp)
                    }
                }
            }

            // Explanations loop
            Text("Review Solution Explanations:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            questions.forEachIndexed { qIdx, question ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "Q${qIdx + 1}. ${question.question}", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        val isCorrect = answers[question.id] == question.correctAnswerIndex
                        Text(
                            text = if (isCorrect) "✓ Correctly Answered" else "❌ Marked Incorrectly",
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCorrect) Color(0xFF166534) else Color(0xFF991B1B)
                        )
                        Text(text = "Correct Answer: ${question.options[question.correctAnswerIndex]}", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Rationale: ${question.explanation}", fontSize = 12.sp)
                    }
                }
            }

            Button(
                onClick = { viewModel.navigateTo("student_dashboard") },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Return to Classroom Feeds")
            }
        }
    } else {
        // Active Quiz layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Header (Progress bar + countdown timer)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Question ${activeIdx + 1} of ${questions.size}", fontWeight = FontWeight.Bold)
                        LinearProgressIndicator(
                            progress = { (activeIdx + 1).toFloat() / questions.size },
                            modifier = Modifier.width(150.dp).clip(CircleShape)
                        )
                    }

                    // Countdown Widget
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                    ) {
                        Text(
                            text = "⏱️ $seconds Secs",
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Question Statement
                Text(
                    text = currentQ.question,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Choices list (touch targets size strictly ≥ 48dp with material ripple feedback)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    currentQ.options.forEachIndexed { index, option ->
                        val isSelected = answers[currentQ.id] == index
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .clickable { viewModel.answerQuizQuestion(currentQ.id, index) }
                                .testTag("quiz_option_${index}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray)
                        ) {
                            Box(modifier = Modifier.fillMaxSize().padding(14.dp), contentAlignment = Alignment.CenterStart) {
                                Text(text = option, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { viewModel.prevQuizQuestion() },
                    enabled = activeIdx > 0,
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Previous")
                }

                val isLast = activeIdx == questions.size - 1
                Button(
                    onClick = {
                        if (isLast) {
                            viewModel.submitQuiz()
                        } else {
                            viewModel.nextQuizQuestion()
                        }
                    },
                    modifier = Modifier.height(48.dp).testTag("quiz_control_next_btn")
                ) {
                    Text(if (isLast) "Submit Test" else "Next Question")
                }
            }
        }
    }
}

// --- 8. DOUBT SOLVER CONTROLLER ---

@Composable
fun DoubtSolvingScreen(viewModel: MainViewModel) {
    val doubts by viewModel.doubts.collectAsStateWithLifecycle()
    val isDoubtAiLoading by viewModel.isDoubtAiLoading.collectAsStateWithLifecycle()

    var textQuery by remember { mutableStateOf("") }
    var doubtResolvingMode by remember { mutableStateOf("AI Explanation") }
    val modes = listOf("AI Explanation", "Ask Teacher", "Community")
    var mockImageAttached by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NicheHeader(title = "Clearing Doubts", subtitle = "Instantly clear confusing scientific questions & concepts")

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("State your doubt details:", fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = textQuery,
                    onValueChange = { textQuery = it },
                    placeholder = { Text("Write your doubt e.g. Why is the center of mass of a hollow hemisphere at R/2?") },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("doubt_query_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Select File/Image Mock Camera attachment
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { mockImageAttached = true },
                        colors = ButtonDefaults.buttonColors(containerColor = if (mockImageAttached) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (mockImageAttached) "✓ Doubt Photo Attached" else "📸 Snap Doubt Photo", color = Color.Black)
                    }
                }

                // Channel Selector buttons
                Text("Select Resolution Channel:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    modes.forEach { mode ->
                        val isSel = doubtResolvingMode == mode
                        OutlinedButton(
                            onClick = { doubtResolvingMode = mode },
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent
                            ),
                            border = BorderStroke(if (isSel) 2.dp else 1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.LightGray)
                        ) {
                            Text(text = mode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) MaterialTheme.colorScheme.primary else Color.DarkGray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                CustomGradientButton(
                    text = "Submit Doubt Query",
                    onClick = {
                        if (textQuery.isNotEmpty()) {
                            viewModel.submitDoubt(textQuery, doubtResolvingMode)
                            textQuery = ""
                            mockImageAttached = false
                        }
                    },
                    tag = "submit_doubt_btn"
                )
            }
        }

        if (isDoubtAiLoading) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Querying NICHE AI Study Server...")
            }
        }

        // Doubt History Feeds
        Text("Doubt Resolution History:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        if (doubts.isEmpty()) {
            EmptyState(message = "No doubt tickets created yet", tip = "Feel free to post physics/math items above.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                doubts.forEach { ticket ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                        .padding(horizontal = 8.dp)
                                ) {
                                    Text(text = ticket.responseType, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                StatusBadge(status = ticket.status)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = ticket.questionText, fontWeight = FontWeight.Bold)

                            if (ticket.status == "Resolved" && ticket.responseBody != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = ticket.responseBody,
                                    fontSize = 13.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 9. COMMUNITY ROOM DISCUSSION CORNER ---

@Composable
fun CommunityRoomScreen(viewModel: MainViewModel) {
    val activeRoom by viewModel.activeCommunityRoom.collectAsStateWithLifecycle()
    val chatMessages by viewModel.getMessagesForRoom(activeRoom).collectAsStateWithLifecycle(initialValue = emptyList())
    val roomsList = listOf("JEE Aspirants", "NEET Biology", "Class 12 Science", "Sem 3 CS")

    var promptMsgText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        NicheHeader(title = "Community Discussion", subtitle = "Study & clear doubts with peers across India")

        // Rooms Selector Scroll
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(roomsList) { room ->
                val isSel = activeRoom == room
                Button(
                    onClick = { viewModel.activeCommunityRoom.value = room },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = room, color = if (isSel) Color.White else Color.Black, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chats Thread List
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            if (chatMessages.isEmpty()) {
                EmptyState(message = "No messages posted in $activeRoom", tip = "Be the first to post a formula tips list!")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                    items(chatMessages) { msg ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row {
                                    Text(text = msg.senderName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (msg.senderRole == "Teacher") Color(0xFFFEF3C7) else Color(0xFFDCFCE7))
                                    ) {
                                        Text(
                                            text = msg.senderRole,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp),
                                            color = if (msg.senderRole == "Teacher") Color(0xFF92400E) else Color(0xFF166534)
                                        )
                                    }
                                }
                            }
                            Text(text = msg.messageText, fontSize = 13.sp, color = Color.DarkGray)
                            HorizontalDivider(modifier = Modifier.padding(top = 4.dp), color = Color.LightGray.copy(alpha = 0.3f))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Message input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = promptMsgText,
                onValueChange = { promptMsgText = it },
                placeholder = { Text("Post core formulas to room...") },
                modifier = Modifier.weight(1f).testTag("chat_input_text"),
                shape = RoundedCornerShape(12.dp)
            )

            IconButton(
                onClick = {
                    if (promptMsgText.isNotEmpty()) {
                        viewModel.postCommunityMessage(promptMsgText)
                        promptMsgText = ""
                    }
                },
                modifier = Modifier.size(52.dp).testTag("chat_submit_btn"),
                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

// --- 10. OFFLINE DOWNLOADS SCREEN ---

@Composable
fun DownloadsScreen(viewModel: MainViewModel) {
    val downloadsList by viewModel.downloads.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        NicheHeader(title = "Offline Files", subtitle = "Access downloaded videos & notes without active data")

        Spacer(modifier = Modifier.height(12.dp))

        if (downloadsList.isEmpty()) {
            EmptyState(message = "No offline materials saved", tip = "Browse Course chapter tabs and click the download button.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(downloadsList) { file ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = if (file.type == "video") "🎥" else "📄", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = file.title, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text(text = "${file.chapterName} • ${file.fileSize}", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        IconButton(onClick = { viewModel.deleteDownloadedItem(file.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }
}

// --- 11. STUDENT PROFILE SCREEN ---

@Composable
fun StudentProfileScreen(viewModel: MainViewModel) {
    val stats by viewModel.studentStats.collectAsStateWithLifecycle()
    var displaySubChoice by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NicheHeader(title = "My Academic Portfolio", subtitle = "Set custom milestones and target goals")

        // Profile details
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = viewModel.userName.value.take(1), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = viewModel.userName.value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = viewModel.userEmail.value, fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "School: ${viewModel.schoolEntered.value}", fontSize = 13.sp)
                Text(text = "Curriculum: ${viewModel.classSelected.value}", fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                Text(text = "Goal Target: ${viewModel.examGoalSelected.value}", fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }

        // Subscription Tier card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("NICHE Premium Pass", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(horizontal = 8.dp)
                    ) {
                        Text("Active Tier", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "You have unlocked Unlimited course lectures, doubts solving and mock assessments.", fontSize = 12.sp)

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { displaySubChoice = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("View Plans & pricing")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Log out Action
        OutlinedButton(
            onClick = { viewModel.logOut() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Log Out Account")
        }
    }

    if (displaySubChoice) {
        Dialog(onDismissRequest = { displaySubChoice = false }) {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("NICHE Subscription Tiers", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    Card(border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Standard - FREE", fontWeight = FontWeight.Bold)
                            Text("Access 2 courses, 5 doubt tickets per month.")
                        }
                    }

                    Card(border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Premium Mastermind - ₹499/Month", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                            Text("Unlock unlimited mock tests, unlimited rapid AI doubt solver and direct mentor messaging channels.")
                        }
                    }

                    Button(
                        onClick = { displaySubChoice = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close window")
                    }
                }
            }
        }
    }
}
