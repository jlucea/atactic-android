package io.atactic.android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.atactic.android.R;

public class QuestDetailActivity extends AppCompatActivity {

    private ArcProgress progressIndicatorView;
    private TextView questNameTextView;
    private TextView questBriefingTextView;
    private TextView questLongDescriptionTextView;
    private TextView questOwnerTextView;
    private TextView questDeadlineTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_detail);

        /*
         * Get references to the views
         */
        progressIndicatorView = (ArcProgress) findViewById(R.id.arc_questdetail_arcprogress);
        questNameTextView = (TextView) findViewById(R.id.tv_questdetail_name);
        questBriefingTextView = (TextView) findViewById(R.id.tv_questdetail_briefing);
        questLongDescriptionTextView = (TextView) findViewById(R.id.tv_questdetail_longdesc);
        questOwnerTextView = (TextView) findViewById(R.id.tv_questdetail_owner);
        questDeadlineTextView = (TextView) findViewById(R.id.tv_questdetail_deadline);

        /*
         * Get all info to display from the intent and set the view values
         */
        Intent intent = getIntent();
        int current = intent.getIntExtra("currentStep",0);
        int goal = intent.getIntExtra("totalSteps",10);

        progressIndicatorView.setProgress(current*100/goal);
        progressIndicatorView.setBottomText(current + " / " + goal);
        questNameTextView.setText(getIntent().getStringExtra("questName"));
        questBriefingTextView.setText(getIntent().getStringExtra("questSummary"));
        questLongDescriptionTextView.setText(getIntent().getStringExtra("questLongDesc"));
        questOwnerTextView.setText(getIntent().getStringExtra("questOwner"));

        String unformattedDeadline = getIntent().getStringExtra("questDeadline");
        String endDateStr = unformattedDeadline.split("T")[0];

        try {
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyLocalizedPattern("yyyy-MM-dd");

            Date endDate = sdf.parse(endDateStr, new ParsePosition(0));
            Date now = Calendar.getInstance().getTime();

            long timeDiff = endDate.getTime() - now.getTime();
            long daysDiff = TimeUnit.MILLISECONDS.toDays(timeDiff);

            questDeadlineTextView.setText(sdf.format(endDate) + "\n" + "Quedan "+daysDiff+" días");

        }catch (Exception e){
            e.printStackTrace();
            questDeadlineTextView.setVisibility(View.INVISIBLE);
            // TODO Also hide icon
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
            Intent i = new Intent(QuestDetailActivity.this, ProfileActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

}
