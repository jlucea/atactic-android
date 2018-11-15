package io.atactic.android.model;

import java.util.List;

public class TargetAccount extends Account {

    private List<ParticipationSummary> participations;

    public TargetAccount(Account acc) {
        this.setId(acc.getId());
        this.setName(acc.getName());
        this.setType(acc.getType());
        this.setAddress(acc.getAddress());
        this.setCity(acc.getCity());
        this.setProvince(acc.getProvince());
        this.setPostalCode(acc.getPostalCode());
        this.setCountry(acc.getCountry());
        this.setLatitude(acc.getLatitude());
        this.setLongitude(acc.getLongitude());
        this.setDistanceTo(acc.getDistanceTo());
    }

    public List<ParticipationSummary> getParticipations() {
        return participations;
    }

    public void setParticipations(List<ParticipationSummary> participations) {
        this.participations = participations;
    }
}
