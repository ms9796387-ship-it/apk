# Deployment & Architecture Guide
## "Blockchain-Based Authentication of Academic Certifications in Remote Learning Systems"
**Final Year Graduation Thesis Project Documentation**

---

## 1. Project Architecture Overview

This project implements a decentralized trust architecture to secure and verify academic certificates. It prevents credentials fraud via **cryptographic pinning** on a blockchain ledger.

The system is split into four decoupled tiers:
1. **Blockchain Consensus Tier (Ethereum Sepolia/Ganache):** Stores immutable records (Fingerprint Hash, ID, Status, and Timestamp) using a Solidity Smart Contract.
2. **Relational Database Tier (MySQL):** Stores user roles, detailed profile metadata, verification history, and system audit logs.
3. **Application Control Tier (Django REST Framework):** Implements JWT-secured REST APIs, handles business validations, triggers blockchain transactions via Web3.py, and serves QR codes.
4. **Client-Facing Presentation Tier (Android App):** Native Android Client (Kotlin / Material 3) with separate panels for Administrators, Institutions, Students, and Employers. Integrates QR Scanning (ZXing) and instant integrity verification.

---

## 2. Smart Contract Deployment (Solidity)

### Prerequisites:
* install Node.js & NPM
* Install Hardhat or Truffle: `npm install -g hardhat`
* Metamask Account configured on **Ethereum Sepolia Testnet** with Test ETH.

### Deployment Steps:
1. Navigate to contract directory:
   ```bash
   mkdir certichain-contracts && cd certichain-contracts
   npx hardhat init
   ```
2. Copy `AcademicCertificateContract.sol` into the `contracts/` directory.
3. Create a deployment script `scripts/deploy.js`:
   ```javascript
   const hre = require("hardhat");

   async function main() {
     const Contract = await hre.ethers.getContractFactory("AcademicCertificateContract");
     console.log("Deploying contract...");
     const contract = await Contract.deploy();
     await contract.waitForDeployment();
     console.log("Contract deployed to:", await contract.getAddress());
   }

   main().catch((error) => {
     console.error(error);
     process.exitCode = 1;
   });
   ```
4. Run deployment on Sepolia:
   ```bash
   npx hardhat run scripts/deploy.js --network sepolia
   ```
5. Note the deployed contract address and copy the ABI from `artifacts/contracts/AcademicCertificateContract.json` to configure the Django Backend and Android Web3j integrations.

---

## 3. Django REST Backend & MySQL Configuration

### Prerequisites:
* Python 3.10+
* MySQL Server 8.0+

### Step-by-Step Setup:
1. Create a MySQL database and user:
   ```sql
   CREATE DATABASE certichain_db;
   CREATE USER 'certi_user'@'localhost' IDENTIFIED BY 'secure_password';
   GRANT ALL PRIVILEGES ON certichain_db.* TO 'certi_user'@'localhost';
   FLUSH PRIVILEGES;
   ```
2. Set up python virtual environment and dependencies:
   ```bash
   python -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   pip install django djangorestframework django-cors-headers mysqlclient web3 pyjwt djangorestframework-simplejwt
   ```
3. Update database settings in `settings.py`:
   ```python
   DATABASES = {
       'default': {
           'ENGINE': 'django.db.backends.mysql',
           'NAME': 'certichain_db',
           'USER': 'certi_user',
           'PASSWORD': 'secure_password',
           'HOST': 'localhost',
           'PORT': '3306',
       }
   }
   ```
4. Run Django migrations:
   ```bash
   python manage.py makemigrations
   python manage.py migrate
   ```
5. Create a superuser:
   ```bash
   python manage.py createsuperuser
   ```
6. Run server:
   ```bash
   python manage.py runserver 0.0.0.0:8000
   ```

---

## 4. Native Android Client Compilation & Deployment

The Android client is built inside the Google AI Studio Environment using Jetpack Compose and Material Design 3.

### Core Modules Configured:
1. **Local Demo Controller Engine:** Implements automatic background synchronization between different simulated user accounts (Admin, Institution, Student, Employer) to allow full, self-contained evaluation in the Android Emulator.
2. **SHA-256 Hashing Engine:** Processes certificate file components locally to check database alignment.
3. **QR Code Scanner & Generator:** Encodes Certificate Metadata and Blockchain Transaction receipts into shareable visual QR codes.

### Running and Testing in Local Environment:
* To compile and package the application debug APK:
  `compile_applet`
* To verify layout regressions or functional units:
  `gradle :app:testDebugUnitTest`

---

## 5. Demonstration and Thesis Defense Workflow

For the academic committee demonstration, use the following interactive sequence:

1. **Onboarding & Authentication:** Introduce the decentralized model. Show the role-based onboarding cards. Log in as an **Admin** (`admin@edu.com`) to inspect institutions' statuses and transaction logs.
2. **Accreditation:** Authorize the University account. Log out and sign back in as **Institution** (`registrar@stu.edu`).
3. **Certificate Issuance:** Select student "John Doe", enter graduation GPA, course, and click **Issue Certificate**. 
   * *Notice the SHA-256 Fingerprint is computed instantly.*
   * *The Blockchain confirmation dialog simulates the Gas Limit estimate, Block Number pinning, and generates a live Transaction Hash.*
   * *A scannable QR Code is generated instantly.*
4. **Student Ledger Review:** Sign in as **Student** (`student@stu.edu`) to view the newly minted digital certificate, download its credential representation, and display the QR Code.
5. **Decentralized Validation:** Sign in as **Employer** (`hr@google.com`). Try verifying John's credentials by typing the ID, pasting the Hash, or simulating the QR scan.
   * *The verification engine queries the blockchain ledger state and displays the green **AUTHENTIC & SECURE** banner.*
   * *Now, log back in as an Institution, select John's certificate, and click **Revoke Certificate** (e.g., due to record correction).*
   * *Log back in as Employer, rerun the check, and witness the system block the credential with an amber **REVOKED ON CHAIN** warning.*
   * *This illustrates real-time, zero-trust verification without central databases.*
