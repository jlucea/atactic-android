package io.atactic.android.presenter;

import java.util.List;

import io.atactic.android.model.Account;
import io.atactic.android.model.TargetAccount;

public interface MapDataPresenter {

    void displayMarkers(List<Account> accounts, List<TargetAccount> targets);
    void displayError(String message);

}
