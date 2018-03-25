package io.atactic.android.element;

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

public class TargetListAdapter extends RecyclerView.Adapter<TargetListAdapter.RecommendedTargetViewHolder>{

    private JSONArray targetsJSONArray;

    @Override
    public RecommendedTargetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_target, parent, false);
        return new RecommendedTargetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecommendedTargetViewHolder holder, int position) {
        try {
            JSONObject ptgtJSON = targetsJSONArray.getJSONObject(position);
            JSONObject ptJSON = null;
            if (ptgtJSON.has("participation"))
                ptJSON = ptgtJSON.getJSONObject("participation");

            holder.setTargetData(ptgtJSON.getJSONObject("account"), ptJSON);

        }catch (JSONException jse){
            jse.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (targetsJSONArray == null){
            Log.w("TargetListAdapter","Target list is NULL");
            return 0;
        }else {
            Log.d("TargetListAdapter","Target list size = "+ targetsJSONArray.length());
            return targetsJSONArray.length();
        }
    }


    public void setContent(JSONArray targetList){
        targetsJSONArray = targetList;
        notifyDataSetChanged();
    }


    /**
     * ViewHolder for a target object
     */
    public class RecommendedTargetViewHolder extends RecyclerView.ViewHolder{

        private ImageView targetIconImageView;
        private TextView targetNameTextView;
        private TextView targetAddressTextView;
        private TextView targetScoreTextView;
        private TextView distanceToTargetTextView;

        public RecommendedTargetViewHolder(View itemView) {
            super(itemView);

            targetIconImageView = itemView.findViewById(R.id.img_target);
            targetNameTextView = itemView.findViewById(R.id.tv_target_name);
            targetAddressTextView = itemView.findViewById(R.id.tv_target_address);
            targetScoreTextView = itemView.findViewById(R.id.tv_target_score);
            distanceToTargetTextView = itemView.findViewById(R.id.tv_distance_to_target);

        }

        public void setTargetData(JSONObject accountObj, JSONObject participationObj){
            try {
                // Parse data to display from JSON object
                String targetNameStr = accountObj.getString("name");
                String fullAddressStr = accountObj.getString("address")
                        + ", " + accountObj.getString("postalCode")
                        + ", " + accountObj.getString("city");

                Double d = accountObj.getDouble("distance");
                int dInt = Math.round(d.floatValue());
                String distanceToTargetStr = Integer.toString(dInt) + " m";

                if (participationObj != null) {
                    String targetScoreStr = Integer.toString(participationObj.getJSONObject("campaign")
                            .getInt("visitScore"));
                    targetScoreTextView.setText(targetScoreStr);
                    targetScoreTextView.setVisibility(View.VISIBLE);
                }else{
                    targetScoreTextView.setVisibility(View.INVISIBLE);
                }

                // Fill views with data
                targetNameTextView.setText(targetNameStr);
                targetAddressTextView.setText(fullAddressStr);
                distanceToTargetTextView.setText(distanceToTargetStr);

            }catch (JSONException err) {
                targetNameTextView.setText("- - -");
                targetAddressTextView.setText("");
            }
        }

    }

}
