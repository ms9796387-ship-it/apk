@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// =================================================================
// 1. EMPLOYER DASHBOARD SCREEN
// =================================================================

@Composable
fun EmployerDashboardScreen(
    onNavigateToQrVerify: () -> Unit,
    onNavigateToVerify: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val verificationRecords by LocalRepository.verificationRecords.collectAsState()
    
    // Calculate metrics
    val totalVerifications = verificationRecords.size
    val verifiedCount = verificationRecords.count { it.verificationResult == "VERIFIED" }
    val successRate = if (totalVerifications > 0) (verifiedCount * 100) / totalVerifications else 100

    Scaffold(
        topBar = {
            EmployerTopBar(
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
            // Welcome Header
            Text(
                text = "Credentials Verifier",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Perform decentralized cryptographic verification of academic certificates.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Stats Cards
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "Total Queries",
                    value = totalVerifications.toString(),
                    icon = Icons.Filled.SafetyCheck,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Verification Success",
                    value = "$successRate%",
                    icon = Icons.Filled.Verified,
                    color = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Sections
            Text(
                text = "Verification Methods",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Method 1: QR Code Scanner
                VerificationMethodItem(
                    title = "Scan Credentials QR Code",
                    desc = "Use the high-speed camera scanner to scan a student's diploma QR code for instant blockchain query.",
                    icon = Icons.Filled.QrCodeScanner,
                    onClick = onNavigateToQrVerify
                )

                // Method 2: Manual Certificate / PDF Hashing
                VerificationMethodItem(
                    title = "Integrity & PDF Hash Verifier",
                    desc = "Verify certificates by entering ID, pasting SHA-256 fingerprint, or uploading a diploma file directly.",
                    icon = Icons.Filled.UploadFile,
                    onClick = onNavigateToVerify
                )

                // Method 3: Queries History
                VerificationMethodItem(
                    title = "Verification Queries Log",
                    desc = "Inspect logs of past verification actions and generated auditing compliance reports.",
                    icon = Icons.Filled.History,
                    onClick = onNavigateToHistory
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerTopBar(onLogout: () -> Unit, onNavigateToProfile: () -> Unit, onNavigateToAbout: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Verifier Console", fontWeight = FontWeight.Bold, fontSize = 18.sp)
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

@Composable
fun VerificationMethodItem(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = GlassCardShape,
        border = glassCardBorder(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = desc,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Launch Tool",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// =================================================================
// 2. CAMERA QR SCANNER MOCK-UP
// =================================================================

@Composable
fun EmployerQrVerifyScreen(
    onNavigateToResult: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isScanning by remember { mutableStateOf(true) }
    var laserOffset by remember { mutableStateOf(0f) }

    // Floating laser effect animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 240f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Academic QR Scanner", fontWeight = FontWeight.Bold) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Cryptographic Scanner Viewfinder",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Position the diploma QR code within the scanning bracket. Authenticity results will load automatically.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Scanning Viewfinder Bracket Box
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Simulated camera feed background with a mock QR pattern
                Canvas(modifier = Modifier.size(180.dp)) {
                    val w = size.width
                    val h = size.height
                    drawRect(color = Color.DarkGray, size = size)
                    // QR Corners
                    drawRect(color = Color.Black, topLeft = Offset(w / 8f, h / 8f), size = Size(w / 3f, h / 3f))
                    drawRect(color = Color.White, topLeft = Offset(w / 5.5f, h / 5.5f), size = Size(w / 5f, h / 5f))
                }

                // Laser sweep line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .offset(y = (laserY - 120).dp)
                        .background(Color.Red.copy(alpha = 0.8f))
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(2.dp))
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Demo Selection list to trigger scan outputs
            Text(
                text = "Select Simulated QR to Scan (Demo Evaluation):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Button(
                onClick = {
                    isScanning = false
                    coroutineScope.launch {
                        delay(800)
                        // John Doe's valid certificate hash
                        val targetHash = CryptoUtils.sha256("CERT_payload:MIT-2023-CS081:Computer Science:3.95")
                        onNavigateToResult(targetHash)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Filled.Verified, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scan John Doe's CS Certificate (VALID)")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    isScanning = false
                    coroutineScope.launch {
                        delay(800)
                        // A modified hash (tampered)
                        val invalidHash = "f3b49e29a1b1836a940fcb28c049ee981a8bdf1da4a04d3e58b16e8647c21fake"
                        onNavigateToResult(invalidHash)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Filled.Cancel, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scan Modified/Forged Certificate (TAMPERED)")
            }
        }
    }
}

// =================================================================
// 3. MANUAL CREDENTIALS / PDF HASH VERIFIER SCREEN
// =================================================================

@Composable
fun EmployerVerifyScreen(
    onNavigateToResult: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var certId by remember { mutableStateOf("") }
    var certHash by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // PDF simulated upload verifier states
    var isUploadingPdf by remember { mutableStateOf(false) }
    var selectedPdfName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Integrity Verifier Tool", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Manual Certificate Verification",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Enter the Certificate ID or paste the SHA-256 document fingerprint directly to perform an instant blockchain lookup query.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // OPTION 1: Verify using Certificate ID
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = glassCardBorder(),
                shape = GlassCardShape
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Option 1: Query by Certificate ID", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = certId,
                        onValueChange = { certId = it; errorMsg = null },
                        label = { Text("e.g. CERT-MIT-2026-00412") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (certId.isBlank()) return@Button
                            val res = LocalRepository.verifyCertificateByID(certId.trim())
                            if (res.certificate != null) {
                                onNavigateToResult(res.certificate.certificateHash)
                            } else {
                                errorMsg = "Certificate ID not found on database or blockchain."
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Query Certificate ID")
                    }
                }
            }

            // OPTION 2: Verify using Cryptographic Hash
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = glassCardBorder(),
                shape = GlassCardShape
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Option 2: Query by SHA-256 Fingerprint", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = certHash,
                        onValueChange = { certHash = it; errorMsg = null },
                        label = { Text("Enter 64-hex SHA-256 hash") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (certHash.isBlank()) return@Button
                            onNavigateToResult(certHash.trim())
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Query Crytographic Hash")
                    }
                }
            }

            // OPTION 3: Verify by PDF File Upload (Extremely elegant demo feature!)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = glassCardBorder(),
                shape = GlassCardShape
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Option 3: Query by PDF File Upload (Demo)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)),
                        shape = GlassCardShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isUploadingPdf = true
                                coroutineScope.launch {
                                    delay(1500)
                                    selectedPdfName = "john_doe_cs_degree_diploma.pdf"
                                    // Generate the exact authentic hash
                                    certHash = CryptoUtils.sha256("CERT_payload:MIT-2023-CS081:Computer Science:3.95")
                                    isUploadingPdf = false
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isUploadingPdf) {
                                CircularProgressIndicator(modifier = Modifier.size(30.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Computing SHA-256 of file stream...", fontSize = 11.sp)
                            } else if (selectedPdfName.isNotEmpty()) {
                                Icon(Icons.Filled.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(36.dp))
                                Text(selectedPdfName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("SHA-256: ${certHash.take(16)}...", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            } else {
                                Icon(Icons.Filled.UploadFile, contentDescription = null, modifier = Modifier.size(36.dp))
                                Text("Click to select simulated diploma PDF", fontSize = 12.sp)
                            }
                        }
                    }

                    if (selectedPdfName.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onNavigateToResult(certHash) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Validate PDF Authenticity")
                        }
                    }
                }
            }

            errorMsg?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// =================================================================
// 4. VERIFICATION RESULTS DETAILS SCREEN
// =================================================================

@Composable
fun EmployerResultScreen(
    hash: String,
    onNavigateBack: () -> Unit
) {
    val result = remember { LocalRepository.verifyCertificateByHash(hash) }
    val cert = result.certificate
    val tx = result.transaction

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Query Verification Receipt", fontWeight = FontWeight.Bold) },
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Big visual emblem
            val (emblem, tint, textTitle) = when (result.status) {
                "VERIFIED" -> Triple(Icons.Filled.CheckCircle, SuccessGreen, "AUTHENTIC & VERIFIED")
                "REVOKED" -> Triple(Icons.Filled.Warning, WarningOrange, "REVOKED ON-CHAIN")
                else -> Triple(Icons.Filled.Cancel, MaterialTheme.colorScheme.error, "TAMPERED / FORGED")
            }

            Icon(
                imageVector = emblem,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(72.dp)
            )

            Text(
                text = textTitle,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = tint
            )

            Text(
                text = result.message,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Divider()

            if (cert != null) {
                // 1. Certificate details
                Text(
                    text = "Academic Registry Credentials",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Start)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = glassCardBorder(),
                    shape = GlassCardShape,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ResultDetailField("Graduate Student Name", cert.studentName)
                        ResultDetailField("University Registration ID", cert.studentIdCard)
                        ResultDetailField("Degree / Academic major", cert.courseName)
                        ResultDetailField("Issuing University Node", cert.institutionName)
                        ResultDetailField("Graduation Date", cert.issueDate)
                        ResultDetailField("Cumulative GPA", cert.gpa.toString())
                    }
                }
            }

            if (tx != null) {
                // 2. Blockchain ledger receipt details
                Text(
                    text = "On-Chain Transaction Details",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Start)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = glassCardBorder(),
                    shape = GlassCardShape,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ResultDetailField("Eth Block Height", "#${tx.blockNumber}")
                        ResultDetailField("Transaction Hash (Tx)", tx.txHash)
                        ResultDetailField("Contract Address", tx.toAddress)
                        ResultDetailField("Gas Units Consumed", "${tx.gasUsed} Gwei")
                        ResultDetailField("Cryptographic Signature", tx.fromAddress)
                        ResultDetailField("Consensus Timestamp", tx.timestamp)
                    }
                }
            }

            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Close Verification Receipt")
            }
        }
    }
}

@Composable
fun ResultDetailField(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        Text(text = value, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
    }
}

// =================================================================
// 5. VERIFICATION HISTORY SCREEN
// =================================================================

@Composable
fun EmployerHistoryScreen(onNavigateBack: () -> Unit) {
    val records by LocalRepository.verificationRecords.collectAsState()

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Verification Queries History", fontWeight = FontWeight.Bold) },
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
                text = "Verification Audit Logs (${records.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Historic log of decentralized validation actions executed under your employer node credentials.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            if (records.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No past verifications recorded.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                    items(records.reversed()) { rec ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = glassCardBorder(),
                            shape = GlassCardShape
                        ) {
                            Row(modifier = Modifier.padding(14.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (rec.verificationResult == "VERIFIED") Icons.Filled.CheckCircle
                                    else if (rec.verificationResult == "REVOKED") Icons.Filled.Warning
                                    else Icons.Filled.Cancel,
                                    contentDescription = null,
                                    tint = if (rec.verificationResult == "VERIFIED") SuccessGreen
                                    else if (rec.verificationResult == "REVOKED") WarningOrange
                                    else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Cert: ${rec.certificateId ?: "N/A"}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "Hash Queried: ${rec.queriedHash.take(16)}...", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    Text(text = rec.verificationDate, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (rec.verificationResult == "VERIFIED") SuccessGreen.copy(alpha = 0.1f)
                                            else if (rec.verificationResult == "REVOKED") WarningOrange.copy(alpha = 0.1f)
                                            else MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = rec.verificationResult,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (rec.verificationResult == "VERIFIED") SuccessGreen
                                        else if (rec.verificationResult == "REVOKED") WarningOrange
                                        else MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
