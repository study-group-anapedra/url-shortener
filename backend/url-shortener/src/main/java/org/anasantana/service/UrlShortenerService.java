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

    public UrlShortenerDTO encurtar(UrlShortenerDTO dto, String identificador) {
        
        // 1. Validação de segurança (evita NullPointer antes de chegar no banco)
        if (dto.getOriginalUrl() == null || dto.getOriginalUrl().isBlank()) {
            throw new UrlInvalidaException("A URL original não pode estar vazia.");
        }

        // 2. Validação de Regra de Negócio (Rate Limit e Formato da URL)
        processarValidacao(dto, identificador);

        // 3. Verificação de Duplicidade
        Optional<UrlShortener> existente = repository.findByOriginalUrl(dto.getOriginalUrl());
        if (existente.isPresent()) {
            return new UrlShortenerDTO(existente.get());
        }

        // 4. Criação da Entidade
        UrlShortener entidade = new UrlShortener();
        entidade.setOriginalUrl(dto.getOriginalUrl());
        entidade.setShortCode(gerarShortCode());

        // 5. Ciclo de Persistência com proteção contra colisão
        for (int i = 0; i < MAX_TENTATIVAS; i++) {
            try {
                repository.save(entidade);
                return new UrlShortenerDTO(entidade);
            } catch (CodigoCurtoNaoDisponivelException e) {
                entidade.setShortCode(gerarShortCode());
            } catch (Exception e) {
                throw new PersistenciaException("Erro de persistência no banco de dados", e);
            }
        }

        throw new CodigoCurtoNaoDisponivelException();
    }

    private void processarValidacao(Object objeto, String identificador) {
        try {
            RegraNegocioValidator.validar(objeto, identificador);
        } catch (UrlInvalidaException | AbusoDeRequisicaoException e) {
            // Relança as exceções de negócio para o Handler capturar (400 e 429)
            throw e;
        } catch (IllegalAccessException e) {
            throw new PersistenciaException("Erro técnico ao processar validações", e);
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