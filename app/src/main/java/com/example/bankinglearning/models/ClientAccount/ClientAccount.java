package com.example.bankinglearning.models.ClientAccount;

import com.example.bankinglearning.models.Account.BaseAccount;
import com.example.bankinglearning.models.Client.Client;

public class ClientAccount {

  private Client client;
  private BaseAccount account;

  public ClientAccount(Client client, BaseAccount account) {
    this.client = client;
    this.account = account;
  }

  public Client getClient() {
    return client;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  public void setAccount(BaseAccount account) {
    this.account = account;
  }

  public BaseAccount getAccount() {
    return account;
  }
}
