package org.example.gestao_empreitadas_proj2.api;

import org.example.gestao_empreitadas_proj2.domain.financeiro.Fatura;
import org.example.gestao_empreitadas_proj2.domain.financeiro.repository.FaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/faturas")
@CrossOrigin(origins = "*")
public class FaturaController {

    @Autowired private FaturaRepository faturaRepository;

    @GetMapping("/obra/{id}")
    public List<Map<String, Object>> faturasObra(@PathVariable Integer id) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Fatura f : faturaRepository.findAll()) {
            if (f.getObraid() != null && f.getObraid().getId().equals(id)) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("faturaid", f.getId()); m.put("numero", f.getNumerofatura());
                m.put("valor", f.getValor()); m.put("estado", f.getEstado());
                m.put("dataEmissao", f.getDataemissao()); m.put("dataVencimento", f.getDatavencimento());
                result.add(m);
            }
        }
        return result;
    }
}