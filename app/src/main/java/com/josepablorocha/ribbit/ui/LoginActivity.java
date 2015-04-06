package com.josepablorocha.ribbit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.josepablorocha.ribbit.R;
import com.josepablorocha.ribbit.RibbitApplication;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LoginActivity extends ActionBarActivity {

    protected ProgressBar mProgressBar;
    protected TextView mSignUpTextView;
    protected TextView mForgotPasswordTextView;
    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginButton;
    protected LinearLayout mButtonLayout;
    protected LinearLayout mEditText;
    protected LinearLayout mBottomTextLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mButtonLayout = (LinearLayout)findViewById(R.id.buttonLayout);
        mEditText = (LinearLayout)findViewById(R.id.editTextLayout);
        mBottomTextLayout = (LinearLayout)findViewById(R.id.bottomTextLayout);

        mUsername = (EditText)findViewById(R.id.usernameField);
        mPassword = (EditText)findViewById(R.id.passwordField);

        mSignUpTextView = (TextView)findViewById(R.id.signUpText);
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        mForgotPasswordTextView = (TextView)findViewById(R.id.forgotPasswordText);
        mForgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        mLoginButton = (Button)findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    // Give a warning...
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(R.string.login_error_message)
                            .setTitle(R.string.sign_up_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    // Login!
                    mProgressBar.setVisibility(ProgressBar.VISIBLE);
                    mButtonLayout.setVisibility(LinearLayout.INVISIBLE);
                    mEditText.setVisibility(LinearLayout.INVISIBLE);
                    mBottomTextLayout.setVisibility(LinearLayout.INVISIBLE);

                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {

                          if (e == null) {
                              RibbitApplication.updateParseInstallation(user);
                              Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                              startActivity(intent);
                          } else {
                              mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                              mButtonLayout.setVisibility(LinearLayout.VISIBLE);
                              mEditText.setVisibility(LinearLayout.VISIBLE);
                              mBottomTextLayout.setVisibility(LinearLayout.VISIBLE);
                              AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                              builder.setMessage(e.getMessage())
                                      .setTitle(R.string.sign_up_error_title)
                                      .setPositiveButton(android.R.string.ok, null);
                              AlertDialog dialog = builder.create();
                              if (!isFinishing())
                                  dialog.show();
                          }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        mButtonLayout.setVisibility(LinearLayout.VISIBLE);
        mEditText.setVisibility(LinearLayout.VISIBLE);
        mBottomTextLayout.setVisibility(LinearLayout.VISIBLE);
    }
}
