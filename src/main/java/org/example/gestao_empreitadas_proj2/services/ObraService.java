package org.example.gestao_empreitadas_proj2.services;

import org.example.gestao_empreitadas_proj2.models.Obra;
import org.example.gestao_empreitadas_proj2.repositories.ObraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ObraService {

    @Autowired
    private ObraRepository obraRepository;

    public void criarObra(Obra obra) {
        if (obra.getClienteid() == null) {
            System.out.println("[BLL ERRO] Não é possível criar obra sem cliente!");
            return;
        }
        obraRepository.save(obra);
        System.out.println("[BLL] Sucesso: Obra registada e relacionada com o Cliente!");
    }

    public List<Obra> listarTodas() {
        return (List<Obra>) obraRepository.findAll();
    }
}
