package com.josepablorocha.ribbit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.josepablorocha.ribbit.R;
import com.josepablorocha.ribbit.RibbitApplication;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends ActionBarActivity {

    protected ProgressBar mProgressBar;
    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mPasswordConfirm;
    protected EditText mEmail;
    protected Button mSignUpButton;
    protected Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mUsername = (EditText)findViewById(R.id.usernameField);
        mPassword = (EditText)findViewById(R.id.passwordField);
        mPasswordConfirm = (EditText)findViewById(R.id.passwordConfirmField);
        mEmail = (EditText)findViewById(R.id.emailField);

        mCancelButton = (Button)findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSignUpButton = (Button)findViewById(R.id.signUpButton);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String passwordConfirm = mPasswordConfirm.getText().toString().trim();
                String email = mEmail.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.sign_up_error_message)
                            .setTitle(R.string.sign_up_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else if (!password.equals(passwordConfirm)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.sign_up_password_error_message)
                            .setTitle(R.string.sign_up_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    mProgressBar.setVisibility(ProgressBar.VISIBLE);
                    mUsername.setVisibility(EditText.INVISIBLE);
                    mPassword.setVisibility(EditText.INVISIBLE);
                    mPasswordConfirm.setVisibility(EditText.INVISIBLE);
                    mEmail.setVisibility(EditText.INVISIBLE);
                    mSignUpButton.setVisibility(Button.INVISIBLE);
                    mCancelButton.setVisibility(Button.INVISIBLE);

                    // Create the new user!
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            mUsername.setVisibility(EditText.VISIBLE);
                            mPassword.setVisibility(EditText.VISIBLE);
                            mPasswordConfirm.setVisibility(EditText.VISIBLE);
                            mEmail.setVisibility(EditText.VISIBLE);
                            mSignUpButton.setVisibility(Button.VISIBLE);
                            mCancelButton.setVisibility(Button.VISIBLE);

                            if (e == null) {
                                RibbitApplication.updateParseInstallation(ParseUser.getCurrentUser());
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
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

}
