# H2 Database Quick Start - Zero Installation Required!

H2 is an embedded database that runs inside your application. **No separate database installation needed!**

## What is H2?

✅ **Embedded database** - runs inside your Java application
✅ **No installation** - just build and run
✅ **File-based** - stores data in a file on your computer
✅ **Web console** - view data through your browser
✅ **MySQL compatible mode** - works with your existing code
✅ **Perfect for development** - quick and easy setup

---

## Complete Setup (2 Steps Only!)

### Step 1: Install Java JDK

You need Java to run the application. H2 is included automatically!

#### Download Java

Choose **one** of these options:

**Option A: Java 11 (Recommended)**
- Download: https://adoptium.net/temurin/releases/?version=11
- Select: **Windows x64 installer (.msi)**

**Option B: Java 17 (Latest LTS)**
- Download: https://adoptium.net/temurin/releases/?version=17
- Select: **Windows x64 installer (.msi)**

**Option C: Java 8 (Project default)**
- Download: https://adoptium.net/temurin/releases/?version=8
- Select: **Windows x64 installer (.msi)**

#### Install Java

1. Run the downloaded `.msi` file
2. Click **Next** → **Next** → **Install**
3. ✅ Make sure **"Add to PATH"** is checked
4. Click **Finish**

#### Verify Installation

Open Command Prompt and type:
```bash
java -version
```

You should see version information. ✅ Java is ready!

---

### Step 2: Build and Run the Application

#### 2.1 Open Command Prompt

Navigate to your project:
```bash
cd "g:\내 드라이브\01.cat-vocabulary-test\cat-vocabulary-test-binary\cat-vocabulary-test-binary"
```

#### 2.2 Build the Application

Run this command:
```bash
mvnw.cmd clean package -P local
```

**What happens:**
- Maven downloads dependencies
- H2 database library is included automatically
- Node.js is auto-installed for React frontend
- Application is compiled and packaged

**Time:** 5-10 minutes (first time), 2 minutes (after that)

Wait for: `BUILD SUCCESS`

#### 2.3 Run the Application

```bash
mvnw.cmd spring-boot:run -P local
```

**What happens:**
- Application starts
- H2 database is created automatically in `./data/` folder
- Tables are created automatically
- Application is ready!

Wait for:
```
Started VocabularyTestApplication in X seconds
```

✅ **Done!** Your app is running with H2 database!

---

## Access Your Application

### Main Application
```
http://localhost:8080/
```

### API Documentation (Swagger)
```
http://localhost:8080/swagger-ui.html
```

### H2 Database Console (View your data!)
```
http://localhost:8080/h2-console
```

#### H2 Console Login:
- **JDBC URL:** `jdbc:h2:file:./data/vocabulary_test`
- **User Name:** `sa`
- **Password:** (leave blank)

Click **Connect** and you can browse your tables!

---

## Load Sample Data

The database tables are created automatically, but they're empty. Let's add sample vocabulary words!

### Option 1: Using H2 Console (Easy - Web Interface)

1. Open H2 Console: http://localhost:8080/h2-console
2. Login (see credentials above)
3. Click in the SQL query box
4. Copy the contents of `sample_data-h2.sql` file
5. Paste into the query box
6. Click **Run** button
7. ✅ 30 vocabulary words added!

### Option 2: Let Hibernate Create Data on Startup

You can also create a data initialization file that runs automatically:

1. Create file: `src/main/resources-local/data.sql`
2. Copy contents from `sample_data-h2.sql`
3. Restart the application
4. Data loads automatically!

---

## Verify Everything Works

### Check Tables in H2 Console

1. Open: http://localhost:8080/h2-console
2. Login
3. You should see 3 tables in left sidebar:
   - `WORD`
   - `WORD_EXAM`
   - `WORD_EXAM_DETAIL`
4. Click on `WORD` → Click **Show Data**
5. You should see vocabulary words!

### Test the API

Open Swagger UI: http://localhost:8080/swagger-ui.html

1. Find **word-exam-api-controller**
2. Click **POST /api/word-exam/start**
3. Click **"Try it out"**
4. Enter:
   - level: `1`
   - detailSection: `1`
5. Click **"Execute"**

You should get exam questions! ✅

---

## Project Structure

```
cat-vocabulary-test-binary/
├── data/                          # H2 database files (created automatically)
│   ├── vocabulary_test.mv.db     # Database file
│   └── vocabulary_test.trace.db  # Log file
├── schema-h2.sql                 # H2 database schema
├── sample_data-h2.sql            # Sample vocabulary data
├── src/
│   └── main/
│       └── resources-local/
│           └── application.properties  # H2 configuration (already set up!)
└── H2_QUICK_START.md             # This file
```

---

## Advantages of H2

| Feature | H2 | MySQL/XAMPP |
|---------|----|----|
| **Installation** | ✅ None needed | ❌ Complex installation |
| **Setup Time** | ✅ 0 minutes | ❌ 20-30 minutes |
| **Start/Stop** | ✅ Automatic with app | ❌ Manual service management |
| **Data Viewing** | ✅ Built-in web console | ⚠️ Needs phpMyAdmin/Workbench |
| **Portability** | ✅ Just a file | ❌ Server required |
| **Best For** | ✅ Development/Testing | Production |

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
Press `Ctrl + C` in the Command Prompt

### Clean build (if issues occur)
```bash
mvnw.cmd clean
rmdir /s /q data
mvnw.cmd package -P local
```

---

## Where is My Data Stored?

H2 stores your database in the `data/` folder in your project:

```
./data/vocabulary_test.mv.db
```

This is a file-based database. Benefits:
- ✅ Easy to backup (just copy the file)
- ✅ Easy to reset (delete the file)
- ✅ Portable (move to another computer)
- ✅ No server running in background

---

## Troubleshooting

### Error: "java is not recognized"

**Problem:** Java not installed or not in PATH

**Solution:**
1. Install Java JDK (see Step 1)
2. Make sure "Add to PATH" was checked
3. Restart Command Prompt
4. Try: `java -version`

### Error: Build fails

**Solution:** Clean and rebuild
```bash
mvnw.cmd clean package -P local
```

### Error: Can't access H2 Console

**Problem:** Application not running or wrong URL

**Solution:**
1. Make sure app is running (you should see "Started VocabularyTestApplication")
2. Use correct URL: http://localhost:8080/h2-console
3. Check JDBC URL: `jdbc:h2:file:./data/vocabulary_test`

### Error: Port 8080 already in use

**Solution:** Change port in `application.properties`:
```properties
server.port=8081
```

Then access: http://localhost:8081/h2-console

### Want to start fresh with empty database?

**Solution:** Delete the data folder
```bash
# Stop the application first (Ctrl+C)
rmdir /s /q data
# Run the application again
mvnw.cmd spring-boot:run -P local
```

---

## Adding More Vocabulary Words

### Method 1: Using H2 Console

1. Open H2 Console: http://localhost:8080/h2-console
2. Login
3. Run SQL:
```sql
INSERT INTO word (level, detail_section, word, meaning, korean, answer, active_yn, create_dt)
VALUES (1, 1, 'hello', 'a greeting', '안녕하세요', 'hello', 'Y', CURRENT_TIMESTAMP);
```

### Method 2: Edit sample_data-h2.sql

1. Open `sample_data-h2.sql`
2. Add more INSERT statements
3. Run in H2 Console

---

## Switching to MySQL Later (Optional)

If you want to use MySQL in production:

1. Install MySQL (see [XAMPP_SETUP_GUIDE.md](XAMPP_SETUP_GUIDE.md))
2. Edit `application.properties`
3. Comment out H2 configuration
4. Uncomment MySQL configuration
5. Use `schema.sql` and `sample_data.sql` (MySQL versions)
6. Rebuild and run

---

## Summary - What You Need

### Required:
- ✅ **Java JDK 8+** (Download: https://adoptium.net/temurin/releases/)

### Already Included:
- ✅ Maven wrapper (`mvnw.cmd`)
- ✅ H2 database (in pom.xml)
- ✅ Node.js (auto-installed)
- ✅ Configuration files
- ✅ Sample data

### Installation Steps:
1. ✅ Install Java JDK
2. ✅ Run `mvnw.cmd clean package -P local`
3. ✅ Run `mvnw.cmd spring-boot:run -P local`
4. ✅ **Done!**

---

## Next Steps

1. ✅ Load sample data using H2 Console
2. ✅ Test the API with Swagger
3. ✅ Explore the web interface
4. ✅ Add your own vocabulary words
5. ✅ Start building features!

---

## Quick Reference Card

| What | Where | How |
|------|-------|-----|
| **Build** | Command Prompt | `mvnw.cmd clean package -P local` |
| **Run** | Command Prompt | `mvnw.cmd spring-boot:run -P local` |
| **Stop** | Command Prompt | `Ctrl + C` |
| **Web App** | Browser | http://localhost:8080 |
| **Database Console** | Browser | http://localhost:8080/h2-console |
| **API Docs** | Browser | http://localhost:8080/swagger-ui.html |
| **Database File** | File System | `./data/vocabulary_test.mv.db` |
| **Config** | Text Editor | `src/main/resources-local/application.properties` |

---

## Need Help?

- **Java Installation:** See Step 1 above
- **Build Issues:** Try `mvnw.cmd clean package -P local`
- **Can't connect to H2:** Check JDBC URL in H2 Console
- **Want MySQL instead:** See [XAMPP_SETUP_GUIDE.md](XAMPP_SETUP_GUIDE.md)

**You're ready to go!** Just install Java and run 2 commands. No database installation needed! 🚀
