package com.jre.service;

import com.jre.model.MoodLog;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportGenerator {
    private MoodTracker moodTracker;
    private BurnoutAnalyzer burnoutAnalyzer;

    public ReportGenerator(MoodTracker moodTracker, BurnoutAnalyzer burnoutAnalyzer) {
        this.moodTracker = moodTracker;
        this.burnoutAnalyzer = burnoutAnalyzer;
    }

    public String generateWeeklyReport() {
        StringBuilder report = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

        report.append("╔══════════════════════════════════════════════════════════════╗\n");
        report.append("║     STUDENT STRESS & MOOD MONITORING SYSTEM                  ║\n");
        report.append("║           Weekly Summary Report                              ║\n");
        report.append("╚══════════════════════════════════════════════════════════════╝\n\n");

        report.append(String.format("Report Generated: %s\n", now.format(formatter)));
        report.append(String.format("Report Period: %s to %s\n\n",
                weekAgo.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                now.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));

        report.append(generateMoodSummary(weekAgo, now)).append("\n");
        report.append(generateStressSummary(weekAgo, now)).append("\n");
        report.append(generateBurnoutAssessment()).append("\n");
        report.append(generateRecommendations());

        report.append("\n═══════════════════════════════════════════════════════════════\n");
        report.append("2025. Alzaga, Arevalo, Letada\n");
        report.append("═══════════════════════════════════════════════════════════════\n");

        return report.toString();
    }

    private String generateMoodSummary(LocalDateTime from, LocalDateTime to) {
        StringBuilder summary = new StringBuilder();
        List<MoodLog> logs = moodTracker.getLogsInRange(from, to);

        summary.append("┌─────────────────────────────────────────────────────────────┐\n");
        summary.append("│                    MOOD ANALYSIS                            │\n");
        summary.append("└─────────────────────────────────────────────────────────────┘\n");

        if (logs.isEmpty()) {
            summary.append("  No mood entries recorded during this period.\n");
            return summary.toString();
        }

        double avgMood = moodTracker.calculateAverageMoodInRange(from, to);
        int highest = logs.stream().mapToInt(MoodLog::getMoodLevel).max().orElse(0);
        int lowest = logs.stream().mapToInt(MoodLog::getMoodLevel).min().orElse(0);

        summary.append(String.format("  Total Entries: %d\n", logs.size()));
        summary.append(String.format("  Average Mood: %.1f/10 %s\n", avgMood, getMoodEmoji(avgMood)));
        summary.append(String.format("  Highest Mood: %d/10\n", highest));
        summary.append(String.format("  Lowest Mood: %d/10\n", lowest));
        summary.append(String.format("  Mood Trend: %s\n", burnoutAnalyzer.detectMoodDecline() ? "Declining" : "✓ Stable/Improving"));
        summary.append(String.format("  Mood Chart: %s\n", generateMoodChart(avgMood)));

        return summary.toString();
    }

    private String generateStressSummary(LocalDateTime from, LocalDateTime to) {
        StringBuilder summary = new StringBuilder();
        List<MoodLog> logs = moodTracker.getLogsInRange(from, to);

        summary.append("┌─────────────────────────────────────────────────────────────┐\n");
        summary.append("│                   STRESS ANALYSIS                           │\n");
        summary.append("└─────────────────────────────────────────────────────────────┘\n");

        if (logs.isEmpty()) {
            summary.append("  No stress entries recorded during this period.\n");
            return summary.toString();
        }

        double avgStress = moodTracker.calculateAverageStressInRange(from, to);
        int highest = logs.stream().mapToInt(MoodLog::getStressLevel).max().orElse(0);
        int lowest = logs.stream().mapToInt(MoodLog::getStressLevel).min().orElse(0);

        summary.append(String.format("  Total Entries: %d\n", logs.size()));
        summary.append(String.format("  Average Stress: %.1f/10 %s\n", avgStress, getStressLevel(avgStress)));
        summary.append(String.format("  Highest Stress: %d/10\n", highest));
        summary.append(String.format("  Lowest Stress: %d/10\n", lowest));
        summary.append(String.format("  Stress Status: %s\n", burnoutAnalyzer.detectHighStress() ? "Elevated" : "✓ Normal"));
        summary.append(String.format("  Stress Chart: %s\n", generateStressChart(avgStress)));

        return summary.toString();
    }

    private String generateBurnoutAssessment() {
        StringBuilder assessment = new StringBuilder();
        String risk = burnoutAnalyzer.analyzeBurnoutRisk();
        int score = burnoutAnalyzer.calculateBurnoutScore();

        assessment.append("┌─────────────────────────────────────────────────────────────┐\n");
        assessment.append("│                BURNOUT RISK ASSESSMENT                      │\n");
        assessment.append("└─────────────────────────────────────────────────────────────┘\n");

        String emoji = risk.equals("HIGH") ? "🔴" : risk.equals("MEDIUM") ? "🟡" : "🟢";
        assessment.append(String.format("  Overall Risk Level: %s %s\n", emoji, risk));
        assessment.append(String.format("  Burnout Score: %d/10\n", score));
        assessment.append(String.format("  Risk Chart: %s\n\n", generateRiskChart(score)));

        List<String> warnings = burnoutAnalyzer.generateWarnings();
        if (!warnings.isEmpty()) {
            assessment.append("  Warnings & Alerts:\n");
            for (String w : warnings) {
                if (!w.isEmpty() && !w.equals("RECOMMENDATIONS:")) assessment.append("    ").append(w).append("\n");
            }
        }

        return assessment.toString();
    }

    private String generateRecommendations() {
        StringBuilder rec = new StringBuilder();
        String risk = burnoutAnalyzer.analyzeBurnoutRisk();

        rec.append("┌─────────────────────────────────────────────────────────────┐\n");
        rec.append("│                    RECOMMENDATIONS                          │\n");
        rec.append("└─────────────────────────────────────────────────────────────┘\n");

        if (risk.equals("HIGH")) {
            rec.append("   URGENT ACTIONS NEEDED:\n");
            rec.append("    • Schedule appointment with counselor/mental health professional\n");
            rec.append("    • Consider requesting extensions for assignments\n");
            rec.append("    • Discuss workload with your professor\n");
            rec.append("    • Practice daily stress-relief activities\n");
        } else if (risk.equals("MEDIUM")) {
            rec.append("   PREVENTIVE MEASURES:\n");
            rec.append("    • Implement stress management techniques\n");
            rec.append("    • Review and optimize your schedule\n");
            rec.append("    • Ensure adequate sleep (7-9 hours)\n");
            rec.append("    • Connect with support network\n");
        } else {
            rec.append("  ✓ MAINTENANCE TIPS:\n");
            rec.append("    • Continue current positive habits\n");
            rec.append("    • Maintain academic-life balance\n");
            rec.append("    • Stay proactive with task management\n");
            rec.append("    • Keep monitoring your well-being\n");
        }

        rec.append("\n  General Well-being Tips:\n");
        rec.append("    • Take regular breaks during study sessions\n");
        rec.append("    • Exercise 3-4 times per week\n");
        rec.append("    • Practice mindfulness or meditation\n");
        rec.append("    • Maintain social connections\n");

        return rec.toString();
    }

    private String getMoodEmoji(double mood) {
        if (mood >= 8) return "😄";
        if (mood >= 6) return "😊";
        if (mood >= 4) return "😐";
        if (mood >= 2) return "😟";
        return "😢";
    }

    private String getStressLevel(double stress) {
        if (stress >= 8) return "Very High";
        if (stress >= 6) return "High";
        if (stress >= 4) return "Moderate";
        if (stress >= 2) return "Low";
        return "Minimal";
    }

    private String generateMoodChart(double avgMood) {
        int bars = (int) Math.round(avgMood);
        StringBuilder chart = new StringBuilder("[");
        for (int i = 0; i < 10; i++) chart.append(i < bars ? "█" : "░");
        chart.append("]");
        return chart.toString();
    }

    private String generateStressChart(double avgStress) {
        int bars = (int) Math.round(avgStress);
        StringBuilder chart = new StringBuilder("[");
        for (int i = 0; i < 10; i++) chart.append(i < bars ? "█" : "░");
        chart.append("]");
        return chart.toString();
    }

    private String generateRiskChart(int score) {
        StringBuilder chart = new StringBuilder("[");
        for (int i = 0; i < 10; i++) chart.append(i < score ? "█" : "░");
        chart.append("]");
        return chart.toString();
    }
}
