package org.example.gestao_empreitadas_proj2.domain.obra.service;
import org.example.gestao_empreitadas_proj2.domain.obra.Esboco;
import org.example.gestao_empreitadas_proj2.domain.obra.Obra;
import org.example.gestao_empreitadas_proj2.domain.obra.repository.EsbocoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class EsbocoService {
    @Autowired private EsbocoRepository repo;
    public Esboco guardarEsboco(Obra obra, String nome, byte[] dados) {
        Esboco e = new Esboco();
        e.setObra(obra); e.setNome(nome); e.setDados(dados); e.setDataCriacao(LocalDateTime.now());
        return repo.save(e);
    }
    public List<Esboco> listarPorObra(Integer obraId) { return repo.findByObraIdOrderByDataCriacaoDesc(obraId); }
    public void eliminar(Integer id) { repo.deleteById(id); }
}