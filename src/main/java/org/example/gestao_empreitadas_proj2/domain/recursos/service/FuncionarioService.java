package org.example.gestao_empreitadas_proj2.domain.recursos.service;
import org.example.gestao_empreitadas_proj2.domain.recursos.Funcionario;
import org.example.gestao_empreitadas_proj2.domain.recursos.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class FuncionarioService {
    @Autowired private FuncionarioRepository repo;
    public void registarFuncionario(Funcionario f) {
        if (f.getNome() == null || f.getNome().length() < 3) { System.out.println("[ERRO BLL] Nome invalido!"); return; }
        repo.save(f);
    }
    public List<Funcionario> listarTodos() { return (List<Funcionario>) repo.findAll(); }
    public void eliminarFuncionario(Integer id) { repo.deleteById(id); }
    public void atualizarFuncionario(Funcionario f) { if (f.getId() != null) repo.save(f); }
}