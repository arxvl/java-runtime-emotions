package com.jre.service;

import com.jre.model.MoodLog;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BurnoutAnalyzer {
    private MoodTracker moodTracker;
    private static final double HIGH_STRESS_THRESHOLD = 7.0;
    private static final double LOW_MOOD_THRESHOLD = 4.0;
    private static final int ANALYSIS_DAYS = 7;

    public BurnoutAnalyzer(MoodTracker moodTracker) {
        this.moodTracker = moodTracker;
    }

    public String analyzeBurnoutRisk() {
        int score = calculateBurnoutScore();
        if (score >= 8) return "HIGH";
        if (score >= 5) return "MEDIUM";
        return "LOW";
    }

    public int calculateBurnoutScore() {
        int score = 0;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(ANALYSIS_DAYS);

        double avgStress = moodTracker.calculateAverageStressInRange(weekAgo, now);
        if (avgStress >= 8) score += 3;
        else if (avgStress >= HIGH_STRESS_THRESHOLD) score += 2;
        else if (avgStress >= 6) score += 1;

        double avgMood = moodTracker.calculateAverageMoodInRange(weekAgo, now);
        if (avgMood <= 3) score += 3;
        else if (avgMood <= LOW_MOOD_THRESHOLD) score += 2;
        else if (avgMood <= 5) score += 1;

        if (detectConsecutiveHighStress(3)) score += 2;
        else if (detectConsecutiveHighStress(2)) score += 1;

        return Math.min(score, 10);
    }

    public boolean detectHighStress() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(ANALYSIS_DAYS);
        double avgStress = moodTracker.calculateAverageStressInRange(weekAgo, now);
        return avgStress >= HIGH_STRESS_THRESHOLD;
    }

    public boolean detectMoodDecline() {
        List<MoodLog> recentLogs = moodTracker.getRecentLogs(ANALYSIS_DAYS);
        if (recentLogs.size() < 4) return false;

        int mid = recentLogs.size() / 2;
        double firstHalf = recentLogs.subList(0, mid).stream().mapToInt(MoodLog::getMoodLevel).average().orElse(0);
        double secondHalf = recentLogs.subList(mid, recentLogs.size()).stream().mapToInt(MoodLog::getMoodLevel).average().orElse(0);

        return (firstHalf - secondHalf) >= 2.0;
    }

    private boolean detectConsecutiveHighStress(int consecutiveDays) {
        List<MoodLog> recentLogs = moodTracker.getRecentLogs(consecutiveDays);
        if (recentLogs.size() < consecutiveDays) return false;
        return recentLogs.stream().allMatch(log -> log.getStressLevel() >= HIGH_STRESS_THRESHOLD);
    }

    public List<String> generateWarnings() {
        List<String> warnings = new ArrayList<>();
        String riskLevel = analyzeBurnoutRisk();

        if (riskLevel.equals("HIGH")) warnings.add("HIGH BURNOUT RISK DETECTED - Immediate action recommended");
        else if (riskLevel.equals("MEDIUM")) warnings.add("MEDIUM BURNOUT RISK - Monitor closely and take preventive measures");

        if (detectHighStress()) warnings.add("Sustained high stress levels detected over the past week");
        if (detectMoodDecline()) warnings.add("Declining mood trend identified - consider reaching out for support");

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
}
