package org.example.gestao_empreitadas_proj2.domain.obra.service;
import org.example.gestao_empreitadas_proj2.domain.obra.Obra;
import org.example.gestao_empreitadas_proj2.domain.obra.repository.ObraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class ObraService {
    @Autowired private ObraRepository obraRepository;
    public void criarObra(Obra obra) {
        if (obra.getClienteid() == null) { System.out.println("[ERRO BLL] A obra tem de ter um cliente!"); return; }
        if (obra.getDatainicio() == null) { System.out.println("[ERRO BLL] A data de inicio e obrigatoria!"); return; }
        if (obra.getEstado() == null) obra.setEstado("INICIADA");
        obraRepository.save(obra);
        System.out.println("[BLL] Obra criada com sucesso!");
    }
    public List<Obra> listarTodas() { return (List<Obra>) obraRepository.findAll(); }
    public Obra buscarPorId(Integer id) { return obraRepository.findById(id).orElse(null); }
    public void atualizarObra(Obra obra) { if (obra.getId() != null) obraRepository.save(obra); }
    public void eliminarObra(Integer id) { obraRepository.deleteById(id); }
}