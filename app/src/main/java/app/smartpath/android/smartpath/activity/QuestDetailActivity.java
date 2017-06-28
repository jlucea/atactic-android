package app.smartpath.android.smartpath.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import app.smartpath.android.smartpath.R;

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
        Intent intent = getIntent();

        progressIndicatorView = (ArcProgress) findViewById(R.id.arc_questdetail_arcprogress);
        int current = intent.getIntExtra("currentStep",0);
        int goal = intent.getIntExtra("totalSteps",10);
        progressIndicatorView.setProgress(current*100/goal);
        progressIndicatorView.setBottomText(current + " / " + goal);

        questNameTextView = (TextView) findViewById(R.id.tv_questdetail_name);
        questNameTextView.setText(getIntent().getStringExtra("questName"));

        questBriefingTextView = (TextView) findViewById(R.id.tv_questdetail_briefing);
        questBriefingTextView.setText(getIntent().getStringExtra("questSummary"));

        questLongDescriptionTextView = (TextView) findViewById(R.id.tv_questdetail_longdesc);
        questLongDescriptionTextView.setText(getIntent().getStringExtra("questLongDesc"));

        questOwnerTextView = (TextView) findViewById(R.id.tv_questdetail_owner);
        questOwnerTextView.setText("Jaime Lucea Quiñones \n Founder & CEO");

        questDeadlineTextView = (TextView) findViewById(R.id.tv_questdetail_deadline);
        questDeadlineTextView.setText("Hasta 31 de octubre de 2017 \n Faltan 124 días");

    }

}
