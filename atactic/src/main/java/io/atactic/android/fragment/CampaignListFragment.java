package io.atactic.android.fragment;


import android.content.Intent;
import android.os.Bundle;
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
import io.atactic.android.activity.CampaignDetailActivity;
import io.atactic.android.datahandler.ParticipationListDataHandler;
import io.atactic.android.datahandler.ParticipationListPresenter;
import io.atactic.android.element.ParticipationListAdapter;
import io.atactic.android.model.Participation;
import io.atactic.android.utils.CredentialsCache;

/**
 * Fragment managing the Campaign List section
 *
 * @author Jaime Lucea
 * @author ATACTIC
 */
public class CampaignListFragment extends Fragment
        implements ParticipationListAdapter.ListItemClickListener, ParticipationListPresenter {

    private static final String LOG_TAG = CampaignListFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private ParticipationListAdapter adapter = new ParticipationListAdapter(this);;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar loadingIndicator;
    private TextView statusMessageTextView;

    private ParticipationListDataHandler dataHandler;

    private List<Participation> campaigns;

    /*
     *  Required empty public constructor
     */
    public CampaignListFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_campaign_list, container, false);

        loadingIndicator = view.findViewById(R.id.questlist_loading_indicator);
        statusMessageTextView = view.findViewById(R.id.tv_campaign_list_status_message);
        refreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        refreshLayout.setOnRefreshListener(() -> requestData());

        if (campaigns == null) {
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        // Get reference to the RecyclerView component
        recyclerView = view.findViewById(R.id.rv_quest_list);

        // Set the layout manager for the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        /*
         * This setting improves performance if changes in content
         * do not change the child layout size in the RecyclerView
         */
        recyclerView.setHasFixedSize(true);

        /*
         * Instantiate fragmentAdapter. The fragmentAdapter is responsible for linking the quest data
         * with the Views that will display quest data.
         */
        recyclerView.setAdapter(adapter);

        if (campaigns == null){
            Log.v(LOG_TAG, "onCreateView - Requesting data...");
            dataHandler = new ParticipationListDataHandler(this);
            requestData();
        }

        return view;
    }

    /**
     * Asynchronous request gets the list of campaigns for the current user
     *  and displays them on the UI
     */
    private void requestData(){

        CredentialsCache.UserCredentials credentials = CredentialsCache.recoverCredentials(getContext());
        if (credentials != null) {
            Log.v(LOG_TAG, "Requesting campaign list for user... " + credentials.getUserId());

            dataHandler.getData(credentials.getUserId());
        }
    }

    @Override
    public void displayCampaignList(List<Participation> data) {
        adapter.setData(data);

        loadingIndicator.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);
        statusMessageTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        campaigns = data;
    }

    @Override
    public void displayMessage(String message) {
        statusMessageTextView.setText(message);

        refreshLayout.setRefreshing(false);
        loadingIndicator.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        statusMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListItemClick(int clickedItemIdex) {
        Log.d(LOG_TAG, "Campaign clicked. Index = " + clickedItemIdex);

        Participation p = campaigns.get(clickedItemIdex);

        // Create an intent and put the quest information to display
        Intent i = new Intent(this.getContext(), CampaignDetailActivity.class);

        i.putExtra("participationId",p.getId());
        i.putExtra("questName", p.getCampaign().getName());
        i.putExtra("questType", p.getCampaign().getType());
        i.putExtra("questSummary", p.getCampaign().getBriefing());
        i.putExtra("questLongDesc", p.getCampaign().getDescription());
        i.putExtra("questDeadline", p.getCampaign().getEndDate().getTime());
        i.putExtra("completionScore", p.getCampaign().getCompletionScore());
        i.putExtra("currentProgress",p.getCurrentProgress());
        String questOwnerStr = p.getCampaign().getOwner().getFirstName() + " "
                + p.getCampaign().getOwner().getLastName()
                + "\n" + p.getCampaign().getOwner().getPosition();
        i.putExtra("questOwner", questOwnerStr);

        // Launch CampaignDetailActivity
        startActivity(i);
    }

}
