package service;

public interface SubscriptionPlan {
    boolean canEnroll(int currentEnrollments);
    String getPlanName();
}