package org.example.gestao_empreitadas_proj2.services;

import org.example.gestao_empreitadas_proj2.models.Esboco;
import org.example.gestao_empreitadas_proj2.models.Obra;
import org.example.gestao_empreitadas_proj2.repositories.EsbocoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EsbocoService {

    @Autowired
    private EsbocoRepository esbocoRepository;

    public Esboco guardarEsboco(Obra obra, String nome, byte[] dados) {
        Esboco e = new Esboco();
        e.setObra(obra);
        e.setNome(nome);
        e.setDados(dados);
        e.setDataCriacao(LocalDateTime.now());
        return esbocoRepository.save(e);
    }

    public List<Esboco> listarPorObra(Integer obraId) {
        return esbocoRepository.findByObraIdOrderByDataCriacaoDesc(obraId);
    }

    public void eliminar(Integer id) {
        esbocoRepository.deleteById(id);
    }
}
