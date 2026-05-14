package org.example.gestao_empreitadas_proj2.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import java.time.Instant;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cliente_id_gen")
    @SequenceGenerator(name = "cliente_id_gen", sequenceName = "seq_cliente", allocationSize = 1)
    @Column(name = "clienteid", nullable = false)
    private Integer id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "morada", length = 250)
    private String morada;

    @Column(name = "nif", nullable = false, length = 20)
    private String nif;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "pessoa_contacto", length = 100)
    private String pessoaContacto;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "data_criacao")
    private Instant dataCriacao;

    // Getters e Setters (Essenciais para a BLL funcionar)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMorada() { return morada; }
    public void setMorada(String morada) { this.morada = morada; }

    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getPessoaContacto() { return pessoaContacto; }
    public void setPessoaContacto(String pessoaContacto) { this.pessoaContacto = pessoaContacto; }

    public Instant getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(Instant dataCriacao) { this.dataCriacao = dataCriacao; }
}