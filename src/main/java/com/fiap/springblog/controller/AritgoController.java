package com.fiap.springblog.controller;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.service.ArtigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/artigos")
public class AritgoController {
    @Autowired
    private ArtigoService artigoService;

    @GetMapping
    public List<Artigo> obterTodos() {
        return artigoService.obterTodos();
    }

    @GetMapping("/{codigo}")
    public Artigo obterPorCodigo(@PathVariable String codigo) {
        return artigoService.obterPorCodigo(codigo);
    }

    @GetMapping("/maiordata")
    public List<Artigo> findByDataGreaterThan(@RequestParam("data") LocalDateTime data) {
        return artigoService.findByDataGreaterThan(data);
    }

    @GetMapping("/data-status")
    public List<Artigo> findByDataAndStatus(
            @RequestParam("data") Instant data,
            @RequestParam("status") Integer status
    ) {
        return artigoService.findByDataAndStatus(data, status);
    }

    @PutMapping
    public void atualizar(@RequestBody Artigo updateArtigo) {
        artigoService.atualizar(updateArtigo);
    }

    @PutMapping(value = "/{id}")
    public void atualizarArtigo(@PathVariable String id, @RequestBody String novaUrl) {
        artigoService.atualizarArtigo(id, novaUrl);
    }

    @PostMapping
    public Artigo criar(@RequestBody Artigo artigo) {
        return artigoService.criar(artigo);
    }

    @DeleteMapping("/{id}")
    public void deleteArtigo(@PathVariable String id) {
        artigoService.deleteById(id);
    }

    @DeleteMapping("/delete")
    public void deleteArtigoById(@RequestParam("Id") String id) {
        artigoService.deleteArtigoById(id);
    }
}
