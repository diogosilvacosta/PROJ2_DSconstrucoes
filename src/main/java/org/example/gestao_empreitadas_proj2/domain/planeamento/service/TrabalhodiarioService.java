package org.example.gestao_empreitadas_proj2.domain.planeamento.service;
import org.example.gestao_empreitadas_proj2.domain.planeamento.Trabalhodiario;
import org.example.gestao_empreitadas_proj2.domain.planeamento.repository.TrabalhodiarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
@Service
public class TrabalhodiarioService {
    @Autowired private TrabalhodiarioRepository repo;
    public void registarTrabalho(Trabalhodiario t) {
        if (t.getObraid() == null) { System.out.println("[ERRO BLL] Sem obra!"); return; }
        if (t.getHorastrabalhadas() == null || t.getHorastrabalhadas() <= 0) { System.out.println("[ERRO BLL] Horas invalidas!"); return; }
        if (t.getHorastrabalhadas() > 24) { System.out.println("[ERRO BLL] Max 24h/dia!"); return; }
        if (t.getPercentagemrealizado() == null || t.getPercentagemrealizado().compareTo(BigDecimal.ZERO) < 0 || t.getPercentagemrealizado().compareTo(new BigDecimal("100")) > 0) { System.out.println("[ERRO BLL] Percentagem invalida!"); return; }
        if (t.getData() == null) { System.out.println("[ERRO BLL] Data obrigatoria!"); return; }
        repo.save(t);
    }
    public List<Trabalhodiario> listarTodos() { return (List<Trabalhodiario>) repo.findAll(); }
}