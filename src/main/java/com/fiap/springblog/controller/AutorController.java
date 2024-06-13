package com.fiap.springblog.controller;

import com.fiap.springblog.model.Autor;
import com.fiap.springblog.service.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/autores")
public class AutorController {
    @Autowired
    private AutorService autorService;

    @GetMapping("/{codigo}")
    public Autor obterPorCodigo(String codigo){
        return autorService.obterPorCodigo(codigo);
    }

    @PostMapping
    public Autor criarAutor(@RequestBody Autor autor) {
        return autorService.criar(autor);
    }
}
