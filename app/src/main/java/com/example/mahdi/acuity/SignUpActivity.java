package com.example.mahdi.acuity;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity {

    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mPasswordConfirm;
    private TextInputLayout mTextPassView;
    private TextInputLayout mTextEmailView;
    private TextInputLayout mTextFnameView;
    private TextInputLayout mTextLnameView;
    private TextInputLayout mTextPassConfirm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEmailView = (EditText) findViewById(R.id.email);
        mFirstName = (EditText) findViewById(R.id.first_name);
        mLastName = (EditText) findViewById(R.id.last_name);
        mTextPassView = (TextInputLayout) findViewById(R.id.password_layout);
        mTextEmailView = (TextInputLayout) findViewById(R.id.email_layout);
        mTextFnameView = (TextInputLayout) findViewById(R.id.first_name_layout);
        mTextLnameView = (TextInputLayout) findViewById(R.id.last_name_layout);
        mTextPassConfirm = (TextInputLayout) findViewById(R.id.password_confirm_layout);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordConfirm = (EditText) findViewById(R.id.password_confirm);
        mPasswordConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_GO) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        mTextPassView.setError(null);
        mTextEmailView.setError(null);
        mTextFnameView.setError(null);
        mTextLnameView.setError(null);
        mTextPassConfirm.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String passConfirm = mPasswordConfirm.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || !isPasswordValid1(password)) {
            mTextPassView.setError(getString(R.string.error_invalid_password1));
            focusView = mPasswordView;
            cancel = true;
        }
        else if (!isPasswordValid2(password)) {
            mTextPassView.setError(getString(R.string.error_invalid_password2));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(email)) {
            mTextEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mTextEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        if (!isValidName(lastName)) {
            mTextLnameView.setError(getString(R.string.error_invalid_name));
            focusView = mLastName;
            cancel = true;
        }
        if (!isValidName(firstName)) {
            mTextFnameView.setError(getString(R.string.error_invalid_name));
            focusView = mFirstName;
            cancel = true;
        }
        if (!isPasswordConfirmValid(password,passConfirm)) {
            mTextPassConfirm.setError(getString(R.string.error_pass_confirm));
            focusView = mPasswordConfirm;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Intent homePage=new Intent(SignUpActivity.this,Main2Activity.class);
            startActivity(homePage);
        }
    }

    private boolean isPasswordConfirmValid(String password1,String password2) {
        return password1.equals(password2);
    }
    private boolean isEmailValid(String email) {
        return limitEmailCharacters(email);
    }

    private boolean isPasswordValid1(String password) {
        return password.length()>=6;
    }
    private boolean isPasswordValid2(String password) {
        return limitPasswordCharacters(password);
    }
    private  boolean isValidName(String name) {
        return limitNameCharacters(name);
    }

    private static boolean limitEmailCharacters(String about){
        final Pattern USER_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        if(USER_NAME_PATTERN.matcher(about).matches()){
            return true;
        }
        return false;
    }
    private static boolean limitPasswordCharacters(String about){
        final Pattern USER_NAME_PATTERN = Pattern.compile("\\p{ASCII}*$");
        if(USER_NAME_PATTERN.matcher(about).matches()){
            return true;
        }
        return false;
    }
    private static boolean limitNameCharacters(String about){
        final Pattern USER_NAME_PATTERN = Pattern.compile("^[a-zA-Z]+$");
        if(USER_NAME_PATTERN.matcher(about).matches()){
            return true;
        }
        return false;
    }
}