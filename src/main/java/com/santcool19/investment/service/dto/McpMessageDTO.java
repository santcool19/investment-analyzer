package com.santcool19.investment.service.dto;

public class McpMessageDTO {
    private Long id;
    private String sender;
    private String recipient;
    private String payload;

    public McpMessageDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}

