/**
 * MainApp.java
 * Main GUI application with Swing interface
 * Complete Student Stress & Mood Monitoring System
 */
package com.jre;

import com.jre.model.*;
import com.jre.service.*;
import com.jre.util.FileHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainApp extends JFrame {
    // Core components
    private User user;
    private MoodTracker moodTracker;
    private WorkloadManager workloadManager;
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
    private static final String TASK_PANEL = "Task";
    private static final String REPORT_PANEL = "Report";

    public MainApp() {
        initializeSystem();
        initializeGUI();
    }

    private void initializeSystem() {
        fileHandler = new FileHandler();
        moodTracker = new MoodTracker();
        workloadManager = new WorkloadManager();
        burnoutAnalyzer = new BurnoutAnalyzer(moodTracker, workloadManager);
        reportGenerator = new ReportGenerator(moodTracker, workloadManager, burnoutAnalyzer);

        loadAllData();
    }

    private void initializeGUI() {
        setTitle("Student Stress & Mood Monitoring System");
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
        mainPanel.add(createTaskPanel(), TASK_PANEL);
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

        // Title
        JLabel titleLabel = new JLabel("JRE: Java Runtime Emotions", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Center content
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel welcomeMsg = new JLabel("<html><center>Welcome Kumsay Students!<br><br>" +
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

        // Title
        JLabel titleLabel = new JLabel("Student Profile Setup", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtStudentId = new JTextField(20);
        JTextField txtFullName = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JTextField txtAge = new JTextField(20);
        JTextField txtCourse = new JTextField(20);

        // Populate if editing
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

        // Buttons
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

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("JRE: Java Runtime Emotions", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel welcomeLabel = new JLabel("", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        if (user != null) {
            welcomeLabel.setText("Welcome, " + user.getFullName() + " (" + user.getStudentId() + ")");
        }
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Menu buttons
        JPanel menuPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton btnMood = createMenuButton("Log Daily Mood & Stress", e -> cardLayout.show(mainPanel, MOOD_PANEL));
        JButton btnTask = createMenuButton("Manage Academic Tasks", e -> {
            refreshTaskPanel();
            cardLayout.show(mainPanel, TASK_PANEL);
        });
        JButton btnReport = createMenuButton("View Weekly Report", e -> {
            refreshReportPanel();
            cardLayout.show(mainPanel, REPORT_PANEL);
        });
        JButton btnBurnout = createMenuButton("Check Burnout Risk", e -> showBurnoutAnalysis());
        JButton btnProfile = createMenuButton("Edit Profile", e -> cardLayout.show(mainPanel, PROFILE_PANEL));
        JButton btnExit = createMenuButton("Exit Application", e -> exitApplication());

        menuPanel.add(btnMood);
        menuPanel.add(btnTask);
        menuPanel.add(btnReport);
        menuPanel.add(btnBurnout);
        menuPanel.add(btnProfile);
        menuPanel.add(btnExit);

        panel.add(menuPanel, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = new JLabel("Last Login: " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")), SwingConstants.CENTER);
        panel.add(footerLabel, BorderLayout.SOUTH);

        return panel;
    }

    // ==================== MOOD LOGGING PANEL ====================
    private JPanel createMoodPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Daily Mood & Stress Entry", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date/Time
        JLabel lblDateTime = new JLabel(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("MMMM dd, yyyy - HH:mm")));
        lblDateTime.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(lblDateTime, gbc);

        // Mood slider
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

        // Stress slider
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

        // Notes
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

        // Buttons
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

    // ==================== TASK PANEL ====================
    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Academic Task Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Split into form and list
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // =================== Task Form ===================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Task Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtTaskName = new JTextField(20);
        JTextArea txtTaskDesc = new JTextArea(2, 20);
        txtTaskDesc.setLineWrap(true);
        txtTaskDesc.setWrapStyleWord(true);
        JTextField txtDueDate = new JTextField(10);
        txtDueDate.setText(LocalDate.now().plusDays(7).toString());

        // Priority - HIGH first
        JRadioButton rbHigh = new JRadioButton("High");
        JRadioButton rbMedium = new JRadioButton("Medium");
        JRadioButton rbLow = new JRadioButton("Low");
        ButtonGroup bgPriority = new ButtonGroup();
        bgPriority.add(rbHigh);
        bgPriority.add(rbMedium);
        bgPriority.add(rbLow);
        rbHigh.setSelected(true); // default = High

        addFormField(formPanel, gbc, 0, "Task Name:", txtTaskName);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(txtTaskDesc), gbc);

        addFormField(formPanel, gbc, 2, "Due Date (YYYY-MM-DD):", txtDueDate);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Priority:"), gbc);
        JPanel priorityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // add in order: High, Medium, Low
        priorityPanel.add(rbHigh);
        priorityPanel.add(rbMedium);
        priorityPanel.add(rbLow);
        gbc.gridx = 1;
        formPanel.add(priorityPanel, gbc);

        // Status combo (includes Checklist)
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Status:"), gbc);
        String[] statuses = {"Pending", "In Progress", "Completed"};
        JComboBox<String> cbStatus = new JComboBox<>(statuses);
        cbStatus.setSelectedItem("Pending");
        gbc.gridx = 1;
        formPanel.add(cbStatus, gbc);

        // =================== Buttons ===================
        JButton btnAddTask = new JButton("Add Task");
        JButton btnUpdateTask = new JButton("Modify Task");
        JButton btnDeleteTask = new JButton("Delete Task");
        JButton btnClearForm = new JButton("Clear Form");

        JPanel formButtonPanel = new JPanel(new FlowLayout());
        formButtonPanel.add(btnAddTask);
        formButtonPanel.add(btnUpdateTask);
        formButtonPanel.add(btnDeleteTask);
        formButtonPanel.add(btnClearForm);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(formButtonPanel, gbc);

        // store editing state id in formPanel client property (null = add mode)
        formPanel.putClientProperty("editingTaskId", null);

        // =================== Task List ===================
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Current Tasks"));

        // 5 columns: last column = hidden entryId
        String[] columnNames = {"Task Name", "Due Date", "Priority", "Status", "entryId"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable taskTable = new JTable(tableModel);
        // hide the entryId column visually
        taskTable.getColumnModel().getColumn(4).setMinWidth(0);
        taskTable.getColumnModel().getColumn(4).setMaxWidth(0);
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(taskTable);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        splitPane.setTopComponent(formPanel);
        splitPane.setBottomComponent(listPanel);
        splitPane.setDividerLocation(300);

        panel.add(splitPane, BorderLayout.CENTER);

        // ---------- helper refresh runnable for this panel ----------
        Runnable refreshLocalTable = () -> {
            DefaultTableModel m = tableModel;
            m.setRowCount(0);
            List<Task> tasks = workloadManager.getTasks();
            for (Task t : tasks) {
                m.addRow(new Object[]{
                        t.getTaskName(),
                        t.getDueDate().toString(),
                        t.getPriority(),
                        t.getStatus(),
                        t.getEntryId()
                });
            }
        };

        // initialize table contents
        refreshLocalTable.run();

        // =================== Button Actions ===================
        btnAddTask.addActionListener(e -> {
            try {
                String taskName = txtTaskName.getText().trim();
                String description = txtTaskDesc.getText().trim();
                LocalDate dueDate = LocalDate.parse(txtDueDate.getText().trim());
                String priority = rbHigh.isSelected() ? "High" : (rbMedium.isSelected() ? "Medium" : "Low");
                String status = (String) cbStatus.getSelectedItem();

                if (taskName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Task name is required!");
                    return;
                }

                // If currently editing, treat Add as new (use Modify button to update)
                Task task = new Task(LocalDateTime.now(), taskName, description, dueDate, priority, status, "");
                workloadManager.addTask(task);
                fileHandler.appendTask(task); // append new entry to file

                JOptionPane.showMessageDialog(this, "Task added successfully!");
                // clear form and refresh
                btnClearForm.doClick();
                refreshLocalTable.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        btnUpdateTask.addActionListener(e -> {
            String editingId = (String) formPanel.getClientProperty("editingTaskId");
            if (editingId == null) {
                JOptionPane.showMessageDialog(this, "Select a task first (from the table) to modify.");
                return;
            }

            try {
                String taskName = txtTaskName.getText().trim();
                String description = txtTaskDesc.getText().trim();
                LocalDate dueDate = LocalDate.parse(txtDueDate.getText().trim());
                String priority = rbHigh.isSelected() ? "High" :rbMedium.isSelected() ? "Medium" : "Low";

                String status = (String) cbStatus.getSelectedItem();

                Task existing = workloadManager.getTaskById(editingId);
                if (existing == null) {
                    JOptionPane.showMessageDialog(this, "The selected task no longer exists.");
                    refreshLocalTable.run();
                    return;
                }

                existing.setTaskName(taskName);
                existing.setDescription(description);
                existing.setDueDate(dueDate);
                existing.setPriority(priority);
                existing.setStatus(status);

                fileHandler.saveTasks(workloadManager.getTasks()); // persist changes
                JOptionPane.showMessageDialog(this, "Task updated successfully!");
                // clear edit state + form + refresh
                btnClearForm.doClick();
                refreshLocalTable.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        btnDeleteTask.addActionListener(e -> {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a task to delete!");
                return;
            }

            String taskId = (String) taskTable.getModel().getValueAt(selectedRow, 4);
            Task task = workloadManager.getTaskById(taskId);
            if (task == null) {
                JOptionPane.showMessageDialog(this, "Selected task not found.");
                refreshLocalTable.run();
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete the task: " + task.getTaskName() + "?",
                    "Delete Task", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean removed = workloadManager.removeTask(taskId);
                if (removed) {
                    fileHandler.saveTasks(workloadManager.getTasks());
                    JOptionPane.showMessageDialog(this, "Task deleted successfully!");
                    // clear form if that task was being edited
                    String editingId = (String) formPanel.getClientProperty("editingTaskId");
                    if (editingId != null && editingId.equals(taskId)) {
                        btnClearForm.doClick();
                    }
                    refreshLocalTable.run();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete task!");
                }
            }
        });

        btnClearForm.addActionListener(e -> {
            txtTaskName.setText("");
            txtTaskDesc.setText("");
            txtDueDate.setText(LocalDate.now().plusDays(7).toString());

            // Reset priority to default (High)
            rbHigh.setSelected(true);
            rbMedium.setSelected(false);
            rbLow.setSelected(false);

            // Reset status to default
            cbStatus.setSelectedItem("Pending");

            // clear editing state
            formPanel.putClientProperty("editingTaskId", null);

            // clear table selection
            taskTable.clearSelection();
        });

        // =================== Table Row Selection ===================
        taskTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow != -1) {
                String taskId = (String) taskTable.getModel().getValueAt(selectedRow, 4);
                Task task = workloadManager.getTaskById(taskId);
                if (task != null) {
                    // populate form
                    txtTaskName.setText(task.getTaskName());
                    txtTaskDesc.setText(task.getDescription());
                    txtDueDate.setText(task.getDueDate().toString());
                    switch (task.getPriority()) {
                        case "High" -> rbHigh.setSelected(true);
                        case "Medium" -> rbMedium.setSelected(true);
                        default -> rbLow.setSelected(true);
                    }
                    cbStatus.setSelectedItem(task.getStatus());
                    // set editing state
                    formPanel.putClientProperty("editingTaskId", task.getEntryId());
                }
            }
        });

        // Bottom panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnRefresh = new JButton("Refresh");
        JButton btnBack = new JButton("Back to Menu");
        btnRefresh.addActionListener(e -> refreshLocalTable.run());
        btnBack.addActionListener(e -> {
            refreshLocalTable.run();
            cardLayout.show(mainPanel, MENU_PANEL);
        });
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBack);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Store table model and table references for global refresh compatibility
        panel.putClientProperty("tableModel", tableModel);
        panel.putClientProperty("taskTable", taskTable);

        return panel;
    }

    // ==================== REPORT PANEL ====================
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Weekly Summary Report", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Report content
        JTextArea txtReport = new JTextArea();
        txtReport.setEditable(false);
        txtReport.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtReport);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnRefresh = new JButton("Refresh Report");
        JButton btnExport = new JButton("Export to File");
        JButton btnBack = new JButton("Back to Menu");

        btnRefresh.addActionListener(e -> {
            String report = reportGenerator.generateWeeklyReport();
            txtReport.setText(report);
        });

        btnExport.addActionListener(e -> exportReport(txtReport.getText()));
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, MENU_PANEL));

        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnExport);
        buttonPanel.add(btnBack);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Store reference
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

    // ==================== GLOBAL refreshTaskPanel (update this one) ====================
    private void refreshTaskPanel() {
        // iterate panels and update any that provided a tableModel / taskTable
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel p = (JPanel) comp;
                DefaultTableModel model = (DefaultTableModel) p.getClientProperty("tableModel");
                JTable table = (JTable) p.getClientProperty("taskTable");
                if (model != null && table != null) {
                    // ensure model has at least 5 columns (TaskName, DueDate, Priority, Status, entryId)
                    model.setRowCount(0);
                    List<Task> tasks = workloadManager.getTasks();
                    for (Task task : tasks) {
                        model.addRow(new Object[]{
                                task.getTaskName(),
                                task.getDueDate().toString(),
                                task.getPriority(),
                                task.getStatus(),
                                task.getEntryId()
                        });
                    }
                    // hide entryId column if not already hidden
                    try {
                        if (table.getColumnModel().getColumnCount() >= 5) {
                            table.getColumnModel().getColumn(4).setMinWidth(0);
                            table.getColumnModel().getColumn(4).setMaxWidth(0);
                            table.getColumnModel().getColumn(4).setPreferredWidth(0);
                        }
                    } catch (Exception ignored) {}
                }
            }
        }
    }


    private void refreshReportPanel() {
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel p = (JPanel) comp;
                JTextArea txtReport = (JTextArea) p.getClientProperty("txtReport");
                if (txtReport != null) {
                    String report = reportGenerator.generateWeeklyReport();
                    txtReport.setText(report);
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

    private void showBurnoutAnalysis() {
        String analysis = burnoutAnalyzer.getDetailedAnalysis();
        JTextArea textArea = new JTextArea(analysis);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        JOptionPane.showMessageDialog(this, scrollPane, "Burnout Risk Assessment", 
        JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportReport(String reportContent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        fileChooser.setSelectedFile(new java.io.File("weekly_report_" + 
            LocalDate.now().toString() + ".txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile());
                writer.write(reportContent);
                writer.close();
                JOptionPane.showMessageDialog(this, "Report exported successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + ex.getMessage());
            }
        }
    }

    private void loadAllData() {
        user = fileHandler.loadUser();
        List<MoodLog> loadedMoodLogs = fileHandler.loadMoodLogs();
        List<Task> loadedTasks = fileHandler.loadTasks();
        
        moodTracker.setMoodLogs(loadedMoodLogs);
        workloadManager.setTasks(loadedTasks);
        
        System.out.println("All data loaded successfully.");
    }

    private void saveAllData() {
        if (user != null) {
            fileHandler.saveUser(user);
        }
        fileHandler.saveMoodLogs(moodTracker.getMoodLogs());
        fileHandler.saveTasks(workloadManager.getTasks());
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
        SwingUtilities.invokeLater(() -> new MainApp());
    }
}