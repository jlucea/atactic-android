package io.atactic.android.model;

import java.util.List;

public class AccountTargetingParticipation extends Participation {

    private List<Account> targetAccounts;

    public List<Account> getTargetAccounts() {
        return targetAccounts;
    }

    public void setTargetAccounts(List<Account> targetAccounts) {
        this.targetAccounts = targetAccounts;
    }
}
