package io.atactic.android.element;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.atactic.android.R;
import io.atactic.android.model.Account;

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.AccountViewHolder>{

    private List<Account> accountList;

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_target, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {
        holder.setData(accountList.get(position));
    }

    @Override
    public int getItemCount() {
        if (accountList != null) {
            return accountList.size();
        } else {
            return 0;
        }
    }

    public void setContent(List<Account> accounts){
        System.out.println("AccountListAdapter - Setting content: " + accounts.size() + " accounts");
        this.accountList = accounts;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for an Account
     */
    public class AccountViewHolder extends RecyclerView.ViewHolder{

        // private ImageView accountImageView;
        private TextView accountNameTextView;
        private TextView accountAddressTextView;
        private TextView targetScoreTextView;
        private TextView distanceToTargetTextView;

        AccountViewHolder(View itemView) {
            super(itemView);

            // accountImageView = itemView.findViewById(R.id.img_target);
            accountNameTextView = itemView.findViewById(R.id.tv_target_name);
            accountAddressTextView = itemView.findViewById(R.id.tv_target_address);
            targetScoreTextView = itemView.findViewById(R.id.tv_target_score);
            distanceToTargetTextView = itemView.findViewById(R.id.tv_distance_to_target);
        }

        public void setData(Account account){
            accountNameTextView.setText(account.getName());
            String fullAddress = account.getAddress() + ", " + account.getCity();
            accountAddressTextView.setText(fullAddress);
            targetScoreTextView.setText("");

            String distanceText = formatDistanceText(account.getDistanceTo());
            distanceToTargetTextView.setText(distanceText);
        }

        private String formatDistanceText(double distance){
            String distanceText;
            if (distance < 1000){
                distanceText = Math.round(distance) + " m";
            } else {
                distanceText = String.format("%.1f Km", distance/100);
            }
            return distanceText;
        }

    }

}
