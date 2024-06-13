package com.fiap.springblog.service.impl;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.repository.ArtigoRepository;
import com.fiap.springblog.service.ArtigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtigoServiceImpl implements ArtigoService {
    @Autowired
    private ArtigoRepository artigoRepository;

    @Override
    public List<Artigo> obterTodos() {
        return List.of();
    }

    @Override
    public Artigo obterPorCodigo(String codigo) {
        return null;
    }

    @Override
    public Artigo criar(Artigo artigo) {
        return null;
    }
}
