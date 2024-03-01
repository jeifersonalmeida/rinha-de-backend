package br.com.jeiferson.rinhadebackend.repository;

import br.com.jeiferson.rinhadebackend.model.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class TransactionRepository {

  private final String QUERY = "SELECT * FROM transaction WHERE customer_id = $1 ORDER BY date DESC LIMIT 10";
  private final String INSERT = "INSERT INTO transaction (value, type, description, date, customer_id) VALUES ($1, $2, $3, $4, $5)";

  @Autowired
  private DatabaseClient client;

  public Flux<Transaction> findLastestTransactions(Integer customerId) {
    return client.sql(QUERY)
        .bind(0, customerId)
        .map(row -> {
          Transaction transaction = new Transaction();
          transaction.setCustomerId((Integer) row.get("customer_id"));
          transaction.setDate((String) row.get("date"));
          transaction.setDescription((String) row.get("description"));
          transaction.setType(((String) row.get("type")).charAt(0));
          transaction.setValue(((Long) row.get("value")).intValue());
          return transaction;
        })
        .all();
  }

  public Mono<Transaction> save(Transaction transaction) {
    return client.sql(INSERT)
        .bind(0, transaction.getValue())
        .bind(1, transaction.getType().toString())
        .bind(2, transaction.getDescription())
        .bind(3, transaction.getDate())
        .bind(4, transaction.getCustomerId())
        .then()
        .thenReturn(transaction);
  }

}
