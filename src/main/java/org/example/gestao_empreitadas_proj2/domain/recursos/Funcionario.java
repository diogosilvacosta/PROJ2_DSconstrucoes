package org.example.gestao_empreitadas_proj2.domain.recursos;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import java.time.Instant;

@Entity
@Table(name = "funcionario")
public class Funcionario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "funcionario_id_gen")
    @SequenceGenerator(name = "funcionario_id_gen", sequenceName = "seq_funcionario", allocationSize = 1)
    @Column(name = "funcionarioid", nullable = false)
    private Integer id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "cargo", nullable = false, length = 100)
    private String cargo;

    @Column(name = "horasdisponiveis")
    private Integer horasdisponiveis;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "data_criacao")
    private Instant dataCriacao;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public Integer getHorasdisponiveis() { return horasdisponiveis; }
    public void setHorasdisponiveis(Integer horasdisponiveis) { this.horasdisponiveis = horasdisponiveis; }
    public Instant getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Instant dataCriacao) { this.dataCriacao = dataCriacao; }
}
