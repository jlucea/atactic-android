package io.atactic.android.activity;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.atactic.android.R;
import io.atactic.android.datahandler.ParticipationListDataHandler;
import io.atactic.android.datahandler.ParticipationListPresenter;
import io.atactic.android.element.AtacticApplication;
import io.atactic.android.element.BottomNavigationBarClickListenerFactory;
import io.atactic.android.element.ParticipationListAdapter;
import io.atactic.android.model.Participation;

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
 */
public class CampaignListActivity extends AppCompatActivity
        implements ParticipationListAdapter.ListItemClickListener, ParticipationListPresenter {

    private ParticipationListDataHandler dataHandler;

    private RecyclerView recyclerView;
    private ParticipationListAdapter adapter;

    private SwipeRefreshLayout refreshLayout;
    private ProgressBar loadingIndicator;

    private TextView statusMessageTextView;

    private static final String LOG_TAG = CampaignListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_list);

        loadingIndicator = findViewById(R.id.questlist_loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        /*
         * Get the reference to the bottom navigation bar and add an ItemSelectedListener
         */
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_quests);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                BottomNavigationBarClickListenerFactory.getClickListener(getBaseContext(),
                        this.getClass()));

        statusMessageTextView = findViewById(R.id.tv_campaign_list_status_message);

        refreshLayout = findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(() -> reloadData());

        // Get reference to the RecyclerView component
        recyclerView = findViewById(R.id.rv_quest_list);

        // Set the layout manager for the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        /*
         * This setting improves performance if changes in content
         * do not change the child layout size in the RecyclerView
         */
        recyclerView.setHasFixedSize(true);

        /*
         * Instantiate fragmentAdapter. The fragmentAdapter is responsible for linking the quest data
         * with the Views that will display quest data.
         */
        adapter = new ParticipationListAdapter(this);
        recyclerView.setAdapter(adapter);

        // Retrieve user identification from global variables
        int userId = ((AtacticApplication)getApplication()).getUserId();
        Log.v(LOG_TAG, "Requesting campaign list for user... " + userId);

        /*
         * Asynchronous request gets the list of quests for the current user
         * and prints them on the UI
         */
        dataHandler = new ParticipationListDataHandler(this);
        dataHandler.getData(userId);
    }


    void reloadData(){
        // Retrieve user identification from global variables
        int userId = ((AtacticApplication)getApplication()).getUserId();
        Log.v(LOG_TAG, "Reloading campaign list...");

        /*
         * Asynchronous request gets the list of quests for the current user
         * and prints them on the UI
         */
        dataHandler.getData(userId);
    }


    @Override
    public void onListItemClick(int clickedItemIdex) {

        Participation participation = adapter.getItem(clickedItemIdex);
        // Toast.makeText(CampaignListActivity.this,"Participation " + participation.getId() + " clicked", Toast.LENGTH_SHORT).show();
        goToCampaignDetail(participation);
    }

    private void goToCampaignDetail(Participation participation){
        //
        // Create an intent and put the quest information to display in the detail view
        //
        Intent i = new Intent(CampaignListActivity.this, CampaignDetailActivity.class);

        i.putExtra("participationId",participation.getId());

        i.putExtra("questName", participation.getCampaign().getName());
        i.putExtra("questType", participation.getCampaign().getType());
        i.putExtra("questSummary", participation.getCampaign().getBriefing());
        i.putExtra("questLongDesc", participation.getCampaign().getDescription());

        i.putExtra("questDeadline", participation.getCampaign().getEndDate().getTime());

        i.putExtra("completionScore", participation.getCampaign().getCompletionScore());
        i.putExtra("currentProgress",participation.getCurrentProgress());

        String questOwnerStr = participation.getCampaign().getOwner().getFirstName() + " "
                + participation.getCampaign().getOwner().getLastName()
                + "\n" + participation.getCampaign().getOwner().getPosition();
        i.putExtra("questOwner", questOwnerStr);

        startActivity(i);
    }


    public void displayCampaignList(List<Participation> participationList) {
        adapter.setData(participationList);

        loadingIndicator.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);
        statusMessageTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayMessage(String message) {

        statusMessageTextView.setText(message);

        refreshLayout.setRefreshing(false);
        loadingIndicator.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        statusMessageTextView.setVisibility(View.VISIBLE);

        // Toast.makeText(CampaignListActivity.this, message, Toast.LENGTH_LONG).show();
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
            Intent i = new Intent(CampaignListActivity.this, ProfileActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
}
