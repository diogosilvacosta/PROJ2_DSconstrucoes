package org.example.gestao_empreitadas_proj2.services;

import org.example.gestao_empreitadas_proj2.models.Funcionario;
import org.example.gestao_empreitadas_proj2.repositories.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FuncionarioService {
    @Autowired
    private FuncionarioRepository repository;

    public void registarFuncionario(Funcionario f) {
        if (f.getNome() == null || f.getNome().length() < 3) return;
        repository.save(f);
        System.out.println("[BLL] Funcionário registado: " + f.getNome());
    }

    public List<Funcionario> listarTodos() {
        return (List<Funcionario>) repository.findAll();
    }
}
