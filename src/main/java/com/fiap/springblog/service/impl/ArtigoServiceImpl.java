package com.fiap.springblog.service.impl;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.Autor;
import com.fiap.springblog.repository.ArtigoRepository;
import com.fiap.springblog.repository.AutorRepository;
import com.fiap.springblog.service.ArtigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArtigoServiceImpl implements ArtigoService {
    private final MongoTemplate mongoTemplate;

    @Autowired
    private ArtigoRepository artigoRepository;
    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    public ArtigoServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

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

    @Override
    public List<Artigo> findByDataGreaterThan(LocalDateTime data) {
        Query query = new Query(Criteria.where("data").gt(data));
        return mongoTemplate.find(query, Artigo.class);
    }
}
