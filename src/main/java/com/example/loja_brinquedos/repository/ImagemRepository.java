package com.example.loja_brinquedos.repository;

import com.example.loja_brinquedos.model.Imagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagemRepository extends JpaRepository<Imagem,Long> {

    List<Imagem> findByBrinquedoId(Long brinquedoId);

}
