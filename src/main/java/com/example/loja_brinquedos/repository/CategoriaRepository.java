package com.example.loja_brinquedos.repository;

import com.example.loja_brinquedos.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findAllByOrderByNomeAsc();

    List<Categoria> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

}
