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
}
