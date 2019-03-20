package io.atactic.android.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import io.atactic.android.R;
import io.atactic.android.datahandler.CampaignRankingDataHandler;
import io.atactic.android.element.ProgressRankingAdapter;
import io.atactic.android.model.Participation;
import io.atactic.android.model.User;
import io.atactic.android.presenter.RankingPresenter;
import io.atactic.android.utils.CredentialsCache;


public class RankingFragment extends Fragment implements RankingPresenter {

    private static final String LOG_TAG = RankingFragment.class.getSimpleName();

    private List<Participation> participations;

    public static final String PARAM_KEY_MODE = "mode";
    public static final String PARAM_KEY_CAMPAIGNID = "cid";

    public static final String MODE_PROGRESS = "progress";
    public static final String MODE_SCORE = "score";

    private RecyclerView rankingRecyclerView;
    private ProgressRankingAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar loadingIndicator;
    private TextView statusMessageTextView;

    public RankingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String mode = getArguments().getString(PARAM_KEY_MODE);
            if (MODE_PROGRESS.equals(mode)) {

                // Will display a campaign progress ranking
                // Recover userId from credentials cache and campaignId from Fragment arguments
                int userId = CredentialsCache.recoverCredentials(this.getContext()).getUserId();
                int campaignId = getArguments().getInt(PARAM_KEY_CAMPAIGNID);

                Log.d(LOG_TAG, "userId=" + userId);
                Log.d(LOG_TAG, "campaignId=" + campaignId);

                // Request data
                new CampaignRankingDataHandler(this).getData(userId, campaignId);
            }
        }
        // TODO else if mode==MODE_SCORE--> Display a score Ranking

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ranking,container, false);

        rankingRecyclerView = view.findViewById(R.id.rv_ranking);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rankingRecyclerView.setHasFixedSize(true);

        adapter = new ProgressRankingAdapter();
        rankingRecyclerView.setAdapter(adapter);

        if (participations != null){
            int userId = CredentialsCache.recoverCredentials(this.getContext()).getUserId();
            adapter.setContent(participations, userId);
        }

        return view;
    }


    @Override
    public void displayProgressRanking(List<Participation> participations) {
        if (participations != null){
            this.participations = participations;
            Log.d(LOG_TAG, "Will display " + participations.size() + " participations");

            int userId = CredentialsCache.recoverCredentials(this.getContext()).getUserId();
            this.adapter.setContent(participations, userId);

        } else {
            Log.w(LOG_TAG,"NULL participation list received");
        }
    }

    @Override
    public void displayScoreRanking(List<User> userList) {
        // TODO
    }

    @Override
    public void displayMessage(String message) {
        Log.d("RankingFragment", "Message: " + message);
    }

}
