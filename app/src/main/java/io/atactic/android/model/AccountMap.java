package io.atactic.android.model;

import java.util.List;

public class AccountMap {

    private List<AccountTargetingParticipation> targetingParticipations;
    private List<Account> accounts;

    public List<AccountTargetingParticipation> getTargetingParticipations() {
        return targetingParticipations;
    }

    public void setTargetingParticipations(List<AccountTargetingParticipation> targetingParticipations) {
        this.targetingParticipations = targetingParticipations;
    }

    public List<Account>  getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account>  accounts) {
        this.accounts = accounts;
    }
}
