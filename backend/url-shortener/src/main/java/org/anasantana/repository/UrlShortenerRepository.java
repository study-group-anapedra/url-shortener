package org.anasantana.repository;

import org.anasantana.annotation.utils.orm.EntityManagerSimples;
import org.anasantana.domain.UrlShortener;

import java.util.Optional;

public class UrlShortenerRepository {

    private final EntityManagerSimples entityManager;

    public UrlShortenerRepository(EntityManagerSimples entityManager) {
        this.entityManager = entityManager;
    }

    public void save(UrlShortener url) {
        // Verificação extra para garantir que shortCode não seja nulo
        if (url.getShortCode() == null || url.getShortCode().isEmpty()) {
            throw new IllegalArgumentException("O shortCode não pode ser nulo ou vazio ao salvar.");
        }
        entityManager.salvar(url);
    }

    public Optional<UrlShortener> findByShortCode(String shortCode) {
        return entityManager
                .buscarPorCampo(UrlShortener.class, "shortCode", shortCode)
                .stream()
                .findFirst();
    }

    public Optional<UrlShortener> findByOriginalUrl(String originalUrl) {
        return entityManager
                .buscarPorCampo(UrlShortener.class, "originalUrl", originalUrl, "originalUrl-index")
                .stream()
                .findFirst();
    }
}
