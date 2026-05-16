package org.example.gestao_empreitadas_proj2.domain.cliente.service;
import org.example.gestao_empreitadas_proj2.domain.cliente.Cliente;
import org.example.gestao_empreitadas_proj2.domain.cliente.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class ClienteService {
    @Autowired private ClienteRepository repo;
    public void registarCliente(Cliente c) {
        if (c.getNif() == null || c.getNif().length() != 9) { System.out.println("[ERRO BLL] NIF invalido!"); return; }
        if (c.getNome() == null || c.getNome().trim().isEmpty()) { System.out.println("[ERRO BLL] Nome vazio!"); return; }
        repo.save(c);
    }
    public List<Cliente> listarTodos() { return (List<Cliente>) repo.findAll(); }
    public void eliminarCliente(Integer id) { repo.deleteById(id); }
}