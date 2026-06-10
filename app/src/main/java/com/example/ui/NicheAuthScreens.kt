package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNext: () -> Unit) {
    var animateStart by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = true) {
        animateStart = true
        delay(1800)
        onNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)), // Deep slate background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = animateStart,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut()
            ) {
                Text(
                    text = "🎓 NICHE",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            AnimatedVisibility(
                visible = animateStart,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 50 }),
                exit = fadeOut()
            ) {
                Text(
                    text = "Learn Smarter, Teach Better",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(48.dp))
            CircularProgressIndicator(color = Color(0xFF2563EB))
        }
    }
}

@Composable
fun OnboardingScreen(onFinish: () -> Unit, onSkip: () -> Unit) {
    var activePage by remember { mutableIntStateOf(0) }
    val pages = listOf(
        OnboardingData(
            title = "Learn Anywhere",
            description = "Access top tier IIT-Delhi & expert video classes, structured lecture notes, and quizzes anytime, anywhere.",
            emoji = "📱"
        ),
        OnboardingData(
            title = "Learn From Verified Teachers",
            description = "All material uploaded undergoes strict academic reviews. Interact with certified course mentors directly.",
            emoji = "🧑‍🏫"
        ),
        OnboardingData(
            title = "Personalized Learning Co-Pilot",
            description = "Our integrated NICHE AI identifies weak curriculum areas and creates summaries & practice suggestions based on your study history.",
            emoji = "✨"
        )
    )

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.testTag("skip_onboarding_btn")
                ) {
                    Text("Skip", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }

            // Animated illustration and content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(80.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = pages[activePage].emoji, fontSize = 72.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = pages[activePage].title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = pages[activePage].description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Bottom control navigation
            Column(modifier = Modifier.fillMaxWidth()) {
                // Page Indicator dots
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    pages.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(width = if (activePage == index) 16.dp else 8.dp, height = 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (activePage == index) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                // CTA Button
                val isLast = activePage == pages.size - 1
                CustomGradientButton(
                    text = if (isLast) "Get Started" else "Next",
                    onClick = {
                        if (activePage < pages.size - 1) {
                            activePage++
                        } else {
                            onFinish()
                        }
                    },
                    tag = "onboarding_action_btn"
                )
            }
        }
    }
}

data class OnboardingData(val title: String, val description: String, val emoji: String)

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (String) -> Unit
) {
    var selectedRole by remember { mutableStateOf("Student") }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Welcome to NICHE",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Choose your role to customize your dashboard layout",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Student Card Selection
                RoleCard(
                    title = "I want to Learn (Student)",
                    description = "Enroll in structured video courses, solve MCQ quiz series, clear study doubts and connect with JEE/NEET study rooms.",
                    icon = "🎓",
                    isSelected = selectedRole == "Student",
                    onClick = { selectedRole = "Student" },
                    tag = "role_student_card"
                )

                // Teacher Card Selection
                RoleCard(
                    title = "I want to Teach (Teacher)",
                    description = "Establish rich course classrooms, upload lesson notes, publish quizzes, clear student doubts and view deep cohorts progress insights.",
                    icon = "🧑‍💻",
                    isSelected = selectedRole == "Teacher",
                    onClick = { selectedRole = "Teacher" },
                    tag = "role_teacher_card"
                )
            }

            CustomGradientButton(
                text = "Continue as $selectedRole",
                onClick = { onRoleSelected(selectedRole) },
                tag = "continue_role_selection_btn"
            )
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    description: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val containerBg = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 42.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    role: String,
    onLogIn: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Log in as a $role",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = emailOrPhone,
                    onValueChange = { emailOrPhone = it },
                    label = { Text("Email Address or Mobile Number") },
                    modifier = Modifier.fillMaxWidth().testTag("login_username_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().testTag("login_password_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { /* Forgot password placeholder */ }) {
                        Text("Forgot Password?", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                CustomGradientButton(
                    text = "Log In",
                    onClick = onLogIn,
                    tag = "submit_login_btn"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Alternative Options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(" or ", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onLogIn,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("💡 Continue via Google / OTP")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account? ")
                Text(
                    text = "Create Account",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToSignUp() }.testTag("goto_signup_link")
                )
            }
        }
    }
}

@Composable
fun SignUpScreen(
    role: String,
    onSignUpSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    viewModel: MainViewModel
) {
    var name by remember { mutableStateOf("") }
    var extraClass by remember { mutableStateOf("Class 12 - Science") }
    var extraSchool by remember { mutableStateOf("St. Xavier's Academy") }
    var mockUploadedId by remember { mutableStateOf(false) }
    var mockUploadedTeachingProof by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Join NICHE",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Creating a $role Account",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(28.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth().testTag("signup_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (role == "Student") {
                    OutlinedTextField(
                        value = extraClass,
                        onValueChange = { extraClass = it },
                        label = { Text("Course or Class Level (e.g. Class 12)") },
                        modifier = Modifier.fillMaxWidth().testTag("signup_class_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = extraSchool,
                        onValueChange = { extraSchool = it },
                        label = { Text("School / College Name") },
                        modifier = Modifier.fillMaxWidth().testTag("signup_school_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.examGoalSelected.value,
                        onValueChange = { viewModel.updateGoal(it) },
                        label = { Text("Exam Target Goal (Optional, e.g. NEET or JEE)") },
                        modifier = Modifier.fillMaxWidth().testTag("signup_goal_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    // Teacher fields
                    OutlinedTextField(
                        value = viewModel.teacherSubject.value,
                        onValueChange = { viewModel.teacherSubject.value = it },
                        label = { Text("Subject Expertise (e.g. Physics, Maths)") },
                        modifier = Modifier.fillMaxWidth().testTag("signup_subject_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.teacherExpt.value,
                        onValueChange = { viewModel.teacherExpt.value = it },
                        label = { Text("Years of Practice Experience") },
                        modifier = Modifier.fillMaxWidth().testTag("signup_expt_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.teacherQual.value,
                        onValueChange = { viewModel.teacherQual.value = it },
                        label = { Text("Qualifications / Degree Profile") },
                        modifier = Modifier.fillMaxWidth().testTag("signup_qual_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Document upload simulators
                    Text("Official Verification Credentials", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { mockUploadedId = true },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (mockUploadedId) Color(0xFFDCFCE7) else Color.Transparent
                            )
                        ) {
                            Text(if (mockUploadedId) "✓ ID File Attached" else "📄 Attach Govt ID")
                        }

                        OutlinedButton(
                            onClick = { mockUploadedTeachingProof = true },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (mockUploadedTeachingProof) Color(0xFFDCFCE7) else Color.Transparent
                            )
                        ) {
                            Text(if (mockUploadedTeachingProof) "✓ Degree Attached" else "🎓 Attach Degree")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                CustomGradientButton(
                    text = "Create Account",
                    onClick = {
                        if (name.isNotEmpty()) {
                            if (role == "Student") {
                                viewModel.userName.value = name
                                viewModel.schoolEntered.value = extraSchool
                                viewModel.classSelected.value = extraClass
                                onSignUpSuccess()
                            } else {
                                viewModel.teacherName.value = name
                                // Go to pending review page
                                viewModel.navigateTo("teacher_pending_review")
                            }
                        }
                    },
                    tag = "submit_signup_btn"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ")
                Text(
                    text = "Sign In",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onBackToLogin() }.testTag("goto_login_link")
                )
            }
        }
    }
}

@Composable
fun ReviewPendingScreen(
    viewModel: MainViewModel,
    onApproved: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                Text(text = "⏳", fontSize = 72.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Verification Pending",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Hi ${viewModel.teacherName.value}, we have received your ID verification credentials and credentials proof. To preserve high classroom standards, our academic reviewers manually check all materials.\n\nSLA Review Period: 12-24 Hours.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }

            // A Mock verification bypass controller so researchers can instantly explore the completed App!
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Reviewer Quick Sandbox Bypass",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Click below to bypass verification instantly and launch Teacher Console.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.isTeacherVerified.value = true
                            onApproved()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Grant Instructor Verification Status")
                    }
                }
            }
        }
    }
}
