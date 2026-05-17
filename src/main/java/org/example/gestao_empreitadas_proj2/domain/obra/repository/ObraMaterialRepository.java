package org.example.gestao_empreitadas_proj2.domain.obra.repository;
import org.example.gestao_empreitadas_proj2.domain.obra.ObraMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
public interface ObraMaterialRepository extends JpaRepository<ObraMaterial, Integer> {
    List<ObraMaterial> findByObraid_Id(Integer obraId);
    @Query("SELECT COALESCE(SUM(m.quantidade * m.precoUnitario), 0) FROM ObraMaterial m WHERE m.obraid.id = :obraId")
    BigDecimal totalCustoMateriais(@Param("obraId") Integer obraId);
}