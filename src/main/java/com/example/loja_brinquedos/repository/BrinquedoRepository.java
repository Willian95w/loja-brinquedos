package com.example.loja_brinquedos.repository;

import com.example.loja_brinquedos.model.Brinquedo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BrinquedoRepository extends JpaRepository<Brinquedo,Long> {

    List<Brinquedo> findByMarcaInAndValorBetween(List<String> marcas, BigDecimal min, BigDecimal max);

    @Query("SELECT b FROM Brinquedo b WHERE (:nome IS NULL OR LOWER(b.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
            "AND (:marcas IS NULL OR b.marca IN :marcas) " +
            "AND (:minValor IS NULL OR b.valor >= :minValor) " +
            "AND (:maxValor IS NULL OR b.valor <= :maxValor)")
    List<Brinquedo> filtrar(
            @Param("nome") String nome,
            @Param("marcas") List<String> marcas,
            @Param("minValor") BigDecimal minValor,
            @Param("maxValor") BigDecimal maxValor);

}
