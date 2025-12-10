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
                .sorted((a, b) -> a.getDate().compareTo(b.getDate())) 
                .collect(Collectors.toList());
    }

    public double calculateAverageMoodInRange(LocalDateTime from, LocalDateTime to) {
        List<MoodLog> logsInRange = getLogsInRange(from, to);
        if (logsInRange.isEmpty()) return 0.0;
        return logsInRange.stream().mapToInt(MoodLog::getMoodLevel).average().orElse(0.0);
    }

    public double calculateAverageStressInRange(LocalDateTime from, LocalDateTime to) {
        List<MoodLog> logsInRange = getLogsInRange(from, to);
        if (logsInRange.isEmpty()) return 0.0;
        return logsInRange.stream().mapToInt(MoodLog::getStressLevel).average().orElse(0.0);
    }

    public List<MoodLog> getRecentLogs(int count) {
        return moodLogs.stream()
                .sorted((a, b) -> a.getDate().compareTo(b.getDate())) 
                .skip(Math.max(0, moodLogs.size() - count))
                .collect(Collectors.toList());
    }

    public int getTotalLogs() {
        return moodLogs.size();
    }
}
