package com.example.loja_brinquedos.service;

import com.example.loja_brinquedos.model.Brinquedo;
import com.example.loja_brinquedos.model.Imagem;
import com.example.loja_brinquedos.repository.BrinquedoRepository;
import com.example.loja_brinquedos.repository.ImagemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImagemService {

    private final ImagemRepository imagemRepository;
    private final BrinquedoRepository brinquedoRepository;

    public ImagemService(ImagemRepository imagemRepository, BrinquedoRepository brinquedoRepository) {
        this.imagemRepository = imagemRepository;
        this.brinquedoRepository = brinquedoRepository;
    }

    public Imagem saveImagem(Long brinquedoId, String caminho) {
        Optional<Brinquedo> brinquedoOpt = brinquedoRepository.findById(brinquedoId);
        if (brinquedoOpt.isPresent()) {
            Imagem imagem = new Imagem();
            imagem.setCaminho(caminho);
            imagem.setBrinquedo(brinquedoOpt.get());
            return imagemRepository.save(imagem);
        } else {
            throw new RuntimeException("Brinquedo não encontrado para adicionar a imagem");
        }
    }

    public List<Imagem> findByBrinquedoId(Long brinquedoId) {
        return imagemRepository.findByBrinquedoId(brinquedoId);
    }

    public void delete(Long id) {
        imagemRepository.deleteById(id);
    }
}
