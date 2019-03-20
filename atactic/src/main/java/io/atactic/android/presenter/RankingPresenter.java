package io.atactic.android.presenter;

import java.util.List;

import io.atactic.android.model.Participation;
import io.atactic.android.model.User;

public interface RankingPresenter {

    /**
     * Display a ranked list of participants and their current progress
     */
    void displayProgressRanking(List<Participation> participationList);

    /**
     * Display a ranked list of users and their scores
     */
    void displayScoreRanking(List<User> userList);

    /**
     * Display a status message
     */
    void displayMessage(String message);

}
