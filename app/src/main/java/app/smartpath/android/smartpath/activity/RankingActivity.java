package app.smartpath.android.smartpath.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;

import app.smartpath.android.smartpath.R;
import app.smartpath.android.smartpath.connect.HttpRequestHandler;
import app.smartpath.android.smartpath.misc.QuestListAdapter;
import app.smartpath.android.smartpath.misc.RankingAdapter;

public class RankingActivity extends AppCompatActivity {

    private RecyclerView rankingRecyclerView;

    private RankingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        // Get reference to the RecyclerView component
        rankingRecyclerView = (RecyclerView) findViewById(R.id.rv_ranking_list);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setHasFixedSize(true);

        adapter = new RankingAdapter();
        rankingRecyclerView.setAdapter(adapter);

        new RankingAsyncHttpRequest().execute();
    }


    public class RankingAsyncHttpRequest extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... params) {
            return HttpRequestHandler.sendRankingRequest();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            Log.d("RankingActivity", jsonArray.toString());
            adapter.setContent(jsonArray);
        }
    }


}
