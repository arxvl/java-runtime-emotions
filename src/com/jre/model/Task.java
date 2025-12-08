/**
 * Task.java
 * Represents an academic task or workload item
 */
package com.jre.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task extends LogEntry {
    private String taskName;
    private String description;
    private LocalDate dueDate;
    private String priority; // Low, Medium, High
    private String status;   // Pending, In Progress, Completed, Overdue

    public Task(LocalDateTime createdDate, String taskName, String description,
                LocalDate dueDate, String priority, String status, String notes) {
        super(createdDate, notes);
        this.taskName = taskName;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
    }

    public Task(String entryId, LocalDateTime createdDate, String taskName, String description,
                LocalDate dueDate, String priority, String status, String notes) {
        super(entryId, createdDate, notes);
        this.taskName = taskName;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
    }

    // Getters
    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOverdue() {
        return !status.equals("Completed") && dueDate.isBefore(LocalDate.now());
    }

    @Override
    public String toFileString() {
        String safeTaskName = taskName.replace("|", "&#124;");
        String safeDescription = description.replace("|", "&#124;").replace("\n", "\\n");
        String safeNotes = notes.replace("|", "&#124;").replace("\n", "\\n");
        return String.format("%s|TASK|%s|%s|%s|%s|%s|%s|%s",
                entryId, formatDate(), safeTaskName, safeDescription,
                dueDate.toString(), priority, status, safeNotes);
    }

    @Override
    public String getLogType() {
        return "TASK";
    }

    public static Task fromFileString(String line) {
        String[] parts = line.split("\\|", 8);
        if (parts.length != 8 || !parts[1].equals("TASK")) {
            throw new IllegalArgumentException("Invalid task format");
        }
        String taskName = parts[2].replace("&#124;", "|");
        String description = parts[3].replace("&#124;", "|").replace("\\n", "\n");
        String notes = parts[7].replace("&#124;", "|").replace("\\n", "\n");
        return new Task(
                parts[0],
                parseDate(parts[1].replace("TASK", parts[1])),
                taskName,
                description,
                LocalDate.parse(parts[4]),
                parts[5],
                parts[6],
                notes
        );
    }

    // Fixed fromFileString method
    public static Task fromFileStringFixed(String line) {
        String[] parts = line.split("\\|", 9);
        if (parts.length != 9 || !parts[1].equals("TASK")) {
            throw new IllegalArgumentException("Invalid task format");
        }
        String taskName = parts[3].replace("&#124;", "|");
        String description = parts[4].replace("&#124;", "|").replace("\\n", "\n");
        String notes = parts[8].replace("&#124;", "|").replace("\\n", "\n");
        return new Task(
                parts[0],
                parseDate(parts[2]),
                taskName,
                description,
                LocalDate.parse(parts[5]),
                parts[6],
                parts[7],
                notes
        );
    }

    @Override
    public String toString() {
        return String.format("Task{ID='%s', Name='%s', Due=%s, Priority=%s, Status=%s}",
                entryId, taskName, dueDate, priority, status);
    }
}