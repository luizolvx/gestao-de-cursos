package model;

public class Enrollment {
    private Student student;
    private Course course;
    private double progress;

    public Enrollment(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.progress = 0.0;
    }
    public Course getCourse() { return course; }
    public double getProgress() { return progress; }
    public void setProgress(double progress) { this.progress = progress; }
}