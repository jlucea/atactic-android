package app.smartpath.android.smartpath.misc;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.smartpath.android.smartpath.R;

/**
 * Creates quest item ViewHolders and populates them with data.
 *
 * @author Jaime Lucea
 */
public class QuestListAdapter extends RecyclerView.Adapter<QuestListAdapter.QuestViewHolder>{

    private String[] questList;

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
        View view = inflater.inflate(R.layout.quest_list_item, parent, false);      // boolean shouldAttachToParentImmediately = false;
        return new QuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuestViewHolder holder, int position) {
        String q = questList[position];
        holder.setContent(q);
    }

    @Override
    public int getItemCount(){
        if (questList!=null) {
            return questList.length;
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
    public void setQuestListData(String[] questListData){
        this.questList = questListData;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for Quest items. Will cache quest item views to optimize performance.
     */
    public class QuestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView questItemView;

        public QuestViewHolder(View itemView){
            super(itemView);
            questItemView = (TextView) itemView.findViewById(R.id.tv_quest_item);
            itemView.setOnClickListener(this);
        }

        // TODO Implement setContent(qName, qDescription, qCurrentStep, qSteps, qDeadline...)
        public void setContent(String questDescription){
            questItemView.setText(questDescription);
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
