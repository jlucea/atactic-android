package io.atactic.android.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import io.atactic.android.activity.AccountDetailActivity;
import io.atactic.android.datahandler.AccountListDataHandler;
import io.atactic.android.datahandler.AccountListPresenter;
import io.atactic.android.element.AccountListAdapter;
import io.atactic.android.manager.LocationManager;
import io.atactic.android.model.Account;
import io.atactic.android.utils.CredentialsCache;

public class AccountListFragment extends Fragment implements AccountListAdapter.ListItemClickListener, AccountListPresenter {

    private AccountListDataHandler dataHandler;

    private AccountListAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar loadingIndicator;
    private TextView statusMessageTextView;
    private RecyclerView accountListRecyclerView;

    private final static String LOG_TAG = AccountListFragment.class.getSimpleName();

    public AccountListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_list, container, false);

        loadingIndicator = view.findViewById(R.id.account_list_loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        refreshLayout = view.findViewById(R.id.account_list_refresh_layout);
        refreshLayout.setOnRefreshListener(this::reloadData);

        accountListRecyclerView = view.findViewById(R.id.rv_target_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        accountListRecyclerView.setLayoutManager(layoutManager);
        accountListRecyclerView.setHasFixedSize(true);

        // Instantiate Adapter and assign it to the RecyclerView
        this.adapter = new AccountListAdapter(this);
        accountListRecyclerView.setAdapter(adapter);

        // Activate LocationManager to update the current location
        LocationManager.getInstance().updateLocation(this.getActivity());

        /*
         * Instantiate Data Handler and request data to display.
         * The Handler is expected to call the displayData or displayMessage functions from this class.
         */
        dataHandler = new AccountListDataHandler(this);
        dataHandler.getData(CredentialsCache.recoverCredentials(getContext()).getUserId());

        statusMessageTextView = view.findViewById(R.id.tv_account_list_status_message);

        return view;
    }


    void reloadData() {
        dataHandler.getData(CredentialsCache.recoverCredentials(getContext()).getUserId());
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        // Get the Account object from the adapter
        Account acc = adapter.getAccount(clickedItemIndex);

        Intent i = new Intent(getContext(), AccountDetailActivity.class);

        i.putExtra("accountId", acc.getId());
        i.putExtra("accountExtId", acc.getExternalId());
        i.putExtra("accountName", acc.getName());
        i.putExtra("accountType", acc.getType());
        i.putExtra("accountAddress", acc.getAddress());
        i.putExtra("accountPostcode", acc.getPostalCode());
        i.putExtra("accountCity", acc.getCity());
        i.putExtra("accountProvince", acc.getProvince());
        i.putExtra("accountCountry", acc.getCountry());
        i.putExtra("accountLatitude", acc.getLatitude());
        i.putExtra("accountLongitude", acc.getLongitude());
        i.putExtra("accountDistanceAway", acc.getDistanceTo());

        startActivity(i);
    }

    @Override
    public void displayAccounts(List<Account> accountList) {
        Log.v(LOG_TAG,"Displaying " + accountList.size() + " accounts");

        this.adapter.setContent(accountList);
        refreshLayout.setRefreshing(false);

        statusMessageTextView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
        accountListRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayMessage(String message) {
        statusMessageTextView.setText(message);

        accountListRecyclerView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
        statusMessageTextView.setVisibility(View.VISIBLE);
    }

}
