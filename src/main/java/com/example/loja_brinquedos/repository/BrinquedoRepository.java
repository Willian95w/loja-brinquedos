package com.example.loja_brinquedos.repository;

import com.example.loja_brinquedos.model.Brinquedo;
import com.example.loja_brinquedos.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Repository
public interface BrinquedoRepository extends JpaRepository<Brinquedo,Long> {

    List<Brinquedo> findTop12ByOrderByViewsDesc();

    List<Brinquedo> findByCategorias_Id(Long id);

    @Query("""
        SELECT b 
        FROM Brinquedo b
        JOIN b.categorias c
        WHERE c.id = :categoriaId
          AND (:nome IS NULL OR LOWER(b.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
          AND (:marcas IS NULL OR b.marca IN :marcas)
          AND (:minValor IS NULL OR b.valor >= :minValor)
          AND (:maxValor IS NULL OR b.valor <= :maxValor)
    """)
    List<Brinquedo> filtrarPorCategoria(
            @Param("categoriaId") Long categoriaId,
            @Param("nome") String nome,
            @Param("marcas") List<String> marcas,
            @Param("minValor") BigDecimal minValor,
            @Param("maxValor") BigDecimal maxValor
    );

    @Query("SELECT DISTINCT b FROM Brinquedo b JOIN b.categorias c WHERE c IN :categorias AND b.id <> :brinquedoId")
    List<Brinquedo> findRelacionadosByCategorias(@Param("categorias") Set<Categoria> categorias,
                                                 @Param("brinquedoId") Long brinquedoId);


}
