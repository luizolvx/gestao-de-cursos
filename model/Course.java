package model;

public class Course {
    private String title;
    private String description;
    private String instructorName;
    private int durationInHours;
    private String difficultyLevel; // BEGINNER, INTERMEDIATE, ADVANCED
    private String status; // ACTIVE, INACTIVE

    public Course(String title, String description, String instructorName, int durationInHours, String difficultyLevel) {
        this.title = title;
        this.description = description;
        this.instructorName = instructorName;
        this.durationInHours = durationInHours;
        this.difficultyLevel = difficultyLevel;
        this.status = "ACTIVE";
    }

    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getInstructorName() { return instructorName; }
    public String getDifficultyLevel() { return difficultyLevel; }
}