package org.example.gestao_empreitadas_proj2.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "obarecurso")
public class Obarecurso {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "obarecurso_id_gen")
    @SequenceGenerator(name = "obarecurso_id_gen", sequenceName = "seq_obarecurso", allocationSize = 1)
    @Column(name = "obarecursoid", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "obraid", nullable = false)
    private Obra obraid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "funcionarioid", nullable = false)
    private Funcionario funcionarioid;

    @Column(name = "horasalocadas", nullable = false)
    private Integer horasalocadas;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "data_alocacao")
    private Instant dataAlocacao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Obra getObraid() {
        return obraid;
    }

    public void setObraid(Obra obraid) {
        this.obraid = obraid;
    }

    public Funcionario getFuncionarioid() {
        return funcionarioid;
    }

    public void setFuncionarioid(Funcionario funcionarioid) {
        this.funcionarioid = funcionarioid;
    }

    public Integer getHorasalocadas() {
        return horasalocadas;
    }

    public void setHorasalocadas(Integer horasalocadas) {
        this.horasalocadas = horasalocadas;
    }

    public Instant getDataAlocacao() {
        return dataAlocacao;
    }

    public void setDataAlocacao(Instant dataAlocacao) {
        this.dataAlocacao = dataAlocacao;
    }

}