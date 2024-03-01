package br.com.jeiferson.rinhadebackend.model.response;

public record StatementInfoResponse(
    Integer total,
    String data_extrato,
    Integer limite
) {
}
