package io.atactic.android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import io.atactic.android.R;
import io.atactic.android.datahandler.ActivityHistoryDataHandler;
import io.atactic.android.element.ActivityListAdapter;
import io.atactic.android.model.Visit;
import io.atactic.android.utils.CredentialsCache;

public class HistoryActivity extends AppCompatActivity {

    private static final String LOG_TAG = HistoryActivity.class.getSimpleName();

    private ActivityListAdapter adapter;
    private FrameLayout loadingIndicatorFrame;
    private TextView statusMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Display back button in action bar
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.loadingIndicatorFrame = findViewById(R.id.activity_history_loading_indicator_layout);
        this.loadingIndicatorFrame.setVisibility(View.VISIBLE);

        this.statusMessageTextView = findViewById(R.id.tv_activity_history_status_message);

        this.adapter = new ActivityListAdapter();
        RecyclerView recyclerView = findViewById(R.id.rv_activity_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        int userId = CredentialsCache.recoverCredentials().getUserId();

        Log.v(LOG_TAG, "Requesting activity history for user " + userId);
        new ActivityHistoryDataHandler(this).getData(userId);
    }


    public void displayData(List<Visit> activities){
        Log.v(LOG_TAG, "Displaying " + activities.size() + " activities");
        adapter.setContent(activities);
        adapter.notifyDataSetChanged();
        this.loadingIndicatorFrame.setVisibility(View.GONE);
    }

    public void displayMessage(String message){
        this.loadingIndicatorFrame.setVisibility(View.GONE);
        statusMessageTextView.setText(message);
        statusMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOG_TAG, "Options item selected");
        this.finish();
        return super.onOptionsItemSelected(item);
    }


}
