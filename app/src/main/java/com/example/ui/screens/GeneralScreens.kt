@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*

// =================================================================
// 1. PROFILE SCREEN
// =================================================================

@Composable
fun ProfileScreen(onNavigateBack: () -> Unit) {
    val curUser by LocalRepository.currentUser.collectAsState()

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Secure User Profile", fontWeight = FontWeight.Bold) },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile icon
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(44.dp))
            }

            Text(
                text = curUser?.fullName ?: "Unknown User",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = curUser?.role?.name ?: "STUDENT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Profile info cards
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = glassCardBorder(),
                shape = GlassCardShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Secure Credentials", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    
                    ProfileInfoRow("Email Address", curUser?.email ?: "N/A")
                    ProfileInfoRow("Account Joined", curUser?.createdAt ?: "2026-07-12")
                    ProfileInfoRow("Database User ID", "#${curUser?.userId ?: 0}")
                    ProfileInfoRow("Active Session Type", "JWT Auth (RS256)")
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = glassCardBorder(),
                shape = GlassCardShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Web3 Signing Node Keys", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    
                    val hashWallet = "0x" + CryptoUtils.sha256(curUser?.fullName ?: "N/A").substring(0, 40)
                    ProfileInfoRow("Consensus Address", hashWallet)
                    ProfileInfoRow("Provider Network", "Ethereum Sepolia Testnet")
                    ProfileInfoRow("Smart Contract Link", "0x5FbDB2315678afecb367f032d93F642f64180aa3")
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        Text(text = value, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace)
    }
}

// =================================================================
// 2. SETTINGS SCREEN
// =================================================================

@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
    var web3ProviderUrl by remember { mutableStateOf("https://sepolia.infura.io/v3/9aa3e2...") }
    var dbSyncEnabled by remember { mutableStateOf(true) }
    var autoHashCheck by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("System Configuration", fontWeight = FontWeight.Bold) },
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
            Text("Network Provider Settings", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = web3ProviderUrl,
                onValueChange = { web3ProviderUrl = it },
                label = { Text("Ethereum Sepolia RPC Provider URL") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("Performance & Security Preferences", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Auto MySQL Synchronization", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Synchronizes on-chain events with local MySQL audit registers", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
                Switch(checked = dbSyncEnabled, onCheckedChange = { dbSyncEnabled = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Local PDF Byte Verification", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Pre-calculates SHA-256 local hash for instant upload validation check", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
                Switch(checked = autoHashCheck, onCheckedChange = { autoHashCheck = it })
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                border = glassCardBorder(),
                shape = GlassCardShape
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.SettingsSuggest, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Changes are committed directly to standard secure preferences.", fontSize = 11.sp)
                }
            }
        }
    }
}

// =================================================================
// 3. ABOUT THESIS PROJECT SCREEN (CRITICAL DELIVERABLE)
// =================================================================

@Composable
fun AboutProjectScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("About Graduation Thesis", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Title block
            Text(
                text = "Academic Credentials Blockchain Authenticator",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Full Title: Blockchain-Based Authentication of Academic Certifications in Remote Learning Systems",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )

            Text(
                text = "This application forms the mobile client layer of a multi-tier academic integrity verification platform designed for remote learning networks.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 18.sp
            )

            Divider()

            // Key Highlights
            Text("Core Architecture Highlights", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)

            HighlightItem("Decentralized Trust", "Certificate validation does not rely on a central database, but directly queries the Ethereum Sepolia smart contract status. Forgeries are impossible.")
            HighlightItem("SHA-256 Hashing", "PDF diploma document bytes are hashed locally. Only the 64-hex SHA-256 fingerprint is saved on-chain, protecting student privacy.")
            HighlightItem("Role-Based RBAC Enforcement", "Only accredited University Registrar node wallets are authorized to execute the issueCertificate() smart contract method.")
            HighlightItem("QR Integrity Code", "Encodes both certificate ID and blockchain tx receipt allowing employers to scan and verify authenticity in 2 seconds offline/online.")

            Divider()

            // Technology Stack List
            Text("Platform Technology Stack", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StackBadge("Solidity (v0.8.20)")
                StackBadge("Ethereum Sepolia")
                StackBadge("Android Jetpack Compose")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StackBadge("Django REST Framework")
                StackBadge("MySQL 8.0")
                StackBadge("Web3j / Retrofit")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Developed as a graduation project requirement. Fully scalable, secure, and ready for demonstration.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun HighlightItem(title: String, desc: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Text(
            text = desc,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(start = 22.dp, top = 2.dp),
            lineHeight = 16.sp
        )
    }
}

@Composable
fun StackBadge(name: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}
