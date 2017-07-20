package app.smartpath.android.smartpath.misc;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.widget.Toast;

import app.smartpath.android.smartpath.R;
import app.smartpath.android.smartpath.activity.CheckInActivity;
import app.smartpath.android.smartpath.activity.QuestListActivity;
import app.smartpath.android.smartpath.activity.QuestMapActivity;


public class BottomNavigationBarClickListener implements BottomNavigationView.OnNavigationItemSelectedListener {

    Context context;
    Class currentActivity;

    public BottomNavigationBarClickListener(Context context, Class currentActivity){
        this.context = context;
        this.currentActivity = currentActivity;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Class destination = null;
        switch (item.getItemId()) {
            case R.id.action_map:
                // Toast.makeText(context, "Map icon clicked", Toast.LENGTH_SHORT).show();
                destination = QuestMapActivity.class;
                break;

            case R.id.action_checkin:
                // Toast.makeText(context, "CheckIn icon clicked", Toast.LENGTH_SHORT).show();
                destination = CheckInActivity.class;
                break;

            case R.id.action_quests:
                // Toast.makeText(context, "Quests icon clicked", Toast.LENGTH_SHORT).show();
                destination = QuestListActivity.class;
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
