package io.atactic.android.activity;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.atactic.android.R;
import io.atactic.android.network.request.ParticipationTargetsRequest;
import io.atactic.android.element.AtacticApplication;
import io.atactic.android.element.QuestDetailDescriptionFragment;
import io.atactic.android.element.QuestDetailGeneralFragment;
import io.atactic.android.element.QuestDetailTargetsFragment;

public class QuestDetailActivity extends AppCompatActivity {

    private ImageView backImg;

    private ArcProgress progressIndicatorView;
    private TextView questNameTextView;
    private TextView questBriefingTextView;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int participationId;
    private String qNameStr;
    private String qTypeStr;
    private String qBriefingStr;
    private int current;
    private int goal;

    String questOwner;
    String formattedDeadline;
    String rewardText;

    String longDescription;

    QuestDetailTargetsFragment fragment3;
    ViewPagerAdapter fragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_detail);

        /*
         * Get references to the views in the header
         */
        progressIndicatorView = findViewById(R.id.arc_questdetail_arcprogress);
        questNameTextView = findViewById(R.id.tv_questdetail_name);
        questBriefingTextView = findViewById(R.id.tv_questdetail_briefing);
        backImg = findViewById(R.id.img_back);

        backImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        /*
         * Get info to display from intent variables
         */
        final int userId = ((AtacticApplication)QuestDetailActivity.this.getApplication()).getUserId();
        readQuestData(getIntent());

        /*
         * Set values for the views in the header
         */
        progressIndicatorView.setProgress(current*100/goal);
        progressIndicatorView.setBottomText(current + " / " + goal);
        questNameTextView.setText(qNameStr);
        questBriefingTextView.setText(qBriefingStr);

        // Set up multi-tab panel
        viewPager = findViewById(R.id.viewpager);
        fragmentAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        QuestDetailGeneralFragment fragment1 = new QuestDetailGeneralFragment(
                questOwner, formattedDeadline, rewardText);
        QuestDetailDescriptionFragment fragment2 = new QuestDetailDescriptionFragment(
                longDescription);

        fragmentAdapter.addFragment(fragment1, "General");
        fragmentAdapter.addFragment(fragment2, "Descripción");


        if ("TARGETED".equals(qTypeStr)) {

            fragment3 = new QuestDetailTargetsFragment();
            fragmentAdapter.addFragment(fragment3, "Objetivos");

            // Get user's location (latitude and longitude)
            FusedLocationProviderClient locationProvider = LocationServices.getFusedLocationProviderClient(this);
            try {
                locationProvider.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // TODO Work with DOUBLE precision position
                                    float userLocationLongitude = (float) location.getLongitude();
                                    float userLocationLatitude = (float) location.getLatitude();
                                    /*
                                    userLocationTextView.setText(userLocationLatitude + " , "
                                        + userLocationLongitude);
                                    */
                                    /*
                                    Log.d(LOG_TAG, "User LATITUDE: " + userLocationLatitude);
                                    Log.d(LOG_TAG, "User LONGITUDE: " + userLocationLongitude);
                                    */

                                    new QuestTargetsHttpRequest().execute(new QuestTargetsRequestParams(
                                            userId, participationId,
                                            userLocationLatitude, userLocationLongitude));
                                }
                            }
                        });
            }catch(SecurityException se){
                Log.w("LOG_TAG", "Security exception caught.·);" +
                        " Device location will probably be unavailable.");
                Log.w("LOG_TAG", se);

                Toast.makeText(QuestDetailActivity.this,
                        R.string.err_no_user_location, Toast.LENGTH_LONG).show();
                finish();
            }
        }

        viewPager.setAdapter(fragmentAdapter);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Reads the quest data to display from the Intent and stores it
     * in the local String variable
     *
     * @param intent
     */
    private void readQuestData(Intent intent){

        participationId = intent.getIntExtra("participationId",0);

        // Header info
        qNameStr = intent.getStringExtra("questName");
        qTypeStr = intent.getStringExtra("questType");
        qBriefingStr = intent.getStringExtra("questSummary");

        // Data to display on ArcProgress view
        current = intent.getIntExtra("currentStep",0);
        goal = intent.getIntExtra("totalSteps",10);

        questOwner = intent.getStringExtra("questOwner");

        // Parse and format deadline information
        String unformattedDeadline = intent.getStringExtra("questDeadline");
        formattedDeadline = "";
        String endDateStr = unformattedDeadline.split("T")[0];
        try {
            SimpleDateFormat parsingDateFormat = new SimpleDateFormat();
            parsingDateFormat.applyLocalizedPattern("yyyy-MM-dd");

            Date endDate = parsingDateFormat.parse(endDateStr, new ParsePosition(0));
            Date now = Calendar.getInstance().getTime();

            long timeDiff = endDate.getTime() - now.getTime();
            long daysDiff = TimeUnit.MILLISECONDS.toDays(timeDiff);

            // SimpleDateFormat writingDateFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault());
            SimpleDateFormat writingDateFormat = new SimpleDateFormat("dd/MM/yyyy");

            formattedDeadline = writingDateFormat.format(endDate) + "\n" + "Quedan "+daysDiff+" días";

        }catch (Exception e){
            // Log.(e);
            e.printStackTrace();
            // TODO Hide icon and textView
        }

        // Get reward details
        int visitScore = intent.getIntExtra("visitScore",0);
        // int completionScore = intent.getIntExtra("completionScore",0);
        rewardText = visitScore + " puntos por visita";

        longDescription = intent.getStringExtra("questLongDesc");
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    private class QuestTargetsRequestParams {
        int userId;
        int participationId;
        float userLocationLatitude;
        float userLocationLongitude;

        public QuestTargetsRequestParams(int uid, int pid, float lat, float lon){
            this.userId = uid;
            this.participationId = pid;
            this.userLocationLatitude = lat;
            this.userLocationLongitude = lon;
        }

    }

    public class QuestTargetsHttpRequest extends AsyncTask<QuestTargetsRequestParams, Void, String> {

        @Override
        protected String doInBackground(QuestTargetsRequestParams... params) {

            // Retrieve user identification from global variables
            int userId = params[0].userId;
            int participationId = params[0].participationId;

            return ParticipationTargetsRequest.send(userId, participationId,
                    params[0].userLocationLatitude, params[0].userLocationLongitude);
        }

        @Override
        protected void onPostExecute(String jsonArray) {
            Log.d("QuestTargetsHttpRequest", "JSON array received: " + jsonArray);

            try {
                JSONArray targets = new JSONArray(jsonArray);

                fragment3.setContent(targets);
                fragmentAdapter.notifyDataSetChanged();

            }catch (JSONException err){
                Log.e("QuestTargetsHttpRequest","Error while parsing targets array", err);
            }

        }
    }



}
