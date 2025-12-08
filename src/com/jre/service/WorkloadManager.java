/**
 * WorkloadManager.java
 * Manages academic tasks and workload tracking
 */
package com.jre.service;

import com.jre.model.Task;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkloadManager {
    private List<Task> tasks;

    // Priority ordering map for sorting and scoring (higher value = higher priority)
    private static final Map<String, Integer> PRIORITY_ORDER = Map.of(
            "high", 3,
            "medium", 2,
            "low", 1
    );

    public WorkloadManager() {
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        if (task == null) return;
        tasks.add(task);
        updateOverdueStatus();
    }

    /**
     * Returns a sorted copy of tasks.
     * Order: High -> Medium -> Low, then by due date (earliest first).
     */
    public List<Task> getTasks() {
        return tasks.stream()
                .sorted(taskPriorityThenDueComparator())
                .collect(Collectors.toList());
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = (tasks == null) ? new ArrayList<>() : new ArrayList<>(tasks);
        updateOverdueStatus();
    }

    public List<Task> getTasksByPriority(String priority) {
        if (priority == null) return new ArrayList<>();
        String pLower = priority.toLowerCase();
        return tasks.stream()
                .filter(task -> safeEqualsIgnoreCase(task.getPriority(), pLower))
                .sorted(taskPriorityThenDueComparator())
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByStatus(String status) {
        if (status == null) return new ArrayList<>();
        String sLower = status.toLowerCase();
        return tasks.stream()
                .filter(task -> safeEqualsIgnoreCase(task.getStatus(), sLower))
                .sorted(taskPriorityThenDueComparator())
                .collect(Collectors.toList());
    }

    public List<Task> getUpcomingTasks(int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        return tasks.stream()
                .filter(task -> !safeEqualsIgnoreCase(task.getStatus(), "completed"))
                .filter(task -> task.getDueDate() != null && !task.getDueDate().isBefore(today))
                .filter(task -> !task.getDueDate().isAfter(endDate))
                .sorted(taskPriorityThenDueComparator())
                .collect(Collectors.toList());
    }

    public List<Task> getOverdueTasks() {
        updateOverdueStatus();
        return tasks.stream()
                .filter(task -> safeEqualsIgnoreCase(task.getStatus(), "overdue"))
                .sorted(taskPriorityThenDueComparator())
                .collect(Collectors.toList());
    }

    public void updateTaskStatus(String taskId, String newStatus) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.setStatus(newStatus);
            updateOverdueStatus();
        }
    }

    public Task getTaskById(String taskId) {
        if (taskId == null) return null;
        return tasks.stream()
                .filter(task -> task.getEntryId() != null && task.getEntryId().equals(taskId))
                .findFirst()
                .orElse(null);
    }

    public boolean removeTask(String taskId) {
        if (taskId == null) return false;
        boolean removed = tasks.removeIf(task -> task.getEntryId() != null && task.getEntryId().equals(taskId));
        if (removed) updateOverdueStatus();
        return removed;
    }

    public boolean updateTask(Task updatedTask) {
        if (updatedTask == null || updatedTask.getEntryId() == null) return false;
        Task existing = getTaskById(updatedTask.getEntryId());
        if (existing != null) {
            existing.setTaskName(updatedTask.getTaskName());
            existing.setDescription(updatedTask.getDescription());
            existing.setDueDate(updatedTask.getDueDate());
            existing.setPriority(updatedTask.getPriority());
            existing.setStatus(updatedTask.getStatus());
            updateOverdueStatus();
            return true;
        }
        return false;
    }

    /**
     * Calculates a workload "intensity" score.
     * Priority weights: High=3, Medium=2, Low=1 (applied in addition to base scores).
     */
    public int calculateWorkloadIntensity() {
        List<Task> pendingTasks = tasks.stream()
                .filter(task -> !safeEqualsIgnoreCase(task.getStatus(), "completed"))
                .collect(Collectors.toList());

        if (pendingTasks.isEmpty()) {
            return 0;
        }

        int score = 0;
        LocalDate today = LocalDate.now();
        for (Task task : pendingTasks) {
            // Base score for any pending task
            score += 1;

            // Add points based on priority (safe lookup)
            int pScore = PRIORITY_ORDER.getOrDefault(
                    (task.getPriority() == null) ? "" : task.getPriority().toLowerCase(), 1);
            score += pScore;

            // Add points for urgency (days until due) with null-safety
            if (task.getDueDate() == null) {
                // no due date => small penalty (no urgency)
                continue;
            }
            long daysUntilDue = java.time.temporal.ChronoUnit.DAYS.between(today, task.getDueDate());
            if (daysUntilDue < 0) {
                score += 5; // Overdue
            } else if (daysUntilDue <= 2) {
                score += 4; // Very urgent
            } else if (daysUntilDue <= 5) {
                score += 2; // Urgent
            }
        }

        return score;
    }

    public double getCompletionRate() {
        if (tasks.isEmpty()) {
            return 0.0;
        }
        long completedCount = tasks.stream()
                .filter(task -> safeEqualsIgnoreCase(task.getStatus(), "completed"))
                .count();
        return (completedCount * 100.0) / tasks.size();
    }

    public int getTotalTasks() {
        return tasks.size();
    }

    public int getCompletedTasks() {
        return (int) tasks.stream()
                .filter(task -> safeEqualsIgnoreCase(task.getStatus(), "completed"))
                .count();
    }

    public int getPendingTasks() {
        return (int) tasks.stream()
                .filter(task -> !safeEqualsIgnoreCase(task.getStatus(), "completed"))
                .count();
    }

    /**
     * Update status to "Overdue" only if the task is past due and is not already
     * marked as Completed, Overdue, or Checklist.
     */
    private void updateOverdueStatus() {
        LocalDate today = LocalDate.now();
        for (Task task : tasks) {
            if (task == null) continue;
            String status = (task.getStatus() == null) ? "" : task.getStatus().toLowerCase();

            // Skip Completed, Overdue and Checklist tasks
            if (status.equals("completed") || status.equals("overdue") || status.equals("checklist")) {
                continue;
            }

            // Only set Overdue if due date exists and is before today
            if (task.getDueDate() != null && task.getDueDate().isBefore(today)) {
                task.setStatus("Overdue");
            }
        }
    }

    // ---------- Helpers ----------

    private static boolean safeEqualsIgnoreCase(String value, String targetLower) {
        if (value == null || targetLower == null) return false;
        return value.equalsIgnoreCase(targetLower);
    }

    private static Comparator<Task> taskPriorityThenDueComparator() {
        return Comparator
                .comparingInt((Task t) -> PRIORITY_ORDER.getOrDefault(
                        (t.getPriority() == null) ? "" : t.getPriority().toLowerCase(), 0))
                .reversed() // higher priority value first (High -> Medium -> Low)
                .thenComparing((Task t) -> t.getDueDate() == null ? LocalDate.MAX : t.getDueDate());
    }
}
