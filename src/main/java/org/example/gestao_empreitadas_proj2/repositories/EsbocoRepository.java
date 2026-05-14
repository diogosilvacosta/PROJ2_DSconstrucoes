package org.example.gestao_empreitadas_proj2.repositories;

import org.example.gestao_empreitadas_proj2.models.Esboco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EsbocoRepository extends JpaRepository<Esboco, Integer> {

    @Query("SELECT e FROM Esboco e WHERE e.obra.id = :obraId ORDER BY e.dataCriacao DESC")
    List<Esboco> findByObraIdOrderByDataCriacaoDesc(@Param("obraId") Integer obraId);
}
