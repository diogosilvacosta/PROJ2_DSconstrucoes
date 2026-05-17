package org.example.gestao_empreitadas_proj2.domain.financeiro.service;
import org.example.gestao_empreitadas_proj2.domain.financeiro.Itemmedicao;
import org.example.gestao_empreitadas_proj2.domain.financeiro.repository.ItemmedicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
@Service
public class ItemmedicaoService {
    @Autowired private ItemmedicaoRepository repo;
    public void adicionarItem(Itemmedicao item) {
        if (item.getPropostaid() == null) { System.out.println("[ERRO BLL] Sem proposta!"); return; }
        if (item.getDescricao() == null || item.getDescricao().isBlank()) { System.out.println("[ERRO BLL] Descricao obrigatoria!"); return; }
        if (item.getQuantidade() == null || item.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) { System.out.println("[ERRO BLL] Quantidade invalida!"); return; }
        if (item.getValorunitario() == null || item.getValorunitario().compareTo(BigDecimal.ZERO) <= 0) { System.out.println("[ERRO BLL] Valor invalido!"); return; }
        repo.save(item);
    }
    public List<Itemmedicao> listarTodos() { return (List<Itemmedicao>) repo.findAll(); }
}