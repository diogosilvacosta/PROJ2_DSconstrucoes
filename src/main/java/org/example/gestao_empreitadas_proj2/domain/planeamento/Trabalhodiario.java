package org.example.gestao_empreitadas_proj2.domain.planeamento;
import jakarta.persistence.*;
import org.example.gestao_empreitadas_proj2.domain.obra.Obra;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
@Entity
@Table(name = "trabalhodiario")
public class Trabalhodiario {
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="trabalhodiario_id_gen")
    @SequenceGenerator(name="trabalhodiario_id_gen",sequenceName="seq_trabalhodiario",allocationSize=1)
    @Column(name="trabalhodiarioid",nullable=false) private Integer id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="obraid",nullable=false) private Obra obraid;
    @Column(name="data",nullable=false) private LocalDate data;
    @Column(name="horastrabalhadas",nullable=false) private Integer horastrabalhadas;
    @Column(name="percentagemrealizado",nullable=false,precision=5,scale=2) private BigDecimal percentagemrealizado;
    @Column(name="descricao",length=Integer.MAX_VALUE) private String descricao;
    @ColumnDefault("CURRENT_TIMESTAMP") @Column(name="data_criacao") private Instant dataCriacao;
    public Integer getId(){return id;} public void setId(Integer id){this.id=id;}
    public Obra getObraid(){return obraid;} public void setObraid(Obra o){this.obraid=o;}
    public LocalDate getData(){return data;} public void setData(LocalDate d){this.data=d;}
    public Integer getHorastrabalhadas(){return horastrabalhadas;} public void setHorastrabalhadas(Integer h){this.horastrabalhadas=h;}
    public BigDecimal getPercentagemrealizado(){return percentagemrealizado;} public void setPercentagemrealizado(BigDecimal p){this.percentagemrealizado=p;}
    public String getDescricao(){return descricao;} public void setDescricao(String d){this.descricao=d;}
    public Instant getDataCriacao(){return dataCriacao;} public void setDataCriacao(Instant d){this.dataCriacao=d;}
}