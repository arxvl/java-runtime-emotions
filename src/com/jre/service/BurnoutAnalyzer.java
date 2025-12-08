/**
 * BurnoutAnalyzer.java
 * Analyzes mood and workload data to detect burnout risk
 */
package com.jre.service;

import com.jre.model.MoodLog;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BurnoutAnalyzer {
    private MoodTracker moodTracker;
    private WorkloadManager workloadManager;

    // Burnout detection thresholds
    private static final double HIGH_STRESS_THRESHOLD = 7.0;
    private static final double LOW_MOOD_THRESHOLD = 4.0;
    private static final int HIGH_WORKLOAD_THRESHOLD = 15;
    private static final int ANALYSIS_DAYS = 7;

    public BurnoutAnalyzer(MoodTracker moodTracker, WorkloadManager workloadManager) {
        this.moodTracker = moodTracker;
        this.workloadManager = workloadManager;
    }

    public String analyzeBurnoutRisk() {
        int riskScore = calculateBurnoutScore();

        if (riskScore >= 8) {
            return "HIGH";
        } else if (riskScore >= 5) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    public int calculateBurnoutScore() {
        int score = 0;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(ANALYSIS_DAYS);

        // Factor 1: High sustained stress (0-3 points)
        double avgStress = moodTracker.calculateAverageStressInRange(weekAgo, now);
        if (avgStress >= 8.0) {
            score += 3;
        } else if (avgStress >= HIGH_STRESS_THRESHOLD) {
            score += 2;
        } else if (avgStress >= 6.0) {
            score += 1;
        }

        // Factor 2: Low or declining mood (0-3 points)
        double avgMood = moodTracker.calculateAverageMoodInRange(weekAgo, now);
        if (avgMood <= 3.0) {
            score += 3;
        } else if (avgMood <= LOW_MOOD_THRESHOLD) {
            score += 2;
        } else if (avgMood <= 5.0) {
            score += 1;
        }

        // Factor 3: High workload intensity (0-3 points)
        int workloadIntensity = workloadManager.calculateWorkloadIntensity();
        if (workloadIntensity >= 25) {
            score += 3;
        } else if (workloadIntensity >= HIGH_WORKLOAD_THRESHOLD) {
            score += 2;
        } else if (workloadIntensity >= 10) {
            score += 1;
        }

        // Factor 4: Consecutive high stress days (0-2 points)
        if (detectConsecutiveHighStress(3)) {
            score += 2;
        } else if (detectConsecutiveHighStress(2)) {
            score += 1;
        }

        return Math.min(score, 10); // Cap at 10
    }

    public boolean detectHighStress() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(ANALYSIS_DAYS);
        double avgStress = moodTracker.calculateAverageStressInRange(weekAgo, now);
        return avgStress >= HIGH_STRESS_THRESHOLD;
    }

    public boolean detectMoodDecline() {
        List<MoodLog> recentLogs = moodTracker.getRecentLogs(ANALYSIS_DAYS);
        if (recentLogs.size() < 4) {
            return false;
        }

        // Compare first half average with second half average
        int midPoint = recentLogs.size() / 2;
        double firstHalfAvg = recentLogs.subList(0, midPoint).stream()
                .mapToInt(MoodLog::getMoodLevel)
                .average()
                .orElse(0);
        double secondHalfAvg = recentLogs.subList(midPoint, recentLogs.size()).stream()
                .mapToInt(MoodLog::getMoodLevel)
                .average()
                .orElse(0);

        // Decline if mood dropped by 2+ points
        return (firstHalfAvg - secondHalfAvg) >= 2.0;
    }

    private boolean detectConsecutiveHighStress(int consecutiveDays) {
        List<MoodLog> recentLogs = moodTracker.getRecentLogs(consecutiveDays);
        if (recentLogs.size() < consecutiveDays) {
            return false;
        }

        return recentLogs.stream()
                .allMatch(log -> log.getStressLevel() >= HIGH_STRESS_THRESHOLD);
    }

    public String assessWorkload() {
        int intensity = workloadManager.calculateWorkloadIntensity();
        if (intensity >= 25) {
            return "VERY HIGH";
        } else if (intensity >= HIGH_WORKLOAD_THRESHOLD) {
            return "HIGH";
        } else if (intensity >= 10) {
            return "MODERATE";
        } else if (intensity >= 5) {
            return "LOW";
        } else {
            return "MINIMAL";
        }
    }

    public List<String> generateWarnings() {
        List<String> warnings = new ArrayList<>();
        String riskLevel = analyzeBurnoutRisk();

        // General risk warning
        if (riskLevel.equals("HIGH")) {
            warnings.add("HIGH BURNOUT RISK DETECTED - Immediate action recommended");
        } else if (riskLevel.equals("MEDIUM")) {
            warnings.add("MEDIUM BURNOUT RISK - Monitor closely and take preventive measures");
        }

        // Specific warnings
        if (detectHighStress()) {
            warnings.add("Sustained high stress levels detected over the past week");
        }

        if (detectMoodDecline()) {
            warnings.add("Declining mood trend identified - consider reaching out for support");
        }

        String workloadLevel = assessWorkload();
        if (workloadLevel.equals("VERY HIGH") || workloadLevel.equals("HIGH")) {
            warnings.add("Academic workload is " + workloadLevel.toLowerCase() + 
                        " - consider prioritizing and delegating tasks");
        }

        int overdueTasks = workloadManager.getOverdueTasks().size();
        if (overdueTasks > 0) {
            warnings.add(overdueTasks + " overdue task(s) detected - address immediately");
        }

        // Recommendations
        if (!warnings.isEmpty()) {
            warnings.add("");
            warnings.add("RECOMMENDATIONS:");
            if (riskLevel.equals("HIGH")) {
                warnings.add("• Speak with academic counselor or mental health professional");
                warnings.add("• Consider reducing course load if possible");
                warnings.add("• Schedule regular breaks and self-care activities");
            } else if (riskLevel.equals("MEDIUM")) {
                warnings.add("• Practice stress management techniques (meditation, exercise)");
                warnings.add("• Improve time management and task prioritization");
                warnings.add("• Maintain regular sleep schedule");
            }
            warnings.add("• Connect with friends and support network");
            warnings.add("• Review and adjust your study schedule");
        }

        return warnings;
    }

    public String getDetailedAnalysis() {
        StringBuilder analysis = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(ANALYSIS_DAYS);

        analysis.append("=== BURNOUT RISK ANALYSIS ===\n\n");
        analysis.append("Analysis Period: Past 7 days\n");
        analysis.append(String.format("Burnout Risk Level: %s\n", analyzeBurnoutRisk()));
        analysis.append(String.format("Burnout Score: %d/10\n\n", calculateBurnoutScore()));

        analysis.append("--- Stress Analysis ---\n");
        double avgStress = moodTracker.calculateAverageStressInRange(weekAgo, now);
        analysis.append(String.format("Average Stress: %.1f/10\n", avgStress));
        analysis.append(String.format("Status: %s\n\n", 
                avgStress >= HIGH_STRESS_THRESHOLD ? "HIGH" : "✓ Normal"));

        analysis.append("--- Mood Analysis ---\n");
        double avgMood = moodTracker.calculateAverageMoodInRange(weekAgo, now);
        analysis.append(String.format("Average Mood: %.1f/10\n", avgMood));
        analysis.append(String.format("Trend: %s\n\n", 
                detectMoodDecline() ? "Declining" : "✓ Stable"));

        analysis.append("--- Workload Analysis ---\n");
        analysis.append(String.format("Workload Level: %s\n", assessWorkload()));
        analysis.append(String.format("Pending Tasks: %d\n", workloadManager.getPendingTasks()));
        analysis.append(String.format("Overdue Tasks: %d\n\n", workloadManager.getOverdueTasks().size()));

        List<String> warnings = generateWarnings();
        if (!warnings.isEmpty()) {
            analysis.append("--- Warnings & Recommendations ---\n");
            for (String warning : warnings) {
                analysis.append(warning).append("\n");
            }
        }

        return analysis.toString();
    }
}