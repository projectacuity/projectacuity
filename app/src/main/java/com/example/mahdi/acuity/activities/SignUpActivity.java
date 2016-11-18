package com.example.mahdi.acuity.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnClickListener {
    private static final String TAG = "SignUpActivity";
    SessionManager session;
    private TextInputEditText mEmailView;
    private TextInputEditText mPasswordView;
    private TextInputEditText mNickname;
    private TextInputEditText mPasswordConfirm;
    private TextInputLayout mTextPassView;
    private TextInputLayout mTextEmailView;
    private TextInputLayout mTextNicknameView;
    private TextInputLayout mTextPassConfirm;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;
    final String defaultUserPhoto = "https://firebasestorage.googleapis.com/v0/b/projectacuity.appspot.com/o/photos%2Fphoto.png?alt=media&token=f7613099-8b08-4ef2-8624-c4503db5941d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionManager(getApplicationContext());
        mEmailView = (TextInputEditText) findViewById(R.id.email);
        mNickname = (TextInputEditText) findViewById(R.id.nickname);
        mPasswordView = (TextInputEditText) findViewById(R.id.password);
        mPasswordConfirm = (TextInputEditText) findViewById(R.id.password_confirm);
        mTextPassView = (TextInputLayout) findViewById(R.id.password_layout);
        mTextEmailView = (TextInputLayout) findViewById(R.id.email_layout);
        mTextNicknameView = (TextInputLayout) findViewById(R.id.nickname_layout);
        mTextPassConfirm = (TextInputLayout) findViewById(R.id.password_confirm_layout);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Signing up...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        mPasswordConfirm.setOnEditorActionListener(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(mNickname.getText().toString())
                            .setPhotoUri(Uri.parse(defaultUserPhoto))
                            .build();
                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            mProgress.dismiss();
                            startActivity(intent);
                            finish();
                        }
                    });

                }
            }
        };
    }
    @Override
    public void onResume(){
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        mProgress.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            mProgress.dismiss();
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        mTextPassView.setError(null);
        mTextEmailView.setError(null);
        mTextNicknameView.setError(null);
        mTextPassConfirm.setError(null);
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String nickname = mNickname.getText().toString();
        String passConfirm = mPasswordConfirm.getText().toString();
        boolean valid = true;
        View focusView = null;
        if (TextUtils.isEmpty(password) || !isPasswordValid1(password)) {
            mTextPassView.setError(getString(R.string.error_invalid_password1));
            focusView = mPasswordView;
            valid = false;
        } else if (!isPasswordValid2(password)) {
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
        if (!isValidName(nickname)) {
            mTextNicknameView.setError(getString(R.string.error_invalid_nickname));
            focusView = mNickname;
            valid = false;
        }
        if (!isPasswordConfirmValid(password, passConfirm)) {
            mTextPassConfirm.setError(getString(R.string.error_pass_confirm));
            focusView = mPasswordConfirm;
            valid = false;
        }
        if (!valid) {
            focusView.requestFocus();
        }
        return valid;
    }

    private void onAuthSuccess(FirebaseUser firebaseUser) {
        String nickname = mNickname.getText().toString();
        String email = mEmailView.getText().toString();
        writeNewUser(firebaseUser.getUid(), nickname, email, defaultUserPhoto);
    }

    private void writeNewUser(String userId, String name, String email, String photoUrl) {
        User user = new User(name, email, photoUrl);
        Map<String, Object> userValues = user.toMap();
        mDatabase.child("users").child(userId).setValue(userValues);
        session.createLoginSession(user.getUsername(),user.getEmail());
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_sign_in_button) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
            createAccount(mEmailView.getText().toString(), mPasswordView.getText().toString());
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == EditorInfo.IME_ACTION_GO) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
            createAccount(mEmailView.getText().toString(), mPasswordView.getText().toString());
            return true;
        }
        return false;
    }

    private boolean isPasswordConfirmValid(String password1, String password2) {
        return password1.equals(password2);
    }

    private boolean isEmailValid(String email) {
        return limitEmailCharacters(email);
    }

    private boolean isPasswordValid1(String password) {
        return password.length() >= 6;
    }

    private boolean isPasswordValid2(String password) {
        return limitPasswordCharacters(password);
    }

    private boolean isValidName(String name) {
        return limitNameCharacters(name);
    }

    private static boolean limitEmailCharacters(String about) {
        final Pattern USER_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        return USER_NAME_PATTERN.matcher(about).matches();
    }

    private static boolean limitPasswordCharacters(String about) {
        final Pattern USER_NAME_PATTERN = Pattern.compile("\\p{ASCII}*$");
        return USER_NAME_PATTERN.matcher(about).matches();
    }

    private static boolean limitNameCharacters(String about) {
        final Pattern USER_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._]+$");
        return USER_NAME_PATTERN.matcher(about).matches();
    }
}