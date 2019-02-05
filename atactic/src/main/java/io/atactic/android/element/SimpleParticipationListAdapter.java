package io.atactic.android.element;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.atactic.android.R;
import io.atactic.android.model.Participation;

/**
 * Creates quest item ViewHolders and populates them with data.
 *
 * @author Jaime Lucea
 */
public class SimpleParticipationListAdapter extends RecyclerView.Adapter<SimpleParticipationListAdapter.SimpleParticipationViewHolder>{

    private List<Participation> participationList;

    private ListItemClickListener clickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIdex);
    }

    /**
     * This constructor takes a click listener as a parameter, allowing to use the adapter
     * as a component with an external click handler, such as one from an activity.
     *
     * @param listener Click listener
     */
    public SimpleParticipationListAdapter(ListItemClickListener listener){
        clickListener = listener;
    }

    /**
     * Returns a ViewHolder object for a quest item and inflates the corresponding view.
     */
    @Override
    public SimpleParticipationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_simple_participation, parent, false);      // boolean shouldAttachToParentImmediately = false;
        return new SimpleParticipationViewHolder(view);
    }

    /**
     * Uses the ViewHolder to populate the rows' view elements with data
     */
    @Override
    public void onBindViewHolder(SimpleParticipationViewHolder holder, int position) {
        holder.setContent(participationList.get(position));
    }

    @Override
    public int getItemCount(){
        if (participationList != null) {
            return participationList.size();
        }else{
            return 0;
        }
    }

    /**
     * This method is used to set the quest list data on a ParticipationListAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new adapter to display it.
     *
     * @param campaigns The new quest list data to be displayed.
     */
    public void setData(List<Participation> campaigns){
        this.participationList = campaigns;
        notifyDataSetChanged();
    }

    public Participation getItem(int index){
        return participationList.get(index);
    }

    /**
     * ViewHolder class for Quest items. Will cache quest item views to optimize performance.
     */
    public class SimpleParticipationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView campaignNameTextView;
        private final TextView campaignDeadlineTextView;
        private final TextView campaignProgressTextView;

        SimpleParticipationViewHolder(View itemView){
            super(itemView);

            /* Store private references to the view components of the quest item */

            campaignNameTextView = itemView.findViewById(R.id.tv_campaign_name);
            campaignDeadlineTextView = itemView.findViewById(R.id.tv_campaign_deadline);
            campaignProgressTextView = itemView.findViewById(R.id.tv_campaign_progress);

            /* Add a click listener to the view holder */
            itemView.setOnClickListener(this);
        }

        public void setContent(Participation participation) {

            campaignNameTextView.setText(participation.getCampaign().getName());
            // campaignDeadlineTextView.setText(participation.getCampaign().getEndDate().toString());
            // campaignProgressTextView.setText(String.valueOf((int)(participation.getCurrentProgress()*100)) + " %");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dateStr = sdf.format(participation.getCampaign().getEndDate());
            campaignDeadlineTextView.setText(dateStr);

            String progressStr = String.format(Locale.getDefault(), "%.1f", participation.getCurrentProgress()*100) + "%";
            campaignProgressTextView.setText(progressStr);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            if (clickListener != null)
                clickListener.onListItemClick(clickedPosition);
        }

    }


}
