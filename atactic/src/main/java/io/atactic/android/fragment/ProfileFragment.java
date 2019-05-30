package io.atactic.android.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import io.atactic.android.R;
import io.atactic.android.activity.ChangePasswordActivity;
import io.atactic.android.activity.HistoryActivity;
import io.atactic.android.datahandler.ProfileManager;
import io.atactic.android.utils.CredentialsCache;

/**
 * Fragment containing the profile screen
 *
 * @author Jaime Lucea
 * @author ATACTIC
 */
public class ProfileFragment extends Fragment implements View.OnClickListener, ProfileManager.ProfileDataPresenter {

    private TextView userNameTextView;
    private TextView userPositionTextView;
    private TextView userScoreTextView;

    private LogoutResponder logoutResponder;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Data
        new ProfileManager(this).getData();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userNameTextView = view.findViewById(R.id.tv_user_name);
        userPositionTextView = view.findViewById(R.id.tv_user_position);
        userScoreTextView = view.findViewById(R.id.tv_user_score);

        // Get references to menu options and set this class as click listener
        TextView activityRegisterMenuOption = view.findViewById(R.id.tv_activity_register);
        TextView changePasswordMenuOption = view.findViewById(R.id.tv_change_password);
        TextView exitMenuOption = view.findViewById(R.id.tv_exit);

        activityRegisterMenuOption.setOnClickListener(this);
        changePasswordMenuOption.setOnClickListener(this);
        exitMenuOption.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.logoutResponder = (LogoutResponder) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.logoutResponder = null;
    }


    public void displayData(String fullName, String position, String score){
        userNameTextView.setText(fullName);
        userPositionTextView.setText(position);
        userScoreTextView.setText(score);
    }


    public void displayMessage(String message){
        Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_activity_register) {
            Intent i = new Intent(this.getContext(), HistoryActivity.class);
            startActivity(i);

        }else if (v.getId() == R.id.tv_change_password) {
            Intent i = new Intent(this.getContext(), ChangePasswordActivity.class);
            startActivity(i);

        }else if (v.getId() == R.id.tv_exit){
            CredentialsCache.crearAll(this.getContext());
            this.logoutResponder.doLogout();
        }

    }

}
