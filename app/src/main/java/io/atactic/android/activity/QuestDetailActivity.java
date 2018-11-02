package io.atactic.android.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.atactic.android.R;
import io.atactic.android.manager.CampaignDataHandler;
import io.atactic.android.model.Account;
import io.atactic.android.element.QuestDetailDescriptionFragment;
import io.atactic.android.element.QuestDetailGeneralFragment;
import io.atactic.android.element.CampaignTargetsFragment;

public class QuestDetailActivity extends AppCompatActivity {

    private int participationId;
    private String qNameStr;
    // private String qTypeStr;
    private String qBriefingStr;

    private double currentProgress;
    // private int current;
    // private int goal;

    String questOwner;
    String formattedDeadline;
    String rewardText;
    String longDescription;

    CampaignTargetsFragment fragment3;
    ViewPagerAdapter fragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_detail);

        /*
         * Get references to the views in the header
         */
        ArcProgress progressIndicatorView = findViewById(R.id.arc_questdetail_arcprogress);
        TextView questNameTextView = findViewById(R.id.tv_questdetail_name);
        TextView questBriefingTextView = findViewById(R.id.tv_questdetail_briefing);

        ImageView backImgButton = findViewById(R.id.img_back);
        backImgButton.setOnClickListener(v -> finish());

        /*
         * Get info to display from intent variables
         */
        getCampaignDataFromIntent(getIntent());

        /*
         * Set values for the views in the header
         */
        progressIndicatorView.setProgress((int)(currentProgress*100));
        questNameTextView.setText(qNameStr);
        questBriefingTextView.setText(qBriefingStr);

        // Set up multi-tab panel
        ViewPager viewPager = findViewById(R.id.viewpager);
        fragmentAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        QuestDetailGeneralFragment fragment1 = new QuestDetailGeneralFragment(
                questOwner, formattedDeadline, rewardText);

        QuestDetailDescriptionFragment fragment2 = new QuestDetailDescriptionFragment(
                longDescription);

        fragment3 = new CampaignTargetsFragment();

        fragmentAdapter.addFragment(fragment1, "General");
        fragmentAdapter.addFragment(fragment2, "Descripción");
        fragmentAdapter.addFragment(fragment3, "Objetivos");

        viewPager.setAdapter(fragmentAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        /*
         * Call CampaignDataHandler to request campaign targets in order to fill the fragment.
         * Once finished, the DataHandler is supposed to call the displayCampaignTargets method
         */
        new CampaignDataHandler(this).getCampaignTargets(participationId);
    }


    public void displayCampaignTargets(List<Account> targets){
        fragment3.setContent(targets);
        fragmentAdapter.notifyDataSetChanged();
    }


    /**
     * Reads the campaign data to display from the Intent and stores them
     * in the local properties
     *
     * @param intent Intent containing the campaign data to display
     */
    private void getCampaignDataFromIntent(Intent intent){

        participationId = intent.getIntExtra("participationId",0);

        // Header info
        qNameStr = intent.getStringExtra("questName");
        // qTypeStr = intent.getStringExtra("questType");
        qBriefingStr = intent.getStringExtra("questSummary");

        // Data to display on ArcProgress view
        // current = intent.getIntExtra("currentStep",0);
        // goal = intent.getIntExtra("totalSteps",10);
        this.currentProgress = intent.getDoubleExtra("currentProgress", 0);

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
        // int visitScore = intent.getIntExtra("visitScore",0);
        int completionScore = intent.getIntExtra("completionScore",0);
        rewardText = completionScore + " puntos por completar la campaña";

        longDescription = intent.getStringExtra("questLongDesc");
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
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

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
