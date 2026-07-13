// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

/**
 * @title AcademicCertificateContract
 * @dev Secure, immutable on-chain ledger for authenticating academic credentials in remote learning systems.
 * Part of the Graduation Project: "Blockchain-Based Authentication of Academic Certifications in Remote Learning Systems"
 */
contract AcademicCertificateContract {
    
    address public admin;
    
    struct Certificate {
        string certificateId;
        string studentId;
        string courseName;
        string certificateHash; // SHA-256 fingerprint of the PDF certificate
        address institutionWallet;
        uint256 timestamp;
        bool isValid;
        bool isRevoked;
        string revocationReason;
    }
    
    // Mapping from Certificate SHA-256 hash to Certificate record
    mapping(string => Certificate) private certificates;
    
    // Mapping from Certificate ID to Certificate SHA-256 hash for lookup
    mapping(string => string) private idToHash;
    
    // Authorized academic institutions allowed to issue certificates
    mapping(address => bool) public authorizedInstitutions;
    mapping(address => string) public institutionNames;
    
    // Audit Logging Event Structures
    event CertificateIssued(
        string indexed certificateId,
        string studentId,
        string certificateHash,
        address indexed institution,
        uint256 timestamp
    );
    
    event CertificateRevoked(
        string indexed certificateId,
        string certificateHash,
        address indexed institution,
        string reason,
        uint256 timestamp
    );
    
    event InstitutionAuthorized(address indexed institution, string name, uint256 timestamp);
    event InstitutionDeauthorized(address indexed institution, uint256 timestamp);

    modifier onlyAdmin() {
        require(msg.sender == admin, "Caller is not the system administrator");
        _;
    }
    
    modifier onlyAuthorizedInstitution() {
        require(authorizedInstitutions[msg.sender] || msg.sender == admin, "Caller is not an authorized academic institution");
        _;
    }

    constructor() {
        admin = msg.sender;
    }
    
    /**
     * @dev Authorize an academic institution to issue certificates.
     */
    function authorizeInstitution(address _institution, string calldata _name) external onlyAdmin {
        authorizedInstitutions[_institution] = true;
        institutionNames[_institution] = _name;
        emit InstitutionAuthorized(_institution, _name, block.timestamp);
    }
    
    /**
     * @dev Deauthorize an academic institution.
     */
    function deauthorizeInstitution(address _institution) external onlyAdmin {
        authorizedInstitutions[_institution] = false;
        emit InstitutionDeauthorized(_institution, block.timestamp);
    }

    /**
     * @dev Issues a new academic certificate and binds its SHA-256 hash to the blockchain.
     */
    function issueCertificate(
        string calldata _certificateId,
        string calldata _studentId,
        string calldata _courseName,
        string calldata _certificateHash
    ) external onlyAuthorizedInstitution {
        require(bytes(_certificateId).length > 0, "Certificate ID cannot be empty");
        require(bytes(_certificateHash).length == 64, "SHA-256 hash must be exactly 64 hex characters");
        require(certificates[_certificateHash].timestamp == 0, "Certificate with this fingerprint hash already exists");
        require(bytes(idToHash[_certificateId]).length == 0, "Certificate ID already registered");

        certificates[_certificateHash] = Certificate({
            certificateId: _certificateId,
            studentId: _studentId,
            courseName: _courseName,
            certificateHash: _certificateHash,
            institutionWallet: msg.sender,
            timestamp: block.timestamp,
            isValid: true,
            isRevoked: false,
            revocationReason: ""
        });

        idToHash[_certificateId] = _certificateHash;

        emit CertificateIssued(_certificateId, _studentId, _certificateHash, msg.sender, block.timestamp);
    }

    /**
     * @dev Revoke a certificate due to academic integrity issues, typos, or administrative updates.
     */
    function revokeCertificate(string calldata _certificateId, string calldata _reason) external onlyAuthorizedInstitution {
        string memory certHash = idToHash[_certificateId];
        require(bytes(certHash).length > 0, "Certificate ID does not exist");
        
        Certificate storage cert = certificates[certHash];
        require(cert.isValid, "Certificate is already invalid");
        require(!cert.isRevoked, "Certificate is already revoked");
        
        // Only the issuing institution or the system admin can revoke
        require(cert.institutionWallet == msg.sender || msg.sender == admin, "Only issuing institution can revoke");

        cert.isValid = false;
        cert.isRevoked = true;
        cert.revocationReason = _reason;

        emit CertificateRevoked(_certificateId, certHash, msg.sender, _reason, block.timestamp);
    }

    /**
     * @dev Retrieve complete certificate information using its SHA-256 hash.
     */
    function getCertificateByHash(string calldata _certificateHash) external view returns (
        string memory certificateId,
        string memory studentId,
        string memory courseName,
        address institutionWallet,
        uint256 timestamp,
        bool isValid,
        bool isRevoked,
        string memory revocationReason
    ) {
        Certificate memory cert = certificates[_certificateHash];
        require(cert.timestamp > 0, "Certificate fingerprint not found on blockchain");
        return (
            cert.certificateId,
            cert.studentId,
            cert.courseName,
            cert.institutionWallet,
            cert.timestamp,
            cert.isValid,
            cert.isRevoked,
            cert.revocationReason
        );
    }

    /**
     * @dev Resolve a certificate hash using its ID.
     */
    function getHashById(string calldata _certificateId) external view returns (string memory) {
        string memory certHash = idToHash[_certificateId];
        require(bytes(certHash).length > 0, "Certificate ID does not exist");
        return certHash;
    }

    /**
     * @dev Instantly verifies certificate authenticity.
     * Returns 0 for VALID, 1 for REVOKED, and reverts or returns 2 for NOT_FOUND.
     */
    function verifyCertificate(string calldata _certificateHash) external view returns (uint8 statusCode, string memory statusText) {
        Certificate memory cert = certificates[_certificateHash];
        if (cert.timestamp == 0) {
            return (2, "NOT_FOUND_OR_TAMPERED");
        }
        if (cert.isRevoked) {
            return (1, "REVOKED");
        }
        if (cert.isValid) {
            return (0, "VALID_AND_SECURE");
        }
        return (2, "INVALID");
    }
}
