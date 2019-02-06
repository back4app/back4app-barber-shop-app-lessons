package com.example.back4app.barbershop_lesson3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ParseException;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class RegisterActivity extends AppCompatActivity {

    private EditText usernameView;
    private EditText emailView;
    private EditText passwordView;
    private EditText passwordAgainView;
    private ImageView profilePhoto;
    private ImageView editButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameView = findViewById(R.id.username);
        emailView = findViewById(R.id.email);
        passwordView = findViewById(R.id.password);
        passwordAgainView = findViewById(R.id.password_again);
        profilePhoto = findViewById(R.id.profile_photo);
        editButton = findViewById(R.id.edit_button);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.null_photo);
        profilePhoto.setImageBitmap(bitmap);


        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_photo)), 1);
            }
        });


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_photo)), 1);
            }
        });

        final Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validating the sign up data
                boolean validationError = false;

                int numberOfErrors = 0;

                ArrayList errorArray = new ArrayList();
                StringBuilder validationErrorMessage = new StringBuilder("");

                if(isEmpty(usernameView)){
                    numberOfErrors++;
                    validationError = true;
                    errorArray.add(getString(R.string.username));
                }
                if(isEmpty(emailView)){
                    numberOfErrors++;
                    validationError = true;
                    errorArray.add(getString(R.string.email));
                }
                if(isEmpty(passwordView)){
                    numberOfErrors++;
                    validationError = true;
                    errorArray.add(getString(R.string.password));
                }
                if(isEmpty(passwordAgainView)){
                    numberOfErrors++;
                    validationError = true;
                    errorArray.add(getString(R.string.password_again));
                }

                if(validationError){

                    validationErrorMessage.append(getString(R.string.please_complete));

                    if(numberOfErrors == 1){
                        validationErrorMessage.append(getString(R.string.required_field) + " ");
                    }
                    else{
                        validationErrorMessage.append(getString(R.string.required_fields) + " ");
                    }

                    for(int i = 1; i <= numberOfErrors; i++){
                        validationErrorMessage.append(errorArray.get(i - 1).toString());
                        if(i < numberOfErrors - 1){
                            validationErrorMessage.append(", ");
                        }
                        else if(i == numberOfErrors - 1){
                            validationErrorMessage.append(" " + getString(R.string.and) + " ");
                        }

                    }

                    validationErrorMessage.append(".");


                }

                if(!validationError){
                  if(!isEmailValid(emailView.getText().toString())){
                      validationErrorMessage.append(getString(R.string.not_valid_email));
                      Toast.makeText(RegisterActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                      return;
                  }
                  if(!isMatching(passwordView, passwordAgainView)){
                      validationErrorMessage.append(getString(R.string.not_matching_passwords));
                      Toast.makeText(RegisterActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                      return;
                  }
                }
                else{
                    Toast.makeText(RegisterActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                    return;
                }


                final ProgressDialog dlg = new ProgressDialog(RegisterActivity.this, R.style.AlertDialogTheme);
                dlg.setTitle(getString(R.string.wait));
                dlg.setMessage(getString(R.string.signing));
                dlg.show();


                Bitmap bitmapImage = ((BitmapDrawable) profilePhoto.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                byte[] byteArray = stream.toByteArray();

                final ParseFile file = new ParseFile(usernameView.getText().toString() + ".jpeg", byteArray);
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(null == e){
                            // Sign up with parse
                            try {
                                // Reset errors
                                emailView.setError(null);
                                passwordView.setError(null);

                                // Sign up with Parse
                                ParseUser user = new ParseUser();
                                user.setUsername(usernameView.getText().toString());
                                user.setPassword(passwordView.getText().toString());
                                user.setEmail(emailView.getText().toString());
                                user.put("Photo", file);

                                user.signUpInBackground(new SignUpCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        dlg.cancel();
                                        if (e == null) {
                                            ParseUser.logOut();
                                            alertDisplayer(getString(R.string.message_successful_creation), getString(R.string.welcome) + " " + usernameView.getText().toString() + "!\n" + getString(R.string.verify_email), false);
                                        } else {
                                            ParseUser.logOut();
                                            alertDisplayer(getString(R.string.message_unsuccessful_creation), e.getMessage(), true);
                                        }
                                    }
                                });
                            } catch (Exception exc) {
                                dlg.cancel();
                                alertDisplayer(getString(R.string.message_unsuccessful_creation), exc.getMessage(), true);
                            }
                        }
                        else{
                            // Some error happened uploading the profile picture
                            dlg.cancel();
                            Toast.makeText(RegisterActivity.this, getString(R.string.error_uploading_photo), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });



    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1){
            profilePhoto.setImageURI(data.getData());
        }
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

    private boolean isMatching(EditText text1, EditText text2){
        if(text1.getText().toString().equals(text2.getText().toString()))
            return true;
        return false;
    }

    private void alertDisplayer(String title, String message, final boolean error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                dialog.cancel();
            }
        });
    }
}
