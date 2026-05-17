package org.example.gestao_empreitadas_proj2.api;

import org.example.gestao_empreitadas_proj2.domain.obra.Obra;
import org.example.gestao_empreitadas_proj2.domain.obra.repository.ObraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PortfolioController {

    @Autowired private ObraRepository obraRepository;

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
                item.put("cliente", obra.getClienteid() != null ? obra.getClienteid().getNome() : "");
                result.add(item);
            }
        }
        return result;
    }
}