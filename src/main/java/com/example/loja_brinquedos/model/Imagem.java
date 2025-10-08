package com.example.loja_brinquedos.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @NotBlank
    @Column(name = "public_id", nullable = false)
    private String publicId;

}
