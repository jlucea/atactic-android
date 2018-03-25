package io.atactic.android.element;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import io.atactic.android.R;
import io.atactic.android.connect.HttpRequestHandler;

/**
 * Created by Jaime on 28/9/17.
 */

public class QuestDetailTargetsFragment extends Fragment {

    // This variable will hold the data to display
    private JSONArray questTargetsJSONArray;
    private RecyclerView targetListRecyclerView;
    private TargetListAdapter adapter;

    public QuestDetailTargetsFragment(){}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.v("QuestTargetsFragment", "Fragment OnCreateView");

        View view = inflater.inflate(R.layout.fragment_quest_detail_3,container, false);
        targetListRecyclerView = view.findViewById(R.id.rv_target_list);
        targetListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        targetListRecyclerView.setHasFixedSize(true);

        adapter = new TargetListAdapter();
        targetListRecyclerView.setAdapter(adapter);

        if (questTargetsJSONArray != null) {
            adapter.setContent(questTargetsJSONArray);
        }else{
            Log.w("QuestTargetsFragment", "Operating fragment with NULL data");
        }
        return view;
    }


    public void setContent(JSONArray content){

        this.questTargetsJSONArray = content;
    }



}
