package model;

public class SupportTicket {
    private String title;
    private String message;
    private String senderEmail;

    public SupportTicket(String title, String message, String senderEmail) {
        this.title = title;
        this.message = message;
        this.senderEmail = senderEmail;
    }
    @Override
    public String toString() {
        return "[" + title + "] " + message + " (De: " + senderEmail + ")";
    }
}