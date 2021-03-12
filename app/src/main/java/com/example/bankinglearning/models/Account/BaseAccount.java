package com.example.bankinglearning.models.Account;

public abstract class BaseAccount {
    private String accountNo;
    private Double balance;
    private String accountType;


    public BaseAccount(String accountNo, Double balance, String accountType) {
        this.accountNo = accountNo;
        this.balance = balance;
        this.accountType=accountType;
    }



    public String getAccountNo() {
        return accountNo;
    }


    public Double getBalance() {
        return balance;
    }

    public String getAccountType() {
        return accountType;
    }


    public void setBalance(Double balance) {
        this.balance = balance;
    }


}
