package com.jre;

import com.jre.model.*;
import com.jre.service.*;
import com.jre.util.FileHandler;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainApp extends JFrame {
    // Core components
    private User user;
    private MoodTracker moodTracker;
    private BurnoutAnalyzer burnoutAnalyzer;
    private ReportGenerator reportGenerator;
    private FileHandler fileHandler;

    // GUI Components
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Panel names
    private static final String WELCOME_PANEL = "Welcome";
    private static final String PROFILE_PANEL = "Profile";
    private static final String MENU_PANEL = "Menu";
    private static final String MOOD_PANEL = "Mood";
    private static final String REPORT_PANEL = "Report";

    public MainApp() {
        initializeSystem();
        initializeGUI();
    }

    private void initializeSystem() {
        fileHandler = new FileHandler();
        moodTracker = new MoodTracker();

        // Load data
        loadAllData();

        // Initialize BurnoutAnalyzer and ReportGenerator after loading logs
        burnoutAnalyzer = new BurnoutAnalyzer(moodTracker);
        reportGenerator = new ReportGenerator(moodTracker, burnoutAnalyzer);
    }

    private void initializeGUI() {
        setTitle("Student Mood & Stress Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create all panels
        mainPanel.add(createWelcomePanel(), WELCOME_PANEL);
        mainPanel.add(createProfilePanel(), PROFILE_PANEL);
        mainPanel.add(createMenuPanel(), MENU_PANEL);
        mainPanel.add(createMoodPanel(), MOOD_PANEL);
        mainPanel.add(createReportPanel(), REPORT_PANEL);

        add(mainPanel);

        // Show appropriate panel
        if (user == null) {
            cardLayout.show(mainPanel, WELCOME_PANEL);
        } else {
            cardLayout.show(mainPanel, MENU_PANEL);
        }

        setVisible(true);
    }

    // ==================== WELCOME PANEL ====================
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Student Mood & Stress Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel welcomeMsg = new JLabel("<html><center>Welcome Students!<br><br>" +
                "Track your mood, manage stress, and monitor your well-being.<br><br>" +
                "Set up a profile first.</center></html>");
        welcomeMsg.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(welcomeMsg, gbc);

        JButton startButton = new JButton("Create Profile");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.addActionListener(e -> cardLayout.show(mainPanel, PROFILE_PANEL));
        gbc.gridy = 1;
        centerPanel.add(startButton, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    // ==================== PROFILE PANEL ====================
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Student Profile Setup", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtStudentId = new JTextField(20);
        JTextField txtFullName = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JTextField txtAge = new JTextField(20);
        JTextField txtCourse = new JTextField(20);

        if (user != null) {
            txtStudentId.setText(user.getStudentId());
            txtStudentId.setEditable(false);
            txtFullName.setText(user.getFullName());
            txtEmail.setText(user.getEmail());
            txtAge.setText(String.valueOf(user.getAge()));
            txtCourse.setText(user.getCourse());
        }

        addFormField(formPanel, gbc, 0, "Student ID:", txtStudentId);
        addFormField(formPanel, gbc, 1, "Full Name:", txtFullName);
        addFormField(formPanel, gbc, 2, "Email:", txtEmail);
        addFormField(formPanel, gbc, 3, "Age:", txtAge);
        addFormField(formPanel, gbc, 4, "Course/Major:", txtCourse);

        panel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save Profile");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String studentId = txtStudentId.getText().trim();
                String fullName = txtFullName.getText().trim();
                String email = txtEmail.getText().trim();
                int age = Integer.parseInt(txtAge.getText().trim());
                String course = txtCourse.getText().trim();

                if (studentId.isEmpty() || fullName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Student ID and Name are required!");
                    return;
                }

                user = new User(studentId, fullName, email, age, course);
                fileHandler.saveUser(user);
                JOptionPane.showMessageDialog(this, "Profile saved successfully!");
                cardLayout.show(mainPanel, MENU_PANEL);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid age!");
            }
        });

        cancelButton.addActionListener(e -> {
            if (user == null) {
                cardLayout.show(mainPanel, WELCOME_PANEL);
            } else {
                cardLayout.show(mainPanel, MENU_PANEL);
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ==================== MAIN MENU PANEL ====================
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Student Mood & Stress Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel welcomeLabel = new JLabel("", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getFullName() + " (" + user.getStudentId() + ")");
        }
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton btnMood = createMenuButton("Log Daily Mood & Stress", e -> cardLayout.show(mainPanel, MOOD_PANEL));
        JButton btnReport = createMenuButton("View Weekly Report", e -> {
            refreshReportPanel();
            cardLayout.show(mainPanel, REPORT_PANEL);
        });
        JButton btnProfile = createMenuButton("Edit Profile", e -> cardLayout.show(mainPanel, PROFILE_PANEL));
        JButton btnExit = createMenuButton("Exit Application", e -> exitApplication());

        menuPanel.add(btnMood);
        menuPanel.add(btnReport);
        menuPanel.add(btnProfile);
        menuPanel.add(btnExit);

        panel.add(menuPanel, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel("Last Login: " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")), SwingConstants.CENTER);
        panel.add(footerLabel, BorderLayout.SOUTH);

        return panel;
    }

    // ==================== MOOD PANEL ====================
    private JPanel createMoodPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Daily Mood & Stress Entry", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblDateTime = new JLabel(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("MMMM dd, yyyy - HH:mm")));
        lblDateTime.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        formPanel.add(lblDateTime, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Mood Level (1-10):"), gbc);
        JSlider moodSlider = new JSlider(1, 10, 5);
        moodSlider.setMajorTickSpacing(1);
        moodSlider.setPaintTicks(true);
        moodSlider.setPaintLabels(true);
        JLabel moodValue = new JLabel("5");
        moodValue.setFont(new Font("Arial", Font.BOLD, 18));
        moodSlider.addChangeListener(e -> moodValue.setText(String.valueOf(moodSlider.getValue())));
        gbc.gridx = 1;
        formPanel.add(moodSlider, gbc);
        gbc.gridx = 2;
        formPanel.add(moodValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Stress Level (1-10):"), gbc);
        JSlider stressSlider = new JSlider(1, 10, 5);
        stressSlider.setMajorTickSpacing(1);
        stressSlider.setPaintTicks(true);
        stressSlider.setPaintLabels(true);
        JLabel stressValue = new JLabel("5");
        stressValue.setFont(new Font("Arial", Font.BOLD, 18));
        stressSlider.addChangeListener(e -> stressValue.setText(String.valueOf(stressSlider.getValue())));
        gbc.gridx = 1;
        formPanel.add(stressSlider, gbc);
        gbc.gridx = 2;
        formPanel.add(stressValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        formPanel.add(new JLabel("Notes/Observations:"), gbc);
        JTextArea txtNotes = new JTextArea(5, 30);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        JScrollPane scrollNotes = new JScrollPane(txtNotes);
        gbc.gridy = 4;
        formPanel.add(scrollNotes, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save Entry");
        JButton viewHistoryButton = new JButton("View History");
        JButton backButton = new JButton("Back to Menu");

        saveButton.addActionListener(e -> {
            int mood = moodSlider.getValue();
            int stress = stressSlider.getValue();
            String notes = txtNotes.getText();

            MoodLog log = new MoodLog(LocalDateTime.now(), mood, stress, notes);
            moodTracker.addMoodLog(log);
            fileHandler.appendMoodLog(log);

            JOptionPane.showMessageDialog(this, "Mood entry saved successfully!");
            txtNotes.setText("");
            moodSlider.setValue(5);
            stressSlider.setValue(5);
        });

        viewHistoryButton.addActionListener(e -> showMoodHistory());
        backButton.addActionListener(e -> cardLayout.show(mainPanel, MENU_PANEL));

        buttonPanel.add(saveButton);
        buttonPanel.add(viewHistoryButton);
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ==================== REPORT PANEL ====================
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Weekly Summary Report", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        JTextArea txtReport = new JTextArea();
        txtReport.setEditable(false);
        txtReport.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtReport);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnRefresh = new JButton("Refresh Report");
        JButton btnExport = new JButton("Export to File");
        JButton btnBack = new JButton("Back to Menu");

        btnRefresh.addActionListener(e -> txtReport.setText(reportGenerator.generateWeeklyReport()));
        btnExport.addActionListener(e -> exportReport(txtReport.getText()));
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, MENU_PANEL));

        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnExport);
        buttonPanel.add(btnBack);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        panel.putClientProperty("txtReport", txtReport);
        return panel;
    }

    // ==================== HELPER METHODS ====================
    private JButton createMenuButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setPreferredSize(new Dimension(300, 50));
        button.addActionListener(listener);
        return button;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void refreshReportPanel() {
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel p = (JPanel) comp;
                JTextArea txtReport = (JTextArea) p.getClientProperty("txtReport");
                if (txtReport != null) {
                    txtReport.setText(reportGenerator.generateWeeklyReport());
                }
            }
        }
    }

    private void showMoodHistory() {
        List<MoodLog> logs = moodTracker.getRecentLogs(10);
        StringBuilder history = new StringBuilder("Recent Mood Entries:\n\n");

        if (logs.isEmpty()) {
            history.append("No entries found.");
        } else {
            for (MoodLog log : logs) {
                history.append(String.format("%s - Mood: %d, Stress: %d\n%s\n\n",
                        log.getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")),
                        log.getMoodLevel(),
                        log.getStressLevel(),
                        log.getNotes().isEmpty() ? "(No notes)" : log.getNotes()));
            }
        }

        JTextArea textArea = new JTextArea(history.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "Mood History", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportReport(String reportContent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        fileChooser.setSelectedFile(new java.io.File("weekly_report_" +
                LocalDate.now().toString() + ".txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile())) {
                writer.write(reportContent);
                JOptionPane.showMessageDialog(this, "Report exported successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + ex.getMessage());
            }
        }
    }

    private void loadAllData() {
        user = fileHandler.loadUser();
        List<MoodLog> loadedMoodLogs = fileHandler.loadMoodLogs();
        moodTracker.setMoodLogs(loadedMoodLogs);
        System.out.println("All data loaded successfully.");
    }

    private void saveAllData() {
        if (user != null) {
            fileHandler.saveUser(user);
        }
        fileHandler.saveMoodLogs(moodTracker.getMoodLogs());
        System.out.println("All data saved successfully.");
    }

    private void exitApplication() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Save data before exiting?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            saveAllData();
            System.exit(0);
        } else if (choice == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}
