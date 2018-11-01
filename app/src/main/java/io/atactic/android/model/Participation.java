package io.atactic.android.model;

public class Participation {

    private int id;
    private String campaignName;
    private int completionScore;
    private double currentProgress;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public int getCompletionScore() {
        return completionScore;
    }

    public void setCompletionScore(int completionScore) {
        this.completionScore = completionScore;
    }

    public double getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(double currentProgress) {
        this.currentProgress = currentProgress;
    }
}
