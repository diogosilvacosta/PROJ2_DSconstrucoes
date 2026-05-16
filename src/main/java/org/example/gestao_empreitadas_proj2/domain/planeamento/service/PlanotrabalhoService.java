package org.example.gestao_empreitadas_proj2.domain.planeamento.service;
import org.example.gestao_empreitadas_proj2.domain.planeamento.Planotrabalho;
import org.example.gestao_empreitadas_proj2.domain.planeamento.repository.PlanotrabalhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class PlanotrabalhoService {
    @Autowired private PlanotrabalhoRepository repo;
    public void criarFase(Planotrabalho f) {
        if (f.getObraid() == null) { System.out.println("[ERRO BLL] Sem obra!"); return; }
        if (f.getDescricao() == null || f.getDescricao().isBlank()) { System.out.println("[ERRO BLL] Descricao obrigatoria!"); return; }
        if (f.getDuracao() == null || f.getDuracao() <= 0) { System.out.println("[ERRO BLL] Duracao invalida!"); return; }
        if (f.getEstado() == null || f.getEstado().isBlank()) f.setEstado("PLANEJADA");
        repo.save(f);
    }
    public void iniciarFase(Integer id) {
        Planotrabalho f = repo.findById(id).orElse(null);
        if (f == null) { System.out.println("[ERRO BLL] Fase nao encontrada!"); return; }
        if (!"PLANEJADA".equals(f.getEstado())) { System.out.println("[ERRO BLL] So inicia PLANEJADA!"); return; }
        f.setEstado("EM_EXECUCAO"); repo.save(f);
    }
    public List<Planotrabalho> listarTodas() { return (List<Planotrabalho>) repo.findAll(); }
}