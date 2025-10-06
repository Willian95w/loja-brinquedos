package com.example.loja_brinquedos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
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
    private Set<Brinquedo> brinquedos = new HashSet<>();

    //Construtores
    public Categoria() {}

    public Categoria(String nome, String descricao, String imagem) {
        this.nome = nome;
        this.descricao = descricao;
        this.imagem = imagem;
    }

    //Getters e Setters
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public Set<Brinquedo> getBrinquedos() {
        return brinquedos;
    }

    public void setBrinquedos(Set<Brinquedo> brinquedos) {
        this.brinquedos = brinquedos;
    }

    //Metodo para obter a quantidade de produtos na categoria
    public int getQuantidadeProdutos() {
        return brinquedos.size();
    }

}
