package com.example.mahdi.acuity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;


public class SignInActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {

    private static final String TAG = "SignInActivity";
    // Session Manager Class
    SessionManager session;
    private EditText mEmailView;
    private EditText mPasswordView;
    private TextInputLayout mTextPassView;
    private TextInputLayout mTextEmailView;
    private LinearLayout mProgressView;
    private View mLoginFormView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Session Manager
        session = new SessionManager(getApplicationContext());
        //EditText
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        //TextInputLayout
        mTextPassView = (TextInputLayout) findViewById(R.id.password_layout);
        mTextEmailView = (TextInputLayout) findViewById(R.id.email_layout);
        mProgressView = (LinearLayout) findViewById(R.id.progressBar);
        mLoginFormView = findViewById(R.id.login_form);
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Sign in...");
        mProgress.setMessage("Please wait.");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        //Button
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        mPasswordView.setOnEditorActionListener(this);
        //Authentication
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        mProgress.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            session.createLoginSession();
                            mProgress.dismiss();
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            finish();
                        }
                        else {
                            mProgress.dismiss();
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private boolean validateForm() {

        mTextPassView.setError(null);
        mTextEmailView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean valid = true;
        View focusView = null;

        if (TextUtils.isEmpty(password) || !isPasswordValid1(password)) {
            mTextPassView.setError(getString(R.string.error_invalid_password1));
            focusView = mPasswordView;
            valid = false;
        }
        else if (!isPasswordValid2(password)) {
            mTextPassView.setError(getString(R.string.error_invalid_password2));
            focusView = mPasswordView;
            valid = false;
        }

        if (TextUtils.isEmpty(email)) {
            mTextEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            valid = false;
        } else if (!isEmailValid(email)) {
            mTextEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            valid = false;
        }

        if (!valid) {
            focusView.requestFocus();
        }
        return valid;
    }
//        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_sign_in_button) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
            signIn(mEmailView.getText().toString(), mPasswordView.getText().toString());
        }
    }
    @Override
    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == EditorInfo.IME_ACTION_GO) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
            signIn(mEmailView.getText().toString(), mPasswordView.getText().toString());
            return true;
        }
        return false;
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
    private static boolean limitEmailCharacters(String about){
        final Pattern USER_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        return USER_NAME_PATTERN.matcher(about).matches();
    }
    private static boolean limitPasswordCharacters(String about){
        final Pattern USER_NAME_PATTERN = Pattern.compile("\\p{ASCII}*$");
        return USER_NAME_PATTERN.matcher(about).matches();
    }

}