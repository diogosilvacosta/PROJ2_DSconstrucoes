package org.example.gestao_empreitadas_proj2.domain.financeiro;

import jakarta.persistence.*;
import org.example.gestao_empreitadas_proj2.domain.cliente.Cliente;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "proposta")
public class Proposta {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proposta_id_gen")
    @SequenceGenerator(name = "proposta_id_gen", sequenceName = "seq_proposta", allocationSize = 1)
    @Column(name = "propostaid", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "clienteid", nullable = false)
    private Cliente clienteid;

    @Column(name = "descricao", length = Integer.MAX_VALUE)
    private String descricao;

    @Column(name = "dataproposta", nullable = false)
    private LocalDate dataproposta;

    @ColumnDefault("'RASCUNHO'")
    @Column(name = "estado", length = 20)
    private String estado;

    @Column(name = "valortotal", precision = 15, scale = 2)
    private BigDecimal valortotal;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "data_criacao")
    private Instant dataCriacao;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Cliente getClienteid() { return clienteid; }
    public void setClienteid(Cliente clienteid) { this.clienteid = clienteid; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDate getDataproposta() { return dataproposta; }
    public void setDataproposta(LocalDate dataproposta) { this.dataproposta = dataproposta; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public BigDecimal getValortotal() { return valortotal; }
    public void setValortotal(BigDecimal valortotal) { this.valortotal = valortotal; }
    public Instant getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Instant dataCriacao) { this.dataCriacao = dataCriacao; }
}
