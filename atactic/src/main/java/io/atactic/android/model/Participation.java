package io.atactic.android.model;

import java.io.Serializable;

public class Participation implements Serializable {

    private int id;

    private User participant;
    private Campaign campaign;

    private double currentProgress;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getParticipant() {
        return participant;
    }

    public void setParticipant(User participant) {
        this.participant = participant;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public double getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(double currentProgress) {
        this.currentProgress = currentProgress;
    }
}
