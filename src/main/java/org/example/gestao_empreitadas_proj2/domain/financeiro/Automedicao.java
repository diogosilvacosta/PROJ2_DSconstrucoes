package org.example.gestao_empreitadas_proj2.domain.financeiro;
import jakarta.persistence.*;
import org.example.gestao_empreitadas_proj2.domain.obra.Obra;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.math.BigDecimal;
import java.time.Instant;
@Entity
@Table(name = "automedicao")
public class Automedicao {
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="automedicao_id_gen")
    @SequenceGenerator(name="automedicao_id_gen",sequenceName="seq_automedicao",allocationSize=1)
    @Column(name="automedicaoid",nullable=false) private Integer id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @OnDelete(action=OnDeleteAction.CASCADE)
    @JoinColumn(name="obraid",nullable=false) private Obra obraid;
    @Column(name="percentagemtrabalho",nullable=false,precision=5,scale=2) private java.math.BigDecimal percentagemtrabalho;
    @ColumnDefault("'RASCUNHO'") @Column(name="estado",length=20) private String estado;
    @Column(name="valor",precision=15,scale=2) private java.math.BigDecimal valor;
    @ColumnDefault("CURRENT_TIMESTAMP") @Column(name="datacriacao") private Instant datacriacao;
    public Integer getId(){return id;} public void setId(Integer id){this.id=id;}
    public Obra getObraid(){return obraid;} public void setObraid(Obra o){this.obraid=o;}
    public BigDecimal getPercentagemtrabalho(){return percentagemtrabalho;} public void setPercentagemtrabalho(BigDecimal p){this.percentagemtrabalho=p;}
    public String getEstado(){return estado;} public void setEstado(String e){this.estado=e;}
    public BigDecimal getValor(){return valor;} public void setValor(BigDecimal v){this.valor=v;}
    public Instant getDatacriacao(){return datacriacao;} public void setDatacriacao(Instant d){this.datacriacao=d;}
}