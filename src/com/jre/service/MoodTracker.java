/**
 * MoodTracker.java
 * Manages collection of mood logs and provides analysis
 */
package com.jre.service;

import com.jre.model.MoodLog;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MoodTracker {
    private List<MoodLog> moodLogs;

    public MoodTracker() {
        this.moodLogs = new ArrayList<>();
    }

    public void addMoodLog(MoodLog log) {
        moodLogs.add(log);
    }

    public List<MoodLog> getMoodLogs() {
        return new ArrayList<>(moodLogs);
    }

    public void setMoodLogs(List<MoodLog> logs) {
        this.moodLogs = new ArrayList<>(logs);
    }

    public List<MoodLog> getLogsInRange(LocalDateTime from, LocalDateTime to) {
        return moodLogs.stream()
                .filter(log -> !log.getDate().isBefore(from) && !log.getDate().isAfter(to))
                .collect(Collectors.toList());
    }

    public double calculateAverageMood() {
        if (moodLogs.isEmpty()) {
            return 0.0;
        }
        return moodLogs.stream()
                .mapToInt(MoodLog::getMoodLevel)
                .average()
                .orElse(0.0);
    }

    public double calculateAverageStress() {
        if (moodLogs.isEmpty()) {
            return 0.0;
        }
        return moodLogs.stream()
                .mapToInt(MoodLog::getStressLevel)
                .average()
                .orElse(0.0);
    }

    public double calculateAverageMoodInRange(LocalDateTime from, LocalDateTime to) {
        List<MoodLog> logsInRange = getLogsInRange(from, to);
        if (logsInRange.isEmpty()) {
            return 0.0;
        }
        return logsInRange.stream()
                .mapToInt(MoodLog::getMoodLevel)
                .average()
                .orElse(0.0);
    }

    public double calculateAverageStressInRange(LocalDateTime from, LocalDateTime to) {
        List<MoodLog> logsInRange = getLogsInRange(from, to);
        if (logsInRange.isEmpty()) {
            return 0.0;
        }
        return logsInRange.stream()
                .mapToInt(MoodLog::getStressLevel)
                .average()
                .orElse(0.0);
    }

    public int getHighestMood() {
        return moodLogs.stream()
                .mapToInt(MoodLog::getMoodLevel)
                .max()
                .orElse(0);
    }

    public int getLowestMood() {
        return moodLogs.stream()
                .mapToInt(MoodLog::getMoodLevel)
                .min()
                .orElse(0);
    }

    public int getHighestStress() {
        return moodLogs.stream()
                .mapToInt(MoodLog::getStressLevel)
                .max()
                .orElse(0);
    }

    public int getLowestStress() {
        return moodLogs.stream()
                .mapToInt(MoodLog::getStressLevel)
                .min()
                .orElse(0);
    }

    public MoodLog getLatestLog() {
        if (moodLogs.isEmpty()) {
            return null;
        }
        return moodLogs.stream()
                .max((log1, log2) -> log1.getDate().compareTo(log2.getDate()))
                .orElse(null);
    }

    public List<MoodLog> getRecentLogs(int count) {
        return moodLogs.stream()
                .sorted((log1, log2) -> log2.getDate().compareTo(log1.getDate()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public int getTotalLogs() {
        return moodLogs.size();
    }
}