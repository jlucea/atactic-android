package io.atactic.android.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.atactic.android.R;
import io.atactic.android.datahandler.ActivityHistoryDataHandler;
import io.atactic.android.element.ActivityListAdapter;
import io.atactic.android.element.AtacticApplication;
import io.atactic.android.model.Account;
import io.atactic.android.model.Visit;

public class HistoryActivity extends AppCompatActivity {

    private static final String LOG_TAG = "HistoryActivity";

    private ActivityListAdapter adapter;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        this.adapter = new ActivityListAdapter();
        this.recyclerView = findViewById(R.id.rv_activity_list);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(adapter);

        // List<Visit> data = createDummyData();
        // adapter.setContent(data);

        int userId = ((AtacticApplication) this.getApplication()).getUserId();

        new ActivityHistoryDataHandler(this).getData(userId);
    }


    public void displayData(List<Visit> activities){
        adapter.setContent(activities);
        adapter.notifyDataSetChanged();
    }

    public void displayMessage(String message){

    }


    private List<Visit> createDummyData() {
        Visit v1 = new Visit();
        Visit v2 = new Visit();
        Visit v3 = new Visit();

        Account acc = new Account();
        acc.setName("Dr. Quiñones");

        v1.setAccount(acc); v2.setAccount(acc); v3.setAccount(acc);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = sdf.parse("18/11/2018 13:17");
            v1.setDate(date); v2.setDate(date); v3.setDate(date);
        }catch (ParseException pe){
            pe.printStackTrace();
        }

        List<Visit> visits = new ArrayList<>(3);
        visits.add(v1); visits.add(v2); visits.add(v3);
        return visits;
    }

}
