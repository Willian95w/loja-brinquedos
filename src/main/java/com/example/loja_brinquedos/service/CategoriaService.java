package com.example.loja_brinquedos.service;

import com.example.loja_brinquedos.model.Categoria;
import com.example.loja_brinquedos.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    private final String baseUrl = "https://loja-brinquedos.onrender.com";

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> findAll() {
        List<Categoria> categorias = categoriaRepository.findAll();

        categorias.forEach(c -> {
            if (c.getImagem() != null) {
                c.setImagem(baseUrl + c.getImagem());
            }
        });

        return categorias;
    }

    public List<Categoria> findByNome(String nome) {
        List<Categoria> categorias = categoriaRepository.findByNomeContainingIgnoreCase(nome);

        String baseUrl = "https://loja-brinquedos.onrender.com";
        categorias.forEach(c -> {
            if (c.getImagem() != null) {
                c.setImagem(baseUrl + c.getImagem());
            }
        });

        return categorias;
    }

    public int getQuantidadeProdutos(Categoria categoria) {
        return categoria.getBrinquedos().size();
    }

}
