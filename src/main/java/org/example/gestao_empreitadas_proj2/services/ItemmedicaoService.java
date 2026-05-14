package org.example.gestao_empreitadas_proj2.services;

import org.example.gestao_empreitadas_proj2.models.Itemmedicao;
import org.example.gestao_empreitadas_proj2.repositories.ItemmedicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ItemmedicaoService {

    @Autowired
    private ItemmedicaoRepository itemmedicaoRepository;

    // REGRA BLL: Adicionar item de medição a uma proposta
    public void adicionarItem(Itemmedicao item) {
        // Regra 1: Tem de estar associado a uma proposta
        if (item.getPropostaid() == null) {
            System.out.println("[ERRO BLL] O item de medição tem de estar associado a uma proposta!");
            return;
        }
        // Regra 2: Descrição obrigatória
        if (item.getDescricao() == null || item.getDescricao().isBlank()) {
            System.out.println("[ERRO BLL] A descrição do item é obrigatória!");
            return;
        }
        // Regra 3: Quantidade tem de ser positiva
        if (item.getQuantidade() == null || item.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("[ERRO BLL] A quantidade tem de ser positiva!");
            return;
        }
        // Regra 4: Valor unitário tem de ser positivo
        if (item.getValorunitario() == null || item.getValorunitario().compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("[ERRO BLL] O valor unitário tem de ser positivo!");
            return;
        }
        itemmedicaoRepository.save(item);
        BigDecimal total = item.getQuantidade().multiply(item.getValorunitario());
        System.out.println("[BLL] Item '" + item.getDescricao() + "' adicionado! Valor total: " + total + "€");
    }

    // Listar todos os itens de medição
    public List<Itemmedicao> listarTodos() {
        return (List<Itemmedicao>) itemmedicaoRepository.findAll();
    }
}
