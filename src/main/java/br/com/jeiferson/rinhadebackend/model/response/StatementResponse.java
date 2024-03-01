package br.com.jeiferson.rinhadebackend.model.response;

import java.util.List;

public record StatementResponse(
    StatementInfoResponse saldo,
    List<StatementTransactionResponse> ultimas_transacoes
) { }
