package com.fiap.springblog.service;

import com.fiap.springblog.model.Artigo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface ArtigoService {
    List<Artigo> obterTodos();

    Artigo obterPorCodigo(String codigo);

    Artigo criar(Artigo artigo);

    List<Artigo> findByDataGreaterThan(LocalDateTime data);

    List<Artigo> findByDataAndStatus(Instant data, Integer status);

    void atualizar(Artigo updateArtigo);

    void atualizarArtigo(String id, String novaURL);

    void deleteById(String id);

    void deleteArtigoById(String id);

    List<Artigo> findArtigoByStatusAndDataGreaterThan(Integer status, Instant data);

    List<Artigo> obterArtigoPorDataHora(Instant de, Instant ate);

    List<Artigo> encontrarArtigoComplexos(Integer status, Instant data, String titulo);

    Page<Artigo> listaArtigos(Pageable pageable);
}
