package org.example.gestao_empreitadas_proj2.domain.obra;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(name = "esboco")
public class Esboco {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "esboco_id_gen")
    @SequenceGenerator(name = "esboco_id_gen", sequenceName = "seq_esboco", allocationSize = 1)
    @Column(name = "esbocoid", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "obraid", nullable = false)
    private Obra obra;

    @Column(name = "nome", nullable = false)
    private String nome;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "dados", nullable = false, columnDefinition = "bytea")
    private byte[] dados;

    @Column(name = "datacriacao", nullable = false)
    private LocalDateTime dataCriacao;

    public Esboco() {}
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Obra getObra() { return obra; }
    public void setObra(Obra obra) { this.obra = obra; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public byte[] getDados() { return dados; }
    public void setDados(byte[] dados) { this.dados = dados; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime d) { this.dataCriacao = d; }
}
