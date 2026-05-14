package org.example.gestao_empreitadas_proj2.services;

import org.example.gestao_empreitadas_proj2.models.Obarecurso;
import org.example.gestao_empreitadas_proj2.repositories.ObarecursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObarecursoService {

    @Autowired
    private ObarecursoRepository obarecursoRepository;

    // REGRA BLL: Alocar funcionário a uma obra
    public void alocarRecurso(Obarecurso recurso) {
        // Regra 1: Obra obrigatória
        if (recurso.getObraid() == null) {
            System.out.println("[ERRO BLL] O recurso tem de estar associado a uma obra!");
            return;
        }
        // Regra 2: Funcionário obrigatório
        if (recurso.getFuncionarioid() == null) {
            System.out.println("[ERRO BLL] O recurso tem de estar associado a um funcionário!");
            return;
        }
        // Regra 3: Horas alocadas têm de ser positivas
        if (recurso.getHorasalocadas() == null || recurso.getHorasalocadas() <= 0) {
            System.out.println("[ERRO BLL] As horas alocadas têm de ser um valor positivo!");
            return;
        }
        // Regra 4: Máximo de 200 horas alocadas por alocação
        if (recurso.getHorasalocadas() > 200) {
            System.out.println("[ERRO BLL] Não é possível alocar mais de 200 horas por registo!");
            return;
        }
        obarecursoRepository.save(recurso);
        System.out.println("[BLL] Funcionário alocado com sucesso! Horas: " + recurso.getHorasalocadas() + "h");
    }

    // Listar todos os recursos alocados
    public List<Obarecurso> listarTodos() {
        return (List<Obarecurso>) obarecursoRepository.findAll();
    }
}
