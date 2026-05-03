package org.example.gestao_empreitadas_proj2.repositories;

import org.example.gestao_empreitadas_proj2.models.MedicaoCampo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicaoCampoRepository extends JpaRepository<MedicaoCampo, Integer> {
    List<MedicaoCampo> findByObraid_IdOrderByDataRegistoDesc(Integer obraId);
}
