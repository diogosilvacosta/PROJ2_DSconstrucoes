package org.example.gestao_empreitadas_proj2.api;

import org.example.gestao_empreitadas_proj2.domain.obra.Obra;
import org.example.gestao_empreitadas_proj2.domain.obra.ObraMaterial;
import org.example.gestao_empreitadas_proj2.domain.obra.ObraMaodeobra;
import org.example.gestao_empreitadas_proj2.domain.obra.repository.ObraRepository;
import org.example.gestao_empreitadas_proj2.domain.obra.repository.ObraMaterialRepository;
import org.example.gestao_empreitadas_proj2.domain.obra.repository.ObraMaodeobraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/obra")
@CrossOrigin(origins = "*")
public class ObraController {

    @Autowired private ObraRepository obraRepository;
    @Autowired private ObraMaterialRepository obraMaterialRepository;
    @Autowired private ObraMaodeobraRepository obraMaodeobraRepository;

    @GetMapping("/{id}/detalhe")
    public Map<String, Object> detalheObra(@PathVariable Integer id) {
        Map<String, Object> result = new LinkedHashMap<>();
        Optional<Obra> opt = obraRepository.findById(id);
        if (opt.isEmpty()) return result;
        Obra obra = opt.get();
        result.put("obraid", obra.getId());
        result.put("estado", obra.getEstado());
        result.put("datainicio", obra.getDatainicio());
        result.put("percentagem", obra.getPercentagemconclusao());

        List<ObraMaterial> materiais = obraMaterialRepository.findByObraid_Id(id);
        BigDecimal custoMat = obraMaterialRepository.totalCustoMateriais(id);
        List<Map<String, Object>> listaMat = new ArrayList<>();
        for (ObraMaterial m : materiais) {
            Map<String, Object> mi = new LinkedHashMap<>();
            mi.put("descricao", m.getDescricao()); mi.put("unidade", m.getUnidade());
            mi.put("quantidade", m.getQuantidade()); mi.put("precoUnit", m.getPrecoUnitario());
            mi.put("total", m.getCustoTotal()); listaMat.add(mi);
        }
        result.put("materiais", listaMat);
        result.put("custoMateriais", custoMat);

        List<ObraMaodeobra> maodeobra = obraMaodeobraRepository.findByObraid_Id(id);
        BigDecimal custoMdo = obraMaodeobraRepository.totalCustoMaodeobra(id);
        List<Map<String, Object>> listaMdo = new ArrayList<>();
        for (ObraMaodeobra m : maodeobra) {
            Map<String, Object> mi = new LinkedHashMap<>();
            mi.put("funcionario", m.getFuncionarioid() != null ? m.getFuncionarioid().getNome() : "N/A");
            mi.put("horas", m.getHoras()); mi.put("custoHora", m.getCustoHora());
            mi.put("total", m.getCustoTotal()); listaMdo.add(mi);
        }
        result.put("maodeobra", listaMdo);
        result.put("custoMaodeobra", custoMdo);

        if (obra.getPropostaid() != null) {
            BigDecimal orcamento = obra.getPropostaid().getValortotal();
            BigDecimal custoTotal = custoMat.add(custoMdo);
            result.put("orcamento", orcamento);
            result.put("custoTotal", custoTotal);
            result.put("extras", custoTotal.subtract(orcamento).max(BigDecimal.ZERO));
        }
        return result;
    }

    @GetMapping("/{id}/faturas")
    public List<Map<String, Object>> faturasObra(@PathVariable Integer id) {
        return List.of();
    }
}