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
            String accountName = targetsJSONArray.getJSONObject(position)
                    .getJSONObject("account").getString("name");

            holder.setTargetData(accountName);

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


    public class RecommendedTargetViewHolder extends RecyclerView.ViewHolder{

        private ImageView targetIconImageView;
        private TextView targetNameTextView;


        public RecommendedTargetViewHolder(View itemView) {
            super(itemView);

            targetIconImageView = itemView.findViewById(R.id.img_target);
            targetNameTextView = itemView.findViewById(R.id.tv_target_name);
        }

        public void setTargetData(String targetName){
            targetNameTextView.setText(targetName);
        }


    }

}
