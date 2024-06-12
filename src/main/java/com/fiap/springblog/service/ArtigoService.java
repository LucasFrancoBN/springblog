package com.fiap.springblog.service;

import com.fiap.springblog.model.Artigo;

import java.util.List;

public interface ArtigoService {
    List<Artigo> obterTodos();

    Artigo obterPorCodigo(String codigo);

    Artigo criar(Artigo artigo);
}
