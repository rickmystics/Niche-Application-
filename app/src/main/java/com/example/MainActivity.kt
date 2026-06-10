package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavigationCoordinator(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigationCoordinator(viewModel: MainViewModel) {
    val route by viewModel.currentRoute.collectAsStateWithLifecycle()
    val role by viewModel.userRole.collectAsStateWithLifecycle()

    // Top-Level Router mapping
    when (route) {
        "splash" -> SplashScreen(
            onNext = { viewModel.navigateTo("onboarding") }
        )
        "onboarding" -> OnboardingScreen(
            onFinish = { viewModel.navigateTo("role_selection") },
            onSkip = { viewModel.navigateTo("role_selection") }
        )
        "role_selection" -> RoleSelectionScreen(
            onRoleSelected = { chosenRole ->
                viewModel.setRole(chosenRole)
                viewModel.navigateTo("login")
            }
        )
        "login" -> LoginScreen(
            role = role ?: "Student",
            onLogIn = {
                viewModel.logIn()
            },
            onNavigateToSignUp = {
                viewModel.navigateTo("signup")
            }
        )
        "signup" -> SignUpScreen(
            role = role ?: "Student",
            onSignUpSuccess = {
                viewModel.logIn()
            },
            onBackToLogin = {
                viewModel.navigateTo("login")
            },
            viewModel = viewModel
        )
        "teacher_pending_review" -> ReviewPendingScreen(
            viewModel = viewModel,
            onApproved = {
                viewModel.logIn()
            }
        )
        // Router delegates for Teacher administrative scope
        "teacher_dashboard" -> TeacherMainContainer(viewModel = viewModel)

        // Router delegates for Student learning scope
        else -> {
            StudentMainContainer(viewModel = viewModel)
        }
    }
}
