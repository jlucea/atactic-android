package io.atactic.android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.atactic.android.R;
import io.atactic.android.datahandler.PasswordManager;
import io.atactic.android.utils.CredentialsCache;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    // References to text fields
    private EditText newPassWordTextField;
    private EditText verificationTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Display back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newPassWordTextField = findViewById(R.id.et_new_pwd);
        verificationTextField = findViewById(R.id.et_new_pwd2);

        Button changePasswordButton = findViewById(R.id.btn_change_pwd);
        changePasswordButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (newPassWordTextField.getText().toString().equals(verificationTextField.getText().toString())){
            // Toast.makeText(this, "Se cambiará el password", Toast.LENGTH_SHORT).show();

            int userId = CredentialsCache.recoverCredentials(this).getUserId();

            // Call PasswordManager to change the password
            new PasswordManager(this).changePassword(userId, newPassWordTextField.getText().toString());

        }else{
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }


    public void setOk(){
        // Show success message
        Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();

        // Close screen and redirect to caller action
        this.finish();
    }


    public void setError(String message){
        // Show error message
        Toast.makeText(this, "Error: "+message, Toast.LENGTH_SHORT).show();

        // Close screen and redirect to caller action
        this.finish();
    }

}
