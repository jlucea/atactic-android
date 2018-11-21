package io.atactic.android.element;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import io.atactic.android.R;
import io.atactic.android.model.Visit;

public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.ActivityViewHolder> {

    private List<Visit> activityList;

    @NonNull
    @Override
    public ActivityListAdapter.ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_item_visit, viewGroup, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder viewHolder, int i) {
        viewHolder.setData(activityList.get(i));
    }

    @Override
    public int getItemCount() {
        if (activityList != null) {
            return activityList.size();
        }else{
            return 0;
        }
    }

    public void setContent(List<Visit> activities){
        this.activityList = activities;
    }


    public class ActivityViewHolder extends RecyclerView.ViewHolder {

        private TextView accountNameTextView;
        private TextView visitDateTextView;

        ActivityViewHolder(@NonNull View itemView) {
            super(itemView);

            accountNameTextView = itemView.findViewById(R.id.tv_visited_account_name);
            visitDateTextView = itemView.findViewById(R.id.tv_date_of_visit);
        }

        public void setData(Visit v){
            accountNameTextView.setText(v.getAccount().getName());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateStr = sdf.format(v.getDate());
            visitDateTextView.setText(dateStr);
        }

    }

}
