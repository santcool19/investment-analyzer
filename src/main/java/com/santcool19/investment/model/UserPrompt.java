package com.santcool19.investment.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class UserPrompt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    @Lob
    private String prompt;
    private Instant createdAt = Instant.now();

    public UserPrompt() {}

    public UserPrompt(String username, String prompt) {
        this.username = username;
        this.prompt = prompt;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

