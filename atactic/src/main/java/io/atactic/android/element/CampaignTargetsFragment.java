package io.atactic.android.element;

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
import android.widget.FrameLayout;

import java.util.List;

import io.atactic.android.R;
import io.atactic.android.presenter.AccountListPresenter;
import io.atactic.android.datahandler.ParticipationTargetsDataHandler;
import io.atactic.android.model.Account;
import io.atactic.android.utils.CredentialsCache;

public class CampaignTargetsFragment extends Fragment implements AccountListPresenter {

    private static final String LOG_TAG = "CampaignTargetsFragment";

    private RecyclerView recyclerView;
    private List<Account> accounts;
    private FrameLayout loadingIndicatorFrame;

    public CampaignTargetsFragment() {  }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG_TAG, "Fragment on create - recovering credentials");
        int userId = CredentialsCache.recoverCredentials(this.getContext()).getUserId();
        int participationId = getArguments().getInt("pid");

        Log.d(LOG_TAG, "UserId = " + userId);
        Log.d(LOG_TAG, "ParticipationId = " + participationId);

        /*
         * Call ParticipationTargetsDataHandler to request campaign targets in order to fill the fragment.
         * Once finished, the DataHandler is supposed to call the displayTargets method
         */
        new ParticipationTargetsDataHandler(this).getData(userId, participationId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.v(LOG_TAG, "Fragment OnCreateView");

        View view = inflater.inflate(R.layout.fragment_campaign_targets,container, false);

        // Show loading indicator
        loadingIndicatorFrame = view.findViewById(R.id.targets_loading_indicator_layout);

        recyclerView = view.findViewById(R.id.rv_target_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);

        // Link Adapter to RecyclerView
        AccountListAdapter adapter = new AccountListAdapter();
        recyclerView.setAdapter(adapter);

        if (accounts != null) {
            adapter.setContent(accounts);
            // adapter.notifyDataSetChanged();
        }else{
            loadingIndicatorFrame.setVisibility(View.VISIBLE);
            Log.w(LOG_TAG, "Operating fragment with NULL data");
        }
        return view;
    }



    @Override
    public void displayAccounts(List<Account> accountList) {
        loadingIndicatorFrame.setVisibility(View.GONE);

        Log.v(LOG_TAG, "Updating fragment content");
        this.accounts = accountList;
        if (recyclerView != null) {
            if (recyclerView.getAdapter() != null) {
                Log.v(LOG_TAG, "Notify data set changed");
                AccountListAdapter adapter = (AccountListAdapter)this.recyclerView.getAdapter();
                adapter.setContent(accountList);
                adapter.notifyDataSetChanged();
            }else {
                Log.w(LOG_TAG, "Adapter is null");
            }
        }else{
            Log.w(LOG_TAG, "RecyclerView is null");
        }
    }

    @Override
    public void displayMessage(String message) {
        // TODO
    }
}
