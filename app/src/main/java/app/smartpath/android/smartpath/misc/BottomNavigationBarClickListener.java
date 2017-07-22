package app.smartpath.android.smartpath.misc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.widget.Toast;

import app.smartpath.android.smartpath.R;
import app.smartpath.android.smartpath.activity.ProfileActivity;
import app.smartpath.android.smartpath.activity.QuestListActivity;
import app.smartpath.android.smartpath.activity.MapActivity;


public class BottomNavigationBarClickListener implements BottomNavigationView.OnNavigationItemSelectedListener {

    Context context;
    Class currentActivity;

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
                destination = QuestListActivity.class;
                break;

            case R.id.action_profile:
                // Toast.makeText(context, "Profile icon clicked", Toast.LENGTH_SHORT).show();
                destination = ProfileActivity.class;
                break;
        }

        if (destination!=currentActivity) {
            Intent i = new Intent(context, destination);
            context.startActivity(i);

            return true;
        }else{
            return false;
        }

    }


}
