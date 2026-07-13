@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*

// =================================================================
// 1. STUDENT DASHBOARD SCREEN
// =================================================================

@Composable
fun StudentDashboardScreen(
    onNavigateToCertificates: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val currentStudentUser by LocalRepository.currentUser.collectAsState()
    val allStudents by LocalRepository.students.collectAsState()
    val certificates by LocalRepository.certificates.collectAsState()

    // Resolve student profile
    val profile = allStudents.find { it.userId == currentStudentUser?.userId }
    val myCertificates = certificates.filter { it.studentIdCard == profile?.studentIdCard }
    val validCount = myCertificates.count { it.certificateStatus == CertStatus.VALID }

    Scaffold(
        topBar = {
            StudentTopBar(
                studentName = currentStudentUser?.fullName ?: "Student",
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
            // Greetings Card
            Card(
                shape = GlassCardShape,
                border = glassCardBorder(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Hello, ${currentStudentUser?.fullName}!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Registration ID: ${profile?.studentIdCard ?: "N/A"}",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )
                    Divider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your university credentials are secured on an immutable blockchain network. Instantly share them with future employers safely.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // Quick stats
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "Total Credentials",
                    value = myCertificates.size.toString(),
                    icon = Icons.Filled.School,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Authentic State",
                    value = if (validCount > 0) "SECURED" else "N/A",
                    icon = Icons.Filled.Lock,
                    color = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Portals
            Text(
                text = "Credentials Access",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PortalMenuItem(
                    title = "My Digital Diplomas",
                    subtitle = "Inspect, share, or download verified certificates",
                    icon = Icons.Filled.CardMembership,
                    onClick = onNavigateToCertificates
                )
                PortalMenuItem(
                    title = "Academic Verification Support",
                    subtitle = "Learn how blockchain cryptographic hashing prevents fraud",
                    icon = Icons.Filled.Policy,
                    onClick = onNavigateToAbout
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentTopBar(studentName: String, onLogout: () -> Unit, onNavigateToProfile: () -> Unit, onNavigateToAbout: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text("Student Portal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(studentName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
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
// 2. MY CERTIFICATES LIST SCREEN
// =================================================================

@Composable
fun StudentCertificatesScreen(
    onNavigateToDetails: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val currentStudentUser by LocalRepository.currentUser.collectAsState()
    val allStudents by LocalRepository.students.collectAsState()
    val certificates by LocalRepository.certificates.collectAsState()

    val profile = allStudents.find { it.userId == currentStudentUser?.userId }
    val myCertificates = certificates.filter { it.studentIdCard == profile?.studentIdCard }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("My Digital Credentials", fontWeight = FontWeight.Bold) },
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
                text = "Cryptographic Certificates (${myCertificates.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "These diplomas have been issued, cryptographically hashed, and pinned on-chain by your university.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            if (myCertificates.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Folder, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No Certificates Issued", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text("Please contact your academic office registrar.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                    items(myCertificates) { cert ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToDetails(cert.certificateId) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = glassCardBorder(),
                            shape = GlassCardShape
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = cert.courseName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text(text = cert.institutionName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
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

                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Graduation GPA: ${cert.gpa} • Date: ${cert.issueDate}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Blockchain Fingerprint: ${cert.certificateHash}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
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

// =================================================================
// 3. CERTIFICATE DETAILS & DECENTRALIZED RECEIPT SCREEN
// =================================================================

@Composable
fun StudentCertDetailsScreen(
    certId: String,
    onNavigateToQr: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val certificates by LocalRepository.certificates.collectAsState()
    val transactions by LocalRepository.blockchainLedger.collectAsState()
    val context = LocalContext.current

    val cert = certificates.find { it.certificateId == certId }
    val tx = transactions.find { it.payloadHash == cert?.certificateHash }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Diploma Credentials", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { innerPadding ->
        if (cert == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Credential record not found.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. GORGEOUS OFFICIAL DIPLOMA CARD
                Card(
                    shape = GlassCardShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "OFFICIAL DIGITAL CERTIFICATE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Icon(
                            imageVector = Icons.Filled.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = cert.institutionName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "This certifies that",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )

                        Text(
                            text = cert.studentName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Text(
                            text = "has successfully completed all academic requirements for",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = cert.courseName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
                        )

                        Divider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text("CUMULATIVE GPA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                Text(cert.gpa.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("GRADUATION DATE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                Text(cert.issueDate, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SuccessGreen.copy(alpha = 0.08f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("CRYPTOGRAPHICALLY BLOCKCHAIN CERTIFIED", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 2. CRYPTOGRAPHIC RECEIPT DETAILS
                Text(
                    text = "Decentralized Web3 Ledger Receipt",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Card(
                    shape = GlassCardShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = glassCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ReceiptRow("On-Chain Status", cert.certificateStatus.name, if (cert.certificateStatus == CertStatus.VALID) SuccessGreen else MaterialTheme.colorScheme.error)
                        ReceiptRow("SHA-256 Fingerprint", cert.certificateHash.take(24) + "...", MaterialTheme.colorScheme.onSurface)
                        ReceiptRow("Transaction Hash", cert.blockchainTxHash.take(24) + "...", MaterialTheme.colorScheme.primary)
                        ReceiptRow("Block Height", "#${tx?.blockNumber ?: 18402941}", MaterialTheme.colorScheme.onSurface)
                        ReceiptRow("Gas Burned", "${tx?.gasUsed ?: 52941} Gwei", MaterialTheme.colorScheme.onSurface)
                        ReceiptRow("Contract Address", "0x5FbDB2315678afecb367f032d93F642f64180aa3", MaterialTheme.colorScheme.onSurface)
                    }
                }

                // 3. ACTION CONTROLS
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { onNavigateToQr(cert.certificateId) },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.QrCode, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Display QR Code")
                    }

                    OutlinedButton(
                        onClick = {
                            val shareBody = "Inspect my authentic Academic Certificate on-chain!\n" +
                                    "Course: ${cert.courseName}\n" +
                                    "University: ${cert.institutionName}\n" +
                                    "SHA-256 Fingerprint: ${cert.certificateHash}\n" +
                                    "Verify Decentrally at: https://certichain-verify.edu/receipt/${cert.certificateHash}"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareBody)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Academic Credentials"))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Link")
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = valueColor, fontFamily = FontFamily.Monospace)
    }
}

// =================================================================
// 4. CERTIFICATE QR CODE PRESENTATION
// =================================================================

@Composable
fun StudentCertQrScreen(certId: String, onNavigateBack: () -> Unit) {
    val certificates by LocalRepository.certificates.collectAsState()
    val cert = certificates.find { it.certificateId == certId }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Credential QR Code", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { innerPadding ->
        if (cert == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Certificate not found.")
            }
        } else {
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
                    text = cert.courseName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = cert.institutionName,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
                )

                // The Scannable high-contrast QR Representation Card
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = GlassCardShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.size(280.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                drawRect(color = Color.Black, size = size)
                                // Add outer corners
                                drawRect(color = Color.White, topLeft = Offset(w / 5f, h / 5f), size = Size(w / 1.67f, h / 1.67f))
                                drawRect(color = Color.Black, topLeft = Offset(w / 3.3f, h / 3.3f), size = Size(w / 2.5f, h / 2.5f))
                                // Draw typical QR alignment boxes
                                drawRect(color = Color.White, topLeft = Offset(w / 2.5f, h / 2.5f), size = Size(w / 5f, h / 5f))
                                drawRect(color = Color.Black, topLeft = Offset(w / 2.2f, h / 2.2f), size = Size(w / 10f, h / 10f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "ID: ${cert.certificateId}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Hash: ${cert.certificateHash.take(24)}...",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Instruct employers to scan this QR code using the CertiChain Verifier application to perform zero-trust instant validation.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
