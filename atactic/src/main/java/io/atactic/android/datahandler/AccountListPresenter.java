package io.atactic.android.datahandler;

import java.util.List;

import io.atactic.android.model.Account;

public interface AccountListPresenter {

    void displayAccounts(List<Account> accountList);

    void displayMessage(String message);
}
