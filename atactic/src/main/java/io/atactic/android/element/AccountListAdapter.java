package io.atactic.android.element;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import io.atactic.android.R;
import io.atactic.android.activity.AccountDetailActivity;
import io.atactic.android.activity.AccountListActivity;
import io.atactic.android.model.Account;
import io.atactic.android.utils.DistanceUtils;

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.AccountViewHolder>{

    private List<Account> accountList;

    private ListItemClickListener clickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIdex);
    }

    /**
     * Constructor with no click listener
     */
    public AccountListAdapter(){ }

    public AccountListAdapter(ListItemClickListener listener){
        System.out.println("Instancing Account List Adapter with a click listener");
        this.clickListener = listener;
    }

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

    public Account getAccount(int index){
        return this.accountList.get(index);
    }

    /**
     * ViewHolder for an Account
     */
    public class AccountViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

            if (clickListener != null) itemView.setOnClickListener(this);
        }

        public void setData(Account account){
            accountNameTextView.setText(account.getName());
            String fullAddress = account.getAddress() + ", " + account.getCity();
            accountAddressTextView.setText(fullAddress);
            targetScoreTextView.setText(String.valueOf(account.getRelevanceScore()));

            String distanceText = DistanceUtils.formatDistanceText(account.getDistanceTo());
            distanceToTargetTextView.setText(distanceText);
        }


        @Override
        public void onClick(View v) {
            System.out.println("Account View Holder onClick");
            int clickedPosition = getAdapterPosition();
            if (clickListener != null) clickListener.onListItemClick(clickedPosition);
        }

    }


}
