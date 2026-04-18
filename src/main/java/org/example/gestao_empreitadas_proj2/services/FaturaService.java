package org.example.gestao_empreitadas_proj2.services;

import org.example.gestao_empreitadas_proj2.models.Fatura;
import org.example.gestao_empreitadas_proj2.repositories.FaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FaturaService {

    @Autowired
    private FaturaRepository faturaRepository;

    // REGRA BLL: Emitir fatura com validações
    public void emitirFatura(Fatura fatura) {
        // Regra 1: Tem de ter auto-medição associada
        if (fatura.getAutomedicaoid() == null) {
            System.out.println("[ERRO BLL] A fatura tem de estar associada a uma auto-medição!");
            return;
        }
        // Regra 2: Valor tem de ser positivo
        if (fatura.getValor() == null || fatura.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("[ERRO BLL] O valor da fatura tem de ser positivo!");
            return;
        }
        // Regra 3: Data de vencimento tem de ser depois da data de emissão
        if (fatura.getDatavencimento() != null && fatura.getDataemissao() != null &&
            fatura.getDatavencimento().isBefore(fatura.getDataemissao())) {
            System.out.println("[ERRO BLL] A data de vencimento não pode ser anterior à data de emissão!");
            return;
        }
        // Regra 4: Número de fatura obrigatório
        if (fatura.getNumerofatura() == null || fatura.getNumerofatura().isBlank()) {
            System.out.println("[ERRO BLL] O número de fatura é obrigatório!");
            return;
        }
        // Estado por omissão
        if (fatura.getEstado() == null || fatura.getEstado().isBlank()) {
            fatura.setEstado("EMITIDA");
        }
        faturaRepository.save(fatura);
        System.out.println("[BLL] Fatura '" + fatura.getNumerofatura() + "' emitida com sucesso! Valor: " + fatura.getValor() + "€");
    }

    // REGRA BLL: Registar pagamento de fatura
    public void registarPagamento(Integer id) {
        Fatura f = faturaRepository.findById(id).orElse(null);
        if (f == null) {
            System.out.println("[ERRO BLL] Fatura com ID " + id + " não encontrada!");
            return;
        }
        if ("PAGA".equals(f.getEstado())) {
            System.out.println("[ERRO BLL] Esta fatura já foi paga!");
            return;
        }
        f.setEstado("PAGA");
        f.setDataPagamento(LocalDate.now());
        faturaRepository.save(f);
        System.out.println("[BLL] Fatura '" + f.getNumerofatura() + "' marcada como PAGA!");
    }

    // Listar todas as faturas
    public List<Fatura> listarTodas() {
        return (List<Fatura>) faturaRepository.findAll();
    }
}
