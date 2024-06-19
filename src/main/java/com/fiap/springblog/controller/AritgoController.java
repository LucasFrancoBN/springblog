package com.fiap.springblog.controller;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.ArtigoStatusCount;
import com.fiap.springblog.service.ArtigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/status-maiordata")
    public List<Artigo> findByStatusAndDataGreaterThan(
            @RequestParam("status") Integer status,
            @RequestParam("data") Instant data) {
        return artigoService.findArtigoByStatusAndDataGreaterThan(status, data);
    }

    @GetMapping("/periodo")
    public List<Artigo> obterArtigoPorDataHora(
            @RequestParam("de") Instant de,
            @RequestParam("ate") Instant ate) {
        return artigoService.obterArtigoPorDataHora(de, ate);
    }

    @GetMapping("/artigo-complexo")
    public List<Artigo> encontrarArtigosComplexos(
            @RequestParam("status") Integer status,
            @RequestParam("data") Instant date,
            @RequestParam("titulo") String titulo
    ) {
        return artigoService.encontrarArtigoComplexos(status, date, titulo);
    }

    @GetMapping("/pagina-artigo")
    public ResponseEntity<Page<Artigo>> obterArtigosPaginados(Pageable pageable) {
        return ResponseEntity.ok(artigoService.listaArtigos(pageable));
    }

    @GetMapping("/status-ordenado")
    public List<Artigo> findArtigoByStatusOrderByTituloAsc(@RequestParam("status") Integer status) {
        return artigoService.findArtigoByStatusOrderByTituloAsc(status);
    }

    @GetMapping("/status-query-ordenacao")
    public List<Artigo> obterArtigoPorStatusComOrdernacao(@RequestParam("status") Integer status) {
        return artigoService.obterArtigoPorStatusOrdernacao(status);
    }

    @GetMapping("/status-contagem")
    public List<ArtigoStatusCount> contarArtigosPorStatus() {
        return artigoService.contarArtigosPorStatus();
    }

    @GetMapping("/buscatexto")
    public List<Artigo> findByText(@RequestParam("searchTerm")  String searchTerm) {
        return artigoService.findByText(searchTerm);
    }
}
