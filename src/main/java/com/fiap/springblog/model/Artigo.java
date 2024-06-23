package com.fiap.springblog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Document
public class Artigo {
    @Id
    private String codigo;

    @NotBlank(message = "O título do artigo não pode estar em branco")
    private String titulo;

    @NotNull(message = "A data do artigo não pode ser nula")
    private Instant data;

    @NotBlank(message = "O título do texto não pode estar em branco")
    @TextIndexed
    private String texto;

    private String url;

    @NotNull(message = "A status do artigo não pode ser nula")
    private Integer status;

    @DBRef
    private Autor autor;

    @Version
    private Long version;
}