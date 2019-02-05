package io.atactic.android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import io.atactic.android.R;
import io.atactic.android.datahandler.ActivityHistoryDataHandler;
import io.atactic.android.element.ActivityListAdapter;
import io.atactic.android.element.AtacticApplication;
import io.atactic.android.model.Visit;

public class HistoryActivity extends AppCompatActivity {

    private static final String LOG_TAG = HistoryActivity.class.getSimpleName();;

    private ActivityListAdapter adapter;
    private RecyclerView recyclerView;
    private FrameLayout loadingIndicatorFrame;
    private TextView statusMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        this.loadingIndicatorFrame = findViewById(R.id.activity_history_loading_indicator_layout);
        this.loadingIndicatorFrame.setVisibility(View.VISIBLE);

        this.statusMessageTextView = findViewById(R.id.tv_activity_history_status_message);

        this.adapter = new ActivityListAdapter();
        this.recyclerView = findViewById(R.id.rv_activity_list);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(adapter);

        int userId = ((AtacticApplication) this.getApplication()).getUserId();

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

}
