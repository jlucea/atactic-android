package io.atactic.android.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.atactic.android.R;
import io.atactic.android.fragment.CampaignInfoFragment;
import io.atactic.android.fragment.CampaignTargetsFragment;
import io.atactic.android.fragment.ParticipantRankingFragment;
import io.atactic.android.model.Campaign;
import io.atactic.android.model.Participation;
import io.atactic.android.model.User;

public class CampaignDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = "CampaignDetailActivity";

    private ArcProgress progressIndicatorView;
    private TextView progressValuesTextView;
    private TextView questNameTextView;
    private TextView questBriefingTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_detail);

        final String FRAGMENT_TITLE_GENERAL = "Detalles";
        final String FRAGMENT_TITLE_TARGET_ACCOUNTS = "Objetivos";
        final String FRAGMENT_TITLE_RANKING = "Ranking";

        // Display back button in action bar
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get references to the views in the header
        progressIndicatorView = findViewById(R.id.arc_questdetail_arcprogress);
        progressValuesTextView = findViewById(R.id.tv_progress_values);
        questNameTextView = findViewById(R.id.tv_questdetail_name);
        questBriefingTextView = findViewById(R.id.tv_questdetail_briefing);

        // Get info to display from intent
        Participation participation = getParticipationFromIntent(getIntent());

        // Display header data
        displayParticipationData(participation);

        // Set up multi-tab panel as a ViewPagerAdapter containing different fragments
        ViewPager viewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter fragmentAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        /**
         * DETAILS FRAGMENT
         * TODO Use bundle instead of constructor parameter
         */
        CampaignInfoFragment infoFragment = new CampaignInfoFragment(participation);

        fragmentAdapter.addFragment(infoFragment, FRAGMENT_TITLE_GENERAL);


        if (this.shouldDisplayTargets(participation)) {

            // Add participation targets fragment
            CampaignTargetsFragment targetListFragment = new CampaignTargetsFragment();
            Bundle args = new Bundle();
            args.putInt("pid",participation.getId());
            targetListFragment.setArguments(args);
            fragmentAdapter.addFragment(targetListFragment, FRAGMENT_TITLE_TARGET_ACCOUNTS);
        }

        if (this.shouldDisplayRanking()) {

            // Add ranking fragment
            ParticipantRankingFragment participantRankingFragment = new ParticipantRankingFragment();

            // Set arguments
            Bundle args = new Bundle();
            args.putInt(ParticipantRankingFragment.PARAM_KEY_CAMPAIGNID, participation.getCampaign().getId());
            participantRankingFragment.setArguments(args);

            // Add fragment
            fragmentAdapter.addFragment(participantRankingFragment, FRAGMENT_TITLE_RANKING);
        }

        viewPager.setAdapter(fragmentAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Log.v(LOG_TAG, "Options item selected");
        this.finish();
        return super.onOptionsItemSelected(item);
    }


    private boolean shouldDisplayTargets(Participation participation){
        return Campaign.CAMPAIGN_TYPE_SEGMENT_COVERAGE.equals(participation.getCampaign().getType());
    }


    private boolean shouldDisplayRanking(){
        return true;
    }

/*
    public void displayTargets(List<Account> data){
        targetListFragment.setContent(data);
    }
    */

    private void displayParticipationData(Participation participation){
        int roundedProgressValue = (int)(participation.getCurrentProgress()*100);
        if (roundedProgressValue > 100) {
            progressIndicatorView.setProgress(100);
        }else{
            progressIndicatorView.setProgress(roundedProgressValue);
        }
        questNameTextView.setText(participation.getCampaign().getName());
        questBriefingTextView.setText(participation.getCampaign().getBriefing());

        // Format progress values and include units, if applicable, based on Campaign Type
        String progressValuesStr;
        if (participation.getCampaign().getType().equals(Campaign.CAMPAIGN_TYPE_SALES_TARGET)||
                participation.getCampaign().getType().equals(Campaign.CAMPAIGN_TYPE_SALES_TARGET_REFERENCED)){

            String currentValueStr = NumberFormat.getNumberInstance(Locale.forLanguageTag("ES")).format((int)participation.getCurrentValue());
            String targetValueStr = NumberFormat.getNumberInstance(Locale.forLanguageTag("ES")).format((int)participation.getTargetValue());

            progressValuesStr = currentValueStr + " " + participation.getCampaign().getCurrency() + " / "
                    + targetValueStr + " " + participation.getCampaign().getCurrency() ;

        }else{
            progressValuesStr = (int)participation.getCurrentValue() + " / " + (int)participation.getTargetValue();
        }
        progressValuesTextView.setText(progressValuesStr);
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

        campaign.setId(intent.getIntExtra("questId",0));
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
        participation.setCurrentValue(intent.getDoubleExtra("currentValue", 0));
        participation.setTargetValue(intent.getDoubleExtra("targetValue", 0));
        participation.setCurrentProgress(intent.getDoubleExtra("currentProgress", 0));

        if (campaign.getType().equals(Campaign.CAMPAIGN_TYPE_SALES_TARGET) || campaign.getType().equals(Campaign.CAMPAIGN_TYPE_SALES_TARGET_REFERENCED)){
            campaign.setCurrency(intent.getStringExtra("currency"));
        }

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
