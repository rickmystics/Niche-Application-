package com.example.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@Composable
fun TeacherMainContainer(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var teacherSubRoute by remember { mutableStateOf("dashboard") }

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
                    selected = teacherSubRoute == "dashboard",
                    onClick = { teacherSubRoute = "dashboard" },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = navColors,
                    modifier = Modifier.testTag("teacher_nav_dashboard")
                )
                NavigationBarItem(
                    selected = teacherSubRoute == "upload",
                    onClick = { teacherSubRoute = "upload" },
                    icon = { Icon(Icons.Default.UploadFile, contentDescription = "Upload") },
                    label = { Text("Upload", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = navColors,
                    modifier = Modifier.testTag("teacher_nav_upload")
                )
                NavigationBarItem(
                    selected = teacherSubRoute == "analytics",
                    onClick = { teacherSubRoute = "analytics" },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = "Analytics") },
                    label = { Text("Analytics", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = navColors,
                    modifier = Modifier.testTag("teacher_nav_analytics")
                )
                NavigationBarItem(
                    selected = teacherSubRoute == "profile",
                    onClick = { teacherSubRoute = "profile" },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = navColors,
                    modifier = Modifier.testTag("teacher_nav_profile")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (teacherSubRoute) {
                "dashboard" -> TeacherDashboard(
                    viewModel = viewModel,
                    onCreateCourseRoute = { teacherSubRoute = "create_course" }
                )
                "create_course" -> TeacherCreateCourseScreen(
                    viewModel = viewModel,
                    onBack = { teacherSubRoute = "dashboard" }
                )
                "upload" -> TeacherContentUpload(
                    viewModel = viewModel,
                    onSuccess = { teacherSubRoute = "dashboard" }
                )
                "analytics" -> TeacherCohortAnalytics(viewModel)
                "profile" -> TeacherProfileScreen(viewModel)
            }
        }
    }
}

// --- 1. TEACHER OVERVIEW DASHBOARD ---

@Composable
fun TeacherDashboard(
    viewModel: MainViewModel,
    onCreateCourseRoute: () -> Unit
) {
    val courses by viewModel.teacherCourses.collectAsStateWithLifecycle()
    var displayLiveClassDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Instructor Portal", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Text(
                    text = "Welcome, Prof. ${viewModel.teacherName.value} 👋",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            StatusBadge(status = "Verified")
        }

        // Metrics Matrix
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "Total Students",
                value = "1,852",
                icon = "👥",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Monthly Profit",
                value = "₹48,250",
                icon = "💰",
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = "Engagement",
                value = "88.2%",
                icon = "📈",
                color = Color(0xFF059669),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Doubt SLA",
                value = "94%",
                icon = "⚡",
                color = Color.Red,
                modifier = Modifier.weight(1f)
            )
        }

        // Quick Actions block
        Text("Quick Console Actions", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onCreateCourseRoute,
                modifier = Modifier.weight(1f).height(48.dp).testTag("action_create_course"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("➕ Add Course", fontSize = 13.sp)
            }

            Button(
                onClick = { displayLiveClassDialog = true },
                modifier = Modifier.weight(1f).height(48.dp).testTag("action_start_live"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("🎥 Start Live Class", fontSize = 13.sp)
            }
        }

        // Classroom Courses Published list
        Text("My Published Courses", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        if (courses.isEmpty()) {
            EmptyState(message = "No courses added to catalog yet", tip = "Tap 'Add Course' above to establish your first study curriculum.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                courses.forEach { sub ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = sub.icon, fontSize = 34.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = sub.name, fontWeight = FontWeight.Bold)
                                Text(text = "${sub.chapters.size} Syllabus Modules • Active Enrollments: 420 students", fontSize = 12.sp, color = Color.Gray)
                            }
                            Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    if (displayLiveClassDialog) {
        Dialog(onDismissRequest = { displayLiveClassDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Broadcast Live Virtual Classroom", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Establish secure streaming parameters. An automatic notification blast will ping all 1,852 subscribed students.",
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { displayLiveClassDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Connect Virtual Classroom Stream")
                    }
                }
            }
        }
    }
}

// --- 2. CREATE COURSE SCREEN ---

@Composable
fun TeacherCreateCourseScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var courseName by remember { mutableStateOf("") }
    var courseDesc by remember { mutableStateOf("") }
    var classLevel by remember { mutableStateOf("Class 12 - Science") }
    var pricingModelIsPaid by remember { mutableStateOf(false) }
    var coursePrice by remember { mutableStateOf("") }
    val isCreating by viewModel.isTeacherCreating.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create New Course", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        }

        OutlinedTextField(
            value = courseName,
            onValueChange = { courseName = it },
            label = { Text("Course / Subject Name (e.g. Organic Chemistry Revision)") },
            modifier = Modifier.fillMaxWidth().testTag("course_name_input"),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = courseDesc,
            onValueChange = { courseDesc = it },
            label = { Text("Syllabus Description Goals") },
            modifier = Modifier.fillMaxWidth().height(100.dp).testTag("course_desc_input"),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = classLevel,
            onValueChange = { classLevel = it },
            label = { Text("Curriculum level (e.g. JEE/NEET, College, Board)") },
            modifier = Modifier.fillMaxWidth().testTag("course_level_input"),
            shape = RoundedCornerShape(12.dp)
        )

        // Dynamic pricing toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Is this a monetized Paid Course?", fontWeight = FontWeight.Bold)
            Switch(
                checked = pricingModelIsPaid,
                onCheckedChange = { pricingModelIsPaid = it },
                modifier = Modifier.testTag("pricing_toggle")
            )
        }

        if (pricingModelIsPaid) {
            OutlinedTextField(
                value = coursePrice,
                onValueChange = { coursePrice = it },
                label = { Text("Course Access Price (INR ₹)") },
                modifier = Modifier.fillMaxWidth().testTag("course_price_input"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isCreating) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            CustomGradientButton(
                text = "Establish Course",
                onClick = {
                    if (courseName.isNotEmpty()) {
                        viewModel.createTeacherCourse(
                            name = courseName,
                            desc = courseDesc,
                            subject = viewModel.teacherSubject.value,
                            classLvl = classLevel,
                            price = if (pricingModelIsPaid) coursePrice else "Free"
                        )
                    }
                },
                tag = "submit_course_creation"
            )
        }
    }
}

// --- 3. UPLOAD CONTENT SCREEN ---

@Composable
fun TeacherContentUpload(
    viewModel: MainViewModel,
    onSuccess: () -> Unit
) {
    var selectedChapterName by remember { mutableStateOf("Module 1 - Starter Introduction") }
    var topicName by remember { mutableStateOf("") }
    var uploadContentType by remember { mutableStateOf("Video") } // "Video" or "Notes"
    val isUploading by viewModel.isTeacherUploading.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NicheHeader(title = "Publish Learning Assets", subtitle = "Distribute videos and revision formula PDF notes to your roster")

        OutlinedTextField(
            value = selectedChapterName,
            onValueChange = { selectedChapterName = it },
            label = { Text("Target Chapter Name") },
            modifier = Modifier.fillMaxWidth().testTag("upload_chapter_input"),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = topicName,
            onValueChange = { topicName = it },
            label = { Text("Topic / File Name (e.g. Kinetic rate constant derivations)") },
            modifier = Modifier.fillMaxWidth().testTag("upload_topic_input"),
            shape = RoundedCornerShape(12.dp)
        )

        // Segment Select type
        Text("Resource Classification:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("Video", "Notes").forEach { type ->
                val isSel = uploadContentType == type
                OutlinedButton(
                    onClick = { uploadContentType = type },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent
                    ),
                    border = BorderStroke(if (isSel) 2.dp else 1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.LightGray)
                ) {
                    Text(text = type, color = if (isSel) MaterialTheme.colorScheme.primary else Color.DarkGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isUploading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            CustomGradientButton(
                text = "Publish to Student Feeds",
                onClick = {
                    if (topicName.isNotEmpty()) {
                        viewModel.uploadTeacherContent(selectedChapterName, topicName, uploadContentType)
                        onSuccess()
                    }
                },
                tag = "submit_upload_asset"
            )
        }
    }
}

// --- 4. COHORT ANALYTICS EXCEL WORKSPACE (WITH GEMINI EDU COMPILER) ---

@Composable
fun TeacherCohortAnalytics(viewModel: MainViewModel) {
    val aiAnalLoading by viewModel.isAnalyticsAiLoading.collectAsStateWithLifecycle()
    val aiInsightsReport by viewModel.analyticsAiInsights.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NicheHeader(title = "Cohort Engagement Studies", subtitle = "View average watch time, quiz accuracy progression metrics")

        // Progress Chart Panel
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Overall Syllabus Cohorts Accuracy Chart", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                // Custom Graphic drawing using dynamic Canvas height parameters
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color.White)
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Graph column blocks
                        ProgressBarBlock(pct = 0.85f, label = "Limits (Ch 1)")
                        ProgressBarBlock(pct = 0.51f, label = "Organic (Ch 2)")
                        ProgressBarBlock(pct = 0.91f, label = "CS (Ch 3)")
                    }
                }
            }
        }

        // Gemini AI Cohort Insights panel
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✨", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NICHE AI Educator Insights Analyst",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                if (aiAnalLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("AI Compiler assessing cohort performance charts...")
                    }
                } else {
                    Text(text = aiInsightsReport, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.loadTeacherAnalyticsReport() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Compile AI Insights Report")
                }
            }
        }
    }
}

@Composable
fun ProgressBarBlock(pct: Float, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
        Text(text = "${(pct * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(80.dp * pct)
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 10.sp, color = Color.Gray)
    }
}

// --- 5. TEACHER PROFILE PRESET ---

@Composable
fun TeacherProfileScreen(viewModel: MainViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NicheHeader(title = "Teaching Portfolio", subtitle = "Certified Course Mentor details")

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = viewModel.teacherName.value.take(1), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Prof. ${viewModel.teacherName.value}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = "Verified Course Instructor • India", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Primary Subject: ${viewModel.teacherSubject.value}", fontSize = 13.sp)
                Text(text = "Years Active: ${viewModel.teacherExpt.value}", fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                Text(text = "Credentials: ${viewModel.teacherQual.value}", fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp), fontWeight = FontWeight.Bold)
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
            border = BorderStroke(1.dp, Color(0xFF86EFAC))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🛡️", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "ID & Degree Verified", fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                    Text(text = "Instructor verified as high-standards Educator on NICHE learning panels.", fontSize = 11.sp, color = Color(0xFF166534))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = { viewModel.logOut() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Log Out Instructor Console")
        }
    }
}
