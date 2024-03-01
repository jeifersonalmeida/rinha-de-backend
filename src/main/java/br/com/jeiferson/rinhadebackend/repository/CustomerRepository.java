package br.com.jeiferson.rinhadebackend.repository;

import br.com.jeiferson.rinhadebackend.model.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class CustomerRepository {

  private final String QUERY = "SELECT * FROM customer WHERE id = $1";
  private final String QUERY_FOR_UPDATE = "SELECT * FROM customer WHERE id = $1 FOR UPDATE";
  private final String UPDATE = "UPDATE customer SET balance = $1, account_limit = $2 WHERE id = $3";

  @Autowired
  private DatabaseClient client;

  public Mono<Customer> findById(Integer customerId) {
    return client.sql(QUERY)
        .bind(0, customerId)
        .map(row -> {
          Customer consumer = new Customer();
          consumer.setId((Integer) row.get("id"));
          consumer.setBalance(((Long) row.get("balance")).intValue());
          consumer.setAccountLimit(((Long) row.get("account_limit")).intValue());
          return consumer;
        })
        .first();
  }

  public Mono<Customer> findByIdForUpdate(Integer customerId) {
    return client.sql(QUERY_FOR_UPDATE)
        .bind(0, customerId)
        .map(row -> {
          Customer consumer = new Customer();
          consumer.setId((Integer) row.get("id"));
          consumer.setBalance(((Long) row.get("balance")).intValue());
          consumer.setAccountLimit(((Long) row.get("account_limit")).intValue());
          return consumer;
        })
        .first();
  }

  public Mono<Customer> update(Customer customer) {
    return client.sql(UPDATE)
        .bind(0, customer.getBalance())
        .bind(1, customer.getAccountLimit())
        .bind(2, customer.getId())
        .then()
        .thenReturn(customer);
  }
}
