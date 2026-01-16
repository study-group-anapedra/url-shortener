package org.anasantana.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.anasantana.annotation.ValidarURL;
import org.anasantana.domain.UrlShortener;

import java.io.Serializable;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlShortenerDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String shortCode;
    private String shortUrl;
    //@ValidarURL(mensagem = "Apenas URLs com http ou https são permitidas")
    private String originalUrl;

    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
        timezone = "UTC"
    )
    private Instant createdAt;

    public UrlShortenerDTO() {
    }

    public UrlShortenerDTO(String originalUrl, String shortCode) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
    }

    public UrlShortenerDTO(UrlShortener entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.shortCode = entity.getShortCode();
            this.originalUrl = entity.getOriginalUrl();
            this.createdAt = entity.getCreatedAt();
            this.shortUrl = "https://api.asantanadev.com/" + entity.getShortCode();
        }
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

    
    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
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
