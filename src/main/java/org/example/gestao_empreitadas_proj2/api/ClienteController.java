package org.example.gestao_empreitadas_proj2.api;

import org.example.gestao_empreitadas_proj2.domain.cliente.Cliente;
import org.example.gestao_empreitadas_proj2.domain.cliente.repository.ClienteRepository;
import org.example.gestao_empreitadas_proj2.domain.obra.Obra;
import org.example.gestao_empreitadas_proj2.domain.obra.repository.ObraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired private ClienteRepository clienteRepository;
    @Autowired private ObraRepository obraRepository;

    @GetMapping("/clientes")
    public List<Map<String, Object>> clientes() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Cliente c : clienteRepository.findAll()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", c.getId()); m.put("nome", c.getNome()); m.put("email", c.getEmail());
            result.add(m);
        }
        return result;
    }

    @GetMapping("/cliente/{id}/obras")
    public List<Map<String, Object>> obrasCliente(@PathVariable Integer id) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Obra obra : obraRepository.findAll()) {
            if (obra.getClienteid() != null && obra.getClienteid().getId().equals(id)) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("obraid", obra.getId()); item.put("estado", obra.getEstado());
                item.put("datainicio", obra.getDatainicio()); item.put("percentagem", obra.getPercentagemconclusao());
                if (obra.getPropostaid() != null) item.put("orcamento", obra.getPropostaid().getValortotal());
                result.add(item);
            }
        }
        return result;
    }
}