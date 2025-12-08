/**
 * FileHandler.java
 * Manages file I/O operations for persistence
 */
package com.jre.util;

import com.jre.model.MoodLog;
import com.jre.model.Task;
import com.jre.model.User;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String DATA_DIR = "data";
    private static final String USER_FILE = DATA_DIR + "/user_profile.txt";
    private static final String MOOD_FILE = DATA_DIR + "/mood_logs.txt";
    private static final String TASK_FILE = DATA_DIR + "/tasks.txt";

    public FileHandler() {
        initializeDataDirectory();
    }

    private void initializeDataDirectory() {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                System.out.println("Data directory created: " + DATA_DIR);
            }
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
        }
    }

    // ==================== USER OPERATIONS ====================

    public void saveUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            writer.write(user.toFileString());
            writer.newLine();
            System.out.println("User profile saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving user profile: " + e.getMessage());
        }
    }

    public User loadUser() {
        File file = new File(USER_FILE);
        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                return User.fromFileString(line);
            }
        } catch (IOException e) {
            System.err.println("Error loading user profile: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing user profile: " + e.getMessage());
        }
        return null;
    }

    // ==================== MOOD LOG OPERATIONS ====================

    public void saveMoodLogs(List<MoodLog> moodLogs) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MOOD_FILE))) {
            for (MoodLog log : moodLogs) {
                writer.write(log.toFileString());
                writer.newLine();
            }
            System.out.println(moodLogs.size() + " mood log(s) saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving mood logs: " + e.getMessage());
        }
    }

    public List<MoodLog> loadMoodLogs() {
        List<MoodLog> moodLogs = new ArrayList<>();
        File file = new File(MOOD_FILE);
        
        if (!file.exists()) {
            return moodLogs;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(MOOD_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        MoodLog log = MoodLog.fromFileString(line);
                        moodLogs.add(log);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Skipping invalid mood log entry: " + e.getMessage());
                    }
                }
            }
            System.out.println(moodLogs.size() + " mood log(s) loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error loading mood logs: " + e.getMessage());
        }
        return moodLogs;
    }

    public void appendMoodLog(MoodLog log) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MOOD_FILE, true))) {
            writer.write(log.toFileString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error appending mood log: " + e.getMessage());
        }
    }

    // ==================== TASK OPERATIONS ====================

    public void saveTasks(List<Task> tasks) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TASK_FILE))) {
            for (Task task : tasks) {
                writer.write(task.toFileString());
                writer.newLine();
            }
            System.out.println(tasks.size() + " task(s) saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    public List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        File file = new File(TASK_FILE);
        
        if (!file.exists()) {
            return tasks;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(TASK_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        Task task = Task.fromFileStringFixed(line);
                        tasks.add(task);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Skipping invalid task entry: " + e.getMessage());
                    }
                }
            }
            System.out.println(tasks.size() + " task(s) loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
        return tasks;
    }

    public void appendTask(Task task) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TASK_FILE, true))) {
            writer.write(task.toFileString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error appending task: " + e.getMessage());
        }
    }

    // ==================== BACKUP OPERATIONS ====================

    public void createBackup() {
        try {
            backupFile(USER_FILE);
            backupFile(MOOD_FILE);
            backupFile(TASK_FILE);
            System.out.println("Backup created successfully.");
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
        }
    }

    private void backupFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            Path source = file.toPath();
            Path backup = Paths.get(filePath + ".backup");
            Files.copy(source, backup, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // ==================== UTILITY METHODS ====================

    public boolean userProfileExists() {
        return new File(USER_FILE).exists();
    }

    public void deleteAllData() {
        try {
            Files.deleteIfExists(Paths.get(USER_FILE));
            Files.deleteIfExists(Paths.get(MOOD_FILE));
            Files.deleteIfExists(Paths.get(TASK_FILE));
            System.out.println("All data deleted successfully.");
        } catch (IOException e) {
            System.err.println("Error deleting data: " + e.getMessage());
        }
    }
}