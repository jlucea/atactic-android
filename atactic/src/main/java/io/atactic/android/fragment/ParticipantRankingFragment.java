package io.atactic.android.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import io.atactic.android.element.ParticipantRankingAdapter;
import io.atactic.android.model.Participation;
import io.atactic.android.utils.CredentialsCache;


public class ParticipantRankingFragment extends Fragment {

    private static final String LOG_TAG = ParticipantRankingFragment.class.getSimpleName();

    private List<Participation> participations;

    public static final String PARAM_KEY_CAMPAIGNID = "cid";

    private RecyclerView rankingRecyclerView;
    private ParticipantRankingAdapter adapter;
    private ProgressBar loadingIndicator;
    private TextView statusMessageTextView;

    public ParticipantRankingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            // Will display a campaign progress ranking
            // Recover userId from credentials cache and campaignId from Fragment arguments
            int userId = CredentialsCache.recoverCredentials(this.getContext()).getUserId();
            int campaignId = getArguments().getInt(PARAM_KEY_CAMPAIGNID);

            Log.d(LOG_TAG, "userId=" + userId);
            Log.d(LOG_TAG, "campaignId=" + campaignId);

            // Request data
            new CampaignRankingDataHandler(this).getData(userId, campaignId);

        } else {
            Log.e(LOG_TAG, "Fragment is operating without parameters!");
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_campaign_ranking,container, false);

        loadingIndicator = view.findViewById(R.id.ranking_loading_indicator);
        statusMessageTextView = view.findViewById(R.id.tv_ranking_fragment_status_message);

        rankingRecyclerView = view.findViewById(R.id.rv_ranking);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rankingRecyclerView.setHasFixedSize(true);

        adapter = new ParticipantRankingAdapter();
        rankingRecyclerView.setAdapter(adapter);

        if (participations != null){
            int userId = CredentialsCache.recoverCredentials(this.getContext()).getUserId();
            adapter.setContent(participations, userId);
        }else{
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        return view;
    }


    public void displayProgressRanking(List<Participation> participations) {
        loadingIndicator.setVisibility(View.GONE);
        if (participations != null){
            this.participations = participations;
            Log.d(LOG_TAG, "Will display " + participations.size() + " participations");

            int userId = CredentialsCache.recoverCredentials(this.getContext()).getUserId();
            this.adapter.setContent(participations, userId);

        } else {
            Log.w(LOG_TAG,"NULL participation list received");
        }
    }

    public void displayMessage(String message) {
        // TODO
        Log.d("ParticipantRankingFragment", "Message: " + message);
    }

}
