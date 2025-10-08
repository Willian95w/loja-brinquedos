package com.example.loja_brinquedos.controller;

import com.example.loja_brinquedos.model.Categoria;
import com.example.loja_brinquedos.service.CategoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<Categoria> getAllCategorias() {
        return categoriaService.findAll();
    }

    @GetMapping("/search")
    public List<Categoria> searchCategorias(@RequestParam String nome) {
        return categoriaService.findByNome(nome);
    }

    @GetMapping("/{id}/quantidade-produtos")
    public int getQuantidadeProdutos(@PathVariable Long id) {
        return categoriaService.findAll().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .map(categoriaService::getQuantidadeProdutos)
                .orElse(0);
    }

    @PostMapping
    public ResponseEntity<Categoria> createCategoria(@RequestBody Categoria categoria) {
        Categoria salva = categoriaService.createCategoria(
                categoria.getNome(),
                categoria.getDescricao(),
                categoria.getImagem()
        );
        return ResponseEntity.ok(salva);
    }

}
