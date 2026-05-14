package org.example.gestao_empreitadas_proj2.services;

import org.example.gestao_empreitadas_proj2.models.Cliente;
import org.example.gestao_empreitadas_proj2.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service // Isto avisa o Spring que esta é a tua camada BLL
public class ClienteService {

    @Autowired // Isto liga a BLL ao Repository (DAL) automaticamente
    private ClienteRepository clienteRepository;

    // MÉTODO PARA REGISTAR COM REGRAS (BLL)
    public void registarCliente(Cliente cliente) {
        // Regra 1: O NIF tem de ter exatamente 9 dígitos
        if (cliente.getNif() == null || cliente.getNif().length() != 9) {
            System.out.println("[ERRO BLL] O NIF '" + cliente.getNif() + "' é inválido!");
            return;
        }

        // Regra 2: O nome não pode estar vazio
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            System.out.println("[ERRO BLL] O nome do cliente não pode estar vazio!");
            return;
        }

        // Se passar as regras, o Repository guarda no Postgres
        clienteRepository.save(cliente);
        System.out.println("[BLL] Cliente '" + cliente.getNome() + "' guardado com sucesso!");
    }

    // MÉTODO PARA LISTAR (Exemplo de utilização)
    public List<Cliente> listarTodos() {
        return (List<Cliente>) clienteRepository.findAll();
    }

    public void eliminarCliente(Integer id) {
        clienteRepository.deleteById(id);
    }
}