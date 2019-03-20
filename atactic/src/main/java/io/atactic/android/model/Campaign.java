package io.atactic.android.model;

import java.util.Date;

public class Campaign {

    public static final String CAMPAIGN_TYPE_INTENSITY = "INTENSITY";
    public static final String CAMPAIGN_TYPE_SEGMENT_COVERAGE = "SEGMENT_COVERAGE";
    public static final String CAMPAIGN_TYPE_SALES_TARGET = "SALES_TARGET_TOTAL";
    public static final String CAMPAIGN_TYPE_SALES_TARGET_REFERENCED = "SALES_TARGET_REFERENCED";

    private int id;
    private String name;

    private String type;
    private String status;
    private int priority;

    private Date startDate;
    private Date endDate;

    private String briefing;
    private String description;

    private int completionScore;

    private User owner;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getBriefing() {
        return briefing;
    }

    public void setBriefing(String briefing) {
        this.briefing = briefing;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCompletionScore() {
        return completionScore;
    }

    public void setCompletionScore(int completionScore) {
        this.completionScore = completionScore;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
