package io.atactic.android.element;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.atactic.android.R;
import io.atactic.android.model.Participation;


public class CampaignInfoFragment extends Fragment {

    private String ownerStr;
    private String deadlineStr;
    private String rewardStr;
    private String longDesc;

    private TextView ownerInfoTextView;
    private TextView deadlineTextView;
    private TextView rewardTextView;
    private TextView longDescriptionTextView;

    public CampaignInfoFragment() {

    }

    public CampaignInfoFragment(Participation participation) {

        ownerStr = participation.getCampaign().getOwner().getFirstName();

        Date now = Calendar.getInstance().getTime();
        long timeDiff = participation.getCampaign().getEndDate().getTime() - now.getTime();
        long daysDiff = TimeUnit.MILLISECONDS.toDays(timeDiff);
        SimpleDateFormat writingDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        deadlineStr = writingDateFormat.format(participation.getCampaign().getEndDate())  + " (Quedan " + daysDiff + " días)";
        rewardStr = participation.getCampaign().getCompletionScore() + " puntos por completar la campaña";
        longDesc = participation.getCampaign().getDescription();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_campaign_info, container, false);
        ownerInfoTextView = view.findViewById(R.id.tv_questdetail_owner);
        deadlineTextView = view.findViewById(R.id.tv_questdetail_deadline);
        rewardTextView = view.findViewById(R.id.tv_questdetail_reward);
        longDescriptionTextView = view.findViewById(R.id.tv_campaign_description);

        longDescriptionTextView.setText(longDesc);
        ownerInfoTextView.setText(ownerStr);
        deadlineTextView.setText(deadlineStr);
        rewardTextView.setText(rewardStr);

        return view;
    }
}
