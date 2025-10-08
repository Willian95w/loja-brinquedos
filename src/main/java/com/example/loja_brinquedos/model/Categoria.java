package com.example.loja_brinquedos.model;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String nome;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @NotBlank
    @Column(nullable = false)
    private String imagem;

    @ManyToMany(mappedBy = "categorias")
    @JsonBackReference
    private Set<Brinquedo> brinquedos = new HashSet<>();

    //Construtores
    public Categoria() {
    }

    public Categoria(String nome, String descricao, String imagem) {
        this.nome = nome;
        this.descricao = descricao;
        this.imagem = imagem;
        this.brinquedos = new HashSet<>();
    }

}
