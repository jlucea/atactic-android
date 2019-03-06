package io.atactic.android.element;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.atactic.android.R;
import io.atactic.android.model.Participation;

/**
 * Creates quest item ViewHolders and populates them with data.
 *
 * @author Jaime Lucea
 */
public class ParticipationListAdapter extends RecyclerView.Adapter<ParticipationListAdapter.ParticipationViewHolder>{

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
    public ParticipationListAdapter(ListItemClickListener listener){
        clickListener = listener;
    }

    /**
     * Returns a ViewHolder object for a quest item and inflates the corresponding view.
     */
    @Override
    public ParticipationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_quest, parent, false);      // boolean shouldAttachToParentImmediately = false;
        return new ParticipationViewHolder(view);
    }

    /**
     * Uses the ViewHolder to populate the rows' view elements with data
     */
    @Override
    public void onBindViewHolder(ParticipationViewHolder holder, int position) {
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
    public class ParticipationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ArcProgress progressIndicatorView;
        private final TextView questNameTextView;
        private final TextView questBriefingTextView;
        private final TextView questDeadlineTextView;
        // private final TextView questScoreTextView;

        ParticipationViewHolder(View itemView){
            super(itemView);

            /* Store private references to the view components of the quest item */

            questNameTextView = itemView.findViewById(R.id.tv_quest_name);
            questBriefingTextView = itemView.findViewById(R.id.tv_quest_briefing);
            progressIndicatorView = itemView.findViewById(R.id.arc_progress);
            questDeadlineTextView = itemView.findViewById(R.id.tv_quest_deadline);
            // questScoreTextView = itemView.findViewById(R.id.tv_quest_score);

            /* Add a click listener to the view holder */
            itemView.setOnClickListener(this);
        }

        public void setContent(Participation participation) {
            int roundedProgressValue = (int)(participation.getCurrentProgress()*100);
            if (roundedProgressValue > 100) {
                progressIndicatorView.setProgress(100);
            }else{
                progressIndicatorView.setProgress(roundedProgressValue);
            }
            questNameTextView.setText(participation.getCampaign().getName());
            questBriefingTextView.setText(participation.getCampaign().getBriefing());

            Date now = Calendar.getInstance().getTime();
            long timeDiff = participation.getCampaign().getEndDate().getTime() - now.getTime();
            long daysDiff = TimeUnit.MILLISECONDS.toDays(timeDiff);
            String deadlineText = "Quedan "+ daysDiff + " días";
            questDeadlineTextView.setText(deadlineText);

            // questScoreTextView.setText(String.valueOf(participation.getCampaign().getCompletionScore()));
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            clickListener.onListItemClick(clickedPosition);
        }

    }

}
