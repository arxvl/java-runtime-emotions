# JRE: Java Runtime Emotions

---

## PROJECT STRUCTURE
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

