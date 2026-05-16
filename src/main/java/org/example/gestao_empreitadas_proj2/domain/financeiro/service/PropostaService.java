package org.example.gestao_empreitadas_proj2.domain.financeiro.service;
import org.example.gestao_empreitadas_proj2.domain.financeiro.Proposta;
import org.example.gestao_empreitadas_proj2.domain.financeiro.repository.PropostaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
@Service
public class PropostaService {
    @Autowired private PropostaRepository repo;
    public void criarProposta(Proposta p) {
        if (p.getValortotal() == null || p.getValortotal().compareTo(BigDecimal.ZERO) <= 0) { System.out.println("[ERRO BLL] Valor invalido!"); return; }
        repo.save(p);
    }
    public List<Proposta> listarTodas() { return (List<Proposta>) repo.findAll(); }
    public void eliminarProposta(Integer id) { repo.deleteById(id); }
    public void atualizarProposta(Proposta p) { if (p.getId() != null) repo.save(p); }
}