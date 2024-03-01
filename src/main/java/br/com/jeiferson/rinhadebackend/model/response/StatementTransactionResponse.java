package br.com.jeiferson.rinhadebackend.model.response;

public record StatementTransactionResponse(
    Integer valor,
    Character tipo,
    String descricao,
    String realizada_em
) {
}
