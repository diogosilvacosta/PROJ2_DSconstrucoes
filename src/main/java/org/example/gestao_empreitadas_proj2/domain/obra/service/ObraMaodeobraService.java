package org.example.gestao_empreitadas_proj2.domain.obra.service;
import org.example.gestao_empreitadas_proj2.domain.obra.ObraMaodeobra;
import org.example.gestao_empreitadas_proj2.domain.obra.repository.ObraMaodeobraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
@Service
public class ObraMaodeobraService {
    @Autowired private ObraMaodeobraRepository repo;
    public void alocarMaodeobra(ObraMaodeobra mdo) {
        if (mdo.getObraid() == null) { System.out.println("[ERRO BLL] Sem obra!"); return; }
        if (mdo.getFuncionarioid() == null) { System.out.println("[ERRO BLL] Sem funcionario!"); return; }
        if (mdo.getHoras() == null || mdo.getHoras() <= 0) { System.out.println("[ERRO BLL] Horas invalidas!"); return; }
        if (mdo.getHoras() > 744) { System.out.println("[ERRO BLL] Maximo 744h!"); return; }
        if (repo.existsByObraid_IdAndFuncionarioid_Id(mdo.getObraid().getId(), mdo.getFuncionarioid().getId())) {
            System.out.println("[ERRO BLL] Funcionario ja alocado a esta obra!"); return;
        }
        repo.save(mdo);
    }
    public List<ObraMaodeobra> listarPorObra(Integer obraId) { return repo.findByObraid_Id(obraId); }
    public BigDecimal custoTotalMaodeobra(Integer obraId) { return repo.totalCustoMaodeobra(obraId); }
    public List<ObraMaodeobra> listarTodos() { return repo.findAll(); }
}