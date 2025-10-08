package com.example.loja_brinquedos.service;

import com.example.loja_brinquedos.model.Brinquedo;
import com.example.loja_brinquedos.repository.BrinquedoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BrinquedoService {

    private final BrinquedoRepository brinquedoRepository;

    public BrinquedoService(BrinquedoRepository brinquedoRepository) {
        this.brinquedoRepository = brinquedoRepository;
    }

    public List<Brinquedo> findAll() {
        return brinquedoRepository.findAll();
    }

    public List<Brinquedo> findTop8MaisAcessados() {
        return brinquedoRepository.findTop8ByOrderByViewsDesc();
    }

    public List<Brinquedo> findByCategoriaId(Long idCategoria) {
        return brinquedoRepository.findByCategorias_Id(idCategoria);
    }

    public Optional<Brinquedo> findById(Long id) {
        return brinquedoRepository.findById(id);
    }

    public List<Brinquedo> filtrarPorCategoria(Long categoriaId, String nome, List<String> marcas,
                                               BigDecimal minValor, BigDecimal maxValor) {
        // se a lista de marcas estiver vazia, passamos null
        if (marcas != null && marcas.isEmpty()) {
            marcas = null;
        }

        return brinquedoRepository.filtrarPorCategoria(categoriaId, nome, marcas, minValor, maxValor);
    }

    public Brinquedo save(Brinquedo brinquedo) {
        return brinquedoRepository.save(brinquedo);
    }

    public void delete(Long id) {
        brinquedoRepository.deleteById(id);
    }

    public void incrementViews(Long id) {
        brinquedoRepository.findById(id).ifPresent(brinquedo -> {
            brinquedo.setViews(brinquedo.getViews() + 1);
            brinquedoRepository.save(brinquedo);
        });
    }

}
