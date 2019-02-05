package io.atactic.android.element;

import android.os.Bundle;
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
import io.atactic.android.model.Account;

public class CampaignTargetsFragment extends Fragment {

    private static final String LOG_TAG = "CampaignTargetsFragment";

    private RecyclerView recyclerView;
    private List<Account> accounts;
    private FrameLayout loadingIndicatorFrame;

    public CampaignTargetsFragment() {  }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.v(LOG_TAG, "Fragment OnCreateView");

        View view = inflater.inflate(R.layout.fragment_quest_detail_3,container, false);

        // Show loading indicator
        loadingIndicatorFrame = view.findViewById(R.id.targets_loading_indicator_layout);
        loadingIndicatorFrame.setVisibility(View.VISIBLE);

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
            Log.w(LOG_TAG, "Operating fragment with NULL data");
        }
        return view;
    }


    public void setContent(List<Account> content){

        loadingIndicatorFrame.setVisibility(View.GONE);

        Log.v(LOG_TAG, "Updating fragment content");
        this.accounts = content;
        if (recyclerView != null) {
            if (recyclerView.getAdapter() != null) {
                Log.v(LOG_TAG, "Notify data set changed");
                AccountListAdapter adapter = (AccountListAdapter)this.recyclerView.getAdapter();
                adapter.setContent(content);
                adapter.notifyDataSetChanged();
            }else {
                Log.w(LOG_TAG, "Adapter is null");
            }
        }else{
            Log.w(LOG_TAG, "RecyclerView is null");
        }
    }

}
