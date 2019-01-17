package com.example.back4app.babershopapp_lesson2;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.Intent;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.LogInCallback;
import com.parse.RequestPasswordResetCallback;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameView;
    private EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameView = (EditText) findViewById(R.id.usename);
        passwordView = (EditText) findViewById(R.id.password);


        final Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validating the login data

                boolean validationError = false;

                StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.please_insert) + " ");
                if(isEmpty(usernameView)){
                    validationError = true;
                    validationErrorMessage.append(getString(R.string.an_username));
                }
                if(isEmpty(passwordView)){
                    if(validationError){
                        validationErrorMessage.append(" " + getString(R.string.and) + " ");
                    }

                    validationError = true;
                    validationErrorMessage.append(getString(R.string.a_password));
                }

                validationErrorMessage.append(".");

                if(validationError){
                    Toast.makeText(LoginActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                // Setting up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(LoginActivity.this, R.style.AlertDialogTheme);
                dlg.setTitle(getString(R.string.wait));
                dlg.setMessage(getString(R.string.logging));
                dlg.show();

                // Reset errors
                usernameView.setError(null);
                passwordView.setError(null);


                ParseUser.logInInBackground(usernameView.getText().toString(), passwordView.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        dlg.dismiss();
                        if (parseUser != null) {
                            alertDisplayer(getString(R.string.sucessful_login), getString(R.string.welcome_back) + " " + usernameView.getText().toString() + "!", false);
                        } else {
                            ParseUser.logOut();
                            alertDisplayer(getString(R.string.unsucessful_login), e.getMessage() + " " + getString(R.string.cant_login), true);
                        }
                    }
                });

            }
        });

        final TextView resetPassword = findViewById(R.id.reset_password);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_reset_password, null);
                final EditText emailEditText = (EditText) mView.findViewById(R.id.email);
                Button confirmButton = (Button) mView.findViewById(R.id.confirm);
                Button cancelButton = (Button) mView.findViewById(R.id.cancel);

                builder.setView(mView);

                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isEmailValid(emailEditText.getText().toString())) {
                            Toast.makeText(LoginActivity.this, getString(R.string.not_valid_email), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(!emailEditText.getText().toString().isEmpty()){
                            final String email = emailEditText.getText().toString();
                            ParseQuery<ParseUser> query = ParseUser.getQuery();
                            query.whereEqualTo("email", email);
                            query.setLimit(1);
                            query.findInBackground(new FindCallback<ParseUser>() {
                                @Override
                                public void done(List<ParseUser> user, ParseException e) {
                                    if(e == null && user.size() > 0){
                                        // The query was sucessful

                                        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e == null){
                                                    // An email was sucessfuly sent with reset instructions
                                                    Toast.makeText(LoginActivity.this, getString(R.string.reset_instructions_sent), Toast.LENGTH_LONG).show();
                                                }
                                                else{
                                                    Toast.makeText(LoginActivity.this, getString(R.string.error_reseting), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else{
                                        Toast.makeText(LoginActivity.this, getString(R.string.email_not_found), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else{
                            Toast.makeText(LoginActivity.this, getString(R.string.email_empty), Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    private boolean isEmpty(EditText text){
        if(text.getText().toString().trim().length() > 0){
            return false;
        }

        return true;
    }

    private boolean isEmailValid(CharSequence email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void alertDisplayer(String title, String message, final boolean error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_default, null);
        final TextView titleTextView = (TextView) mView.findViewById(R.id.title);
        final TextView messageTextView = (TextView) mView.findViewById(R.id.message);
        Button confirmButton = (Button) mView.findViewById(R.id.confirm);

        titleTextView.setText(title);
        messageTextView.setText(message);

        builder.setView(mView);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!error) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                dialog.cancel();
            }
        });
    }

}
