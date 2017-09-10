package io.atactic.android.activity;

import android.content.Intent;
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

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.atactic.android.R;
import io.atactic.android.element.QuestDetailDescriptionFragment;
import io.atactic.android.element.QuestDetailGeneralFragment;

public class QuestDetailActivity extends AppCompatActivity {

    private ImageView backImg;

    private ArcProgress progressIndicatorView;
    private TextView questNameTextView;
    private TextView questBriefingTextView;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String qNameStr;
    private String qBriefingStr;
    private int current;
    private int goal;

    String questOwner;
    String formattedDeadline;
    String rewardText;

    String longDescription;

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
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        QuestDetailGeneralFragment fragment1 = new QuestDetailGeneralFragment(
                questOwner, formattedDeadline, rewardText);
        QuestDetailDescriptionFragment fragment2 = new QuestDetailDescriptionFragment(
                longDescription);

        adapter.addFragment(fragment1, "General");
        adapter.addFragment(fragment2,"Descripción");

        viewPager.setAdapter(adapter);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void readQuestData(Intent intent){
        // Header info
        qNameStr = intent.getStringExtra("questName");
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
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyLocalizedPattern("yyyy-MM-dd");

            Date endDate = sdf.parse(endDateStr, new ParsePosition(0));
            Date now = Calendar.getInstance().getTime();

            long timeDiff = endDate.getTime() - now.getTime();
            long daysDiff = TimeUnit.MILLISECONDS.toDays(timeDiff);

            formattedDeadline = sdf.format(endDate) + "\n" + "Quedan "+daysDiff+" días";
            // questDeadlineTextView.setText(sdf.format(endDate) + "\n" + "Quedan "+daysDiff+" días");

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


}
