package org.anasantana.service;

import org.anasantana.annotation.utils.validador.Validador;
import org.anasantana.domain.UrlShortener;
import org.anasantana.dto.UrlShortenerDTO;
import org.anasantana.repository.UrlShortenerRepository;
import org.anasantana.service.exception.*;

import java.util.Optional;
import java.util.UUID;

public class UrlShortenerService {

    private static final int MAX_TENTATIVAS = 10;
    private final UrlShortenerRepository repository;

    public UrlShortenerService(UrlShortenerRepository repository) {
        this.repository = repository;
    }

    public UrlShortenerDTO encurtar(String originalUrl, String identificador) {
        Optional<UrlShortener> existente = repository.findByOriginalUrl(originalUrl);
        if (existente.isPresent()) {
            return new UrlShortenerDTO(existente.get());
        }

        UrlShortener entidade = new UrlShortener();
        entidade.setOriginalUrl(originalUrl);
        entidade.setShortCode(gerarShortCode());

        processarValidacao(entidade, identificador);

        for (int i = 0; i < MAX_TENTATIVAS; i++) {
            try {
                repository.save(entidade);
                return new UrlShortenerDTO(entidade);
            } catch (CodigoCurtoNaoDisponivelException e) {
                entidade.setShortCode(gerarShortCode());
            } catch (Exception e) {
                throw new PersistenciaException("Erro de persistência", e);
            }
        }

        throw new CodigoCurtoNaoDisponivelException();
    }

    private void processarValidacao(UrlShortener entidade, String identificador) {
        try {
            Validador.validar(entidade, identificador);
        } catch (IllegalAccessException e) {
            throw new PersistenciaException("Erro de reflexão na validação", e);
        }
    }

    public UrlShortenerDTO buscarPorShortCode(String shortCode) {
        return repository.findByShortCode(shortCode)
                .map(UrlShortenerDTO::new)
                .orElseThrow(UrlNaoEncontradaException::new);
    }

    private String gerarShortCode() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8);
    }
}