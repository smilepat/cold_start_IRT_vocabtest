# Quick Start Guide - Easiest Way to Run the Vocabulary Test App

Follow this simplified guide to get your application running quickly.

## Overview

You need to install **2 things**:
1. **Java JDK 8+** (to run the application)
2. **XAMPP** (easy MySQL with GUI)

Everything else is already included in the project!

---

## Step 1: Install XAMPP (Easy MySQL)

**Time:** 10 minutes

XAMPP is the easiest way to install MySQL. It has a simple control panel to start/stop MySQL.

📖 **Follow:** [XAMPP_SETUP_GUIDE.md](XAMPP_SETUP_GUIDE.md)

**Summary:**
1. Download XAMPP: https://www.apachefriends.org/download.html
2. Install it (just click Next, Next, Next)
3. Open XAMPP Control Panel
4. Click "Start" next to MySQL
5. Click "Admin" to open phpMyAdmin
6. Create database `vocabulary_test`
7. Import `schema.sql` and `sample_data.sql`

✅ Done! MySQL is ready.

---

## Step 2: Install Java JDK

**Time:** 5 minutes

### 2.1 Download Java

Choose **one** of these options:

**Option A: Java 8 (LTS - Project default)**
- Download: https://adoptium.net/temurin/releases/?version=8
- Select: Windows x64 installer

**Option B: Java 11 (LTS - Recommended)**
- Download: https://adoptium.net/temurin/releases/?version=11
- Select: Windows x64 installer

**Option C: Java 17 (Latest LTS)**
- Download: https://adoptium.net/temurin/releases/?version=17
- Select: Windows x64 installer

### 2.2 Install Java

1. Run the downloaded `.msi` installer
2. Click **Next** → **Next** → **Install**
3. ✅ Make sure "Add to PATH" is checked
4. Click **Finish**

### 2.3 Verify Java Installation

Open Command Prompt and type:
```bash
java -version
```

You should see something like:
```
openjdk version "11.0.x" or "1.8.x" or "17.0.x"
```

✅ Done! Java is ready.

---

## Step 3: Configure Database Connection

**Time:** 1 minute

Edit this file:
```
src/main/resources-local/application.properties
```

Update the MySQL password (line 9):
```properties
vocabulary.jpa.password=YourXAMPPPassword
```

If you didn't set a password in XAMPP, leave it blank:
```properties
vocabulary.jpa.password=
```

✅ Done! Configuration complete.

---

## Step 4: Build the Application

**Time:** 5-10 minutes (first time), 2 minutes (after that)

Open Command Prompt in the project directory:
```bash
cd "g:\내 드라이브\01.cat-vocabulary-test\cat-vocabulary-test-binary\cat-vocabulary-test-binary"
```

Run the build command:
```bash
mvnw.cmd clean package -P local
```

**What happens:**
1. Maven downloads dependencies (first time only)
2. Node.js is auto-installed
3. React frontend is built
4. Application is packaged

Wait for: `BUILD SUCCESS`

✅ Done! Application is built.

---

## Step 5: Run the Application

**Time:** 30 seconds

### Make sure MySQL is running:
1. Open XAMPP Control Panel
2. MySQL should be **green** (running)
3. If not, click **"Start"** next to MySQL

### Run the application:
```bash
mvnw.cmd spring-boot:run -P local
```

Wait for:
```
Started VocabularyTestApplication in X seconds
```

✅ Done! Application is running.

---

## Step 6: Access the Application

Open your web browser:

### Main Application
```
http://localhost:8080/
```

### API Documentation (Swagger)
```
http://localhost:8080/swagger-ui.html
```

### Test API Endpoint
```
http://localhost:8080/api/environment
```

✅ Done! You're ready to use the application.

---

## Testing the Application

### Start a New Exam

Open Swagger UI: http://localhost:8080/swagger-ui.html

1. Find **"word-exam-api-controller"**
2. Click **POST /api/word-exam/start**
3. Click **"Try it out"**
4. Enter:
   - level: `1`
   - detailSection: `1`
5. Click **"Execute"**

You should get a response with exam questions!

---

## Common Commands

### Build the application
```bash
mvnw.cmd clean package -P local
```

### Run the application
```bash
mvnw.cmd spring-boot:run -P local
```

### Stop the application
Press `Ctrl + C` in the command prompt

### Start MySQL (if stopped)
Open XAMPP Control Panel → Click "Start" next to MySQL

### View database
Open phpMyAdmin: http://localhost/phpmyadmin

---

## Troubleshooting

### Error: "Port 8080 already in use"

**Solution 1:** Stop the process using port 8080
```bash
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Solution 2:** Change the port in `application.properties`:
```properties
server.port=8081
```

### Error: "Communications link failure"

**Problem:** Can't connect to MySQL

**Solution:**
1. Open XAMPP Control Panel
2. Make sure MySQL is **green** (running)
3. If not, click **"Start"**

### Error: "java is not recognized"

**Problem:** Java not installed or not in PATH

**Solution:**
1. Install Java (see Step 2)
2. Make sure "Add to PATH" was checked during installation
3. Restart Command Prompt
4. Try: `java -version`

### Error: Build fails with frontend errors

**Solution:** Clean and rebuild
```bash
rmdir /s /q src\main\vocabulary-react-app\node_modules
rmdir /s /q target
mvnw.cmd clean package -P local
```

---

## What You've Accomplished

✅ XAMPP installed with MySQL running
✅ Java JDK installed
✅ Database created with 30 sample vocabulary words
✅ Application built and running
✅ Ready to test vocabulary exams!

---

## Next Steps

1. **Explore the API** - Use Swagger UI to test endpoints
2. **Add more words** - Use phpMyAdmin to add vocabulary
3. **Customize** - Modify the React frontend
4. **Deploy** - Package as WAR for production server

---

## Quick Reference

| Component | How to Start | How to Access |
|-----------|-------------|---------------|
| MySQL | XAMPP Control Panel → Start | - |
| phpMyAdmin | Browser → http://localhost/phpmyadmin | root / YourPassword |
| Application | `mvnw.cmd spring-boot:run -P local` | http://localhost:8080 |
| Swagger UI | - | http://localhost:8080/swagger-ui.html |

---

## Files You Created/Modified

- ✅ `schema.sql` - Database structure
- ✅ `sample_data.sql` - 30 vocabulary words
- ✅ `src/main/resources-local/application.properties` - Database config
- ✅ `XAMPP_SETUP_GUIDE.md` - Detailed XAMPP instructions
- ✅ `DEPLOYMENT_GUIDE.md` - Full deployment guide
- ✅ `QUICK_START.md` - This file

---

## Need More Help?

- **XAMPP Setup:** See [XAMPP_SETUP_GUIDE.md](XAMPP_SETUP_GUIDE.md)
- **Full Deployment:** See [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)
- **MySQL Checklist:** See [MYSQL_SETUP_CHECKLIST.md](MYSQL_SETUP_CHECKLIST.md)
