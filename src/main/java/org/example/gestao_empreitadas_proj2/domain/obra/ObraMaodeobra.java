package org.example.gestao_empreitadas_proj2.domain.obra;

import jakarta.persistence.*;
import org.example.gestao_empreitadas_proj2.domain.recursos.Funcionario;
import org.hibernate.annotations.ColumnDefault;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "obra_maodeobra")
public class ObraMaodeobra {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "obra_maodeobra_id_gen")
    @SequenceGenerator(name = "obra_maodeobra_id_gen", sequenceName = "seq_obra_maodeobra", allocationSize = 1)
    @Column(name = "maodeobraid", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "obraid", nullable = false)
    private Obra obraid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "funcionarioid", nullable = false)
    private Funcionario funcionarioid;

    @Column(name = "horas", nullable = false)
    private Integer horas;

    @ColumnDefault("15.00")
    @Column(name = "custo_hora", nullable = false, precision = 8, scale = 2)
    private BigDecimal custoHora = new BigDecimal("15.00");

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "data_alocacao")
    private Instant dataAlocacao;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Obra getObraid() { return obraid; }
    public void setObraid(Obra obraid) { this.obraid = obraid; }
    public Funcionario getFuncionarioid() { return funcionarioid; }
    public void setFuncionarioid(Funcionario f) { this.funcionarioid = f; }
    public Integer getHoras() { return horas; }
    public void setHoras(Integer horas) { this.horas = horas; }
    public BigDecimal getCustoHora() { return custoHora; }
    public void setCustoHora(BigDecimal custoHora) { this.custoHora = custoHora; }
    public Instant getDataAlocacao() { return dataAlocacao; }
    public void setDataAlocacao(Instant dataAlocacao) { this.dataAlocacao = dataAlocacao; }
    public BigDecimal getCustoTotal() {
        if (horas != null && custoHora != null) return new BigDecimal(horas).multiply(custoHora);
        return BigDecimal.ZERO;
    }
}
