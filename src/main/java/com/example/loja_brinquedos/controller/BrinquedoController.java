package com.example.loja_brinquedos.controller;

import com.example.loja_brinquedos.model.Brinquedo;
import com.example.loja_brinquedos.model.Categoria;
import com.example.loja_brinquedos.model.Imagem;
import com.example.loja_brinquedos.repository.CategoriaRepository;
import com.example.loja_brinquedos.repository.ImagemRepository;
import com.example.loja_brinquedos.service.BrinquedoService;
import com.example.loja_brinquedos.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @GetMapping("/{id}")
    public ResponseEntity<Brinquedo> getBrinquedoById(@PathVariable Long id) {
        return brinquedoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Brinquedo> searchBrinquedos(@RequestParam String nome) {
        return brinquedoService.findByNome(nome);
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

        // Cria o brinquedo
        Brinquedo brinquedo = new Brinquedo();
        brinquedo.setCodigo(codigo);
        brinquedo.setNome(nome);
        brinquedo.setValor(valor);
        brinquedo.setMarca(marca);
        brinquedo.setDescricao(descricao);
        brinquedo.setDetalhes(detalhes);

        // Associa categorias
        Set<Categoria> categorias = new HashSet<>();
        for (Long id : categoriaIds) {
            categoriaRepository.findById(id).ifPresent(categorias::add);
        }
        brinquedo.setCategorias(categorias);

        // Faz upload das imagens para o Cloudinary
        List<Imagem> imagens = new ArrayList<>();
        for (MultipartFile arquivo : arquivos) {
            if (!arquivo.isEmpty()) {
                // Faz upload pro Cloudinary (retorna o Map com dados da imagem)
                Map<String, Object> uploadResult = cloudinaryService.uploadFile(arquivo);

                // Extrai URL e public_id
                String imageUrl = (String) uploadResult.get("secure_url");
                String publicId = (String) uploadResult.get("public_id");

                // Cria a entidade Imagem
                Imagem imagem = new Imagem();
                imagem.setCaminho(imageUrl);
                imagem.setPublicId(publicId);
                imagem.setBrinquedo(brinquedo);

                imagens.add(imagem);
            }
        }

        brinquedo.setImagens(imagens);

        Brinquedo salvo = brinquedoService.save(brinquedo);
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Brinquedo> updateBrinquedo(
            @PathVariable Long id,
            @RequestParam String codigo,
            @RequestParam String nome,
            @RequestParam BigDecimal valor,
            @RequestParam String marca,
            @RequestParam String descricao,
            @RequestParam String detalhes,
            @RequestParam List<Long> categoriaIds,
            @RequestParam(value = "novasImagens", required = false) List<MultipartFile> novasImagens,
            @RequestParam(value = "imagensRemover", required = false) List<Long> imagensRemover
    ) throws Exception {

        Optional<Brinquedo> brinquedoOpt = brinquedoService.findById(id);
        if (brinquedoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Brinquedo brinquedo = brinquedoOpt.get();

        // Atualiza campos básicos
        brinquedo.setCodigo(codigo);
        brinquedo.setNome(nome);
        brinquedo.setValor(valor);
        brinquedo.setMarca(marca);
        brinquedo.setDescricao(descricao);
        brinquedo.setDetalhes(detalhes);

        // Atualiza categorias
        Set<Categoria> categorias = new HashSet<>();
        for (Long catId : categoriaIds) {
            categoriaRepository.findById(catId).ifPresent(categorias::add);
        }
        brinquedo.setCategorias(categorias);

        // Remove imagens, se solicitado
        if (imagensRemover != null && !imagensRemover.isEmpty()) {
            List<Imagem> imagensParaRemover = new ArrayList<>();

            for (Imagem img : brinquedo.getImagens()) {
                if (imagensRemover.contains(img.getId())) {
                    // Exclui do Cloudinary se tiver publicId
                    if (img.getPublicId() != null) {
                        try {
                            cloudinaryService.deleteFile(img.getPublicId());
                        } catch (Exception e) {
                            System.err.println("Erro ao deletar do Cloudinary: " + e.getMessage());
                        }
                    }
                    imagensParaRemover.add(img);
                }
            }

            // Remove do banco
            brinquedo.getImagens().removeAll(imagensParaRemover);
            imagemRepository.deleteAll(imagensParaRemover);
        }

        // Adiciona novas imagens
        if (novasImagens != null && !novasImagens.isEmpty()) {
            for (MultipartFile arquivo : novasImagens) {
                if (!arquivo.isEmpty()) {
                    Map<String, Object> uploadResult = cloudinaryService.uploadFile(arquivo);
                    String imageUrl = (String) uploadResult.get("secure_url");
                    String publicId = (String) uploadResult.get("public_id");

                    Imagem imagem = new Imagem();
                    imagem.setCaminho(imageUrl);
                    imagem.setPublicId(publicId);
                    imagem.setBrinquedo(brinquedo);
                    brinquedo.getImagens().add(imagem);
                }
            }
        }

        Brinquedo salvo = brinquedoService.save(brinquedo);
        return ResponseEntity.ok(salvo);
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
