package com.fiap.springblog.service;

import com.fiap.springblog.model.Autor;

public interface AutorService {
    Autor criar(Autor autor);

    Autor obterPorCodigo(String codigo);
}
