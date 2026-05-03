package org.example.gestao_empreitadas_proj2.services;

import org.example.gestao_empreitadas_proj2.models.MedicaoCampo;
import org.example.gestao_empreitadas_proj2.repositories.MedicaoCampoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicaoCampoService {

    @Autowired
    private MedicaoCampoRepository medicaoCampoRepository;

    public void guardarMedicao(MedicaoCampo medicao) {
        if (medicao.getObraid() == null) {
            throw new IllegalArgumentException("Obra é obrigatória para registar uma medição.");
        }
        if (medicao.getDescricao() == null || medicao.getDescricao().isBlank()) {
            throw new IllegalArgumentException("A descrição é obrigatória.");
        }
        medicaoCampoRepository.save(medicao);
        System.out.println("[BLL] Medição de campo registada para obra #" + medicao.getObraid().getId());
    }

    public List<MedicaoCampo> listarPorObra(Integer obraId) {
        return medicaoCampoRepository.findByObraid_IdOrderByDataRegistoDesc(obraId);
    }

    public void eliminar(Integer id) {
        medicaoCampoRepository.deleteById(id);
    }
}
