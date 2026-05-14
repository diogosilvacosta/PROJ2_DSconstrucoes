package org.example.gestao_empreitadas_proj2.services;

import org.example.gestao_empreitadas_proj2.models.ObraMaterial;
import org.example.gestao_empreitadas_proj2.repositories.ObraMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ObraMaterialService {

    @Autowired
    private ObraMaterialRepository obraMaterialRepository;

    // REGRA BLL: Adicionar material a uma obra
    public void adicionarMaterial(ObraMaterial material) {
        if (material.getObraid() == null) {
            System.out.println("[ERRO BLL] Material tem de estar associado a uma obra!");
            return;
        }
        if (material.getDescricao() == null || material.getDescricao().isBlank()) {
            System.out.println("[ERRO BLL] A descrição do material é obrigatória!");
            return;
        }
        if (material.getQuantidade() == null || material.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("[ERRO BLL] A quantidade tem de ser positiva!");
            return;
        }
        if (material.getPrecoUnitario() == null || material.getPrecoUnitario().compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("[ERRO BLL] O preço unitário não pode ser negativo!");
            return;
        }
        if (material.getUnidade() == null || material.getUnidade().isBlank()) {
            material.setUnidade("un");
        }
        obraMaterialRepository.save(material);
        BigDecimal custoTotal = material.getCustoTotal();
        System.out.printf("[BLL] Material '%s' adicionado! Qtd: %s %s × %.2f€ = %.2f€%n",
            material.getDescricao(), material.getQuantidade(), material.getUnidade(),
            material.getPrecoUnitario(), custoTotal);
    }

    // REGRA BLL: Listar materiais de uma obra
    public List<ObraMaterial> listarPorObra(Integer obraId) {
        return obraMaterialRepository.findByObraid_Id(obraId);
    }

    // REGRA BLL: Custo total de materiais de uma obra
    public BigDecimal custoTotalMateriais(Integer obraId) {
        return obraMaterialRepository.totalCustoMateriais(obraId);
    }

    public List<ObraMaterial> listarTodos() {
        return obraMaterialRepository.findAll();
    }
}
