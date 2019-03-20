package io.atactic.android.element;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import io.atactic.android.R;
import io.atactic.android.model.Participation;

public class ProgressRankingAdapter extends RecyclerView.Adapter<ProgressRankingAdapter.RankedParticipantViewHolder>{

    private static final String LOG_TAG = ProgressRankingAdapter.class.getSimpleName();

    private List<Participation> participationList;
    private int userId;

    @NonNull
    @Override
    public RankedParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Log.v(LOG_TAG,"ViewHolder created");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_ranking, parent, false);
        return new RankedParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankedParticipantViewHolder holder, int position) {
        Log.d(LOG_TAG,"ViewHolder Bind ("+position+")");
        holder.setData(position + 1, participationList.get(position));
    }

    @Override
    public int getItemCount() {
        if (participationList == null){
            Log.w(LOG_TAG,"Ranking NULL");
            return 0;
        }else {
            Log.d(LOG_TAG,"Ranking size = " + participationList.size());
            return participationList.size();
        }
    }


    public void setContent(List<Participation> participations, int userId){
        Log.v(LOG_TAG,"Setting content: " + participations.size() + " participations");
        this.participationList = participations;
        this.userId = userId;
        notifyDataSetChanged();
    }


    class RankedParticipantViewHolder extends RecyclerView.ViewHolder {

        private ImageView rankedUserPortraitImageView;
        private TextView rankTextView;
        private TextView usernameTextView;
        private TextView progressTextView;


        RankedParticipantViewHolder(View itemView) {
            super(itemView);

            rankTextView = itemView.findViewById(R.id.tv_rank);
            usernameTextView = itemView.findViewById(R.id.tv_ranked_user_name);
            progressTextView = itemView.findViewById(R.id.tv_ranked_user_score);
            /*
            userPortrait.setImageResource(R.drawable.icon_user_32x32);
            */
        }

        void setData(int rank, Participation participation){

            rankTextView.setText(String.valueOf(rank));

            String fullUserName = participation.getParticipant().getFirstName()
                    + " " + participation.getParticipant().getLastName();
            usernameTextView.setText(fullUserName);

            String progressStr = String.format(Locale.getDefault(),
                    "%.1f", participation.getCurrentProgress()*100).concat(" %");
            progressTextView.setText(progressStr);
        }

    }

}
