package io.atactic.android.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import io.atactic.android.R;
import io.atactic.android.element.AccountListAdapter;
import io.atactic.android.manager.AccountListDataHandler;
import io.atactic.android.element.BottomNavigationBarClickListenerFactory;
import io.atactic.android.model.Account;

public class AccountListActivity extends AppCompatActivity {

    private AccountListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priority_list);

        /*
         * Initialize bottom navigation bar. Update click listener and mark selected item
         */
        BottomNavigationView bottomNavigationBar = findViewById(R.id.bottom_navigation);
        bottomNavigationBar.setSelectedItemId(R.id.action_priorities);
        bottomNavigationBar.setOnNavigationItemSelectedListener(
                BottomNavigationBarClickListenerFactory.getClickListener(getBaseContext(),
                        this.getClass()));

        // Get reference to the RecyclerView component
        RecyclerView accountListRecyclerView = findViewById(R.id.rv_target_list);

        // Set the layout manager for the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        accountListRecyclerView.setLayoutManager(layoutManager);
        accountListRecyclerView.setHasFixedSize(true);

        // Instantiate Adapter and assign it to the RecyclerView
        this.adapter = new AccountListAdapter();
        accountListRecyclerView.setAdapter(adapter);

        /*
         * Instantiate Data Handler and request data to display.
         * The Handler is expected to call the displayData or displayMessage functions from this class.
         */
        new AccountListDataHandler(this).getData();
    }

    /**
     * Updates the data display
     *
     * @param accountList List of Accounts to display
     */
    public void displayData(List<Account> accountList){
        System.out.println("AccountListActivity setting adapter content");
        this.adapter.setContent(accountList);
    }


    public void displayMessage(String message){
        // TODO
    }

    public void showLoadingIndicator(){
        // TODO
    }

}
