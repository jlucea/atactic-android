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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.atactic.android.R;
import io.atactic.android.datahandler.ParticipationTargetsDataHandler;
import io.atactic.android.element.CampaignDescriptionFragment;
import io.atactic.android.element.CampaignInfoFragment;
import io.atactic.android.element.CampaignTargetsFragment;
import io.atactic.android.model.Account;
import io.atactic.android.model.Campaign;
import io.atactic.android.model.Participation;
import io.atactic.android.model.User;

public class CampaignDetailActivity extends AppCompatActivity {

    private Participation participation;

    ArcProgress progressIndicatorView;
    TextView questNameTextView;
    TextView questBriefingTextView;
    ViewPagerAdapter fragmentAdapter;

    CampaignInfoFragment generalDataFragment;
    CampaignDescriptionFragment longDescriptionFragment;
    CampaignTargetsFragment targetListFragment;

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

        ImageView backImgButton = findViewById(R.id.img_back);
        backImgButton.setOnClickListener(v -> finish());

        /*
         * Get info to display from intent variables
         */
        participation = getParticipationFromIntent(getIntent());

        displayParticipationData(participation);

        // Set up multi-tab panel
        ViewPager viewPager = findViewById(R.id.viewpager);
        fragmentAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        generalDataFragment = new CampaignInfoFragment(participation);
        longDescriptionFragment = new CampaignDescriptionFragment(
                participation.getCampaign().getDescription());

        fragmentAdapter.addFragment(generalDataFragment, "General");
        fragmentAdapter.addFragment(longDescriptionFragment, "Descripción");

        if ("SEGMENT_COVERAGE".equals(participation.getCampaign().getType())) {

            targetListFragment = new CampaignTargetsFragment();
            fragmentAdapter.addFragment(targetListFragment, "Objetivos");
        }

        viewPager.setAdapter(fragmentAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if ("SEGMENT_COVERAGE".equals(participation.getCampaign().getType())) {
            /*
             * Call ParticipationTargetsDataHandler to request campaign targets in order to fill the fragment.
             * Once finished, the DataHandler is supposed to call the displayTargets method
             */
            new ParticipationTargetsDataHandler(this).getData(participation.getId());
        }
    }


    public void displayTargets(List<Account> data){
        targetListFragment.setContent(data);
    }

    private void displayParticipationData(Participation participation){
        progressIndicatorView.setProgress((int)(participation.getCurrentProgress()*100));
        questNameTextView.setText(participation.getCampaign().getName());
        questBriefingTextView.setText(participation.getCampaign().getBriefing());
    }


    /**
     * Reads the campaign data to display from the Intent and stores them
     * in the local properties
     *
     * @param intent Intent containing the campaign data to display
     */
    private Participation getParticipationFromIntent(Intent intent){

        Participation participation = new Participation();
        Campaign campaign = new Campaign();

        participation.setId(intent.getIntExtra("participationId",0));

        campaign.setName(intent.getStringExtra("questName"));
        campaign.setType(intent.getStringExtra("questType"));
        campaign.setBriefing(intent.getStringExtra("questSummary"));
        campaign.setDescription(intent.getStringExtra("questLongDesc"));

        User administrator = new User();
        // The full name and position of the administrator is stored in the First Name property
        administrator.setFirstName(intent.getStringExtra("questOwner"));
        campaign.setOwner(administrator);

        Date endDate = new Date();
        endDate.setTime(intent.getLongExtra("questDeadline", -1));
        campaign.setEndDate(endDate);

        campaign.setCompletionScore(intent.getIntExtra("completionScore",0));

        participation.setCurrentProgress(intent.getDoubleExtra("currentProgress", 0));

        participation.setCampaign(campaign);
        return participation;
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
