package com.fiap.springblog.service.impl;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.ArtigoStatusCount;
import com.fiap.springblog.model.Autor;
import com.fiap.springblog.model.AutorTotalArtigo;
import com.fiap.springblog.repository.ArtigoRepository;
import com.fiap.springblog.repository.AutorRepository;
import com.fiap.springblog.service.ArtigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    @Override
    public List<Artigo> findByDataAndStatus(Instant data, Integer status) {
        Query query = new Query(Criteria.where("data")
                .is(data).and("status").is(status));
        return mongoTemplate.find(query, Artigo.class);
    }

    @Override
    public void atualizar(Artigo updateArtigo) {
        artigoRepository.save(updateArtigo);
    }

    @Override
    public void atualizarArtigo(String id, String novaURL) {
        // Critério de busca pelo "_id"
        Query query = new Query(Criteria.where("_id").is(id));
        // Definindo os campos que serão atualizados
        Update update = new Update().set("url", novaURL);
        // Executo a atualização
        mongoTemplate.updateFirst(query, update, Artigo.class);
    }

    @Override
    public void deleteById(String id) {
        artigoRepository.deleteById(id);
    }

    @Override
    public void deleteArtigoById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, Artigo.class);
    }

    @Override
    public List<Artigo> findArtigoByStatusAndDataGreaterThan(Integer status, Instant data) {
        return artigoRepository.findArtigoByStatusAndDataGreaterThan(status, data);
    }

    @Override
    public List<Artigo> obterArtigoPorDataHora(Instant de, Instant ate) {
        return artigoRepository.obterArtigoPorDataHora(de, ate);
    }

    @Override
    public List<Artigo> encontrarArtigoComplexos(Integer status, Instant data, String titulo) {
        Criteria criteria = new Criteria();

        // Filtrar artigos com data menor ou igual ao valor fornecido
        criteria.and("data").lte(data);

        // Filtrar artigos com o status especificado
        if(status != null) {
            criteria.and("status").is(status);
        }

        // Filtrar artigos cujo titulo exista
        if(titulo != null && !titulo.isEmpty()) {
            criteria.and("titulo").regex(titulo, "i");
        }

        Query query = new Query(criteria);
        return mongoTemplate.find(query, Artigo.class);
    }

    @Override
    public Page<Artigo> listaArtigos(Pageable pageable) {
        return artigoRepository.findAll(pageable);
    }

    @Override
    public List<Artigo> findArtigoByStatusOrderByTituloAsc(Integer status) {
        return artigoRepository.findArtigoByStatusOrderByTituloAsc(status);
    }

    @Override
    public List<Artigo> obterArtigoPorStatusOrdernacao(Integer status) {
        return artigoRepository.obterArtigoPorStatusOrdernacao(status);
    }

    @Override
    public List<Artigo> findByText(String searchTerm) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(searchTerm);
        Query query = TextQuery.queryText(criteria).sortByScore();
        return mongoTemplate.find(query, Artigo.class);
    }

    @Override
    public List<ArtigoStatusCount> contarArtigosPorStatus() {
        TypedAggregation<Artigo> aggregation = Aggregation.newAggregation(
          Artigo.class,
          Aggregation.group("status").count().as("count"),
          Aggregation.project("count").and("status").previousOperation()
        );

        AggregationResults<ArtigoStatusCount> results = mongoTemplate.aggregate(aggregation, ArtigoStatusCount.class);
        return results.getMappedResults();
    }

    @Override
    public List<AutorTotalArtigo> calcularTotalArtigosPorAutorPeriodo(Instant dataInicio, Instant dataFim) {
        TypedAggregation<Artigo> aggregation = Aggregation.newAggregation(
                Artigo.class,
                Aggregation.match(
                        Criteria.where("data").gte(dataInicio).lte(dataFim)
                ),
                Aggregation.group("autor").count().as("totalArtigos"),
                Aggregation.project("totalArtigos").and("autor").previousOperation()
        );

        AggregationResults<AutorTotalArtigo> result = mongoTemplate.aggregate(aggregation, AutorTotalArtigo.class);
        return result.getMappedResults();
    }


}
