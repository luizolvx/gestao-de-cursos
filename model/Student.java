package model;
import java.util.*;
import service.SubscriptionPlan;

public class Student extends User {
    private SubscriptionPlan plan;
    private List<Enrollment> enrollments = new ArrayList<>();

    public Student(String name, String email, SubscriptionPlan plan) {
        super(name, email);
        this.plan = plan;
    }
    public SubscriptionPlan getPlan() { return plan; }
    public void setPlan(SubscriptionPlan plan) { this.plan = plan; }
    public List<Enrollment> getEnrollments() { return enrollments; }
}