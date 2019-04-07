package io.atactic.android.element;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.atactic.android.R;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankedUserViewHolder>{

    private JSONArray jsonArrayRanking;
    private int userId;

    @Override
    public RankedUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("RankingAdapter","ViewHolder created");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_ranked_user, parent, false);
        return new RankedUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RankedUserViewHolder holder, int position) {
        Log.d("RankingAdapter","ViewHolder Binded ("+position+")");
        try {
            JSONObject rankedUserJsonData = jsonArrayRanking.getJSONObject(position);
            holder.setRankedUserDataToDisplay(position+1,rankedUserJsonData, userId);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (jsonArrayRanking == null){
            Log.w("RankingAdapter","Ranking NULL");
            return 0;
        }else {
            Log.d("RankingAdapter","Ranking size = "+jsonArrayRanking.length());
            return jsonArrayRanking.length();
        }
    }


    public void setContent(JSONArray ranking, int userId){
        jsonArrayRanking = ranking;
        this.userId = userId;
        notifyDataSetChanged();
    }


    public class RankedUserViewHolder extends RecyclerView.ViewHolder{

        private ImageView rankedUserPortraitImageView;
        private TextView rankedUserRankTextView;
        private TextView rankedUserNameTextView;
        private TextView rankedUserScoreTextView;


        public RankedUserViewHolder(View itemView) {
            super(itemView);

            rankedUserRankTextView = itemView.findViewById(R.id.tv_rank);
            rankedUserNameTextView = itemView.findViewById(R.id.tv_ranked_user_name);
            rankedUserScoreTextView = itemView.findViewById(R.id.tv_ranked_user_score);
            /*
            userPortrait.setImageResource(R.drawable.icon_user_32x32);
            */
        }

        public void setRankedUserDataToDisplay(int rank, JSONObject rankedUserData, int userId){
            try {
                int rankedUserId = rankedUserData.getInt("userId");
                String fullName = rankedUserData.getString("firstName") + " " +
                    rankedUserData.getString("lastName");
                int score = rankedUserData.getInt("score");

                rankedUserRankTextView.setText("#".concat(String.valueOf(rank)));
                rankedUserNameTextView.setText(fullName);
                rankedUserScoreTextView.setText(String.valueOf(score));

                if (rankedUserId == userId){
                    // Highlight current user in the list
                    rankedUserNameTextView.setTextColor(
                            ContextCompat.getColor(rankedUserNameTextView.getContext(),
                                    R.color.atactic_brighter_red));
                    rankedUserNameTextView.setTypeface(null, Typeface.BOLD);
                    rankedUserScoreTextView.setTextColor(
                            ContextCompat.getColor(rankedUserNameTextView.getContext(),
                                    R.color.atactic_brighter_red));
                    rankedUserScoreTextView.setTypeface(null, Typeface.BOLD);

                }else{
                    rankedUserNameTextView.setTextColor(
                            ContextCompat.getColor(rankedUserNameTextView.getContext(),
                                    R.color.atactic_dark_gray));
                    rankedUserNameTextView.setTypeface(null, Typeface.NORMAL);
                    rankedUserScoreTextView.setTextColor(
                            ContextCompat.getColor(rankedUserNameTextView.getContext(),
                                    R.color.atactic_dark_gray));
                    rankedUserScoreTextView.setTypeface(null, Typeface.NORMAL);
                }

            }catch(JSONException jse){
                jse.printStackTrace();
            }
        }


    }

}
