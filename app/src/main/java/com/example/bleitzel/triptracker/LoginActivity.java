package com.example.bleitzel.triptracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;


public class LoginActivity extends AppCompatActivity {

    private final String APP_ID = "C16A2833-21B0-AD96-FF9B-E48452D25500";
    private final String SECRET_KEY = "8D534AA7-EC7B-A835-FFA9-7CE7B7CD9100";
    private EditText mNameEdit;
    private EditText mEmailEdit;
    private EditText mPasswordEdit;
    private Button mSignUpButton;
    private Button mLoginButton;
    private TextView mSignUpTextView;
    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mNameEdit = (EditText) findViewById(R.id.enter_name);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mSignUpTextView = (TextView) findViewById(R.id.sign_up_text);
        SignUpTextOnClick signUpTextOnClick = new SignUpTextOnClick();
        mSignUpTextView.setOnClickListener(signUpTextOnClick);
        mEmailEdit = (EditText) findViewById(R.id.enter_email);
        mPasswordEdit = (EditText) findViewById(R.id.enter_password);
        mSignUpTextView.setOnClickListener(signUpTextOnClick);
        Backendless.initApp(this, APP_ID, SECRET_KEY, "v1");
    }

    @Override
    public void onBackPressed() {
        mSignUpButton.setVisibility(View.GONE);
        mNameEdit.setVisibility(View.GONE);
        mLoginButton.setVisibility(View.VISIBLE);
        mSignUpTextView.setVisibility(View.VISIBLE);
    }

    private class SignUpTextOnClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            mSignUpButton.setVisibility(View.VISIBLE);
            mNameEdit.setVisibility(View.VISIBLE);
            mLoginButton.setVisibility(View.GONE);
            mSignUpTextView.setVisibility(View.GONE);
        }
    }
    public void warnUser(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(message);
        builder.setTitle(R.string.authentication_error_title);
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class MySignUpOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String userEmail = mEmailEdit.getText().toString();
            String password = mPasswordEdit.getText().toString();
            String name = mNameEdit.getText().toString();

            userEmail = userEmail.trim();
            password = password.trim();
            name = name.trim();

            if (!userEmail.isEmpty() && !password.isEmpty() && !name.isEmpty()) {
                BackendlessUser user = null;

                user.setEmail(userEmail);
                user.setPassword(password);
                user.setProperty("name", name);

                Backendless.UserService.register(user,
                        new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser backendlessUser) {
                                Log.i(TAG, "Registration successful for " + backendlessUser.getEmail());
                                Intent intent = new Intent(LoginActivity.this, TripListActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Log.i(TAG, "Registration failed: " + fault.getMessage());
                            }
                        });
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage(R.string.empty_field_signup_error);
                builder.setTitle(R.string.authentication_error_title);
                builder.setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    private class LoginButtonOnClick implements  View.OnClickListener{

        @Override
        public void onClick(final View view) {
            String email = mEmailEdit.getText().toString();
            String password = mPasswordEdit.getText().toString();

            email = email.trim();
            password = password.trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                final ProgressDialog pDialog = ProgressDialog.show(LoginActivity.this,
                        "Please Wait!",
                        "Logging in...",
                        true);
                Backendless.UserService.login(email, password,
                        new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser response) {
                        Toast.makeText(view.getContext(), response.getProperty("name") + " logged in successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        warnUser(fault.getMessage());
                        pDialog.dismiss();
                    }
                });

            }
            else{
                warnUser(getString(R.string.empty_field_signup_error));
            }
        }
    }

    public boolean validateData(String email, String password){
        if (email.contains("@")){
            if (password.length() >= 6){
                if (!password.contains(email.split("@")[0])){
                    return true;
                }
                else {
                    warnUser("Password cannot match or contain any portion of the email address.");
                }
            }
            else {
                warnUser("Password does not meet complexity requirements.");
            }
        }
        else{
            warnUser("Email address " + email + " doesn't follow standard address format. Please check and retype your email address.");
        }
        return false;
    }

}