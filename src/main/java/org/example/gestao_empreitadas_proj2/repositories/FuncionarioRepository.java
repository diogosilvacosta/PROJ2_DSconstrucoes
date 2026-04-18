package org.example.gestao_empreitadas_proj2.repositories;

import org.example.gestao_empreitadas_proj2.models.Funcionario;
import org.springframework.data.repository.CrudRepository;

public interface FuncionarioRepository extends CrudRepository<Funcionario, Integer> {
}