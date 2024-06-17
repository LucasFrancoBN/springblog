package com.fiap.springblog.repository;

import com.fiap.springblog.model.Artigo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ArtigoRepository extends MongoRepository<Artigo, String> {
    List<Artigo> findArtigoByDataAndStatus(LocalDateTime data, Integer status);

    void deleteById(String id);
}
