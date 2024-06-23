package com.fiap.springblog.controller;

import com.fiap.springblog.model.*;
import com.fiap.springblog.service.ArtigoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimistcLockingFailureException(
            OptimisticLockingFailureException exception
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro de concorrência: O artigo foi atualizado por outro usuário. Por Favor, tente novamente");
    }

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

//    @PostMapping
//    public Artigo criar(@RequestBody Artigo artigo) {
//        return artigoService.criar(artigo);
//    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Artigo artigo) {
        return artigoService.criar(artigo);
    }

    @PutMapping("/atualizar-artigo-corpo/{id}")
    public ResponseEntity<?> atualizarArtigo(@PathVariable String id, @Valid @RequestBody Artigo artigo) {
        return artigoService.atualizarArtigo(id, artigo);
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

    @GetMapping("/total-artigo-autor-periodo")
    public List<AutorTotalArtigo> calcularTotalArtigosPorAutorPeriodo(
            @RequestParam("dataInicio") Instant dataInicio,
            @RequestParam("dataFim") Instant dataFim) {
        return artigoService.calcularTotalArtigosPorAutorPeriodo(dataInicio, dataFim);
    }


    @PostMapping("/artigo-com-autor")
    public ResponseEntity<?> criarArtigoComAutor(@RequestBody ArtigoComAutorRequest request) {
        Artigo artigo = request.getArtigo();
        Autor autor = request.getAutor();
        return artigoService.criarArtigoComAutor(artigo, autor);
    }

    @DeleteMapping("/delete-artigo-autor")
    public void excluirArtigoEAutor(@RequestBody Artigo artigo) {
        artigoService.excluirArtigoEAutor(artigo);
    }
}
