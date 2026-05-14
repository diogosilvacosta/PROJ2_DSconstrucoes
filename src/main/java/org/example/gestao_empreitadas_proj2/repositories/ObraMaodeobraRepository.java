package org.example.gestao_empreitadas_proj2.repositories;

import org.example.gestao_empreitadas_proj2.models.ObraMaodeobra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ObraMaodeobraRepository extends JpaRepository<ObraMaodeobra, Integer> {
    List<ObraMaodeobra> findByObraid_Id(Integer obraId);

    boolean existsByObraid_IdAndFuncionarioid_Id(Integer obraId, Integer funcionarioId);

    @Query("SELECT COALESCE(SUM(m.horas * m.custoHora), 0) FROM ObraMaodeobra m WHERE m.obraid.id = :obraId")
    BigDecimal totalCustoMaodeobra(@Param("obraId") Integer obraId);
}
