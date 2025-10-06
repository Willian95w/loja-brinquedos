package com.example.loja_brinquedos.service;

import com.example.loja_brinquedos.model.Categoria;
import com.example.loja_brinquedos.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    public List<Categoria> findByNome(String nome) {
        return categoriaRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public void delete(Long id) {
        categoriaRepository.deleteById(id);
    }

    public int getQuantidadeProdutos(Categoria categoria) {
        return categoria.getBrinquedos().size();
    }

}
