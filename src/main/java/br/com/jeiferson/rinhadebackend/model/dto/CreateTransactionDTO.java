package br.com.jeiferson.rinhadebackend.model.dto;

public record CreateTransactionDTO(
    Double valor,
    Character tipo,
    String descricao
) {
}
