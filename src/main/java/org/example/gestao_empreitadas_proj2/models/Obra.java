package org.example.gestao_empreitadas_proj2.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "obra")
public class Obra {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "obra_id_gen")
    @SequenceGenerator(name = "obra_id_gen", sequenceName = "seq_obra", allocationSize = 1)
    @Column(name = "obraid", nullable = false)
    private Integer id;


    @OneToOne(fetch = FetchType.LAZY, optional = true) // optional = true aqui
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "propostaid", nullable = true) // nullable = true aqui
    private Proposta propostaid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "clienteid", nullable = false)
    private Cliente clienteid;

    @Column(name = "datainicio", nullable = false)
    private LocalDate datainicio;

    @Column(name = "estado", length = 20)
    private String estado = "INICIADA";

    @Column(name = "percentagemconclusao", precision = 5, scale = 2)
    private BigDecimal percentagemconclusao = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "directorobra")
    private Funcionario directorobra;

    @Column(name = "data_criacao")
    private Instant dataCriacao = Instant.now();

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Proposta getPropostaid() { return propostaid; }
    public void setPropostaid(Proposta propostaid) { this.propostaid = propostaid; }
    public Cliente getClienteid() { return clienteid; }
    public void setClienteid(Cliente clienteid) { this.clienteid = clienteid; }
    public LocalDate getDatainicio() { return datainicio; }
    public void setDatainicio(LocalDate datainicio) { this.datainicio = datainicio; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public BigDecimal getPercentagemconclusao() { return percentagemconclusao; }
    public void setPercentagemconclusao(BigDecimal percentagemconclusao) { this.percentagemconclusao = percentagemconclusao; }
    public Funcionario getDirectorobra() {
        return directorobra;
    }

    public void setDirectorobra(Funcionario directorobra) {
        this.directorobra = directorobra;
    }
}