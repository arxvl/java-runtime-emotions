/**
 * LogEntry.java
 * Abstract parent class for all log entries (MoodLog and Task)
 */
package com.jre.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public abstract class LogEntry {
    protected String entryId;
    protected LocalDateTime date;
    protected String notes;

    protected LogEntry(LocalDateTime date, String notes) {
        this.entryId = generateId();
        this.date = date;
        this.notes = notes;
    }

    protected LogEntry(String entryId, LocalDateTime date, String notes) {
        this.entryId = entryId;
        this.date = date;
        this.notes = notes;
    }

    private String generateId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Getters
    public String getEntryId() {
        return entryId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getNotes() {
        return notes;
    }

    // Setters
    public void setNotes(String notes) {
        this.notes = notes;
    }

    protected String formatDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return date.format(formatter);
    }

    protected static LocalDateTime parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return LocalDateTime.parse(dateStr, formatter);
    }

    // Abstract methods to be implemented by subclasses
    public abstract String toFileString();
    public abstract String getLogType();
}