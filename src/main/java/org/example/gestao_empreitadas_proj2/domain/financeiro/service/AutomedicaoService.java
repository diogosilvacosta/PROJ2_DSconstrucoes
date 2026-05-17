package org.example.gestao_empreitadas_proj2.domain.financeiro.service;
import org.example.gestao_empreitadas_proj2.domain.financeiro.Automedicao;
import org.example.gestao_empreitadas_proj2.domain.financeiro.repository.AutomedicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
@Service
public class AutomedicaoService {
    @Autowired private AutomedicaoRepository repo;
    public void criarAutomedicao(Automedicao a) {
        if (a.getObraid() == null) { System.out.println("[ERRO BLL] Sem obra!"); return; }
        if (a.getPercentagemtrabalho() == null || a.getPercentagemtrabalho().compareTo(BigDecimal.ZERO) < 0 || a.getPercentagemtrabalho().compareTo(new BigDecimal("100")) > 0) { System.out.println("[ERRO BLL] Percentagem invalida!"); return; }
        if (a.getEstado() == null || a.getEstado().isBlank()) a.setEstado("RASCUNHO");
        repo.save(a);
    }
    public void aprovarAutomedicao(Integer id) {
        Automedicao a = repo.findById(id).orElse(null);
        if (a == null) { System.out.println("[ERRO BLL] Nao encontrada!"); return; }
        if (!"RASCUNHO".equals(a.getEstado())) { System.out.println("[ERRO BLL] So aprova RASCUNHO!"); return; }
        a.setEstado("APROVADO"); repo.save(a);
    }
    public List<Automedicao> listarTodas() { return (List<Automedicao>) repo.findAll(); }
    public void eliminarAutomedicao(Integer id) { repo.deleteById(id); }
}