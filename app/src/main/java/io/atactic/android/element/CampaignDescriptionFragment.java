package io.atactic.android.element;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.atactic.android.R;

public class CampaignDescriptionFragment extends Fragment {

    private TextView questDescriptionTextView;
    private String content;

    public CampaignDescriptionFragment(){}

    public CampaignDescriptionFragment(String longDescriptionText) {
        this.content = longDescriptionText;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quest_detail_2, container, false);
        questDescriptionTextView = view.findViewById(R.id.tv_questdetail_longdesc);
        questDescriptionTextView.setText(content);
        return view;
    }

}
