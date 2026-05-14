package org.example.gestao_empreitadas_proj2.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "obra_material")
public class ObraMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "obra_material_id_gen")
    @SequenceGenerator(name = "obra_material_id_gen", sequenceName = "seq_obra_material", allocationSize = 1)
    @Column(name = "materialid", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "obraid", nullable = false)
    private Obra obraid;

    @Column(name = "descricao", nullable = false, length = 255)
    private String descricao;

    @ColumnDefault("'un'")
    @Column(name = "unidade", length = 20)
    private String unidade = "un";

    @Column(name = "quantidade", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "data_criacao")
    private Instant dataCriacao;

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Obra getObraid() { return obraid; }
    public void setObraid(Obra obraid) { this.obraid = obraid; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

    public Instant getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Instant dataCriacao) { this.dataCriacao = dataCriacao; }

    public BigDecimal getCustoTotal() {
        if (quantidade != null && precoUnitario != null)
            return quantidade.multiply(precoUnitario);
        return BigDecimal.ZERO;
    }
}
