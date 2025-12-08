/**
 * User.java
 * Represents a student user in the system
 */
package com.jre.model;

public class User {
    private String studentId;
    private String fullName;
    private String email;
    private int age;
    private String course;

    public User(String studentId, String fullName, String email, int age, String course) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
        this.age = age;
        this.course = course;
    }

    // Getters
    public String getStudentId() {
        return studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public String getCourse() {
        return course;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return String.format("User{ID='%s', Name='%s', Email='%s', Age=%d, Course='%s'}",
                studentId, fullName, email, age, course);
    }

    public String toFileString() {
        return String.format("%s|%s|%s|%d|%s",
                studentId, fullName, email, age, course);
    }

    public static User fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid user data format");
        }
        return new User(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4]);
    }
}