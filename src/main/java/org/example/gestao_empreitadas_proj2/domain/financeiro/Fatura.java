package org.example.gestao_empreitadas_proj2.domain.financeiro;
import jakarta.persistence.*;
import org.example.gestao_empreitadas_proj2.domain.obra.Obra;
import org.example.gestao_empreitadas_proj2.domain.cliente.Cliente;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
@Entity
@Table(name = "fatura")
public class Fatura {
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="fatura_id_gen")
    @SequenceGenerator(name="fatura_id_gen",sequenceName="seq_fatura",allocationSize=1)
    @Column(name="faturaid",nullable=false) private Integer id;
    @OneToOne(fetch=FetchType.LAZY,optional=false) @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="automedicaoid",nullable=false) private Automedicao automedicaoid;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="obraid",nullable=false) private Obra obraid;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="clienteid",nullable=false) private Cliente clienteid;
    @Column(name="numerofatura",nullable=false,length=30) private String numerofatura;
    @Column(name="valor",nullable=false,precision=15,scale=2) private BigDecimal valor;
    @ColumnDefault("'EMITIDA'") @Column(name="estado",length=20) private String estado;
    @Column(name="dataemissao",nullable=false) private LocalDate dataemissao;
    @Column(name="datavencimento",nullable=false) private LocalDate datavencimento;
    @ColumnDefault("23") @Column(name="iva",precision=5,scale=2) private BigDecimal iva;
    @ColumnDefault("0") @Column(name="retencao",precision=5,scale=2) private BigDecimal retencao;
    @ColumnDefault("CURRENT_TIMESTAMP") @Column(name="data_criacao") private Instant dataCriacao;
    @Column(name="data_pagamento") private LocalDate dataPagamento;
    public Integer getId(){return id;} public void setId(Integer id){this.id=id;}
    public Automedicao getAutomedicaoid(){return automedicaoid;} public void setAutomedicaoid(Automedicao a){this.automedicaoid=a;}
    public Obra getObraid(){return obraid;} public void setObraid(Obra o){this.obraid=o;}
    public Cliente getClienteid(){return clienteid;} public void setClienteid(Cliente c){this.clienteid=c;}
    public String getNumerofatura(){return numerofatura;} public void setNumerofatura(String n){this.numerofatura=n;}
    public BigDecimal getValor(){return valor;} public void setValor(BigDecimal v){this.valor=v;}
    public String getEstado(){return estado;} public void setEstado(String e){this.estado=e;}
    public LocalDate getDataemissao(){return dataemissao;} public void setDataemissao(LocalDate d){this.dataemissao=d;}
    public LocalDate getDatavencimento(){return datavencimento;} public void setDatavencimento(LocalDate d){this.datavencimento=d;}
    public BigDecimal getIva(){return iva;} public void setIva(BigDecimal i){this.iva=i;}
    public BigDecimal getRetencao(){return retencao;} public void setRetencao(BigDecimal r){this.retencao=r;}
    public Instant getDataCriacao(){return dataCriacao;} public void setDataCriacao(Instant d){this.dataCriacao=d;}
    public LocalDate getDataPagamento(){return dataPagamento;} public void setDataPagamento(LocalDate d){this.dataPagamento=d;}
}