package br.com.jeiferson.rinhadebackend.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

public class Customer {
  @Id
  Integer id;
  Integer balance;

  @Column("account_limit")
  Integer accountLimit;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public int getBalance() {
    return balance;
  }

  public void setBalance(Integer balance) {
    this.balance = balance;
  }

  public int getAccountLimit() {
    return accountLimit;
  }

  public void setAccountLimit(Integer accountLimit) {
    this.accountLimit = accountLimit;
  }

}
