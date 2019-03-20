package io.atactic.android.presenter;

import java.util.List;

import io.atactic.android.model.Participation;

public interface ParticipationListPresenter {

    void displayCampaignList(List<Participation> data);

    void displayMessage(String message);

}
