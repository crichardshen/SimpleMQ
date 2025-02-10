package model;

public class SubscriptionResponse {
    private String websocketUrl;
    private String token;
    private String topic;
    
    public SubscriptionResponse(String websocketUrl, String token, String topic) {
        this.websocketUrl = websocketUrl;
        this.token = token;
        this.topic = topic;
    }
    
    // getters and setters
} 