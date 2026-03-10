package service;

public class PremiumPlan implements SubscriptionPlan {
    @Override
    public boolean canEnroll(int currentEnrollments) {
        return true; // Sem limites
    }
    @Override
    public String getPlanName() { return "PREMIUM"; }
}