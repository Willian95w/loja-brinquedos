package com.example.loja_brinquedos.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "imagens")
public class Imagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String caminho;

    @ManyToOne
    @JoinColumn(name = "brinquedo_id", nullable = false)
    @JsonBackReference
    private Brinquedo brinquedo;

    //Construtores
    public Imagem() {}

    public Imagem(String caminho, Brinquedo brinquedo) {
        this.caminho = caminho;
        this.brinquedo = brinquedo;
    }

    //Getters e Setters
    public Long getId() {
        return id;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public Brinquedo getBrinquedo() {
        return brinquedo;
    }

    public void setBrinquedo(Brinquedo brinquedo) {
        this.brinquedo = brinquedo;
    }

}
