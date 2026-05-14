package org.example.gestao_empreitadas_proj2.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "fatura")
public class Fatura {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fatura_id_gen")
    @SequenceGenerator(name = "fatura_id_gen", sequenceName = "seq_fatura", allocationSize = 1)
    @Column(name = "faturaid", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "automedicaoid", nullable = false)
    private Automedicao automedicaoid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "obraid", nullable = false)
    private Obra obraid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "clienteid", nullable = false)
    private Cliente clienteid;

    @Column(name = "numerofatura", nullable = false, length = 30)
    private String numerofatura;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @ColumnDefault("'EMITIDA'")
    @Column(name = "estado", length = 20)
    private String estado;

    @Column(name = "dataemissao", nullable = false)
    private LocalDate dataemissao;

    @Column(name = "datavencimento", nullable = false)
    private LocalDate datavencimento;

    @ColumnDefault("23")
    @Column(name = "iva", precision = 5, scale = 2)
    private BigDecimal iva;

    @ColumnDefault("0")
    @Column(name = "retencao", precision = 5, scale = 2)
    private BigDecimal retencao;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "data_criacao")
    private Instant dataCriacao;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Automedicao getAutomedicaoid() {
        return automedicaoid;
    }

    public void setAutomedicaoid(Automedicao automedicaoid) {
        this.automedicaoid = automedicaoid;
    }

    public Obra getObraid() {
        return obraid;
    }

    public void setObraid(Obra obraid) {
        this.obraid = obraid;
    }

    public Cliente getClienteid() {
        return clienteid;
    }

    public void setClienteid(Cliente clienteid) {
        this.clienteid = clienteid;
    }

    public String getNumerofatura() {
        return numerofatura;
    }

    public void setNumerofatura(String numerofatura) {
        this.numerofatura = numerofatura;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getDataemissao() {
        return dataemissao;
    }

    public void setDataemissao(LocalDate dataemissao) {
        this.dataemissao = dataemissao;
    }

    public LocalDate getDatavencimento() {
        return datavencimento;
    }

    public void setDatavencimento(LocalDate datavencimento) {
        this.datavencimento = datavencimento;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public BigDecimal getRetencao() {
        return retencao;
    }

    public void setRetencao(BigDecimal retencao) {
        this.retencao = retencao;
    }

    public Instant getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Instant dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

}