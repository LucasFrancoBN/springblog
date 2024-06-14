package com.fiap.springblog.service;

import com.fiap.springblog.model.Artigo;

import java.time.LocalDateTime;
import java.util.List;

public interface ArtigoService {
    List<Artigo> obterTodos();

    Artigo obterPorCodigo(String codigo);

    Artigo criar(Artigo artigo);

    List<Artigo> findByDataGreaterThan(LocalDateTime data);

    List<Artigo> findByDataAndStatus(LocalDateTime data, Integer status);

    void atualizar(Artigo updateArtigo);
}
