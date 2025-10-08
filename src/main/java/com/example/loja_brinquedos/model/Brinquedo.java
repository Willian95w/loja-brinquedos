package com.example.loja_brinquedos.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@Entity
@Table(name = "brinquedos")
public class Brinquedo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @NotNull
    @Column(nullable = false)
    private BigDecimal valor;

    @NotBlank
    @Column(nullable = false)
    private String marca;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String detalhes;

    @NotEmpty(message = "O brinquedo deve ter pelo menos uma categoria")
    @ManyToMany
    @JoinTable(
            name = "produto_categoria",
            joinColumns = @JoinColumn(name = "produto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    @JsonManagedReference
    private Set<Categoria> categorias = new HashSet<>();

    @NotEmpty
    @OneToMany(mappedBy = "brinquedo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Imagem> imagens = new ArrayList<>();

    @Column(nullable = false)
    private Long views = 0L;

    //Construtores
    public Brinquedo() {
    }

    public Brinquedo(String codigo, String nome, BigDecimal valor, String marca, String descricao, String detalhes) {
        this.codigo = codigo;
        this.nome = nome;
        this.valor = valor;
        this.marca = marca;
        this.descricao = descricao;
        this.detalhes = detalhes;
        this.categorias = new HashSet<>();
        this.imagens = new ArrayList<>();
        this.views = 0L;
    }

}
