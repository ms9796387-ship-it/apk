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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*

// =================================================================
// 1. ADMIN DASHBOARD SCREEN
// =================================================================

@Composable
fun AdminDashboardScreen(
    onNavigateToInstitutions: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToAuditLogs: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val users by LocalRepository.users.collectAsState()
    val institutions by LocalRepository.institutions.collectAsState()
    val students by LocalRepository.students.collectAsState()
    val certificates by LocalRepository.certificates.collectAsState()
    val transactions by LocalRepository.blockchainLedger.collectAsState()
    val verificationRecords by LocalRepository.verificationRecords.collectAsState()

    Scaffold(
        topBar = {
            AdminTopBar(onLogout = onLogout, onNavigateToProfile = onNavigateToProfile, onNavigateToAbout = onNavigateToAbout)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Title
            Text(
                text = "System Admin Controls",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Consolidated Decentralized Governance Console",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Grid Stats Row 1
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "Total Users",
                    value = users.size.toString(),
                    icon = Icons.Filled.People,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Institutions",
                    value = institutions.size.toString(),
                    icon = Icons.Filled.School,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Grid Stats Row 2
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "Certifications",
                    value = certificates.size.toString(),
                    icon = Icons.Filled.CardMembership,
                    color = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Blockchain Tx",
                    value = transactions.size.toString(),
                    icon = Icons.Filled.Link,
                    color = WarningOrange,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Grid Stats Row 3
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "Verifications",
                    value = verificationRecords.size.toString(),
                    icon = Icons.Filled.SafetyCheck,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Students",
                    value = students.size.toString(),
                    icon = Icons.Filled.School,
                    color = Color.Magenta,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Quick Links
            Text(
                text = "Operational Portals",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PortalMenuItem(
                    title = "Institution Accreditation & Approval",
                    subtitle = "Verify authority and issue cryptographic signing permits",
                    icon = Icons.Filled.Verified,
                    onClick = onNavigateToInstitutions
                )
                PortalMenuItem(
                    title = "User Directory Registry",
                    subtitle = "Manage Student, Institution, and Verifier nodes",
                    icon = Icons.Filled.AccountTree,
                    onClick = onNavigateToUsers
                )
                PortalMenuItem(
                    title = "Blockchain Ledger Block Explorer",
                    subtitle = "Inspect block transactions, gas consumed, and ledger receipts",
                    icon = Icons.Filled.Storage,
                    onClick = onNavigateToTransactions
                )
                PortalMenuItem(
                    title = "Immutable Audit & Integrity Logs",
                    subtitle = "Track administrative, security, and verification actions",
                    icon = Icons.Filled.ListAlt,
                    onClick = onNavigateToAuditLogs
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopBar(onLogout: () -> Unit, onNavigateToProfile: () -> Unit, onNavigateToAbout: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("CertiChain Admin", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        actions = {
            IconButton(onClick = onNavigateToAbout) {
                Icon(Icons.Filled.Info, contentDescription = "About Project")
            }
            IconButton(onClick = onNavigateToProfile) {
                Icon(Icons.Filled.AccountCircle, contentDescription = "Profile")
            }
            IconButton(onClick = onLogout) {
                Icon(Icons.Filled.ExitToApp, contentDescription = "Log Out", tint = MaterialTheme.colorScheme.error)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = GlassCardShape,
        border = glassCardBorder(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun PortalMenuItem(
    title: String,
    subtitle: String,
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
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// =================================================================
// 2. INSTITUTION ACCREDITATION LIST SCREEN
// =================================================================

@Composable
fun AdminInstitutionsScreen(onNavigateBack: () -> Unit) {
    val institutions by LocalRepository.institutions.collectAsState()

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Academic Accreditation", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Accredited Universities List",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Only authorized accredited universities are assigned smart contract keys to issue verified academic credentials.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
            }

            items(institutions) { inst ->
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = GlassCardShape,
                    border = glassCardBorder(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = inst.institutionName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            // Status tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (inst.accreditationStatus) SuccessGreen.copy(alpha = 0.1f)
                                        else WarningOrange.copy(alpha = 0.1f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (inst.accreditationStatus) "APPROVED" else "PENDING",
                                    color = if (inst.accreditationStatus) SuccessGreen else WarningOrange,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Linked Node Owner Wallet: 0x" + CryptoUtils.sha256(inst.institutionName).substring(0, 40),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (!inst.accreditationStatus) {
                            Button(
                                onClick = { LocalRepository.approveInstitution(inst.institutionId) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                            ) {
                                Icon(Icons.Filled.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Accredit & Approve Authority")
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Authorized Web3 Issuing Key Enabled",
                                    fontSize = 12.sp,
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Medium
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
// 3. USER MANAGEMENT SCREEN
// =================================================================

@Composable
fun AdminUsersScreen(onNavigateBack: () -> Unit) {
    val users by LocalRepository.users.collectAsState()

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Network User Directory", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "System Nodes & Roles",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Role-Based Access Control list enforcing cryptographically signed node roles.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
            }

            items(users) { usr ->
                Card(
                    shape = GlassCardShape,
                    border = glassCardBorder(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (usr.role) {
                                        UserRole.ADMIN -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        UserRole.INSTITUTION -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                                        UserRole.STUDENT -> SuccessGreen.copy(alpha = 0.1f)
                                        UserRole.EMPLOYER -> Color.Magenta.copy(alpha = 0.1f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (usr.role) {
                                    UserRole.ADMIN -> Icons.Filled.Shield
                                    UserRole.INSTITUTION -> Icons.Filled.School
                                    UserRole.STUDENT -> Icons.Filled.School
                                    UserRole.EMPLOYER -> Icons.Filled.Business
                                },
                                contentDescription = null,
                                tint = when (usr.role) {
                                    UserRole.ADMIN -> MaterialTheme.colorScheme.primary
                                    UserRole.INSTITUTION -> MaterialTheme.colorScheme.secondary
                                    UserRole.STUDENT -> SuccessGreen
                                    UserRole.EMPLOYER -> Color.Magenta
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = usr.fullName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = usr.email, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        // Role tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = usr.role.name,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// =================================================================
// 4. BLOCKCHAIN TRANSACTIONS SCREEN
// =================================================================

@Composable
fun AdminTransactionsScreen(onNavigateBack: () -> Unit) {
    val transactions by LocalRepository.blockchainLedger.collectAsState()
    var selectedTx by remember { mutableStateOf<BlockchainTransaction?>(null) }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Blockchain Transactions Ledger", fontWeight = FontWeight.Bold) },
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
                text = "Simulated Sepolia Testnet State",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Cryptographic ledger records immutable certificate pinning. Select any transaction to view block receipts.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                items(transactions.reversed()) { tx ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTx = tx },
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        shape = GlassCardShape,
                        border = glassCardBorder(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Link,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Block #${tx.blockNumber}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SuccessGreen.copy(alpha = 0.1f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("CONFIRMED", color = SuccessGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "TX: ${tx.txHash}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Gas Used: ${tx.gasUsed} gwei • Hash: ${tx.payloadHash.take(16)}...",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }

    // Block Receipt Dialog
    selectedTx?.let { tx ->
        AlertDialog(
            onDismissRequest = { selectedTx = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ReceiptLong, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Blockchain Block Receipt", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReceiptField("Transaction Hash", tx.txHash)
                    ReceiptField("Block Height", "#${tx.blockNumber}")
                    ReceiptField("Sender Address (Inst)", tx.fromAddress)
                    ReceiptField("Contract Address", tx.toAddress)
                    ReceiptField("Gas Gas Limit/Used", "${tx.gasUsed} Gwei")
                    ReceiptField("Value Transferred", "${tx.valueInEth} ETH (Payload Only)")
                    ReceiptField("Certificate Fingerprint (SHA-256)", tx.payloadHash)
                    ReceiptField("Consensus Timestamp", tx.timestamp)
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedTx = null }) {
                    Text("Close Receipt")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun ReceiptField(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        Text(
            text = value,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 1.dp)
        )
    }
}

// =================================================================
// 5. AUDIT LOGS SCREEN
// =================================================================

@Composable
fun AdminAuditLogsScreen(onNavigateBack: () -> Unit) {
    val auditLogs by LocalRepository.auditLogs.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredLogs = auditLogs.filter { log ->
        log.userName.contains(searchQuery, ignoreCase = true) ||
        log.action.contains(searchQuery, ignoreCase = true) ||
        log.description.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Immutable Audit Trail", fontWeight = FontWeight.Bold) },
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
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Audit Logs") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Filled.Clear, contentDescription = null) }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(10.dp)
            )

            Text(
                text = "Relational Audit Logs Record (${filteredLogs.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredLogs.reversed()) { log ->
                    Card(
                        shape = GlassCardShape,
                        border = glassCardBorder(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = log.action,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = log.timestamp,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = log.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "By: ${log.userName} (User ID: ${log.userId ?: 0})",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}
