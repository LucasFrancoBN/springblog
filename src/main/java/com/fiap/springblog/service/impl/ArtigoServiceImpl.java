package com.fiap.springblog.service.impl;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.ArtigoStatusCount;
import com.fiap.springblog.model.Autor;
import com.fiap.springblog.model.AutorTotalArtigo;
import com.fiap.springblog.repository.ArtigoRepository;
import com.fiap.springblog.repository.AutorRepository;
import com.fiap.springblog.service.ArtigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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
    private MongoTransactionManager transactionManager;

    @Autowired
    public ArtigoServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artigo> obterTodos() {
        return artigoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Artigo obterPorCodigo(String codigo) {
        return artigoRepository
                .findById(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Artigo não existe"));
    }

    @Override
    public ResponseEntity<?> criar(Artigo artigo) {
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

        try {
            artigoRepository.save(artigo);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Artigo já existe na coleção");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao criar artigo: " + e.getMessage());
        }
    }

    /*
    @Override
    @Transactional
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

        try {
            // Salva o artigo já com o autor cadastrado
            return artigoRepository.save(artigo);

        } catch (OptimisticLockingFailureException exception) {
            // desenvolver estratégia

            // 1. Recuperar o documento mais recente do banco de dados (na coleção Artigo)
            Artigo artigoMaisRecente = artigoRepository.findById(artigo.getCodigo()).orElse(null);

            if(artigoMaisRecente != null) {
                // 2. Atualizar os campos desejados
                artigoMaisRecente.setTitulo(artigo.getTitulo());
                artigoMaisRecente.setTexto(artigo.getTexto());
                artigoMaisRecente.setStatus(artigo.getStatus());

                // 3. Incrementar versão manualmente do documento
                artigoMaisRecente.setVersion(artigoMaisRecente.getVersion() + 1);

                // Tentar salvar novamente
                return artigoRepository.save(artigoMaisRecente);
            } else {
                throw new RuntimeException("Artigo não encontrado: " + artigo.getCodigo());
            }
        }
    }
    */

    @Override
    public ResponseEntity<?> atualizarArtigo(String id, Artigo artigo) {
        try {
            Artigo existenteArtigo = artigoRepository.findById(id).orElse(null);

            if(existenteArtigo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Artigo não encontrado na coleção");
            }

            // Atualizar alguns dados do artigo existente
            existenteArtigo.setTitulo(artigo.getTitulo());
            existenteArtigo.setData(artigo.getData());
            existenteArtigo.setTexto(artigo.getTexto());

            artigoRepository.save(existenteArtigo);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao atualizar  artigo: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artigo> findByDataGreaterThan(LocalDateTime data) {
        Query query = new Query(Criteria.where("data").gt(data));
        return mongoTemplate.find(query, Artigo.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artigo> findByDataAndStatus(Instant data, Integer status) {
        Query query = new Query(Criteria.where("data")
                .is(data).and("status").is(status));
        return mongoTemplate.find(query, Artigo.class);
    }

    @Override
    @Transactional
    public void atualizar(Artigo updateArtigo) {
        artigoRepository.save(updateArtigo);
    }

    @Override
    @Transactional
    public void atualizarArtigo(String id, String novaURL) {
        // Critério de busca pelo "_id"
        Query query = new Query(Criteria.where("_id").is(id));
        // Definindo os campos que serão atualizados
        Update update = new Update().set("url", novaURL);
        // Executo a atualização
        mongoTemplate.updateFirst(query, update, Artigo.class);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        artigoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteArtigoById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, Artigo.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artigo> findArtigoByStatusAndDataGreaterThan(Integer status, Instant data) {
        return artigoRepository.findArtigoByStatusAndDataGreaterThan(status, data);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artigo> obterArtigoPorDataHora(Instant de, Instant ate) {
        return artigoRepository.obterArtigoPorDataHora(de, ate);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Page<Artigo> listaArtigos(Pageable pageable) {
        return artigoRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artigo> findArtigoByStatusOrderByTituloAsc(Integer status) {
        return artigoRepository.findArtigoByStatusOrderByTituloAsc(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artigo> obterArtigoPorStatusOrdernacao(Integer status) {
        return artigoRepository.obterArtigoPorStatusOrdernacao(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Artigo> findByText(String searchTerm) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(searchTerm);
        Query query = TextQuery.queryText(criteria).sortByScore();
        return mongoTemplate.find(query, Artigo.class);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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

    @Override
    public ResponseEntity<?> criarArtigoComAutor(Artigo artigo, Autor autor) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.execute(status -> {
           try {
               // Iniciar a transação
               autorRepository.save(autor);
               artigo.setData(Instant.now());
               artigoRepository.save(artigo);

           } catch (Exception exception) {
                // Tratar o erro e lançar a transação de volta em caso de exceção
               status.setRollbackOnly();
               throw new RuntimeException("Erro ao criar artigo com autor: " + exception.getMessage());
           }
           return null;
        });

        return null;
    }

    @Override
    public void excluirArtigoEAutor(Artigo artigo) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        transactionTemplate.execute(status -> {
            try {
                // Inicia a transação
                artigoRepository.delete(artigo);
                Autor autor = artigo.getAutor();
                autorRepository.delete(autor);
            } catch (Exception ex) {
                // Tratar o erro e lançar a transações de volta em caso de exceção
                status.setRollbackOnly();
                throw new RuntimeException("Erro ao excluir artigo e autor: " + ex.getMessage());
            }
            return null;
        });
    }
}
