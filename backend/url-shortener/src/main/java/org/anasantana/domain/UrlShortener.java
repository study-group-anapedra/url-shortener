package org.anasantana.domain;

import org.anasantana.annotation.*;
import java.time.Instant;
import java.util.UUID;

@Document(collection = "urls")
public class UrlShortener {

    @Id
    private String id; // Alterado para String

    @Indexed(unique = true)
    private String shortCode;

    //@ValidarURL
    private String originalUrl;

    private Instant createdAt;

    public UrlShortener() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
    }

    public UrlShortener(String originalUrl, String shortCode) {
        this.id = UUID.randomUUID().toString();
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}