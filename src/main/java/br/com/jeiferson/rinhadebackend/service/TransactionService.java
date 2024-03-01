package br.com.jeiferson.rinhadebackend.service;

import br.com.jeiferson.rinhadebackend.exception.NotFoundException;
import br.com.jeiferson.rinhadebackend.exception.UnprocessableEntityException;
import br.com.jeiferson.rinhadebackend.model.dto.CreateTransactionDTO;
import br.com.jeiferson.rinhadebackend.model.entity.Customer;
import br.com.jeiferson.rinhadebackend.model.entity.Transaction;
import br.com.jeiferson.rinhadebackend.model.response.TransactionResponse;
import br.com.jeiferson.rinhadebackend.repository.CustomerRepository;
import br.com.jeiferson.rinhadebackend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Service
public class TransactionService {

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private TransactionRepository transactionRepository;

  @Transactional
  public Mono<TransactionResponse> create(int customerId, CreateTransactionDTO createTransactionDTO) {
    return customerRepository.findByIdForUpdate(customerId)
        .switchIfEmpty(Mono.error(new NotFoundException("CustomerNotFoundException")))
        .publishOn(Schedulers.boundedElastic())
        .flatMap(c -> {
          if (!isValidTransaction(createTransactionDTO, c)) {
            return Mono.error(new UnprocessableEntityException("BalanceLessThanLimitException"));
          } else {
            if (createTransactionDTO.tipo() == 'c') {
              c.setBalance(c.getBalance() + createTransactionDTO.valor().intValue());
            } else {
              c.setBalance(c.getBalance() - createTransactionDTO.valor().intValue());
            }
            return Mono.zip(customerRepository.update(c), createTransaction(createTransactionDTO, c.getId()));
          }
        })
        .map(tuple -> generateResponse(tuple.getT1()));
  }

  private TransactionResponse generateResponse(Customer customer) {
    return new TransactionResponse(customer.getAccountLimit(), customer.getBalance());
  }

  private Mono<Transaction> createTransaction(CreateTransactionDTO createTransactionDTO, Integer customerID) {
    Transaction transaction = new Transaction();
    transaction.setValue(createTransactionDTO.valor().intValue());
    transaction.setType(createTransactionDTO.tipo());
    transaction.setDescription(createTransactionDTO.descricao());
    transaction.setDate(LocalDateTime.now().toString());
    transaction.setCustomerId(customerID);
    return transactionRepository.save(transaction);
  }

  private boolean isValidTransaction(CreateTransactionDTO createTransactionDTO, Customer customer) {
    if (createTransactionDTO.valor().intValue() != createTransactionDTO.valor()) {
      return false;
    } else if (!createTransactionDTO.tipo().equals('c') && !createTransactionDTO.tipo().equals('d')) {
      return false;
    } else if (createTransactionDTO.valor() < 0) {
      return false;
    } else if (createTransactionDTO.descricao() == null || createTransactionDTO.descricao().isEmpty() || createTransactionDTO.descricao().length() > 10) {
      return false;
    } else if (createTransactionDTO.tipo().equals('d') && ((customer.getBalance() - createTransactionDTO.valor()) < (customer.getAccountLimit() * -1))) {
      return false;
    }
    return true;
  }

}
