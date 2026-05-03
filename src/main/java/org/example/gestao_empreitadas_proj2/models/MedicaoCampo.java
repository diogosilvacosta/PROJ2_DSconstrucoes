package org.example.gestao_empreitadas_proj2.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicao_campo")
public class MedicaoCampo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "medicao_campo_id_gen")
    @SequenceGenerator(name = "medicao_campo_id_gen", sequenceName = "seq_medicao_campo", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "obraid", nullable = false)
    private Obra obraid;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "comprimento", precision = 10, scale = 3)
    private BigDecimal comprimento;

    @Column(name = "largura", precision = 10, scale = 3)
    private BigDecimal largura;

    @Column(name = "altura", precision = 10, scale = 3)
    private BigDecimal altura;

    @Column(name = "area", precision = 10, scale = 3)
    private BigDecimal area;

    @Column(name = "notas", length = 1000)
    private String notas;

    @Column(name = "esbocoImagem", columnDefinition = "TEXT")
    private String esbocoImagemBase64;

    @Column(name = "data_registo")
    private LocalDateTime dataRegisto = LocalDateTime.now();

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Obra getObraid() { return obraid; }
    public void setObraid(Obra obraid) { this.obraid = obraid; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getComprimento() { return comprimento; }
    public void setComprimento(BigDecimal comprimento) { this.comprimento = comprimento; }

    public BigDecimal getLargura() { return largura; }
    public void setLargura(BigDecimal largura) { this.largura = largura; }

    public BigDecimal getAltura() { return altura; }
    public void setAltura(BigDecimal altura) { this.altura = altura; }

    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public String getEsbocoImagemBase64() { return esbocoImagemBase64; }
    public void setEsbocoImagemBase64(String esbocoImagemBase64) { this.esbocoImagemBase64 = esbocoImagemBase64; }

    public LocalDateTime getDataRegisto() { return dataRegisto; }
    public void setDataRegisto(LocalDateTime dataRegisto) { this.dataRegisto = dataRegisto; }
}
