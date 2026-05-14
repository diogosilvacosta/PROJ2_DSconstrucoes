package org.example.gestao_empreitadas_proj2.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "planotrabalhos")
public class Planotrabalho {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "planotrabalhos_id_gen")
    @SequenceGenerator(name = "planotrabalhos_id_gen", sequenceName = "seq_planotrabalhos", allocationSize = 1)
    @Column(name = "faseid", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "obraid", nullable = false)
    private Obra obraid;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "duracao", nullable = false)
    private Integer duracao;

    @ColumnDefault("'PLANEJADA'")
    @Column(name = "estado", length = 20)
    private String estado;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "data_criacao")
    private Instant dataCriacao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Obra getObraid() {
        return obraid;
    }

    public void setObraid(Obra obraid) {
        this.obraid = obraid;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Instant getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Instant dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

}