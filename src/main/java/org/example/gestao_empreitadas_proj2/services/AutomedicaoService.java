package org.example.gestao_empreitadas_proj2.services;

import org.example.gestao_empreitadas_proj2.models.Automedicao;
import org.example.gestao_empreitadas_proj2.repositories.AutomedicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AutomedicaoService {

    @Autowired
    private AutomedicaoRepository automedicaoRepository;

    // REGRA BLL: Criar auto-medição com validações
    public void criarAutomedicao(Automedicao automedicao) {
        // Regra 1: Tem de estar associada a uma obra
        if (automedicao.getObraid() == null) {
            System.out.println("[ERRO BLL] A auto-medição tem de estar associada a uma obra!");
            return;
        }
        // Regra 2: Percentagem tem de ser entre 0 e 100
        if (automedicao.getPercentagemtrabalho() == null ||
            automedicao.getPercentagemtrabalho().compareTo(BigDecimal.ZERO) < 0 ||
            automedicao.getPercentagemtrabalho().compareTo(new BigDecimal("100")) > 0) {
            System.out.println("[ERRO BLL] A percentagem de trabalho tem de ser entre 0 e 100!");
            return;
        }
        // Regra 3: Estado por omissão é RASCUNHO
        if (automedicao.getEstado() == null || automedicao.getEstado().isBlank()) {
            automedicao.setEstado("RASCUNHO");
        }
        automedicaoRepository.save(automedicao);
        System.out.println("[BLL] Auto-medição criada com sucesso! Estado: " + automedicao.getEstado());
    }

    // REGRA BLL: Aprovar auto-medição
    public void aprovarAutomedicao(Integer id) {
        Automedicao am = automedicaoRepository.findById(id).orElse(null);
        if (am == null) {
            System.out.println("[ERRO BLL] Auto-medição com ID " + id + " não encontrada!");
            return;
        }
        if (!"RASCUNHO".equals(am.getEstado())) {
            System.out.println("[ERRO BLL] Só é possível aprovar auto-medições em estado RASCUNHO!");
            return;
        }
        am.setEstado("APROVADO");
        automedicaoRepository.save(am);
        System.out.println("[BLL] Auto-medição ID " + id + " aprovada com sucesso!");
    }

    // Eliminar auto-medição
    public void eliminarAutomedicao(Integer id) {
        automedicaoRepository.deleteById(id);
    }

    // Listar todas as auto-medições
    public List<Automedicao> listarTodas() {
        return (List<Automedicao>) automedicaoRepository.findAll();
    }
}
