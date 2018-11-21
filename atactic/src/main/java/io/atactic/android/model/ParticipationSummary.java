package io.atactic.android.model;

/**
 * This class is a brief representation of a participation that contains only
 * the Campaign's id and name, the current progress and completion reward.
 */
public class ParticipationSummary {

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
