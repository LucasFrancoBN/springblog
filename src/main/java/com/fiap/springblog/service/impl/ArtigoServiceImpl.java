package com.fiap.springblog.service.impl;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.Autor;
import com.fiap.springblog.repository.ArtigoRepository;
import com.fiap.springblog.repository.AutorRepository;
import com.fiap.springblog.service.ArtigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtigoServiceImpl implements ArtigoService {
    @Autowired
    private ArtigoRepository artigoRepository;
    @Autowired
    private AutorRepository autorRepository;

    @Override
    public List<Artigo> obterTodos() {
        return artigoRepository.findAll();
    }

    @Override
    public Artigo obterPorCodigo(String codigo) {
        return artigoRepository
                .findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Artigo não existe"));
    }

    @Override
    public Artigo criar(Artigo artigo) {
        // Se o autor existe
        if(artigo.getAutor().getCodigo() != null) {
            // recuperar autor
            Autor autor = autorRepository
                    .findById(artigo.getAutor().getCodigo())
                    .orElseThrow(() -> new IllegalArgumentException(("Autor inexistente")));

            // Define o autor no artigo
            artigo.setAutor(autor);
        } else {
            // Caso contrário, não atribuir um autor ao artigo
            artigo.setAutor(null);
        }

        // Salva o artigo já com o autor cadastrado
        return artigoRepository.save(artigo);
    }
}
