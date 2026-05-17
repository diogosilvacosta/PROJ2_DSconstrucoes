package org.example.gestao_empreitadas_proj2.domain.recursos.service;
import org.example.gestao_empreitadas_proj2.domain.recursos.Obarecurso;
import org.example.gestao_empreitadas_proj2.domain.recursos.repository.ObarecursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class ObarecursoService {
    @Autowired private ObarecursoRepository repo;
    public void alocarRecurso(Obarecurso r) {
        if (r.getObraid() == null) { System.out.println("[ERRO BLL] Sem obra!"); return; }
        if (r.getFuncionarioid() == null) { System.out.println("[ERRO BLL] Sem funcionario!"); return; }
        if (r.getHorasalocadas() == null || r.getHorasalocadas() <= 0) { System.out.println("[ERRO BLL] Horas invalidas!"); return; }
        if (r.getHorasalocadas() > 200) { System.out.println("[ERRO BLL] Maximo 200h!"); return; }
        repo.save(r);
    }
    public List<Obarecurso> listarTodos() { return (List<Obarecurso>) repo.findAll(); }
}