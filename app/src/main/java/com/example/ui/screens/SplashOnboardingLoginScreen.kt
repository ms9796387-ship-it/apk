@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LocalRepository
import com.example.data.UserRole
import com.example.ui.theme.*
import kotlinx.coroutines.delay

// =================================================================
// 1. SPLASH SCREEN
// =================================================================

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "Alpha"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1.05f else 0.85f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "Scale"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                        Color(0xFF0F172A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Hexagon/Blockchain Emblem
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(110.dp * scaleAnim)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(2.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(32.dp))
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Decentralized Security Icon",
                    tint = Color.White,
                    modifier = Modifier.size(54.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            Text(
                text = "CertiChain Auth",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.animateContentSize()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Blockchain-Based Academic Certifications Authentication",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(120.dp))
            
            CircularProgressIndicator(
                color = Color.White.copy(alpha = 0.8f),
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "SECURE • DECENTRALIZED • IMMUTABLE",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.5f),
                letterSpacing = 2.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// =================================================================
// 2. ONBOARDING SCREEN
// =================================================================

@Composable
fun OnboardingScreen(onNavigateToLogin: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }
    
    val onboardingPages = listOf(
        OnboardingData(
            title = "Academic Forgery Prevention",
            desc = "Universities can securely issue academic certifications, transforming credentials into cryptographically signed digital fingerprints.",
            icon = Icons.Filled.VerifiedUser
        ),
        OnboardingData(
            title = "Immutable Ledger Storage",
            desc = "Each certification SHA-256 fingerprint is registered on a decentralized smart contract. Tampering with certificates is impossible.",
            icon = Icons.Filled.Hub
        ),
        OnboardingData(
            title = "Instant Zero-Trust Verification",
            desc = "Employers and verification institutions can scan a QR code to query the blockchain, instantly verifying academic compliance in seconds.",
            icon = Icons.Filled.QrCodeScanner
        )
    )

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicator
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    onboardingPages.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .size(width = if (index == currentPage) 24.dp else 8.dp, height = 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (index == currentPage) MaterialTheme.colorScheme.primary 
                                    else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                                )
                        )
                    }
                }

                Button(
                    onClick = {
                        if (currentPage < onboardingPages.size - 1) {
                            currentPage++
                        } else {
                            onNavigateToLogin()
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (currentPage == onboardingPages.size - 1) "Get Started" else "Next")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "GRADUATION PROJECT",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            Crossfade(targetState = currentPage, label = "PageTransition") { pageIndex ->
                val data = onboardingPages[pageIndex]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
                    ) {
                        Icon(
                            imageVector = data.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(68.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = data.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = data.desc,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

data class OnboardingData(val title: String, val desc: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

// =================================================================
// 3. LOGIN SCREEN
// =================================================================

@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Text(
                text = "Welcome Back",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "Secure Blockchain Academic Registry Access",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            // Form Cards
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = GlassCardShape,
                border = glassCardBorder(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Sign In Credentials",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = null },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onNavigateToForgotPassword) {
                            Text("Forgot Password?", fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Fields cannot be empty"
                                return@Button
                            }
                            isLoading = true
                            // Simulate net network latency
                            errorMessage = null
                            val success = LocalRepository.login(email.trim(), password)
                            if (success) {
                                val role = LocalRepository.currentUser.value!!.role
                                onLoginSuccess(role)
                            } else {
                                errorMessage = "Invalid email or secret credential hash combination."
                                isLoading = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Secure Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Demo Credentials Helper
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                shape = GlassCardShape
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎓 Evaluation Demo Accounts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    DemoCredItem("Admin: admin@edu.com (admin123)", onClick = { email = "admin@edu.com"; password = "admin123" })
                    DemoCredItem("Institution: registrar@mit.edu (mit123)", onClick = { email = "registrar@mit.edu"; password = "mit123" })
                    DemoCredItem("Student: student@edu.com (student123)", onClick = { email = "student@edu.com"; password = "student123" })
                    DemoCredItem("Employer: verifier@google.com (google123)", onClick = { email = "verifier@google.com"; password = "google123" })
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account?", fontSize = 14.sp)
                TextButton(onClick = onNavigateToRegister) {
                    Text("Register Now", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun DemoCredItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        fontWeight = FontWeight.Medium
    )
}

// =================================================================
// 4. REGISTRATION SCREEN
// =================================================================

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegistrationSuccess: () -> Unit
) {
    var selectedRoleIndex by remember { mutableStateOf(0) } // 0: Student, 1: Institution, 2: Employer
    val roles = listOf("Student", "Institution", "Employer")

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var extraField1 by remember { mutableStateOf("") } // Student Card / Company Name
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Create Account", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Enroll in the Network",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "Select your specific role to gain appropriate smart contract and database access permissions.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Segmented Tab Selector
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                roles.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = roles.size),
                        onClick = { 
                            selectedRoleIndex = index
                            name = ""
                            email = ""
                            password = ""
                            extraField1 = ""
                            errorMessage = null
                            successMessage = null
                        },
                        selected = index == selectedRoleIndex
                    ) {
                        Text(label, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = GlassCardShape,
                border = glassCardBorder(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "${roles[selectedRoleIndex]} Registration Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; errorMessage = null },
                        label = { Text(if (selectedRoleIndex == 1) "Institution Name" else "Full Name") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = null },
                        label = { Text("Academic / Business Email") },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedRoleIndex == 0) {
                        // Student card number
                        OutlinedTextField(
                            value = extraField1,
                            onValueChange = { extraField1 = it; errorMessage = null },
                            label = { Text("University Registration / Card ID") },
                            leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    } else if (selectedRoleIndex == 2) {
                        // Company Name
                        OutlinedTextField(
                            value = extraField1,
                            onValueChange = { extraField1 = it; errorMessage = null },
                            label = { Text("Company Name / Employer Agency") },
                            leadingIcon = { Icon(Icons.Filled.Business, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    successMessage?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = it,
                            color = SuccessGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                                errorMessage = "Please fill in all general parameters"
                                return@Button
                            }
                            if ((selectedRoleIndex == 0 || selectedRoleIndex == 2) && extraField1.isBlank()) {
                                errorMessage = "Specific role parameters are missing"
                                return@Button
                            }
                            isLoading = true
                            
                            val isSuccessful = when (selectedRoleIndex) {
                                0 -> LocalRepository.registerStudent(name.trim(), email.trim(), password, extraField1.trim(), 1)
                                1 -> LocalRepository.registerInstitution(name.trim(), email.trim(), password)
                                else -> LocalRepository.registerEmployer(name.trim(), email.trim(), password, extraField1.trim())
                            }

                            if (isSuccessful) {
                                successMessage = "Account registered successfully on the database system!"
                                name = ""
                                email = ""
                                password = ""
                                extraField1 = ""
                                isLoading = false
                                onRegistrationSuccess()
                            } else {
                                errorMessage = "This email is already registered on the node network."
                                isLoading = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Secure Register", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// =================================================================
// 5. FORGOT PASSWORD SCREEN
// =================================================================

@Composable
fun ForgotPasswordScreen(onNavigateBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Credentials Recovery", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            ) {
                Icon(
                    imageVector = Icons.Filled.Key,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Recover Crypto Passkey",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter your registered academic email. We will dispatch a recovery hash to securely override your system passkey.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Registered Email Address") },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            successMsg?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = SuccessGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        successMsg = "A cryptographically signed recovery token has been dispatched to $email. Please check your academic inbox."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Generate Recovery Token", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
