package com.example.loja_brinquedos.controller;

import com.example.loja_brinquedos.model.Brinquedo;
import com.example.loja_brinquedos.model.Imagem;
import com.example.loja_brinquedos.repository.CategoriaRepository;
import com.example.loja_brinquedos.repository.ImagemRepository;
import com.example.loja_brinquedos.service.BrinquedoService;
import com.example.loja_brinquedos.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/brinquedos")
public class BrinquedoController {

    private final BrinquedoService brinquedoService;
    private final CategoriaRepository categoriaRepository;
    private final ImagemRepository imagemRepository;
    private final CloudinaryService cloudinaryService;

    public BrinquedoController(BrinquedoService brinquedoService,
                             CategoriaRepository categoriaRepository,
                             ImagemRepository imagemRepository,
                             CloudinaryService cloudinaryService) {
        this.brinquedoService = brinquedoService;
        this.categoriaRepository = categoriaRepository;
        this.imagemRepository = imagemRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping
    public List<Brinquedo> getAllBrinquedos() {
        return brinquedoService.findAll();
    }

    @GetMapping("/mais-acessados")
    public List<Brinquedo> getMaisAcessados() {
        return brinquedoService.findTop8MaisAcessados();
    }

    @GetMapping("/categoria/{id}")
    public List<Brinquedo> getByCategoriaIdAndFiltros(
            @PathVariable Long id,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) List<String> marcas,
            @RequestParam(required = false) BigDecimal minValor,
            @RequestParam(required = false) BigDecimal maxValor) {

        return brinquedoService.filtrarPorCategoria(id, nome, marcas, minValor, maxValor);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBrinquedoById(@PathVariable Long id) {
        Map<String, Object> resultado = brinquedoService.getBrinquedoComRelacionados(id);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/marcas")
    public List<String> getMarcas() {
        return brinquedoService.findAllMarcas();
    }

    @PostMapping
    public ResponseEntity<Brinquedo> createBrinquedo(
            @RequestParam String codigo,
            @RequestParam String nome,
            @RequestParam BigDecimal valor,
            @RequestParam String marca,
            @RequestParam String descricao,
            @RequestParam String detalhes,
            @RequestParam List<Long> categoriaIds,
            @RequestParam("imagens") List<MultipartFile> arquivos) throws Exception {

        Brinquedo salvo = brinquedoService.criarBrinquedo(codigo, nome, valor, marca, descricao, detalhes, categoriaIds, arquivos);
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Brinquedo> atualizarBrinquedo(
            @PathVariable Long id,
            @RequestParam(required = false) String codigo,
            @RequestParam String nome,
            @RequestParam BigDecimal valor,
            @RequestParam String marca,
            @RequestParam String descricao,
            @RequestParam String detalhes,
            @RequestParam List<Long> categoriaIds
    ) {
        Brinquedo atualizado = brinquedoService.atualizarBrinquedo(
                id, codigo, nome, valor, marca, descricao, detalhes, categoriaIds
        );
        return ResponseEntity.ok(atualizado);
    }

    @PostMapping("/{id}/imagens")
    public ResponseEntity<List<Imagem>> adicionarImagens(
            @PathVariable Long id,
            @RequestParam("arquivos") List<MultipartFile> arquivos
    ) throws Exception {
        List<Imagem> novasImagens = brinquedoService.adicionarImagens(id, arquivos);
        return ResponseEntity.ok(novasImagens);
    }

    @DeleteMapping("/{id}/imagens")
    public ResponseEntity<Void> removerTodasImagens(@PathVariable Long id) throws Exception {
        brinquedoService.removerTodasImagens(id);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrinquedo(@PathVariable Long id) {
        brinquedoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
