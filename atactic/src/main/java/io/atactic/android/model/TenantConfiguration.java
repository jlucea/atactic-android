package io.atactic.android.model;

public class TenantConfiguration {

    String defaultLanguage;
    String accountsLiteral;

    boolean checkInEnabled;
    boolean proximityCheckinRequired;
    int maxCheckInDistance;

    boolean displayRanking;

    int routeWaypoints;

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getAccountsLiteral() {
        return accountsLiteral;
    }

    public void setAccountsLiteral(String accountsLiteral) {
        this.accountsLiteral = accountsLiteral;
    }

    public boolean isCheckInEnabled() {
        return checkInEnabled;
    }

    public void setCheckInEnabled(boolean checkInEnabled) {
        this.checkInEnabled = checkInEnabled;
    }

    public boolean isProximityCheckinRequired() {
        return proximityCheckinRequired;
    }

    public void setProximityCheckinRequired(boolean proximityCheckinRequired) {
        this.proximityCheckinRequired = proximityCheckinRequired;
    }

    public int getMaxCheckInDistance() {
        return maxCheckInDistance;
    }

    public void setMaxCheckInDistance(int maxCheckInDistance) {
        this.maxCheckInDistance = maxCheckInDistance;
    }

    public boolean isDisplayRanking() {
        return displayRanking;
    }

    public void setDisplayRanking(boolean displayRanking) {
        this.displayRanking = displayRanking;
    }

    public int getRouteWaypoints() {
        return routeWaypoints;
    }

    public void setRouteWaypoints(int routeWaypoints) {
        this.routeWaypoints = routeWaypoints;
    }
}
