package com.example.loja_brinquedos.controller;

import com.example.loja_brinquedos.model.Imagem;
import com.example.loja_brinquedos.service.ImagemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/imagens")
public class ImagemController {

    private final ImagemService imagemService;

    public ImagemController(ImagemService imagemService) {
        this.imagemService = imagemService;
    }

    @GetMapping
    public List<Imagem> getImagensByBrinquedo(@RequestParam Long brinquedoId) {
        return imagemService.findByBrinquedoId(brinquedoId);
    }

    @PostMapping
    public Imagem saveImagem(@RequestParam Long brinquedoId, @RequestParam String caminho) {
        return imagemService.saveImagem(brinquedoId, caminho);
    }

    @DeleteMapping("/{id}")
    public void deleteImagem(@PathVariable Long id) {
        imagemService.delete(id);
    }

}
