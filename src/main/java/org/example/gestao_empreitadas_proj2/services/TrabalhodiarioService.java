package org.example.gestao_empreitadas_proj2.services;

import org.example.gestao_empreitadas_proj2.models.Trabalhodiario;
import org.example.gestao_empreitadas_proj2.repositories.TrabalhodiarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TrabalhodiarioService {

    @Autowired
    private TrabalhodiarioRepository trabalhodiarioRepository;

    // REGRA BLL: Registar trabalho diário com validações
    public void registarTrabalho(Trabalhodiario trabalho) {
        // Regra 1: Tem de estar associado a uma obra
        if (trabalho.getObraid() == null) {
            System.out.println("[ERRO BLL] O trabalho diário tem de estar associado a uma obra!");
            return;
        }
        // Regra 2: Horas trabalhadas têm de ser positivas e no máximo 24h
        if (trabalho.getHorastrabalhadas() == null || trabalho.getHorastrabalhadas() <= 0) {
            System.out.println("[ERRO BLL] As horas trabalhadas têm de ser um valor positivo!");
            return;
        }
        if (trabalho.getHorastrabalhadas() > 24) {
            System.out.println("[ERRO BLL] As horas trabalhadas não podem ultrapassar 24 horas por dia!");
            return;
        }
        // Regra 3: Percentagem realizada tem de ser entre 0 e 100
        if (trabalho.getPercentagemrealizado() == null ||
            trabalho.getPercentagemrealizado().compareTo(BigDecimal.ZERO) < 0 ||
            trabalho.getPercentagemrealizado().compareTo(new BigDecimal("100")) > 0) {
            System.out.println("[ERRO BLL] A percentagem realizada tem de ser entre 0 e 100!");
            return;
        }
        // Regra 4: Data obrigatória
        if (trabalho.getData() == null) {
            System.out.println("[ERRO BLL] A data do trabalho é obrigatória!");
            return;
        }
        trabalhodiarioRepository.save(trabalho);
        System.out.println("[BLL] Trabalho diário registado com sucesso! Horas: " + trabalho.getHorastrabalhadas() + "h, Progresso: " + trabalho.getPercentagemrealizado() + "%");
    }

    // Listar todos os registos de trabalho diário
    public List<Trabalhodiario> listarTodos() {
        return (List<Trabalhodiario>) trabalhodiarioRepository.findAll();
    }
}
