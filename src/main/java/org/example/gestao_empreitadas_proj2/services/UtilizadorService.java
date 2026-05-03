package org.example.gestao_empreitadas_proj2.services;

import org.example.gestao_empreitadas_proj2.models.Utilizador;
import org.example.gestao_empreitadas_proj2.repositories.UtilizadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class UtilizadorService {

    @Autowired
    private UtilizadorRepository utilizadorRepository;

    /** Cria um utilizador admin padrão se não existir nenhum na BD. */
    public void garantirAdminPadrao() {
        if (utilizadorRepository.count() == 0) {
            Utilizador admin = new Utilizador();
            admin.setUsername("admin");
            admin.setPassword(hashSHA256("admin123"));
            admin.setNome("Administrador");
            admin.setRole("ADMIN");
            utilizadorRepository.save(admin);
            System.out.println("[INFO] Utilizador admin criado. Username: admin | Password: admin123");
        }
    }

    /** Verifica credenciais. Devolve o Utilizador se correto, Optional.empty() se falhar. */
    public Optional<Utilizador> autenticar(String username, String password) {
        Optional<Utilizador> opt = utilizadorRepository.findByUsername(username);
        if (opt.isEmpty()) return Optional.empty();
        Utilizador u = opt.get();
        if (u.getPassword().equals(hashSHA256(password))) {
            return Optional.of(u);
        }
        return Optional.empty();
    }

    public void criarUtilizador(String username, String password, String nome, String role) {
        if (utilizadorRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username '" + username + "' já existe.");
        }
        Utilizador u = new Utilizador();
        u.setUsername(username);
        u.setPassword(hashSHA256(password));
        u.setNome(nome);
        u.setRole(role);
        utilizadorRepository.save(u);
    }

    private String hashSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes());
            return HexFormat.of().formatHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao calcular hash", e);
        }
    }
}
