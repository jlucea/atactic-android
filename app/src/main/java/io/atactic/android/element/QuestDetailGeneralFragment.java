package io.atactic.android.element;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.atactic.android.R;


public class QuestDetailGeneralFragment extends Fragment {

    private String ownerStr;
    private String deadlineStr;
    private String rewardStr;

    private TextView ownerInfoTextView;
    private TextView deadlineTextView;
    private TextView rewardTextView;


    public QuestDetailGeneralFragment() { }

    public QuestDetailGeneralFragment(String ownerInfo, String deadlineInfo,
                                      String rewardDescription) {
        this.ownerStr = ownerInfo;
        this.deadlineStr = deadlineInfo;
        this.rewardStr = rewardDescription;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quest_detail_1, container, false);
        ownerInfoTextView = view.findViewById(R.id.tv_questdetail_owner);
        deadlineTextView = view.findViewById(R.id.tv_questdetail_deadline);
        rewardTextView = view.findViewById(R.id.tv_questdetail_reward);
        ownerInfoTextView.setText(ownerStr);
        deadlineTextView.setText(deadlineStr);
        rewardTextView.setText(rewardStr);
        return view;
    }
}
