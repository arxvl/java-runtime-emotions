# JRE: Java Runtime Emotions
## Compilation and Execution Guide

---

## PROJECT STRUCTURE

Create the following directory structure:

```
jre/
├── src/
│   └── com/
│       └── jre/
│           ├── model/
│           │   ├── User.java
│           │   ├── LogEntry.java
│           │   ├── MoodLog.java
│           │   └── Task.java
│           ├── service/
│           │   ├── MoodTracker.java
│           │   ├── WorkloadManager.java
│           │   ├── BurnoutAnalyzer.java
│           │   └── ReportGenerator.java
│           ├── util/
│           │   └── FileHandler.java
│           └── MainApp.java
├── data/
│   └── (data files will be created here automatically)
└── README.md
```

---

## STEP-BY-STEP COMPILATION INSTRUCTIONS

### Method 1: Using Command Line (Windows)

1. **Save all Java files in the correct package structure**

2. **Open Command Prompt and navigate to the project root:**
   ```cmd
   cd C:\path\to\jre
   ```

3. **Compile all Java files:**
   ```cmd
   javac -d bin src/com/jre/model/*.java src/com/jre/service/*.java src/com/jre/util/*.java src/com/jre/MainApp.java
   ```

4. **Run the application:**
   ```cmd
   java -cp bin com.jre.MainApp
   ```

### Method 2: Using Command Line (Linux/Mac)

1. **Navigate to project root:**
   ```bash
   cd /path/to/jre
   ```

2. **Compile:**
   ```bash
   javac -d bin src/com/jre/model/*.java src/com/jre/service/*.java src/com/jre/util/*.java src/com/jre/MainApp.java
   ```

3. **Run:**
   ```bash
   java -cp bin com.jre.MainApp
   ```

### Method 3: Using an IDE (Eclipse)

1. **Create a new Java Project:**
   - File → New → Java Project
   - Name: `jre`
   - Click Finish

2. **Create package structure:**
   - Right-click `src` folder
   - New → Package: `com.jre`
   - Create subpackages: `com.jre.model`, `com.jre.service`, `com.jre.util`

3. **Add Java files:**
   - Copy each .java file to its respective package

4. **Run the application:**
   - Right-click `MainApp.java`
   - Run As → Java Application

### Method 4: Using IntelliJ IDEA

1. **Create New Project:**
   - File → New → Project
   - Select Java
   - Name: jre
   - Create

2. **Create package structure:**
   - Right-click `src` folder
   - New → Package
   - Enter package names as needed

3. **Add source files:**
   - Copy all .java files to appropriate packages

4. **Run:**
   - Right-click `MainApp.java`
   - Run 'MainApp.main()'

### Method 5: Simple Compilation Script

**For Windows (compile.bat):**
```batch
@echo off
echo Compiling Student Stress and Mood Monitoring System...
mkdir bin 2>nul
javac -d bin src\com\jre\model\*.java src\com\jre\service\*.java src\com\jre\util\*.java src\com\jre\MainApp.java
if %errorlevel% == 0 (
    echo Compilation successful!
    echo Run: java -cp bin com.jre.MainApp
) else (
    echo Compilation failed!
)
pause
```

**For Linux/Mac (compile.sh):**
```bash
#!/bin/bash
echo "Compiling Student Stress and Mood Monitoring System..."
mkdir -p bin
javac -d bin src/com/jre/model/*.java src/com/jre/service/*.java src/com/jre/util/*.java src/com/jre/MainApp.java
if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Run: java -cp bin com.jre.MainApp"
else
    echo "Compilation failed!"
fi
```

Make executable: `chmod +x compile.sh`

---

## RUNNING THE APPLICATION

### Option 1: Command Line
```bash
java -cp bin com.jre.MainApp
```

### Option 2: Create Run Script

**Windows (run.bat):**
```batch
@echo off
java -cp bin com.jre.MainApp
pause
```

**Linux/Mac (run.sh):**
```bash
#!/bin/bash
java -cp bin com.jre.MainApp
```

---

## SYSTEM REQUIREMENTS

- **Java Development Kit (JDK)**: Version 8 or higher
- **Operating System**: Windows, Linux, or macOS
- **RAM**: Minimum 512 MB
- **Disk Space**: 50 MB for application and data files

---

## VERIFYING INSTALLATION

After first run, verify that the `data` directory is created with these files:
- `user_profile.txt` (after creating profile)
- `mood_logs.txt` (after logging mood)
- `tasks.txt` (after adding tasks)

---

## TROUBLESHOOTING

### Issue: "javac is not recognized"
**Solution**: Add Java to your PATH environment variable
- Windows: Control Panel → System → Advanced → Environment Variables
- Add Java bin directory to PATH

### Issue: Compilation errors
**Solution**: 
- Ensure all files are in correct package directories
- Check Java version: `java -version`
- Use JDK, not just JRE

### Issue: GUI doesn't display
**Solution**:
- Ensure you're using JDK with JavaFX/Swing support
- Try: `java -Dsun.java2d.uiScale=1.0 -cp bin com.jre.MainApp`

### Issue: Data files not created
**Solution**:
- Check write permissions in application directory
- Manually create `data` folder if needed

### Issue: ClassNotFoundException
**Solution**:
- Verify package structure matches source code
- Ensure `-cp bin` is specified when running

---

## USAGE INSTRUCTIONS

### First Time Setup:
1. Launch the application
2. Click "Create Profile" on welcome screen
3. Fill in all required fields:
   - Student ID (e.g., CS-2024-001)
   - Full Name
   - Email
   - Age
   - Course/Major
4. Click "Save Profile"

### Daily Usage:
1. **Log Mood & Stress:**
   - Select "Log Daily Mood & Stress"
   - Adjust mood slider (1-10)
   - Adjust stress slider (1-10)
   - Add optional notes
   - Click "Save Entry"

2. **Manage Tasks:**
   - Select "Manage Academic Tasks"
   - Fill in task details
   - Set priority and due date
   - Click "Add Task"

3. **View Reports:**
   - Select "View Weekly Report"
   - Review your weekly statistics
   - Export report if needed

4. **Check Burnout Risk:**
   - Select "Check Burnout Risk"
   - Review detailed analysis
   - Follow recommendations

---

## DATA MANAGEMENT

### Backup Data:
Copy the entire `data` folder to a safe location

### Restore Data:
Replace the `data` folder with your backup

### Reset Application:
Delete all files in the `data` folder

---

## FEATURES OVERVIEW

**User Profile Management**
**Daily Mood & Stress Tracking**
**Academic Task Management**
**Burnout Risk Detection**
**Weekly Summary Reports**
**Data Persistence (Text Files)**
**Interactive Swing GUI**
**Comprehensive Analytics**

---

