package com.example.loja_brinquedos.repository;

import com.example.loja_brinquedos.model.Brinquedo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrinquedoRepository extends JpaRepository<Brinquedo,Long> {

    List<Brinquedo> findByNomeContainingIgnoreCase(String nome);

}
