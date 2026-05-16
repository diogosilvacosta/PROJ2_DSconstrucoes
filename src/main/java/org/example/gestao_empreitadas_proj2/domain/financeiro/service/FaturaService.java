package org.example.gestao_empreitadas_proj2.domain.financeiro.service;
import org.example.gestao_empreitadas_proj2.domain.financeiro.Fatura;
import org.example.gestao_empreitadas_proj2.domain.financeiro.repository.FaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Service
public class FaturaService {
    @Autowired private FaturaRepository repo;
    public void emitirFatura(Fatura f) {
        if (f.getAutomedicaoid() == null) { System.out.println("[ERRO BLL] Sem automedicao!"); return; }
        if (f.getValor() == null || f.getValor().compareTo(BigDecimal.ZERO) <= 0) { System.out.println("[ERRO BLL] Valor invalido!"); return; }
        if (f.getDatavencimento() != null && f.getDataemissao() != null && f.getDatavencimento().isBefore(f.getDataemissao())) { System.out.println("[ERRO BLL] Data vencimento anterior a emissao!"); return; }
        if (f.getNumerofatura() == null || f.getNumerofatura().isBlank()) { System.out.println("[ERRO BLL] Numero fatura obrigatorio!"); return; }
        if (f.getEstado() == null || f.getEstado().isBlank()) f.setEstado("EMITIDA");
        repo.save(f);
    }
    public void registarPagamento(Integer id) {
        Fatura f = repo.findById(id).orElse(null);
        if (f == null) { System.out.println("[ERRO BLL] Fatura nao encontrada!"); return; }
        if ("PAGA".equals(f.getEstado())) { System.out.println("[ERRO BLL] Ja paga!"); return; }
        f.setEstado("PAGA"); f.setDataPagamento(LocalDate.now()); repo.save(f);
    }
    public List<Fatura> listarTodas() { return (List<Fatura>) repo.findAll(); }
}