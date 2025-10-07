package com.example.loja_brinquedos.controller;

import com.example.loja_brinquedos.model.Brinquedo;
import com.example.loja_brinquedos.model.Categoria;
import com.example.loja_brinquedos.model.Imagem;
import com.example.loja_brinquedos.repository.CategoriaRepository;
import com.example.loja_brinquedos.repository.ImagemRepository;
import com.example.loja_brinquedos.service.BrinquedoService;
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

    private static final String upload_dir = "src/main/resources/static/uploads/";

    public BrinquedoController(BrinquedoService brinquedoService, CategoriaRepository categoriaRepository, ImagemRepository imagemRepository) {
        this.brinquedoService = brinquedoService;
        this.categoriaRepository = categoriaRepository;
        this.imagemRepository = imagemRepository;
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
            @RequestParam("imagens") List<MultipartFile> arquivos) throws IOException {

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

        // Associa imagens
        List<Imagem> imagens = new ArrayList<>();
        Path diretorio = Paths.get(upload_dir);
        if (!Files.exists(diretorio)) {
            Files.createDirectories(diretorio);
        }

        for (MultipartFile arquivo : arquivos) {
            if (!arquivo.isEmpty()) {
                String nomeArquivo = System.currentTimeMillis() + "_" + arquivo.getOriginalFilename();
                Path caminhoArquivo = diretorio.resolve(nomeArquivo);
                Files.copy(arquivo.getInputStream(), caminhoArquivo);

                Imagem imagem = new Imagem();
                imagem.setCaminho("/uploads/" + nomeArquivo);
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
    ) throws IOException {

        Optional<Brinquedo> brinquedoOpt = brinquedoService.findById(id);
        if (brinquedoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Brinquedo brinquedo = brinquedoOpt.get();

        // Atualiza campos do brinquedo
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

        // Remove imagens existentes, se solicitado
        if (imagensRemover != null) {
            brinquedo.getImagens().removeIf(img -> {
                if (imagensRemover.contains(img.getId())) {
                    // Remove do banco
                    imagemRepository.delete(img);
                    // Remove do disco
                    try {
                        Path caminhoArquivo = Paths.get("src/main/resources/static" + img.getCaminho());
                        Files.deleteIfExists(caminhoArquivo);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            });
        }

        // Adiciona novas imagens
        if (novasImagens != null) {
            Path diretorio = Paths.get(upload_dir);
            if (!Files.exists(diretorio)) {
                Files.createDirectories(diretorio);
            }

            for (MultipartFile arquivo : novasImagens) {
                if (!arquivo.isEmpty()) {
                    String nomeArquivo = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
                    Path caminhoArquivo = diretorio.resolve(nomeArquivo);
                    Files.copy(arquivo.getInputStream(), caminhoArquivo);

                    Imagem imagem = new Imagem();
                    imagem.setCaminho("/uploads/" + nomeArquivo);
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
