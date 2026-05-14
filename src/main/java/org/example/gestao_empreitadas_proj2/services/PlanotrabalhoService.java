package org.example.gestao_empreitadas_proj2.services;

import org.example.gestao_empreitadas_proj2.models.Planotrabalho;
import org.example.gestao_empreitadas_proj2.repositories.PlanotrabalhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanotrabalhoService {

    @Autowired
    private PlanotrabalhoRepository planotrabalhoRepository;

    // REGRA BLL: Criar fase do plano de trabalhos com validações
    public void criarFase(Planotrabalho fase) {
        // Regra 1: Tem de estar associada a uma obra
        if (fase.getObraid() == null) {
            System.out.println("[ERRO BLL] A fase do plano tem de estar associada a uma obra!");
            return;
        }
        // Regra 2: Descrição obrigatória
        if (fase.getDescricao() == null || fase.getDescricao().isBlank()) {
            System.out.println("[ERRO BLL] A descrição da fase é obrigatória!");
            return;
        }
        // Regra 3: Duração tem de ser positiva
        if (fase.getDuracao() == null || fase.getDuracao() <= 0) {
            System.out.println("[ERRO BLL] A duração da fase tem de ser positiva (em dias)!");
            return;
        }
        // Estado por omissão
        if (fase.getEstado() == null || fase.getEstado().isBlank()) {
            fase.setEstado("PLANEJADA");
        }
        planotrabalhoRepository.save(fase);
        System.out.println("[BLL] Fase '" + fase.getDescricao() + "' adicionada ao plano de trabalhos! Duração: " + fase.getDuracao() + " dias.");
    }

    // REGRA BLL: Iniciar uma fase
    public void iniciarFase(Integer id) {
        Planotrabalho fase = planotrabalhoRepository.findById(id).orElse(null);
        if (fase == null) {
            System.out.println("[ERRO BLL] Fase com ID " + id + " não encontrada!");
            return;
        }
        if (!"PLANEJADA".equals(fase.getEstado())) {
            System.out.println("[ERRO BLL] Só é possível iniciar fases no estado PLANEJADA!");
            return;
        }
        fase.setEstado("EM_EXECUCAO");
        planotrabalhoRepository.save(fase);
        System.out.println("[BLL] Fase '" + fase.getDescricao() + "' iniciada com sucesso!");
    }

    // Listar todas as fases
    public List<Planotrabalho> listarTodas() {
        return (List<Planotrabalho>) planotrabalhoRepository.findAll();
    }
}
