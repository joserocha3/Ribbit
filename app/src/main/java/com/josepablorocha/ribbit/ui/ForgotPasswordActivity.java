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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;


public class ForgotPasswordActivity extends ActionBarActivity {

    protected ProgressBar mProgressBar;
    protected EditText mEmail;
    protected Button mSendEmailButton;
    protected Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mEmail = (EditText)findViewById(R.id.emailField);

        mCancelButton = (Button)findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSendEmailButton = (Button)findViewById(R.id.sendEmailButton);
        mSendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();

                if (email.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                    builder.setMessage(R.string.forgot_password_enter_email)
                            .setTitle(R.string.sign_up_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    mProgressBar.setVisibility(ProgressBar.VISIBLE);
                    mSendEmailButton.setVisibility(Button.INVISIBLE);
                    mCancelButton.setVisibility(Button.INVISIBLE);
                    mEmail.setVisibility(EditText.INVISIBLE);

                    ParseUser.requestPasswordResetInBackground(email,
                            new RequestPasswordResetCallback() {
                                public void done(ParseException e) {
                                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                    mSendEmailButton.setVisibility(Button.VISIBLE);
                                    mCancelButton.setVisibility(Button.VISIBLE);
                                    mEmail.setVisibility(EditText.VISIBLE);

                                    if (e == null) {
                                        Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
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
