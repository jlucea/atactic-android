package io.atactic.android.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.atactic.android.R;
import io.atactic.android.connect.HttpRequestHandler;
import io.atactic.android.element.AtacticApplication;
import io.atactic.android.element.RankingAdapter;

public class RankingActivity extends AppCompatActivity {

    private ImageView backImageButton;
    private TextView userRankTextView;
    private TextView userScoreTextView;

    private RecyclerView rankingRecyclerView;
    private RankingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        // Get references to header fields
        userRankTextView = findViewById(R.id.tv_ranking_user_rank);
        userScoreTextView = findViewById(R.id.tv_ranking_user_score);

        backImageButton = findViewById(R.id.ctrl_ranking_back);

        backImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // Get reference to the RecyclerView component
        rankingRecyclerView = findViewById(R.id.rv_ranking_list);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setHasFixedSize(true);

        adapter = new RankingAdapter();
        rankingRecyclerView.setAdapter(adapter);

        new RankingAsyncHttpRequest().execute();
    }


    public class RankingAsyncHttpRequest extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... params) {

            // Retrieve user identification from global variables
            int userId = ((AtacticApplication)RankingActivity.this.getApplication()).getUserId();

            return HttpRequestHandler.sendRankingRequest(userId);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            Log.d("RankingActivity", jsonArray.toString());

            int userId = ((AtacticApplication)RankingActivity.this.getApplication()).getUserId();
            Log.d("RankingActivity", "User ID = " + userId);

            adapter.setContent(jsonArray, userId);

            Log.d("RankingActivity", "Starting iteration of JSON Array (length = " + jsonArray.length() + ")");
            // Iterate the jsonArray until we find userId
            // Then get the position and score and print them on the header text views

            for (int r=0; r < jsonArray.length(); r++){
                try {
                    JSONObject rankedUser = jsonArray.getJSONObject(r);
                    if (rankedUser.getInt("userId")==userId){
                        Log.v("RankingActivity", "Found user " + userId + " in the ranking data");
                        userRankTextView.setText(Integer.toString(r+1));
                        int userScore = rankedUser.getInt("score");
                        Log.d("RankingActivity", "User score is " + userScore);
                        userScoreTextView.setText(Integer.toString(userScore));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }


}
