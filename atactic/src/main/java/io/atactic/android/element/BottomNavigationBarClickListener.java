package io.atactic.android.element;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import io.atactic.android.R;
import io.atactic.android.activity.AccountListActivity;
import io.atactic.android.activity.CampaignListActivity;
import io.atactic.android.activity.ProfileActivity;
import io.atactic.android.activity.MapActivity;


public class BottomNavigationBarClickListener implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private Class currentActivity;

    protected BottomNavigationBarClickListener(Context context, Class currentActivity){
        this.context = context;
        this.currentActivity = currentActivity;
    }

    public void setContext(Context newContext){
        this.context = newContext;
    }

    public void setCurrentActivity (Class newActivity) {
        this.currentActivity = newActivity;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Class destination = null;
        switch (item.getItemId()) {
            case R.id.action_map:
                // Toast.makeText(context, "Map icon clicked", Toast.LENGTH_SHORT).show();
                destination = MapActivity.class;
                break;

            case R.id.action_quests:
                // Toast.makeText(context, "Quests icon clicked", Toast.LENGTH_SHORT).show();
                destination = CampaignListActivity.class;
                break;

            case R.id.action_profile:
                // Toast.makeText(context, "Profile icon clicked", Toast.LENGTH_SHORT).show();
                destination = ProfileActivity.class;
                break;

            case R.id.action_priorities:
                // Toast.makeText(context, "Priorities icon clicked", Toast.LENGTH_SHORT).show();
                destination = AccountListActivity.class;
                break;
        }

        if (destination!=currentActivity) {
            Intent i = new Intent(this.context, destination);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.context.startActivity(i);

            return true;
        }else{
            return false;
        }

    }


}