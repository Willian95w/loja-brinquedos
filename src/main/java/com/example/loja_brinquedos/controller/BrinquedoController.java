package com.example.loja_brinquedos.controller;

import com.example.loja_brinquedos.model.Brinquedo;
import com.example.loja_brinquedos.service.BrinquedoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/brinquedos")
public class BrinquedoController {

    private final BrinquedoService brinquedoService;

    public BrinquedoController(BrinquedoService brinquedoService) {
        this.brinquedoService = brinquedoService;
    }

    @GetMapping
    public List<Brinquedo> getAllBrinquedos() {
        return brinquedoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Brinquedo> getBrinquedoById(@PathVariable Long id) {
        Optional<Brinquedo> brinquedo = brinquedoService.findById(id);
        return brinquedo.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Brinquedo> searchBrinquedos(@RequestParam String nome) {
        return brinquedoService.findByNome(nome);
    }

    @PostMapping
    public Brinquedo createBrinquedo(@RequestBody Brinquedo brinquedo) {
        return brinquedoService.save(brinquedo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Brinquedo> updateBrinquedo(@PathVariable Long id, @RequestBody Brinquedo brinquedoDetalhes) {
        Optional<Brinquedo> brinquedoOpt = brinquedoService.findById(id);
        if (brinquedoOpt.isPresent()) {
            Brinquedo brinquedo = brinquedoOpt.get();
            brinquedo.setCodigo(brinquedoDetalhes.getCodigo());
            brinquedo.setNome(brinquedoDetalhes.getNome());
            brinquedo.setValor(brinquedoDetalhes.getValor());
            brinquedo.setMarca(brinquedoDetalhes.getMarca());
            brinquedo.setDescricao(brinquedoDetalhes.getDescricao());
            brinquedo.setDetalhes(brinquedoDetalhes.getDetalhes());
            brinquedo.setCategorias(brinquedoDetalhes.getCategorias());
            brinquedo.setImagens(brinquedoDetalhes.getImagens());
            return ResponseEntity.ok(brinquedoService.save(brinquedo));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrinquedo(@PathVariable Long id) {
        brinquedoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/incrementViews")
    public ResponseEntity<Void> incrementViews(@PathVariable Long id) {
        brinquedoService.incrementViews(id);
        return ResponseEntity.ok().build();
    }

}
