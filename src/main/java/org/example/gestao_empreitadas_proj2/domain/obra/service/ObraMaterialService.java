package org.example.gestao_empreitadas_proj2.domain.obra.service;
import org.example.gestao_empreitadas_proj2.domain.obra.ObraMaterial;
import org.example.gestao_empreitadas_proj2.domain.obra.repository.ObraMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
@Service
public class ObraMaterialService {
    @Autowired private ObraMaterialRepository repo;
    public void adicionarMaterial(ObraMaterial m) {
        if (m.getObraid() == null) { System.out.println("[ERRO BLL] Material sem obra!"); return; }
        if (m.getDescricao() == null || m.getDescricao().isBlank()) { System.out.println("[ERRO BLL] Descricao obrigatoria!"); return; }
        if (m.getQuantidade() == null || m.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) { System.out.println("[ERRO BLL] Quantidade invalida!"); return; }
        if (m.getPrecoUnitario() == null || m.getPrecoUnitario().compareTo(BigDecimal.ZERO) < 0) { System.out.println("[ERRO BLL] Preco invalido!"); return; }
        if (m.getUnidade() == null || m.getUnidade().isBlank()) m.setUnidade("un");
        repo.save(m);
    }
    public List<ObraMaterial> listarPorObra(Integer obraId) { return repo.findByObraid_Id(obraId); }
    public BigDecimal custoTotalMateriais(Integer obraId) { return repo.totalCustoMateriais(obraId); }
    public List<ObraMaterial> listarTodos() { return repo.findAll(); }
}