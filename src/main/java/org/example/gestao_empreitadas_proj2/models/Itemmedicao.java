package org.example.gestao_empreitadas_proj2.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "itemmedicao")
public class Itemmedicao {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemmedicao_id_gen")
    @SequenceGenerator(name = "itemmedicao_id_gen", sequenceName = "seq_itemmedicao", allocationSize = 1)
    @Column(name = "itemid", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "propostaid", nullable = false)
    private Proposta propostaid;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "quantidade", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidade;

    @Column(name = "valorunitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorunitario;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "data_criacao")
    private Instant dataCriacao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Proposta getPropostaid() {
        return propostaid;
    }

    public void setPropostaid(Proposta propostaid) {
        this.propostaid = propostaid;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorunitario() {
        return valorunitario;
    }

    public void setValorunitario(BigDecimal valorunitario) {
        this.valorunitario = valorunitario;
    }

    public Instant getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Instant dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

}