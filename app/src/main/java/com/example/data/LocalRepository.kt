package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// =================================================================
// 1. DATA MODELS
// =================================================================

enum class UserRole { ADMIN, INSTITUTION, STUDENT, EMPLOYER }

enum class CertStatus { VALID, REVOKED }

data class User(
    val userId: Int,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
    val createdAt: String
)

data class InstitutionProfile(
    val institutionId: Int,
    val userId: Int,
    val institutionName: String,
    var accreditationStatus: Boolean,
    val createdAt: String
)

data class StudentProfile(
    val studentId: Int,
    val userId: Int,
    val studentIdCard: String,
    val institutionId: Int,
    val createdAt: String
)

data class Certificate(
    val certificateId: String,
    val studentId: Int,
    val studentName: String,
    val studentIdCard: String,
    val institutionId: Int,
    val institutionName: String,
    val courseName: String,
    val issueDate: String,
    val gpa: Double,
    val certificateHash: String,
    var blockchainTxHash: String,
    var certificateStatus: CertStatus,
    var revocationReason: String = "",
    val qrCodePath: String,
    val createdAt: String
)

data class VerificationRecord(
    val verificationId: Int,
    val employerId: Int,
    val employerName: String,
    val companyName: String,
    val certificateId: String?,
    val queriedHash: String,
    val verificationResult: String, // "VERIFIED", "INVALID", "REVOKED"
    val verificationDate: String
)

data class AuditLog(
    val logId: Int,
    val userId: Int?,
    val userName: String,
    val action: String,
    val description: String,
    val timestamp: String
)

data class BlockchainTransaction(
    val txHash: String,
    val blockNumber: Long,
    val fromAddress: String,
    val toAddress: String,
    val gasUsed: Long,
    val valueInEth: Double,
    val timestamp: String,
    val payloadHash: String,
    val isConfirmed: Boolean
)

// =================================================================
// 2. CRYPTOGRAPHIC UTILITY
// =================================================================

object CryptoUtils {
    fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

// =================================================================
// 3. SECURE LOCAL REPOSITORY (SINGLETON ENGINE)
// =================================================================

object LocalRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val shortDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // State Flows for Reactive UI Updates
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _institutions = MutableStateFlow<List<InstitutionProfile>>(emptyList())
    val institutions: StateFlow<List<InstitutionProfile>> = _institutions.asStateFlow()

    private val _students = MutableStateFlow<List<StudentProfile>>(emptyList())
    val students: StateFlow<List<StudentProfile>> = _students.asStateFlow()

    private val _certificates = MutableStateFlow<List<Certificate>>(emptyList())
    val certificates: StateFlow<List<Certificate>> = _certificates.asStateFlow()

    private val _verificationRecords = MutableStateFlow<List<VerificationRecord>>(emptyList())
    val verificationRecords: StateFlow<List<VerificationRecord>> = _verificationRecords.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLog>>(emptyList())
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    private val _blockchainLedger = MutableStateFlow<List<BlockchainTransaction>>(emptyList())
    val blockchainLedger: StateFlow<List<BlockchainTransaction>> = _blockchainLedger.asStateFlow()

    // Currently logged in session
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        prepopulateData()
    }

    private fun prepopulateData() {
        // 1. Add Default Users
        val adminUser = User(1, "Professor Harold Vance", "admin@edu.com", CryptoUtils.sha256("admin123"), UserRole.ADMIN, dateFormat.format(Date()))
        val instUser1 = User(2, "MIT Academic Registrar Office", "registrar@mit.edu", CryptoUtils.sha256("mit123"), UserRole.INSTITUTION, dateFormat.format(Date()))
        val instUser2 = User(3, "Stanford University Registrar", "registrar@stanford.edu", CryptoUtils.sha256("stanford123"), UserRole.INSTITUTION, dateFormat.format(Date()))
        val studentUser1 = User(4, "John Doe", "student@edu.com", CryptoUtils.sha256("student123"), UserRole.STUDENT, dateFormat.format(Date()))
        val studentUser2 = User(5, "Sarah Jenkins", "sarah@edu.com", CryptoUtils.sha256("student123"), UserRole.STUDENT, dateFormat.format(Date()))
        val employerUser = User(6, "Jane Smith (Tech Recruiter)", "verifier@google.com", CryptoUtils.sha256("google123"), UserRole.EMPLOYER, dateFormat.format(Date()))

        _users.value = listOf(adminUser, instUser1, instUser2, studentUser1, studentUser2, employerUser)

        // 2. Add Institutions
        val mitProf = InstitutionProfile(1, 2, "MIT - Massachusetts Institute of Technology", true, dateFormat.format(Date()))
        val stanfordProf = InstitutionProfile(2, 3, "Stanford University", false, dateFormat.format(Date())) // Needs Admin Approval Demo
        _institutions.value = listOf(mitProf, stanfordProf)

        // 3. Add Students
        val stud1 = StudentProfile(1, 4, "MIT-2023-CS081", 1, dateFormat.format(Date()))
        val stud2 = StudentProfile(2, 5, "STAN-2024-AI942", 2, dateFormat.format(Date()))
        _students.value = listOf(stud1, stud2)

        // 4. Prepopulate dynamic Blockchain Transaction, Certificate and Verification Records
        val nowStr = dateFormat.format(Date())
        val tx1 = "0x" + CryptoUtils.sha256("TX_PAYLOAD_MIT_2023_CS081").substring(0, 64)
        val certHash1 = CryptoUtils.sha256("CERT_payload:MIT-2023-CS081:Computer Science:3.95")
        
        val cert1 = Certificate(
            certificateId = "CERT-MIT-2026-00412",
            studentId = 1,
            studentName = "John Doe",
            studentIdCard = "MIT-2023-CS081",
            institutionId = 1,
            institutionName = "MIT - Massachusetts Institute of Technology",
            courseName = "M.S. in Computer Science & Artificial Intelligence",
            issueDate = "2026-05-18",
            gpa = 3.95,
            certificateHash = certHash1,
            blockchainTxHash = tx1,
            certificateStatus = CertStatus.VALID,
            qrCodePath = "qrcode_cert_mit_2026_00412",
            createdAt = nowStr
        )
        
        _certificates.value = listOf(cert1)

        val blockTx1 = BlockchainTransaction(
            txHash = tx1,
            blockNumber = 18402941,
            fromAddress = "0x8626f6940E2eb28930eFb4CeF49B2d1F2C9C1199", // STU
            toAddress = "0x5FbDB2315678afecb367f032d93F642f64180aa3", // Contract
            gasUsed = 64902,
            valueInEth = 0.0,
            timestamp = nowStr,
            payloadHash = certHash1,
            isConfirmed = true
        )
        _blockchainLedger.value = listOf(blockTx1)

        // 5. Prepopulate Audit Logs
        val log1 = AuditLog(1, 2, "MIT Academic Registrar", "ISSUE_CERTIFICATE", "Issued academic certificate CERT-MIT-2026-00412 for student John Doe", nowStr)
        val log2 = AuditLog(2, 6, "Jane Smith (Tech Recruiter)", "VERIFY_CERTIFICATE_SUCCESS", "Successfully validated authenticity of Certificate ID CERT-MIT-2026-00412", nowStr)
        _auditLogs.value = listOf(log1, log2)

        // 6. Prepopulate Verification logs
        val ver1 = VerificationRecord(
            verificationId = 1,
            employerId = 6,
            employerName = "Jane Smith (Tech Recruiter)",
            companyName = "Google LLC",
            certificateId = "CERT-MIT-2026-00412",
            queriedHash = certHash1,
            verificationResult = "VERIFIED",
            verificationDate = nowStr
        )
        _verificationRecords.value = listOf(ver1)
    }

    // =================================================================
    // AUTHENTICATION & SESSIONS
    // =================================================================

    fun login(email: String, passwordPlain: String): Boolean {
        val hash = CryptoUtils.sha256(passwordPlain)
        val found = _users.value.find { it.email.equals(email, ignoreCase = true) && it.passwordHash == hash }
        if (found != null) {
            _currentUser.value = found
            logActivity(found.userId, found.fullName, "USER_LOGIN", "Logged into system with role: ${found.role.name}")
            return true
        }
        return false
    }

    fun logout() {
        val user = _currentUser.value
        if (user != null) {
            logActivity(user.userId, user.fullName, "USER_LOGOUT", "User closed active secure session")
        }
        _currentUser.value = null
    }

    fun registerStudent(fullName: String, email: String, passwordPlain: String, idCard: String, institutionId: Int): Boolean {
        if (_users.value.any { it.email.equals(email, ignoreCase = true) }) return false
        val newId = _users.value.size + 1
        val hashed = CryptoUtils.sha256(passwordPlain)
        val newUser = User(newId, fullName, email, hashed, UserRole.STUDENT, dateFormat.format(Date()))
        
        _users.value = _users.value + newUser
        val studentProfile = StudentProfile(_students.value.size + 1, newId, idCard, institutionId, dateFormat.format(Date()))
        _students.value = _students.value + studentProfile

        logActivity(newId, fullName, "REGISTER_STUDENT", "Created student account linked with card registration: $idCard")
        return true
    }

    fun registerInstitution(institutionName: String, email: String, passwordPlain: String): Boolean {
        if (_users.value.any { it.email.equals(email, ignoreCase = true) }) return false
        val newId = _users.value.size + 1
        val hashed = CryptoUtils.sha256(passwordPlain)
        val newUser = User(newId, institutionName, email, hashed, UserRole.INSTITUTION, dateFormat.format(Date()))
        
        _users.value = _users.value + newUser
        val instProfile = InstitutionProfile(_institutions.value.size + 1, newId, institutionName, false, dateFormat.format(Date()))
        _institutions.value = _institutions.value + instProfile

        logActivity(newId, institutionName, "REGISTER_INSTITUTION", "Registered new educational body. Status: PENDING_ACCREDITATION")
        return true
    }

    fun registerEmployer(fullName: String, email: String, passwordPlain: String, companyName: String): Boolean {
        if (_users.value.any { it.email.equals(email, ignoreCase = true) }) return false
        val newId = _users.value.size + 1
        val hashed = CryptoUtils.sha256(passwordPlain)
        val newUser = User(newId, fullName, email, hashed, UserRole.EMPLOYER, dateFormat.format(Date()))
        
        _users.value = _users.value + newUser
        // Simply link or keep custom list if required
        logActivity(newId, "$fullName ($companyName)", "REGISTER_EMPLOYER", "Registered corporate credential verifier account.")
        return true
    }

    // =================================================================
    // ADMINISTRATIVE ADMIN MODULE ACTIONS
    // =================================================================

    fun approveInstitution(institutionId: Int) {
        val currentAdmin = _currentUser.value ?: return
        val currentList = _institutions.value.toMutableList()
        val index = currentList.indexOfFirst { it.institutionId == institutionId }
        if (index != -1) {
            val prof = currentList[index]
            prof.accreditationStatus = true
            _institutions.value = currentList
            logActivity(currentAdmin.userId, currentAdmin.fullName, "APPROVE_INSTITUTION", "Accredited and authorized academic institution: ${prof.institutionName}")
        }
    }

    // =================================================================
    // INSTITUTION FUNCTIONS
    // =================================================================

    fun issueCertificate(
        studentId: Int,
        courseName: String,
        gpa: Double,
        dateIssuedStr: String
    ): Certificate? {
        val currentInstUser = _currentUser.value ?: return null
        val profile = _institutions.value.find { it.userId == currentInstUser.userId } ?: return null
        if (!profile.accreditationStatus) return null

        val student = _students.value.find { it.studentId == studentId } ?: return null
        val studentUser = _users.value.find { it.userId == student.userId } ?: return null

        // Calculate a simulated unique Certificate ID
        val randomNum = Random.nextInt(10000, 99999)
        val certificateId = "CERT-STU-${randomNum}"

        // Generate SHA-256 Hash of parameters to match system integrity requirements
        val certDataRaw = "$certificateId:${student.studentIdCard}:$courseName:$gpa:$dateIssuedStr:${profile.institutionName}"
        val sha256Fingerprint = CryptoUtils.sha256(certDataRaw)

        // Write to Simulated Blockchain
        val blockNum = _blockchainLedger.value.maxOfOrNull { it.blockNumber }?.plus(Random.nextLong(1, 20)) ?: 18402941
        val blockTxHash = "0x" + CryptoUtils.sha256(certDataRaw + blockNum).substring(0, 64)
        
        val blockchainTx = BlockchainTransaction(
            txHash = blockTxHash,
            blockNumber = blockNum,
            fromAddress = "0x" + CryptoUtils.sha256(profile.institutionName).substring(0, 40),
            toAddress = "0x5FbDB2315678afecb367f032d93F642f64180aa3", // Contract Address
            gasUsed = Random.nextLong(45000, 75000),
            valueInEth = 0.0,
            timestamp = dateFormat.format(Date()),
            payloadHash = sha256Fingerprint,
            isConfirmed = true
        )
        _blockchainLedger.value = _blockchainLedger.value + blockchainTx

        // Store Certificate locally
        val newCert = Certificate(
            certificateId = certificateId,
            studentId = studentId,
            studentName = studentUser.fullName,
            studentIdCard = student.studentIdCard,
            institutionId = profile.institutionId,
            institutionName = profile.institutionName,
            courseName = courseName,
            issueDate = dateIssuedStr,
            gpa = gpa,
            certificateHash = sha256Fingerprint,
            blockchainTxHash = blockTxHash,
            certificateStatus = CertStatus.VALID,
            qrCodePath = "qrcode_${certificateId.lowercase()}",
            createdAt = dateFormat.format(Date())
        )

        _certificates.value = _certificates.value + newCert
        logActivity(currentInstUser.userId, currentInstUser.fullName, "ISSUE_CERTIFICATE", "Issued credential $certificateId to student ${studentUser.fullName}. TX: $blockTxHash")

        return newCert
    }

    fun revokeCertificate(certificateId: String, reason: String): Boolean {
        val currentInstUser = _currentUser.value ?: return false
        val currentList = _certificates.value.toMutableList()
        val index = currentList.indexOfFirst { it.certificateId == certificateId }
        if (index != -1) {
            val cert = currentList[index]
            cert.certificateStatus = CertStatus.REVOKED
            cert.revocationReason = reason
            _certificates.value = currentList

            // Also flag on blockchain
            val ledgerList = _blockchainLedger.value.toMutableList()
            val txIndex = ledgerList.indexOfFirst { it.payloadHash == cert.certificateHash }
            if (txIndex != -1) {
                // Pin the revocation update in a new simulated block transaction
                val revTxHash = "0x" + CryptoUtils.sha256(certificateId + "REVOCATION").substring(0, 64)
                val blockNum = ledgerList.maxOf { it.blockNumber } + Random.nextLong(1, 10)
                val revTx = BlockchainTransaction(
                    txHash = revTxHash,
                    blockNumber = blockNum,
                    fromAddress = "0x" + CryptoUtils.sha256(currentInstUser.fullName).substring(0, 40),
                    toAddress = "0x5FbDB2315678afecb367f032d93F642f64180aa3",
                    gasUsed = 29402,
                    valueInEth = 0.0,
                    timestamp = dateFormat.format(Date()),
                    payloadHash = cert.certificateHash,
                    isConfirmed = true
                )
                _blockchainLedger.value = _blockchainLedger.value + revTx
                cert.blockchainTxHash = revTxHash
            }

            logActivity(currentInstUser.userId, currentInstUser.fullName, "REVOKE_CERTIFICATE", "Revoked academic certificate $certificateId on blockchain ledger. Reason: $reason")
            return true
        }
        return false
    }

    // =================================================================
    // VERIFICATION WORKFLOWS (FOR EMPLOYERS / VERIFIERS)
    // =================================================================

    fun verifyCertificateByQR(qrPayload: String): VerificationResult {
        // Sample payload: "ID:CERT-MIT-2026-00412;HASH:f3b49e29a1b1836a..."
        val idToken = qrPayload.substringAfter("ID:").substringBefore(";")
        val hashToken = qrPayload.substringAfter("HASH:")
        return verifyCertificateByHash(hashToken, idToken)
    }

    fun verifyCertificateByID(certificateId: String): VerificationResult {
        val cert = _certificates.value.find { it.certificateId.equals(certificateId, ignoreCase = true) }
            ?: return createFailedVerification("ID_NOT_FOUND", "N/A")
        return verifyCertificateByHash(cert.certificateHash, cert.certificateId)
    }

    fun verifyCertificateByHash(hash: String, certificateId: String = "N/A"): VerificationResult {
        val curUser = _currentUser.value
        val cert = _certificates.value.find { it.certificateHash == hash }
        
        val result = when {
            cert == null -> {
                VerificationResult(
                    status = "INVALID / TAMPERED",
                    message = "No matching cryptographic fingerprint found on the blockchain ledger. This document is unauthorized, forged, or altered.",
                    certificate = null,
                    transaction = null
                )
            }
            cert.certificateStatus == CertStatus.REVOKED -> {
                val tx = _blockchainLedger.value.find { it.payloadHash == hash }
                VerificationResult(
                    status = "REVOKED",
                    message = "This certificate was issued but has been officially revoked by the academic institution on-chain.",
                    certificate = cert,
                    transaction = tx
                )
            }
            else -> {
                val tx = _blockchainLedger.value.find { it.payloadHash == hash }
                VerificationResult(
                    status = "VERIFIED",
                    message = "Authentic Academic Certification. The document hash matches the cryptographic fingerprint registered on the decentralized blockchain ledger.",
                    certificate = cert,
                    transaction = tx
                )
            }
        }

        // Record verification in histories
        val recordId = _verificationRecords.value.size + 1
        val newRecord = VerificationRecord(
            verificationId = recordId,
            employerId = curUser?.userId ?: 0,
            employerName = curUser?.fullName ?: "Anonymous Verifier",
            companyName = if (curUser?.role == UserRole.EMPLOYER) "Corporate Partner" else "Public Verifier",
            certificateId = cert?.certificateId,
            queriedHash = hash,
            verificationResult = result.status,
            verificationDate = dateFormat.format(Date())
        )
        _verificationRecords.value = _verificationRecords.value + newRecord

        // Log audit trail
        logActivity(
            userId = curUser?.userId,
            userName = curUser?.fullName ?: "Public Verifier",
            action = "VERIFY_CERTIFICATE",
            description = "Performed cryptographic verification query. Status result: ${result.status}. Target fingerprint: ${hash.take(12)}..."
        )

        return result
    }

    private fun createFailedVerification(status: String, id: String): VerificationResult {
        return VerificationResult(
            status = "INVALID",
            message = "No matching certificate found in active registries. Verification status: $status",
            certificate = null,
            transaction = null
        )
    }

    // =================================================================
    // COMPUTE HASH FROM SIMULATED PDF
    // =================================================================

    fun computePdfHash(fileName: String, bytesCount: Int, gpa: Double, rollNo: String): String {
        val salt = "ACADEMIC_BLOCKCHAIN_SALT_2026"
        return CryptoUtils.sha256("$fileName-$bytesCount-$gpa-$rollNo-$salt")
    }

    // =================================================================
    // UTILS & AUDIT LOGGER
    // =================================================================

    private fun logActivity(userId: Int?, userName: String, action: String, description: String) {
        val logId = _auditLogs.value.size + 1
        val newLog = AuditLog(logId, userId, userName, action, description, dateFormat.format(Date()))
        _auditLogs.value = _auditLogs.value + newLog
    }

    data class VerificationResult(
        val status: String, // "VERIFIED", "INVALID / TAMPERED", "REVOKED"
        val message: String,
        val certificate: Certificate?,
        val transaction: BlockchainTransaction?
    )
}
