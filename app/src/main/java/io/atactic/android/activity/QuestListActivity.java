package io.atactic.android.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.atactic.android.R;
import io.atactic.android.element.AtacticApplication;
import io.atactic.android.element.BottomNavigationBarClickListenerFactory;
import io.atactic.android.element.QuestListAdapter;
import io.atactic.android.connect.HttpRequestHandler;

/**
 * This activity holds a recycler view that displays a scrolling list of active quests.
 *
 * The data shown is gathered from an online API through an asynchronous request task that returns
 * results in JSON format. This response is parsed and converted into a viewable format.
 *
 * The activity implements a click listeners for the elements in the view as well as a top_menu_items bar
 * that is also clickable.
 *
 * @author Jaime Lucea
 * @date May 2017
 */
public class QuestListActivity extends AppCompatActivity
        implements QuestListAdapter.ListItemClickListener {

    private RecyclerView questListRecyclerView;
    private BottomNavigationView bottomNavigationView;

    private QuestListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_list);

        /*
         * Get the reference to the bottom navigation bar and add an ItemSelectedListener
         */
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_quests);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                BottomNavigationBarClickListenerFactory.getClickListener(getBaseContext(),
                        this.getClass()));

        // Get reference to the RecyclerView component
        questListRecyclerView = (RecyclerView) findViewById(R.id.rv_quest_list);

        // Set the layout manager for the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        questListRecyclerView.setLayoutManager(layoutManager);

        /*
         * This setting improves performance if changes in content
         * do not change the child layout size in the RecyclerView
         */
        questListRecyclerView.setHasFixedSize(true);

        /*
         * Instantiate fragmentAdapter. The fragmentAdapter is responsible for linking the quest data
         * with the Views that will display quest data.
         */
        adapter = new QuestListAdapter(this);
        questListRecyclerView.setAdapter(adapter);

        /*
         * Asynchronous request gets the list of quests for the current user
         * and prints them on the UI
         */
        new QuestListAsyncHttpRequest().execute();

        // Activate the check-in floating button
        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab_checkin);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(QuestListActivity.this,CheckInActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void onListItemClick(int clickedItemIdex) {
        try {
            // Recover and parse quest data, which are held in the fragmentAdapter
            JSONObject quest = adapter.getQuest(clickedItemIdex);
            String questName = quest.getJSONObject("campaign").getString("name");
            String questType = quest.getJSONObject("campaign").getString("type");
            String questSummary = quest.getJSONObject("campaign").getString("summary");
            String questLongDesc = quest.getJSONObject("campaign").getString("description");
            String questDeadline = quest.getJSONObject("campaign").getString("endDate");
            String questOwnerFirstName = quest.getJSONObject("campaign").getJSONObject("owner")
                    .getString("firstName");
            String questOwnerLastName = quest.getJSONObject("campaign").getJSONObject("owner")
                    .getString("lastName");
            String questOwnerPosition = quest.getJSONObject("campaign").getJSONObject("owner")
                    .getString("position");
            int currentStep = quest.getInt("currentStep");
            int totalSteps = quest.getInt("totalSteps");
            int visitScore = quest.getJSONObject("campaign").getInt("visitScore");
            int completionScore = quest.getJSONObject("campaign").getInt("completionScore");
            int participationId = quest.getInt("participationId");

            // Toast.makeText(this, "Quest " + questName + " clicked", Toast.LENGTH_SHORT).show();

            // Create an intent and put the quest information to display in the detail view
            Intent i = new Intent(QuestListActivity.this, QuestDetailActivity.class);
            i.putExtra("questName", questName);
            i.putExtra("questType", questType);
            i.putExtra("questSummary", questSummary);
            i.putExtra("questDeadline", questDeadline);
            i.putExtra("questLongDesc", questLongDesc);
            i.putExtra("currentStep", currentStep);
            i.putExtra("totalSteps", totalSteps);
            i.putExtra("visitScore",visitScore);
            i.putExtra("completionScore",completionScore);
            i.putExtra("participationId",participationId);

            String questOwner = questOwnerFirstName + " " + questOwnerLastName
                    + "\n" + questOwnerPosition;

            i.putExtra("questOwner", questOwner);

            startActivity(i);

        }catch (JSONException jsonex){
            Toast.makeText(this, "Error: " + jsonex.getMessage(), Toast.LENGTH_SHORT).show();
            jsonex.printStackTrace();
        }
    }

    /**
     * This class' execute method sends an asynchronous http request to the server
     * and sends the returning JSON Array to the QuestListAdapter.
     *
     */
    public class QuestListAsyncHttpRequest extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... params) {

            // Retrieve user identification from global variables
            int userId = ((AtacticApplication)QuestListActivity.this.getApplication()).getUserId();

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
            // String username = ((AtacticApplication)QuestListActivity.this.getApplication()).getUserName();
            // Toast.makeText(QuestListActivity.this, "Hello "+username, Toast.LENGTH_LONG).show();
        }

    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Use AppCompatActivity's method getMenuInflater to get a handle on the top_menu_items inflater
        MenuInflater inflater = getMenuInflater();
        // Use the inflater's inflate method to inflate our top_menu_items layout to this top_menu_items
        inflater.inflate(R.menu.top_menu_items, menu);
        // Return true so that the top_menu_items is displayed in the Toolbar
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profile_button) {
            Intent i = new Intent(QuestListActivity.this, ProfileActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
}
