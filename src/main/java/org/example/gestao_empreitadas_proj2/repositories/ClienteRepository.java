package org.example.gestao_empreitadas_proj2.repositories;

import org.example.gestao_empreitadas_proj2.models.Cliente;
import org.springframework.data.repository.CrudRepository;

public interface ClienteRepository extends CrudRepository<Cliente, Integer> {
}