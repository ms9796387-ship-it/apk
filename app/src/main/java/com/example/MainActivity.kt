package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.LocalRepository
import com.example.data.UserRole
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CertiChainNavigation()
            }
        }
    }
}

@Composable
fun CertiChainNavigation() {
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            // ==========================================
            // ONBOARDING & AUTHENTICATION FLOWS
            // ==========================================
            composable("splash") {
                SplashScreen(onSplashFinished = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                })
            }

            composable("onboarding") {
                OnboardingScreen(onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                })
            }

            composable("login") {
                LoginScreen(
                    onLoginSuccess = { role ->
                        val destination = when (role) {
                            UserRole.ADMIN -> "admin_dashboard"
                            UserRole.INSTITUTION -> "institution_dashboard"
                            UserRole.STUDENT -> "student_dashboard"
                            UserRole.EMPLOYER -> "employer_dashboard"
                        }
                        navController.navigate(destination) {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate("forgot_password")
                    }
                )
            }

            composable("register") {
                RegisterScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onRegistrationSuccess = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )
            }

            composable("forgot_password") {
                ForgotPasswordScreen(onNavigateBack = { navController.popBackStack() })
            }

            // ==========================================
            // ADMIN MODULE PORTALS
            // ==========================================
            composable("admin_dashboard") {
                AdminDashboardScreen(
                    onNavigateToInstitutions = { navController.navigate("admin_institutions") },
                    onNavigateToUsers = { navController.navigate("admin_users") },
                    onNavigateToTransactions = { navController.navigate("admin_transactions") },
                    onNavigateToAuditLogs = { navController.navigate("admin_audit_logs") },
                    onLogout = {
                        LocalRepository.logout()
                        navController.navigate("login") {
                            popUpTo("admin_dashboard") { inclusive = true }
                        }
                    },
                    onNavigateToProfile = { navController.navigate("profile") },
                    onNavigateToAbout = { navController.navigate("about") }
                )
            }

            composable("admin_institutions") {
                AdminInstitutionsScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable("admin_users") {
                AdminUsersScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable("admin_transactions") {
                AdminTransactionsScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable("admin_audit_logs") {
                AdminAuditLogsScreen(onNavigateBack = { navController.popBackStack() })
            }

            // ==========================================
            // ACADEMIC INSTITUTION MODULE PORTALS
            // ==========================================
            composable("institution_dashboard") {
                InstitutionDashboardScreen(
                    onNavigateToStudents = { navController.navigate("institution_students") },
                    onNavigateToAddStudent = { navController.navigate("institution_add_student") },
                    onNavigateToIssue = { navController.navigate("institution_issue") },
                    onNavigateToCertificates = { navController.navigate("institution_certificates") },
                    onNavigateToRevoked = { navController.navigate("institution_revoked") },
                    onLogout = {
                        LocalRepository.logout()
                        navController.navigate("login") {
                            popUpTo("institution_dashboard") { inclusive = true }
                        }
                    },
                    onNavigateToProfile = { navController.navigate("profile") },
                    onNavigateToAbout = { navController.navigate("about") }
                )
            }

            composable("institution_students") {
                InstitutionStudentsScreen(
                    onNavigateToAddStudent = { navController.navigate("institution_add_student") },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("institution_add_student") {
                InstitutionAddStudentScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable("institution_issue") {
                InstitutionIssueCertScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable("institution_certificates") {
                InstitutionCertificatesScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable("institution_revoked") {
                InstitutionRevokedScreen(onNavigateBack = { navController.popBackStack() })
            }

            // ==========================================
            // STUDENT MODULE PORTALS
            // ==========================================
            composable("student_dashboard") {
                StudentDashboardScreen(
                    onNavigateToCertificates = { navController.navigate("student_certificates") },
                    onNavigateToAbout = { navController.navigate("about") },
                    onNavigateToProfile = { navController.navigate("profile") },
                    onLogout = {
                        LocalRepository.logout()
                        navController.navigate("login") {
                            popUpTo("student_dashboard") { inclusive = true }
                        }
                    }
                )
            }

            composable("student_certificates") {
                StudentCertificatesScreen(
                    onNavigateToDetails = { certId -> navController.navigate("student_cert_details/$certId") },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "student_cert_details/{certId}",
                arguments = listOf(navArgument("certId") { type = NavType.StringType })
            ) { backStackEntry ->
                val certId = backStackEntry.arguments?.getString("certId") ?: ""
                StudentCertDetailsScreen(
                    certId = certId,
                    onNavigateToQr = { id -> navController.navigate("student_cert_qr/$id") },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "student_cert_qr/{certId}",
                arguments = listOf(navArgument("certId") { type = NavType.StringType })
            ) { backStackEntry ->
                val certId = backStackEntry.arguments?.getString("certId") ?: ""
                StudentCertQrScreen(
                    certId = certId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ==========================================
            // EMPLOYER / VERIFIER MODULE PORTALS
            // ==========================================
            composable("employer_dashboard") {
                EmployerDashboardScreen(
                    onNavigateToQrVerify = { navController.navigate("employer_qr_verify") },
                    onNavigateToVerify = { navController.navigate("employer_verify") },
                    onNavigateToHistory = { navController.navigate("employer_history") },
                    onNavigateToAbout = { navController.navigate("about") },
                    onNavigateToProfile = { navController.navigate("profile") },
                    onLogout = {
                        LocalRepository.logout()
                        navController.navigate("login") {
                            popUpTo("employer_dashboard") { inclusive = true }
                        }
                    }
                )
            }

            composable("employer_qr_verify") {
                EmployerQrVerifyScreen(
                    onNavigateToResult = { hash -> navController.navigate("employer_result/$hash") },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("employer_verify") {
                EmployerVerifyScreen(
                    onNavigateToResult = { hash -> navController.navigate("employer_result/$hash") },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "employer_result/{hash}",
                arguments = listOf(navArgument("hash") { type = NavType.StringType })
            ) { backStackEntry ->
                val hash = backStackEntry.arguments?.getString("hash") ?: ""
                EmployerResultScreen(
                    hash = hash,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("employer_history") {
                EmployerHistoryScreen(onNavigateBack = { navController.popBackStack() })
            }

            // ==========================================
            // GENERAL CORE MODULES
            // ==========================================
            composable("profile") {
                ProfileScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable("about") {
                AboutProjectScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}
