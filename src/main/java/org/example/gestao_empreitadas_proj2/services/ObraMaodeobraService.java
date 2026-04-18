package org.example.gestao_empreitadas_proj2.services;

import org.example.gestao_empreitadas_proj2.models.ObraMaodeobra;
import org.example.gestao_empreitadas_proj2.repositories.ObraMaodeobraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ObraMaodeobraService {

    @Autowired
    private ObraMaodeobraRepository obraMaodeobraRepository;

    // REGRA BLL: Alocar funcionário (mão de obra) a uma obra
    public void alocarMaodeobra(ObraMaodeobra mdo) {
        if (mdo.getObraid() == null) {
            System.out.println("[ERRO BLL] Mão de obra tem de estar associada a uma obra!");
            return;
        }
        if (mdo.getFuncionarioid() == null) {
            System.out.println("[ERRO BLL] É obrigatório indicar o funcionário!");
            return;
        }
        if (mdo.getHoras() == null || mdo.getHoras() <= 0) {
            System.out.println("[ERRO BLL] As horas têm de ser positivas!");
            return;
        }
        if (mdo.getHoras() > 744) {
            System.out.println("[ERRO BLL] Máximo de 744 horas por registo (31 dias × 24h)!");
            return;
        }
        if (mdo.getCustoHora() == null || mdo.getCustoHora().compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("[ERRO BLL] O custo por hora não pode ser negativo!");
            return;
        }
        // REGRA BLL: Não duplicar funcionário na mesma obra
        if (obraMaodeobraRepository.existsByObraid_IdAndFuncionarioid_Id(
                mdo.getObraid().getId(), mdo.getFuncionarioid().getId())) {
            System.out.println("[ERRO BLL] Este funcionário já está alocado a esta obra!");
            return;
        }
        obraMaodeobraRepository.save(mdo);
        System.out.printf("[BLL] Funcionário '%s' alocado! %dh × %.2f€/h = %.2f€%n",
            mdo.getFuncionarioid().getNome(), mdo.getHoras(),
            mdo.getCustoHora(), mdo.getCustoTotal());
    }

    // REGRA BLL: Listar mão de obra de uma obra
    public List<ObraMaodeobra> listarPorObra(Integer obraId) {
        return obraMaodeobraRepository.findByObraid_Id(obraId);
    }

    // REGRA BLL: Custo total de mão de obra de uma obra
    public BigDecimal custoTotalMaodeobra(Integer obraId) {
        return obraMaodeobraRepository.totalCustoMaodeobra(obraId);
    }

    public List<ObraMaodeobra> listarTodos() {
        return obraMaodeobraRepository.findAll();
    }
}
