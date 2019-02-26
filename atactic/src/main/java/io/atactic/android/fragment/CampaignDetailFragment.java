package io.atactic.android.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import io.atactic.android.R;
import io.atactic.android.activity.MainActivity;
import io.atactic.android.model.Participation;

/**
 * A simple {@link Fragment} subclass.
 */
public class CampaignDetailFragment extends Fragment {

    private static final String LOG_TAG = CampaignDetailFragment.class.getSimpleName();

    private ArcProgress progressIndicatorView;
    private TextView cnNameTextView;
    TextView cBriefingTextView;

    private Participation participation;


    public CampaignDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onCreateView");

        return inflater.inflate(R.layout.fragment_campaign_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onViewCreated");

        /*
         * Get references to the views in the header
         */
        progressIndicatorView = view.findViewById(R.id.arc_questdetail_arcprogress);
        cnNameTextView = view.findViewById(R.id.tv_questdetail_name);
        cBriefingTextView = view.findViewById(R.id.tv_questdetail_briefing);

        ImageView backImgButton = view.findViewById(R.id.img_back);
        backImgButton.setOnClickListener(v ->
                Log.v(LOG_TAG,"backButtonClicked"));

        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onStart() {

        Log.v(LOG_TAG, "onStart");

        if (getArguments() != null) {
            Log.d(LOG_TAG, "Arguments found");

            participation = (Participation) getArguments().getSerializable("PARTICIPATION");
            Log.d(LOG_TAG, "Campaign name = " + participation.getCampaign().getName());


            progressIndicatorView.setProgress((int)(participation.getCurrentProgress()*100));
            cnNameTextView.setText(participation.getCampaign().getName());
            cBriefingTextView.setText(participation.getCampaign().getBriefing());
        }

        super.onStart();
    }




}
