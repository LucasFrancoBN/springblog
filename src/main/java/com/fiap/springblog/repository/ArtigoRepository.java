package com.fiap.springblog.repository;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.ArtigoStatusCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface ArtigoRepository extends MongoRepository<Artigo, String> {
    List<Artigo> findArtigoByDataAndStatus(LocalDateTime data, Integer status);

    void deleteById(String id);

    List<Artigo> findArtigoByStatusAndDataGreaterThan(Integer status, Instant data);

    @Query("{$and:  [ {'data': { $gte: ?0 }}, {'data':  { $lte:  ?1}}  ]}")
    List<Artigo> obterArtigoPorDataHora(Instant de, Instant ate);

    Page<Artigo> findAll(Pageable pageable);

    List<Artigo> findArtigoByStatusOrderByTituloAsc(Integer status);

    @Query(value = "{ status:  {$eq:  ?0}}", sort = "{titulo:  1}")
    List<Artigo> obterArtigoPorStatusOrdernacao(Integer status);
}
