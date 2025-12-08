/**
 * MoodLog.java
 * Represents a mood and stress log entry
 */
package com.jre.model;

import java.time.LocalDateTime;

public class MoodLog extends LogEntry {
    private int moodLevel;
    private int stressLevel;

    public MoodLog(LocalDateTime date, int moodLevel, int stressLevel, String notes) {
        super(date, notes);
        setMoodLevel(moodLevel);
        setStressLevel(stressLevel);
    }

    public MoodLog(String entryId, LocalDateTime date, int moodLevel, int stressLevel, String notes) {
        super(entryId, date, notes);
        setMoodLevel(moodLevel);
        setStressLevel(stressLevel);
    }

    // Getters
    public int getMoodLevel() {
        return moodLevel;
    }

    public int getStressLevel() {
        return stressLevel;
    }

    // Setters with validation
    public void setMoodLevel(int moodLevel) {
        if (moodLevel < 1 || moodLevel > 10) {
            throw new IllegalArgumentException("Mood level must be between 1 and 10");
        }
        this.moodLevel = moodLevel;
    }

    public void setStressLevel(int stressLevel) {
        if (stressLevel < 1 || stressLevel > 10) {
            throw new IllegalArgumentException("Stress level must be between 1 and 10");
        }
        this.stressLevel = stressLevel;
    }

    @Override
    public String toFileString() {
        String safeNotes = notes.replace("|", "&#124;").replace("\n", "\\n");
        return String.format("%s|MOOD|%s|%d|%d|%s",
                entryId, formatDate(), moodLevel, stressLevel, safeNotes);
    }

    @Override
    public String getLogType() {
        return "MOOD";
    }

    public static MoodLog fromFileString(String line) {
        String[] parts = line.split("\\|", 6);
        if (parts.length != 6 || !parts[1].equals("MOOD")) {
            throw new IllegalArgumentException("Invalid mood log format");
        }
        String notes = parts[5].replace("&#124;", "|").replace("\\n", "\n");
        return new MoodLog(
                parts[0],
                parseDate(parts[2]),
                Integer.parseInt(parts[3]),
                Integer.parseInt(parts[4]),
                notes
        );
    }

    @Override
    public String toString() {
        return String.format("MoodLog{ID='%s', Date=%s, Mood=%d, Stress=%d, Notes='%s'}",
                entryId, date, moodLevel, stressLevel, notes);
    }
}