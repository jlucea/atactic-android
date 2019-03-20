package io.atactic.android.presenter;

import java.util.List;

import io.atactic.android.model.Account;

public interface AccountListPresenter {

    void displayAccounts(List<Account> accountList);

    void displayMessage(String message);
}
