package org.example.gestao_empreitadas_proj2.controllers;

import org.example.gestao_empreitadas_proj2.models.*;
import org.example.gestao_empreitadas_proj2.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired private ObraRepository obraRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private ObraMaterialRepository obraMaterialRepository;
    @Autowired private ObraMaodeobraRepository obraMaodeobraRepository;
    @Autowired private FaturaRepository faturaRepository;

    // ---- PORTAL PÚBLICO: portfólio de obras ----
    @GetMapping("/portfolio")
    public List<Map<String, Object>> portfolio() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Obra obra : obraRepository.findAll()) {
            String estado = obra.getEstado();
            if ("CONCLUIDA".equals(estado) || "EM_PROGRESSO".equals(estado) || "INICIADA".equals(estado)) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("obraid", obra.getId());
                item.put("estado", estado);
                item.put("datainicio", obra.getDatainicio());
                item.put("percentagem", obra.getPercentagemconclusao());
                item.put("cliente", obra.getClienteid() != null ? obra.getClienteid().getNome() : "—");
                result.add(item);
            }
        }
        return result;
    }

    // ---- DETALHE DE OBRA: materiais + mão de obra + extras ----
    @GetMapping("/obra/{id}/detalhe")
    public Map<String, Object> detalheObra(@PathVariable Integer id) {
        Map<String, Object> result = new LinkedHashMap<>();
        Optional<Obra> opt = obraRepository.findById(id);
        if (opt.isEmpty()) return result;

        Obra obra = opt.get();
        result.put("obraid", obra.getId());
        result.put("estado", obra.getEstado());
        result.put("datainicio", obra.getDatainicio());
        result.put("percentagem", obra.getPercentagemconclusao());

        // Materiais
        List<ObraMaterial> materiais = obraMaterialRepository.findByObraid_Id(id);
        BigDecimal custoMat = obraMaterialRepository.totalCustoMateriais(id);
        List<Map<String, Object>> listaMat = new ArrayList<>();
        for (ObraMaterial m : materiais) {
            Map<String, Object> mi = new LinkedHashMap<>();
            mi.put("descricao", m.getDescricao());
            mi.put("unidade", m.getUnidade());
            mi.put("quantidade", m.getQuantidade());
            mi.put("precoUnit", m.getPrecoUnitario());
            mi.put("total", m.getCustoTotal());
            listaMat.add(mi);
        }
        result.put("materiais", listaMat);
        result.put("custoMateriais", custoMat);

        // Mão de obra
        List<ObraMaodeobra> maodeobra = obraMaodeobraRepository.findByObraid_Id(id);
        BigDecimal custoMdo = obraMaodeobraRepository.totalCustoMaodeobra(id);
        List<Map<String, Object>> listaMdo = new ArrayList<>();
        for (ObraMaodeobra m : maodeobra) {
            Map<String, Object> mi = new LinkedHashMap<>();
            mi.put("funcionario", m.getFuncionarioid() != null ? m.getFuncionarioid().getNome() : "N/A");
            mi.put("horas", m.getHoras());
            mi.put("custoHora", m.getCustoHora());
            mi.put("total", m.getCustoTotal());
            listaMdo.add(mi);
        }
        result.put("maodeobra", listaMdo);
        result.put("custoMaodeobra", custoMdo);

        // Orçamento vs custo real
        if (obra.getPropostaid() != null) {
            BigDecimal orcamento = obra.getPropostaid().getValortotal();
            BigDecimal custoTotal = custoMat.add(custoMdo);
            BigDecimal extras = custoTotal.subtract(orcamento).max(BigDecimal.ZERO);
            result.put("orcamento", orcamento);
            result.put("custoTotal", custoTotal);
            result.put("extras", extras);
        }

        return result;
    }

    // ---- OBRAS DE UM CLIENTE ----
    @GetMapping("/cliente/{id}/obras")
    public List<Map<String, Object>> obrasCliente(@PathVariable Integer id) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Obra obra : obraRepository.findAll()) {
            if (obra.getClienteid() != null && obra.getClienteid().getId().equals(id)) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("obraid", obra.getId());
                item.put("estado", obra.getEstado());
                item.put("datainicio", obra.getDatainicio());
                item.put("percentagem", obra.getPercentagemconclusao());
                if (obra.getPropostaid() != null)
                    item.put("orcamento", obra.getPropostaid().getValortotal());
                result.add(item);
            }
        }
        return result;
    }

    // ---- LISTA DE CLIENTES ----
    @GetMapping("/clientes")
    public List<Map<String, Object>> clientes() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Cliente c : clienteRepository.findAll()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId());
            m.put("nome", c.getNome());
            m.put("email", c.getEmail());
            result.add(m);
        }
        return result;
    }

    // ---- FATURAS DE UMA OBRA ----
    @GetMapping("/obra/{id}/faturas")
    public List<Map<String, Object>> faturasObra(@PathVariable Integer id) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Fatura f : faturaRepository.findAll()) {
            if (f.getObraid() != null && f.getObraid().getId().equals(id)) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("faturaid", f.getId());
                m.put("numero", f.getNumerofatura());
                m.put("valor", f.getValor());
                m.put("estado", f.getEstado());
                m.put("dataEmissao", f.getDataemissao());
                m.put("dataVencimento", f.getDatavencimento());
                result.add(m);
            }
        }
        return result;
    }
}
