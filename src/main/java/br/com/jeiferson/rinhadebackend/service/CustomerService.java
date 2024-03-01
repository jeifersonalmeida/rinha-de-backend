package br.com.jeiferson.rinhadebackend.service;

import br.com.jeiferson.rinhadebackend.exception.NotFoundException;
import br.com.jeiferson.rinhadebackend.model.entity.Customer;
import br.com.jeiferson.rinhadebackend.model.response.StatementInfoResponse;
import br.com.jeiferson.rinhadebackend.model.response.StatementResponse;
import br.com.jeiferson.rinhadebackend.model.response.StatementTransactionResponse;
import br.com.jeiferson.rinhadebackend.repository.CustomerRepository;
import br.com.jeiferson.rinhadebackend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private TransactionRepository transactionRepository;

  public Mono<StatementResponse> getStatement(Integer customerId) {
    Mono<Customer> customer = customerRepository.findById(customerId)
        .switchIfEmpty(Mono.error(new NotFoundException("CustomerNotFoundException")));
    Mono<List<StatementTransactionResponse>> transactions = transactionRepository.findLastestTransactions(customerId)
        .collectList().map(transactionList -> transactionList.stream().map(tran -> new StatementTransactionResponse(
            tran.getValue(),
            tran.getType(),
            tran.getDescription(),
            tran.getDate()
        )).collect(Collectors.toList()));

    return Mono.zip(customer, transactions)
        .subscribeOn(Schedulers.boundedElastic())
        .map(zip -> {
            Customer c = zip.getT1();
            List<StatementTransactionResponse> t = zip.getT2();
            return new StatementResponse(
              new StatementInfoResponse(
                  c.getBalance(),
                  LocalDateTime.now().toString(),
                  c.getAccountLimit()
              ),
              t
            );
        });
  }

}
