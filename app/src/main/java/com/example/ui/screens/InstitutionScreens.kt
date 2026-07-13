@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// =================================================================
// 1. INSTITUTION DASHBOARD SCREEN
// =================================================================

@Composable
fun InstitutionDashboardScreen(
    onNavigateToStudents: () -> Unit,
    onNavigateToAddStudent: () -> Unit,
    onNavigateToIssue: () -> Unit,
    onNavigateToCertificates: () -> Unit,
    onNavigateToRevoked: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val currentInstUser by LocalRepository.currentUser.collectAsState()
    val institutions by LocalRepository.institutions.collectAsState()
    val allStudents by LocalRepository.students.collectAsState()
    val certificates by LocalRepository.certificates.collectAsState()
    val verifications by LocalRepository.verificationRecords.collectAsState()

    val profile = institutions.find { it.userId == currentInstUser?.userId }
    val isAccredited = profile?.accreditationStatus ?: false

    // Filter data for this specific institution
    val myStudents = allStudents.filter { it.institutionId == profile?.institutionId }
    val myCertificates = certificates.filter { it.institutionId == profile?.institutionId }
    val validCertsCount = myCertificates.count { it.certificateStatus == CertStatus.VALID }
    val revokedCertsCount = myCertificates.count { it.certificateStatus == CertStatus.REVOKED }

    Scaffold(
        floatingActionButton = {
            if (isAccredited) {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToIssue,
                    icon = { Icon(Icons.Filled.AddCard, contentDescription = null) },
                    text = { Text("Issue Certificate") },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            }
        },
        topBar = {
            InstitutionTopBar(
                instName = profile?.institutionName ?: "Academic Registrar",
                onLogout = onLogout,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToAbout = onNavigateToAbout
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Accreditation Alert Banner
            if (!isAccredited) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = WarningOrange.copy(alpha = 0.08f)),
                    border = glassCardBorder(),
                    shape = GlassCardShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Pending Accreditation", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WarningOrange)
                            Text(
                                "An administrator must accredit your institution node before you can issue on-chain certifications.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.08f)),
                    border = glassCardBorder(),
                    shape = GlassCardShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.GppGood, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Authorized Registrar Node Active", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SuccessGreen)
                            Text(
                                "Gas limits authorized. Smart contract wallet: 0x" + CryptoUtils.sha256(profile?.institutionName ?: "").substring(0, 32) + "...",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // Title
            Text(
                text = "Registrar Operations",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Manage student registers and sign certifications on Ethereum.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Grid Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "Enrolled Students",
                    value = myStudents.size.toString(),
                    icon = Icons.Filled.School,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Valid Credentials",
                    value = validCertsCount.toString(),
                    icon = Icons.Filled.CardMembership,
                    color = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "Revoked Credentials",
                    value = revokedCertsCount.toString(),
                    icon = Icons.Filled.ReportGmailerrorred,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Audit Logs",
                    value = verifications.size.toString(),
                    icon = Icons.Filled.QueryStats,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Quick Links
            Text(
                text = "Student & Cert Management",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PortalMenuItem(
                    title = "Student Directory Registry",
                    subtitle = "Review and add university student records",
                    icon = Icons.Filled.Badge,
                    onClick = onNavigateToStudents
                )
                PortalMenuItem(
                    title = "Register New Student",
                    subtitle = "Enroll and assign a matriculation card number",
                    icon = Icons.Filled.PersonAdd,
                    onClick = onNavigateToAddStudent
                )
                PortalMenuItem(
                    title = "Active Credentials Directory",
                    subtitle = "Verify or revoke published academic certificates",
                    icon = Icons.Filled.LibraryBooks,
                    onClick = onNavigateToCertificates
                )
                PortalMenuItem(
                    title = "Revoked Certifications Ledger",
                    subtitle = "Inspect invalid credentials and revocation reasons",
                    icon = Icons.Filled.Block,
                    onClick = onNavigateToRevoked
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstitutionTopBar(instName: String, onLogout: () -> Unit, onNavigateToProfile: () -> Unit, onNavigateToAbout: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text("Academic Registrar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(instName, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        },
        actions = {
            IconButton(onClick = onNavigateToAbout) { Icon(Icons.Filled.Info, contentDescription = "About") }
            IconButton(onClick = onNavigateToProfile) { Icon(Icons.Filled.AccountCircle, contentDescription = "Profile") }
            IconButton(onClick = onLogout) { Icon(Icons.Filled.ExitToApp, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error) }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

// =================================================================
// 2. STUDENT DIRECTORY MANAGEMENT SCREEN
// =================================================================

@Composable
fun InstitutionStudentsScreen(onNavigateToAddStudent: () -> Unit, onNavigateBack: () -> Unit) {
    val currentInstUser by LocalRepository.currentUser.collectAsState()
    val institutions by LocalRepository.institutions.collectAsState()
    val allStudents by LocalRepository.students.collectAsState()
    val users by LocalRepository.users.collectAsState()

    val profile = institutions.find { it.userId == currentInstUser?.userId }
    val myStudents = allStudents.filter { it.institutionId == profile?.institutionId }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddStudent,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Student")
            }
        },
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("University Student Registry", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Registered Students (${myStudents.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Students registered under this institutional node who are eligible for blockchain certificate issuance.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            if (myStudents.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.School, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No Students Enrolled", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                    items(myStudents) { stud ->
                        val u = users.find { it.userId == stud.userId }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = glassCardBorder(),
                            shape = GlassCardShape
                        ) {
                            Row(modifier = Modifier.padding(14.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = u?.fullName ?: "Unknown Student", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "Card ID: ${stud.studentIdCard} • Email: ${u?.email ?: "N/A"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =================================================================
// 3. ENROLL NEW STUDENT SCREEN
// =================================================================

@Composable
fun InstitutionAddStudentScreen(onNavigateBack: () -> Unit) {
    val currentInstUser by LocalRepository.currentUser.collectAsState()
    val institutions by LocalRepository.institutions.collectAsState()
    val profile = institutions.find { it.userId == currentInstUser?.userId }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var idCard by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var successMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Enroll Student", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
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
                text = "Enroll in University Registry",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Enrolling a student records their basic academic profile in the MySQL database system so they can receive blockchain-pinned certificates.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = GlassCardShape,
                border = glassCardBorder(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Student Academic Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it; errorMsg = null },
                        label = { Text("Student Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMsg = null },
                        label = { Text("Student Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = idCard,
                        onValueChange = { idCard = it; errorMsg = null },
                        label = { Text("University Student Registration / Card ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMsg = null },
                        label = { Text("Assign Default Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    errorMsg?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    successMsg?.let {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = it, color = SuccessGreen, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (fullName.isBlank() || email.isBlank() || idCard.isBlank() || password.isBlank()) {
                                errorMsg = "All parameters are required"
                                return@Button
                            }
                            val success = LocalRepository.registerStudent(
                                fullName.trim(),
                                email.trim(),
                                password,
                                idCard.trim(),
                                profile?.institutionId ?: 1
                            )
                            if (success) {
                                successMsg = "Student enrolled successfully under ${profile?.institutionName}!"
                                fullName = ""
                                email = ""
                                idCard = ""
                                password = ""
                            } else {
                                errorMsg = "Email already exists in the node directory registry."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Add Student to Registry", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

// =================================================================
// 4. BLOCKCHAIN CERTIFICATE ISSUANCE (WIZARD FLOW)
// =================================================================

@Composable
fun InstitutionIssueCertScreen(onNavigateBack: () -> Unit) {
    val currentInstUser by LocalRepository.currentUser.collectAsState()
    val institutions by LocalRepository.institutions.collectAsState()
    val students by LocalRepository.students.collectAsState()
    val users by LocalRepository.users.collectAsState()

    val profile = institutions.find { it.userId == currentInstUser?.userId }
    val myStudents = students.filter { it.institutionId == profile?.institutionId }

    var step by remember { mutableStateOf(1) } // 1: Fill form, 2: Upload & Hash, 3: Blockchain commitment, 4: QR Generated success

    // Form states
    var selectedStudentId by remember { mutableStateOf<Int?>(null) }
    var courseName by remember { mutableStateOf("") }
    var gpaText by remember { mutableStateOf("") }
    var gradDateStr by remember { mutableStateOf("2026-06-25") }
    var selectedFileName by remember { mutableStateOf("") }
    var isUploadingPdf by remember { mutableStateOf(false) }

    // Computed blockchain state
    var computedSha256 by remember { mutableStateOf("") }
    var generatedCertId by remember { mutableStateOf("") }
    var generatedTxHash by remember { mutableStateOf("") }
    var generatedBlockNum by remember { mutableStateOf(0L) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Certificate Issuance", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
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
            // Wizard step indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepBadge("1. Details", step == 1, step > 1)
                Divider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                StepBadge("2. SHA-256", step == 2, step > 2)
                Divider(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                StepBadge("3. Blockchain", step >= 3, step > 3)
            }

            AnimatedContent(targetState = step, label = "WizardStep") { currentStep ->
                when (currentStep) {
                    1 -> {
                        // STEP 1: Enter details
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("Step 1: Certificate Metadata Details", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            
                            // Student Selection Dropdown/Cards
                            Column {
                                Text("Select Enrolled Student", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
                                if (myStudents.isEmpty()) {
                                    Text("No students enrolled. Enroll student first.", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                                } else {
                                    Card(
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            myStudents.forEach { stud ->
                                                val u = users.find { it.userId == stud.userId }
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { selectedStudentId = stud.studentId }
                                                        .background(
                                                            if (selectedStudentId == stud.studentId) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                                            else Color.Transparent
                                                        )
                                                        .padding(12.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    RadioButton(
                                                        selected = selectedStudentId == stud.studentId,
                                                        onClick = { selectedStudentId = stud.studentId }
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text(text = u?.fullName ?: "N/A", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                        Text(text = "ID Card: ${stud.studentIdCard}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = courseName,
                                onValueChange = { courseName = it },
                                label = { Text("Course Name / Major") },
                                placeholder = { Text("e.g. Master of Computer Science") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                OutlinedTextField(
                                    value = gpaText,
                                    onValueChange = { gpaText = it },
                                    label = { Text("Cumlative GPA") },
                                    placeholder = { Text("e.g. 3.92") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                OutlinedTextField(
                                    value = gradDateStr,
                                    onValueChange = { gradDateStr = it },
                                    label = { Text("Date of Graduation") },
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            Button(
                                onClick = {
                                    if (selectedStudentId == null || courseName.isBlank() || gpaText.isBlank()) {
                                        return@Button
                                    }
                                    step = 2
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Next: Upload PDF Certificate", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    2 -> {
                        // STEP 2: PDF simulated uploading & Hashing
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Step 2: Generate PDF Hash", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                            Text(
                                "Upload the official digital PDF certificate copy. The system will compute a local SHA-256 fingerprint of the document to enforce integrity verification.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                                    .clickable {
                                        if (selectedFileName.isEmpty()) {
                                            isUploadingPdf = true
                                            coroutineScope.launch {
                                                delay(2000) // Simulating reading & hashing file
                                                selectedFileName = "${courseName.replace(" ", "_").lowercase()}_diploma.pdf"
                                                val student = myStudents.find { it.studentId == selectedStudentId }
                                                computedSha256 = LocalRepository.computePdfHash(selectedFileName, 482012, gpaText.toDoubleOrNull() ?: 3.5, student?.studentIdCard ?: "N/A")
                                                isUploadingPdf = false
                                            }
                                        }
                                    },
                                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    if (isUploadingPdf) {
                                        CircularProgressIndicator(modifier = Modifier.size(40.dp))
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("Hashing document local byte streams...", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    } else if (selectedFileName.isNotEmpty()) {
                                        Icon(Icons.Filled.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(54.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(selectedFileName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Size: 471.8 KB", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    } else {
                                        Icon(Icons.Outlined.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(54.dp))
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("Select Certificate PDF File", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("PDF representation of academic credential", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    }
                                }
                            }

                            if (computedSha256.isNotEmpty()) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("Computed Cryptographic Fingerprint", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Text(
                                            text = computedSha256,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.padding(top = 4.dp),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    OutlinedButton(
                                        onClick = { step = 1 },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Back")
                                    }
                                    Button(
                                        onClick = {
                                            isUploadingPdf = true
                                            coroutineScope.launch {
                                                delay(2500) // Simulating Smart Contract write mining
                                                val cert = LocalRepository.issueCertificate(
                                                    selectedStudentId!!,
                                                    courseName,
                                                    gpaText.toDoubleOrNull() ?: 3.0,
                                                    gradDateStr
                                                )
                                                if (cert != null) {
                                                    generatedCertId = cert.certificateId
                                                    generatedTxHash = cert.blockchainTxHash
                                                    generatedBlockNum = LocalRepository.blockchainLedger.value.find { it.payloadHash == computedSha256 }?.blockNumber ?: 18402955
                                                    step = 3
                                                }
                                                isUploadingPdf = false
                                            }
                                        },
                                        modifier = Modifier.weight(2f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Commit to Blockchain", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // STEP 3: Blockchain Commitment Mining Animation & Success QR
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(68.dp))
                            Text("Transaction Successful", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "Certificate fingerprint committed on-chain! The digital credential is now immutable and globally verifiable.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.3f)),
                                colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.04f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Certificate ID", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(generatedCertId, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                                    }
                                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Block Height", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("#$generatedBlockNum", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                    }
                                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text("Sepolia Transaction Hash", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 2.dp))
                                        Text(generatedTxHash, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }

                            // QR Preview Mock
                            Card(
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.padding(vertical = 12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(160.dp)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // Simple simulated QR pattern
                                        Canvas(modifier = Modifier.size(140.dp)) {
                                            val w = size.width
                                            val h = size.height
                                            drawRect(color = Color.Black, size = size)
                                            // Make center hollow to look like QR
                                            drawRect(color = Color.White, topLeft = Offset(w * 0.25f, h * 0.25f), size = Size(w * 0.5f, h * 0.5f))
                                            drawRect(color = Color.Black, topLeft = Offset(w * 0.37f, h * 0.37f), size = Size(w * 0.26f, h * 0.26f))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Scannable Academic QR Code", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }

                            Button(
                                onClick = {
                                    step = 1
                                    selectedStudentId = null
                                    courseName = ""
                                    gpaText = ""
                                    selectedFileName = ""
                                    computedSha256 = ""
                                    onNavigateBack()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Complete and Close")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepBadge(text: String, isCurrent: Boolean, isPast: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                when {
                    isCurrent -> MaterialTheme.colorScheme.primary
                    isPast -> SuccessGreen
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                }
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isCurrent || isPast) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

// =================================================================
// 5. ISSUED CERTIFICATES SCREEN (WITH REVOCATION CONTROL)
// =================================================================

@Composable
fun InstitutionCertificatesScreen(onNavigateBack: () -> Unit) {
    val currentInstUser by LocalRepository.currentUser.collectAsState()
    val institutions by LocalRepository.institutions.collectAsState()
    val certificates by LocalRepository.certificates.collectAsState()

    val profile = institutions.find { it.userId == currentInstUser?.userId }
    val myCertificates = certificates.filter { it.institutionId == profile?.institutionId }

    var selectedCertForRevoke by remember { mutableStateOf<Certificate?>(null) }
    var revocationReason by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Active University Credentials", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Issued Certificates Registry (${myCertificates.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Live credentials signed by your private key. You maintain the authority to revoke individual credentials due to academic audit updates.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            if (myCertificates.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.FolderOpen, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No Certificates Issued", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                    items(myCertificates) { cert ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = cert.courseName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(text = "ID: ${cert.certificateId} • Grad Date: ${cert.issueDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (cert.certificateStatus == CertStatus.VALID) SuccessGreen.copy(alpha = 0.1f)
                                                else MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = cert.certificateStatus.name,
                                            color = if (cert.certificateStatus == CertStatus.VALID) SuccessGreen else MaterialTheme.colorScheme.error,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Student: ${cert.studentName} (ID Card: ${cert.studentIdCard})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Fingerprint: ${cert.certificateHash}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                if (cert.certificateStatus == CertStatus.VALID) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    OutlinedButton(
                                        onClick = { selectedCertForRevoke = cert },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Filled.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Revoke Credentials On-Chain")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Revocation Alert Dialog
    selectedCertForRevoke?.let { cert ->
        AlertDialog(
            onDismissRequest = { selectedCertForRevoke = null; revocationReason = "" },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm Cryptographic Revocation", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "You are about to write a permanent Revocation Flag onto the blockchain ledger for ID ${cert.certificateId}. This operation is immutable.",
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = revocationReason,
                        onValueChange = { revocationReason = it },
                        label = { Text("Reason for Revocation") },
                        placeholder = { Text("e.g. Audit revision / typo correction") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (revocationReason.isNotBlank()) {
                            LocalRepository.revokeCertificate(cert.certificateId, revocationReason)
                            selectedCertForRevoke = null
                            revocationReason = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Confirm Immutable Revocation")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCertForRevoke = null; revocationReason = "" }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// =================================================================
// 6. REVOKED CERTIFICATES LEDGER SCREEN
// =================================================================

@Composable
fun InstitutionRevokedScreen(onNavigateBack: () -> Unit) {
    val currentInstUser by LocalRepository.currentUser.collectAsState()
    val institutions by LocalRepository.institutions.collectAsState()
    val certificates by LocalRepository.certificates.collectAsState()

    val profile = institutions.find { it.userId == currentInstUser?.userId }
    val myRevokedCertificates = certificates.filter { it.institutionId == profile?.institutionId && it.certificateStatus == CertStatus.REVOKED }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Revocation Registry", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Revocation History Log (${myRevokedCertificates.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "These academic certifications were invalidated by the authority. These remain published on blockchain ledger as REVOKED for transparent verification auditing.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            if (myRevokedCertificates.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.GppGood, contentDescription = null, modifier = Modifier.size(64.dp), tint = SuccessGreen.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No Revoked Certificates", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                    items(myRevokedCertificates) { cert ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.25f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = cert.courseName, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("REVOKED", color = MaterialTheme.colorScheme.error, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Student: ${cert.studentName} (Matriculation: ${cert.studentIdCard})", fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Reason for Revocation: \"${cert.revocationReason}\"", fontSize = 12.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Revocation Tx: ${cert.blockchainTxHash}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
