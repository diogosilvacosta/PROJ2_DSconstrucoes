package org.example.gestao_empreitadas_proj2.services;

import org.example.gestao_empreitadas_proj2.models.Proposta;
import org.example.gestao_empreitadas_proj2.repositories.PropostaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class PropostaService {
    @Autowired
    private PropostaRepository repository;

    public void criarProposta(Proposta p) {
        if (p.getValortotal() == null || p.getValortotal().compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("[BLL ERRO] Proposta com valor inválido.");
            return;
        }
        repository.save(p);
        System.out.println("[BLL] Proposta guardada com sucesso.");
    }

    public List<Proposta> listarTodas() {
        return (List<Proposta>) repository.findAll();
    }

    public void eliminarProposta(Integer id) {
        repository.deleteById(id);
    }

    public void atualizarProposta(Proposta p) {
        if (p.getId() == null) return;
        repository.save(p);
    }
}
