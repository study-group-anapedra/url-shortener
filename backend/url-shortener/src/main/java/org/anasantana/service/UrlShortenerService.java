package org.anasantana.service;

import org.anasantana.annotation.utils.validador.RegraNegocioValidator;
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

        validarUrl(originalUrl); // ← regra de negócio clara aqui

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

    private void validarUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isBlank()) {
            throw new BusinessException("A URL original não pode ser vazia(service)");
        }

        //if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
        //    throw new BusinessException("Apenas URLs com http ou https são permitidas");
       // }
    }

    private void processarValidacao(UrlShortener entidade, String identificador) {
        try {
            RegraNegocioValidator.validar(entidade, identificador);
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
