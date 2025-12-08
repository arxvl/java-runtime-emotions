/**
 * ReportGenerator.java
 * Generates comprehensive weekly reports
 */
package com.jre.service;

import com.jre.model.MoodLog;
import com.jre.model.Task;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportGenerator {
    private MoodTracker moodTracker;
    private WorkloadManager workloadManager;
    private BurnoutAnalyzer burnoutAnalyzer;

    public ReportGenerator(MoodTracker moodTracker, WorkloadManager workloadManager,
        BurnoutAnalyzer burnoutAnalyzer) {
        this.moodTracker = moodTracker;
        this.workloadManager = workloadManager;
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

        report.append(generateMoodSummary(weekAgo, now));
        report.append("\n");
        report.append(generateStressSummary(weekAgo, now));
        report.append("\n");
        report.append(generateTaskSummary());
        report.append("\n");
        report.append(generateBurnoutAssessment());
        report.append("\n");
        report.append(generateRecommendations());

        report.append("\n");
        report.append("═══════════════════════════════════════════════════════════════\n");
        report.append("Thank you for using JRE: Java Runtime Emotions!\n");
        report.append("═══════════════════════════════════════════════════════════════\n");

        return report.toString();
    }

    public String generateMoodSummary(LocalDateTime from, LocalDateTime to) {
        StringBuilder summary = new StringBuilder();
        List<MoodLog> logsInRange = moodTracker.getLogsInRange(from, to);

        summary.append("┌─────────────────────────────────────────────────────────────┐\n");
        summary.append("│                    MOOD ANALYSIS                            │\n");
        summary.append("└─────────────────────────────────────────────────────────────┘\n");

        if (logsInRange.isEmpty()) {
            summary.append("  No mood entries recorded during this period.\n");
            return summary.toString();
        }

        double avgMood = moodTracker.calculateAverageMoodInRange(from, to);
        int highestMood = logsInRange.stream()
                .mapToInt(MoodLog::getMoodLevel)
                .max()
                .orElse(0);
        int lowestMood = logsInRange.stream()
                .mapToInt(MoodLog::getMoodLevel)
                .min()
                .orElse(0);

        summary.append(String.format("  Total Entries: %d\n", logsInRange.size()));
        summary.append(String.format("  Average Mood: %.1f/10 %s\n", avgMood, getMoodEmoji(avgMood)));
        summary.append(String.format("  Highest Mood: %d/10\n", highestMood));
        summary.append(String.format("  Lowest Mood: %d/10\n", lowestMood));
        summary.append(String.format("  Mood Trend: %s\n", 
                burnoutAnalyzer.detectMoodDecline() ? "Declining" : "✓ Stable/Improving"));
        summary.append(String.format("  Mood Chart: %s\n", generateMoodChart(avgMood)));

        return summary.toString();
    }

    public String generateStressSummary(LocalDateTime from, LocalDateTime to) {
        StringBuilder summary = new StringBuilder();
        List<MoodLog> logsInRange = moodTracker.getLogsInRange(from, to);

        summary.append("┌─────────────────────────────────────────────────────────────┐\n");
        summary.append("│                   STRESS ANALYSIS                           │\n");
        summary.append("└─────────────────────────────────────────────────────────────┘\n");

        if (logsInRange.isEmpty()) {
            summary.append("  No stress entries recorded during this period.\n");
            return summary.toString();
        }

        double avgStress = moodTracker.calculateAverageStressInRange(from, to);
        int highestStress = logsInRange.stream()
                .mapToInt(MoodLog::getStressLevel)
                .max()
                .orElse(0);
        int lowestStress = logsInRange.stream()
                .mapToInt(MoodLog::getStressLevel)
                .min()
                .orElse(0);

        summary.append(String.format("  Total Entries: %d\n", logsInRange.size()));
        summary.append(String.format("  Average Stress: %.1f/10 %s\n", avgStress, getStressLevel(avgStress)));
        summary.append(String.format("  Highest Stress: %d/10\n", highestStress));
        summary.append(String.format("  Lowest Stress: %d/10\n", lowestStress));
        summary.append(String.format("  Stress Status: %s\n", 
                burnoutAnalyzer.detectHighStress() ? "Elevated" : "✓ Normal"));
        summary.append(String.format("  Stress Chart: %s\n", generateStressChart(avgStress)));

        return summary.toString();
    }

    public String generateTaskSummary() {
        StringBuilder summary = new StringBuilder();

        summary.append("┌─────────────────────────────────────────────────────────────┐\n");
        summary.append("│                ACADEMIC WORKLOAD SUMMARY                    │\n");
        summary.append("└─────────────────────────────────────────────────────────────┘\n");

        int totalTasks = workloadManager.getTotalTasks();
        int completedTasks = workloadManager.getCompletedTasks();
        int pendingTasks = workloadManager.getPendingTasks();
        int overdueTasks = workloadManager.getOverdueTasks().size();
        double completionRate = workloadManager.getCompletionRate();

        summary.append(String.format("  Total Tasks: %d\n", totalTasks));
        summary.append(String.format("  Completed: %d (%.1f%%)\n", completedTasks, completionRate));
        summary.append(String.format("  Pending: %d\n", pendingTasks));
        summary.append(String.format("  Overdue: %d %s\n", overdueTasks, 
                overdueTasks > 0 ? "!" : "✓"));
        summary.append(String.format("  Workload Level: %s\n", burnoutAnalyzer.assessWorkload()));

        // Show upcoming tasks
        List<Task> upcoming = workloadManager.getUpcomingTasks(7);
        if (!upcoming.isEmpty()) {
            summary.append("\n  Upcoming Tasks (Next 7 Days):\n");
            int count = Math.min(5, upcoming.size());
            for (int i = 0; i < count; i++) {
                Task task = upcoming.get(i);
                summary.append(String.format("    • %s - Due: %s [%s]\n",
                        task.getTaskName(),
                        task.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd")),
                        task.getPriority()));
            }
            if (upcoming.size() > 5) {
                summary.append(String.format("    ... and %d more\n", upcoming.size() - 5));
            }
        }

        return summary.toString();
    }

    public String generateBurnoutAssessment() {
        StringBuilder assessment = new StringBuilder();
        String riskLevel = burnoutAnalyzer.analyzeBurnoutRisk();
        int burnoutScore = burnoutAnalyzer.calculateBurnoutScore();

        assessment.append("┌─────────────────────────────────────────────────────────────┐\n");
        assessment.append("│                BURNOUT RISK ASSESSMENT                      │\n");
        assessment.append("└─────────────────────────────────────────────────────────────┘\n");

        String riskEmoji;
        String riskColor;
        switch (riskLevel) {
            case "HIGH":
                riskEmoji = "🔴";
                riskColor = "HIGH";
                break;
            case "MEDIUM":
                riskEmoji = "🟡";
                riskColor = "MEDIUM";
                break;
            default:
                riskEmoji = "🟢";
                riskColor = "LOW";
        }

        assessment.append(String.format("  Overall Risk Level: %s %s\n", riskEmoji, riskColor));
        assessment.append(String.format("  Burnout Score: %d/10\n", burnoutScore));
        assessment.append(String.format("  Risk Chart: %s\n\n", generateRiskChart(burnoutScore)));

        List<String> warnings = burnoutAnalyzer.generateWarnings();
        if (!warnings.isEmpty()) {
            assessment.append("  Warnings & Alerts:\n");
            for (String warning : warnings) {
                if (!warning.isEmpty() && !warning.equals("RECOMMENDATIONS:")) {
                    assessment.append("    ").append(warning).append("\n");
                }
            }
        }

        return assessment.toString();
    }

    private String generateRecommendations() {
        StringBuilder recommendations = new StringBuilder();
        String riskLevel = burnoutAnalyzer.analyzeBurnoutRisk();

        recommendations.append("┌─────────────────────────────────────────────────────────────┐\n");
        recommendations.append("│                    RECOMMENDATIONS                          │\n");
        recommendations.append("└─────────────────────────────────────────────────────────────┘\n");

        if (riskLevel.equals("HIGH")) {
            recommendations.append("   URGENT ACTIONS NEEDED:\n");
            recommendations.append("    • Schedule appointment with counselor/mental health professional\n");
            recommendations.append("    • Consider requesting extensions for assignments\n");
            recommendations.append("    • Discuss workload with your professor\n");
            recommendations.append("    • Practice daily stress-relief activities\n");
        } else if (riskLevel.equals("MEDIUM")) {
            recommendations.append("   PREVENTIVE MEASURES:\n");
            recommendations.append("    • Implement stress management techniques\n");
            recommendations.append("    • Review and optimize your schedule\n");
            recommendations.append("    • Ensure adequate sleep (7-9 hours)\n");
            recommendations.append("    • Connect with support network\n");
        } else {
            recommendations.append("  ✓ MAINTENANCE TIPS:\n");
            recommendations.append("    • Continue current positive habits\n");
            recommendations.append("    • Maintain academic-life balance\n");
            recommendations.append("    • Stay proactive with task management\n");
            recommendations.append("    • Keep monitoring your well-being\n");
        }

        recommendations.append("\n  General Well-being Tips:\n");
        recommendations.append("    • Take regular breaks during study sessions\n");
        recommendations.append("    • Exercise 3-4 times per week\n");
        recommendations.append("    • Practice mindfulness or meditation\n");
        recommendations.append("    • Maintain social connections\n");

        return recommendations.toString();
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
        for (int i = 0; i < 10; i++) {
            chart.append(i < bars ? "█" : "░");
        }
        chart.append("]");
        return chart.toString();
    }

    private String generateStressChart(double avgStress) {
        int bars = (int) Math.round(avgStress);
        StringBuilder chart = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            chart.append(i < bars ? "█" : "░");
        }
        chart.append("]");
        return chart.toString();
    }

    private String generateRiskChart(int score) {
        StringBuilder chart = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            chart.append(i < score ? "█" : "░");
        }
        chart.append("]");
        return chart.toString();
    }
}