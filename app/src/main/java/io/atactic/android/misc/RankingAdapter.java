package io.atactic.android.misc;

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

    @Override
    public RankedUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("RankingAdapter","ViewHolder created");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_ranking, parent, false);
        return new RankedUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RankedUserViewHolder holder, int position) {
        Log.d("RankingAdapter","ViewHolder Binded ("+position+")");
        try {
            JSONObject rankedUserJsonData = jsonArrayRanking.getJSONObject(position);
            holder.setRankedUserDataToDisplay(position+1,rankedUserJsonData);

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


    public void setContent(JSONArray ranking){
        jsonArrayRanking = ranking;
        notifyDataSetChanged();
    }


    public class RankedUserViewHolder extends RecyclerView.ViewHolder{

        private ImageView rankedUserPortraitImageView;
        private TextView rankedUserRankTextView;
        private TextView rankedUserNameTextView;
        private TextView rankedUserScoreTextView;


        public RankedUserViewHolder(View itemView) {
            super(itemView);

            rankedUserRankTextView = (TextView)itemView.findViewById(R.id.tv_rank);
            rankedUserNameTextView = (TextView)itemView.findViewById(R.id.tv_ranked_user_name);
            rankedUserScoreTextView = (TextView)itemView.findViewById(R.id.tv_ranked_user_score);
            /*
            userPortrait.setImageResource(R.drawable.icon_user_32x32);
            */
        }

        public void setRankedUserDataToDisplay(int rank, JSONObject rankedUserData){
            try {
                String name = rankedUserData.getString("firstName");
                int score = rankedUserData.getInt("score");

                rankedUserRankTextView.setText(String.valueOf(rank));
                rankedUserNameTextView.setText(name);
                rankedUserScoreTextView.setText(String.valueOf(score));

            }catch(JSONException jse){
                jse.printStackTrace();
            }
        }


    }

}
