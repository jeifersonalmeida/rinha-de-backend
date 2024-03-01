package br.com.jeiferson.rinhadebackend.controller;

import br.com.jeiferson.rinhadebackend.model.dto.CreateTransactionDTO;
import br.com.jeiferson.rinhadebackend.model.response.StatementResponse;
import br.com.jeiferson.rinhadebackend.model.response.TransactionResponse;
import br.com.jeiferson.rinhadebackend.service.CustomerService;
import br.com.jeiferson.rinhadebackend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/clientes")
public class CustomerController {

  @Autowired
  private CustomerService customerService;

  @Autowired
  private TransactionService transactionService;

  @PostMapping("/{id}/transacoes")
  public Mono<ResponseEntity<TransactionResponse>> create(
      @PathVariable String id,
      @RequestBody CreateTransactionDTO createTransactionDTO
  ) {
    return transactionService.create(Integer.parseInt(id), createTransactionDTO)
        .map(transaction -> ResponseEntity.ok().body(transaction));
  }

  @GetMapping("/{id}/extrato")
  public Mono<ResponseEntity<StatementResponse>> statement(
      @PathVariable String id
  ) {
    return customerService.getStatement(Integer.parseInt(id))
        .map(statement -> ResponseEntity.ok().body(statement));
  }
}
