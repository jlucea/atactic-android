package app.smartpath.android.smartpath.misc;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import app.smartpath.android.smartpath.R;

/**
 * Creates quest item ViewHolders and populates them with data.
 *
 * @author Jaime Lucea
 */
public class QuestListAdapter extends RecyclerView.Adapter<QuestListAdapter.QuestViewHolder>{

    private JSONArray questList;

    private ListItemClickListener clickListener;

    /**
     * This constructor takes a click listener as a parameter, allowing to use the adapter
     * as a component with an external click handler, such as one from an activity.
     *
     * @param listener
     */
    public QuestListAdapter(ListItemClickListener listener){
        clickListener = listener;
    }

    /**
     * Returns a ViewHolder object for a quest item and inflates the corresponding view.
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public QuestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_quest, parent, false);      // boolean shouldAttachToParentImmediately = false;
        return new QuestViewHolder(view);
    }

    /**
     * Uses the ViewHolder to populate the rows' view elements with data
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(QuestViewHolder holder, int position) {
        try {
            JSONObject q = questList.getJSONObject(position);

            holder.setContent(q);

        }catch(JSONException jse){
            jse.printStackTrace();
        }
    }

    @Override
    public int getItemCount(){
        if (questList!=null) {
            return questList.length();
        }else{
            return 0;
        }
    }

    /**
     * This method is used to set the quest list data on a QuestListAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new adapter to display it.
     *
     * @param questListData The new quest list data to be displayed.
     */
    public void setQuestListData(JSONArray questListData){
        this.questList = questListData;
        notifyDataSetChanged();
    }

    public JSONArray getQuestList() {
        return questList;
    }

    public JSONObject getQuest(int index) throws JSONException{
        return questList.getJSONObject(index);
    }

    /**
     * ViewHolder class for Quest items. Will cache quest item views to optimize performance.
     */
    public class QuestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final ArcProgress progressIndicatorView;
        private final TextView questDescriptionView;
        private final TextView questDeadlineView;

        public QuestViewHolder(View itemView){
            super(itemView);

            /* Store private references to the view components of the quest item */
            questDescriptionView = (TextView) itemView.findViewById(R.id.tv_quest_item);
            progressIndicatorView = (ArcProgress) itemView.findViewById(R.id.arc_progress);
            questDeadlineView = (TextView) itemView.findViewById(R.id.tv_quest_deadline);

            /* Add a click listener to the view holder */
            itemView.setOnClickListener(this);
        }

        /**
         * Set the contents to display on the views withing the quest ViewHolder
         *
         * @param participationDescription
         */
        public void setContent(JSONObject participationDescription){
            try {
                /* Parse progress values from JSON object */
                String currentStepStr = participationDescription.getString("currentStep");
                String totalStepsStr = participationDescription.getString("totalSteps");
                int curr = Integer.parseInt(currentStepStr);
                int tot = Integer.parseInt(totalStepsStr);
                int progress = curr*100/tot;
                // System.out.println(curr + "/" + tot + " ("+progress+"%)");

                /* Set progress values on progress indicator view */
                progressIndicatorView.setBottomText(currentStepStr+"/"+totalStepsStr);
                progressIndicatorView.setProgress(progress);

                /* Set progress indicator view properties */
                // progressIndicatorView.setStrokeWidth(18);
                // progressIndicatorView.setBottomTextSize(32);
                // progressIndicatorView.setFinishedStrokeColor(R.color.sp_turquoise);
                // progressIndicatorView.setUnfinishedStrokeColor(Color.GRAY);
                // progressIndicatorView.setTextColor(R.color.sp_blue);

                /* Parse quest briefing (summary) */
                String questInstructions = participationDescription.getJSONObject("campaign")
                        .getString("summary");

                /* Display quest information (summary) */
                questDescriptionView.setText(questInstructions);

                /* Display remaining days */
                String qDeadlineStr = participationDescription.getJSONObject("campaign")
                        .getString("endDate");

                String endDateStr = qDeadlineStr.split("T")[0];
                // System.out.println("String to parse as date: "+ endDateStr);

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat();
                    sdf.applyLocalizedPattern("yyyy-MM-dd");

                    Date endDate = sdf.parse(endDateStr, new ParsePosition(0));
                    Date now = Calendar.getInstance().getTime();

                    long timeDiff = endDate.getTime() - now.getTime();
                    long daysDiff = TimeUnit.MILLISECONDS.toDays(timeDiff);

                    // System.out.println("daysDiff: "+daysDiff);
                    questDeadlineView.setText("Quedan "+daysDiff+" días");

                }catch (Exception e){

                    e.printStackTrace();
                    questDeadlineView.setVisibility(View.INVISIBLE);
                }

            }catch (JSONException jsonEx) {
                jsonEx.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            clickListener.onListItemClick(clickedPosition);
        }

    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIdex);
    }


}
