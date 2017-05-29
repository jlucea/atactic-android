package app.smartpath.android.smartpath.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.smartpath.android.smartpath.R;
import app.smartpath.android.smartpath.misc.QuestListAdapter;
import app.smartpath.android.smartpath.misc.SmartPathApplication;
import app.smartpath.android.smartpath.connect.HttpRequestHandler;

/**
 * This activity holds a recycler view that displays a scrolling list of active quests.
 *
 * The data shown is gathered from an online API through an asynchronous request task that returns
 * results in JSON format. This response is parsed and converted into a viewable format.
 *
 * The activity implements a click listeners for the elements in the view as well as a menu bar
 * that is also clickable.
 *
 * @author Jaime Lucea
 * @date May 2017
 */
public class QuestListActivity extends AppCompatActivity implements QuestListAdapter.ListItemClickListener {

    private RecyclerView questListRecyclerView;

    private QuestListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_list);

        // Get reference to the RecyclerView component
        questListRecyclerView = (RecyclerView) findViewById(R.id.rv_quest_list);

        /* Set the layout manager for the RecyclerView */
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        questListRecyclerView.setLayoutManager(layoutManager);

        /*
         * This setting improves performance if changes in content
          * do not change the child layout size in the RecyclerView
         */
        questListRecyclerView.setHasFixedSize(true);

        /*
         * Instantiate adapter. The adapter is responsible for linking the quest data
         * with the Views that will display quest data.
         */
        adapter = new QuestListAdapter(this);
        questListRecyclerView.setAdapter(adapter);

        /*
         * Asynchronous request gets the list of quests for the current user
         * and prints them on the UI
         */
        new QuestListAsyncHttpRequest().execute();
    }

    @Override
    public void onListItemClick(int clickedItemIdex) {
        Toast.makeText(this, "Item " + clickedItemIdex + " clicked", Toast.LENGTH_SHORT).show();

    }

    /**
     * This class' execute method sends an asynchronous http request to the server
     * and displays the result on the UI.
     *
     */
    public class QuestListAsyncHttpRequest extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... params) {

            // Retrieve user identification from global variables
            int userId = ((SmartPathApplication)QuestListActivity.this.getApplication()).getUserId();

            // Send Http request and receive JSON response
            String response = HttpRequestHandler.sendQuestListRequest(userId);

            // Return JSON array containing the data to show in the view
            try {
                return new JSONArray(response);

            }catch (JSONException ex){
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray questData) {
            if (questData != null && questData.length() > 0) {
                adapter.setQuestListData(questData);
            }
            // TODO else show NO QUESTS WHERE FOUND();

            // Say hello
            String username = ((SmartPathApplication)QuestListActivity.this.getApplication()).getUserName();
            Toast.makeText(QuestListActivity.this, "Hello "+username, Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Returns a readable text string format out of the JSON string holding a quest list.
     *
     * Something like "INTENSITY - Yearly Visits - (14/20)"
     *
     * @param JSONResponse
     * @return Chain of printable text strings
     */
    private static String[] formatQuestStrings(String JSONResponse){
        try {
            JSONArray jsonArray = new JSONArray(JSONResponse);
            String[] qStrings = new String[jsonArray.length()];

            for (int i=0; i < jsonArray.length(); i++){
                JSONObject q = jsonArray.getJSONObject(i);

                String type = q.getJSONObject("campaign").getString("type");
                String name = q.getJSONObject("campaign").getString("name");
                String currentStep = q.getString("currentStep");
                String totalSteps = q.getString("totalSteps");

                qStrings[i] = type + " - " + name + "  ("+currentStep+"/"+totalSteps+")" + "\n";
            }
            return qStrings;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.map_button) {
            // TODO Intent that goes to MAP SCREEN
            // Toast.makeText(this,"Go to map view",Toast.LENGTH_LONG).show();
            Intent i = new Intent(QuestListActivity.this, QuestMapActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
