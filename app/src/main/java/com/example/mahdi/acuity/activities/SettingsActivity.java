package com.example.mahdi.acuity.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int gal_intent = 2;
    private static final int camera_intent = 1;
    private ImageView userPhoto;
    private TextView userNickname;
    private TextView userEmail;
    private LinearLayout photoClick;
    private LinearLayout nicknameClick;
    private LinearLayout emailClick;
    private LinearLayout passClick;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userPhoto = (ImageView) findViewById(R.id.add_photo);
        userNickname = (TextView) findViewById(R.id.my_nickname);
        userEmail = (TextView) findViewById(R.id.my_email);
        photoClick = (LinearLayout) findViewById(R.id.photo_click);
        nicknameClick = (LinearLayout) findViewById(R.id.nickname_click);
        passClick = (LinearLayout) findViewById(R.id.pass_click);
        emailClick = (LinearLayout) findViewById(R.id.email_click);
        photoClick.setOnClickListener(this);
        emailClick.setOnClickListener(this);
        nicknameClick.setOnClickListener(this);
        passClick.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUserInfo() {
        final Context contextUserPhoto = userPhoto.getContext();
        final DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userNickname.setText(user.getUsername());
                userEmail.setText(user.getEmail());
                if (user.getPhotoUrl() != null) {
                    Glide.with(contextUserPhoto).load(user.getPhotoUrl()).centerCrop().into(userPhoto);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.photo_click) {
            takePhotoAction();
        }
        else if (view.getId() == R.id.nickname_click) {
            LayoutInflater inflater = LayoutInflater.from(this);
            final View dialogView = inflater.inflate(R.layout.item_nickname, null);
            final AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this)
                    .setView(dialogView)
                    .setCancelable(false)
                    .setTitle("Change your nickname")
                    .setPositiveButton("Done", null)
                    .setNegativeButton("Cancel", null)
                    .create();
            final TextInputEditText mNickname = (TextInputEditText) dialogView.findViewById(R.id.nickname);
            final TextInputLayout mTextNicknameView = (TextInputLayout) dialogView.findViewById(R.id.nickname_layout);
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button buttonPositive = (dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                    buttonPositive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!validateNickname(mNickname, mTextNicknameView)) {
                                return;
                            } else {
                                dialog.dismiss();
                            }
                        }
                    });
                    Button buttonNegative = (dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                    buttonNegative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            });
            dialog.show();
        } else if (view.getId() == R.id.email_click) {
            LayoutInflater inflater = LayoutInflater.from(this);
            final View dialogView = inflater.inflate(R.layout.item_email, null);
            final AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this)
                    .setView(dialogView)
                    .setCancelable(false)
                    .setTitle("Change your email")
                    .setPositiveButton("Done", null)
                    .setNegativeButton("Cancel", null)
                    .create();
            final TextInputEditText mEmailView = (TextInputEditText) dialogView.findViewById(R.id.email);
            final TextInputLayout mTextEmailView = (TextInputLayout) dialogView.findViewById(R.id.email_layout);
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button buttonPositive = (dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                    buttonPositive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!validateEmail(mEmailView, mTextEmailView)) {
                                return;
                            } else {
                                dialog.dismiss();
                            }
                        }
                    });
                    Button buttonNegative = (dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                    buttonNegative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            });
            dialog.show();
        }
        else if (view.getId() == R.id.pass_click) {
            LayoutInflater inflater = LayoutInflater.from(this);
            final View dialogView = inflater.inflate(R.layout.item_pass, null);
            final AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this)
                    .setView(dialogView)
                    .setCancelable(false)
                    .setTitle("Change your password")
                    .setPositiveButton("Done", null)
                    .setNegativeButton("Cancel", null)
                    .create();
            final TextInputEditText mPasswordView = (TextInputEditText) dialogView.findViewById(R.id.password);
            final TextInputLayout mTextPassView = (TextInputLayout) dialogView.findViewById(R.id.password_layout);
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button buttonPositive = (dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                    buttonPositive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!validatePass(mPasswordView, mTextPassView)) {
                                return;
                            } else {
                                dialog.dismiss();
                            }
                        }
                    });
                    Button buttonNegative = (dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                    buttonNegative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            });
            dialog.show();
        }
    }
    protected void takePhotoAction() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.add_a_photo_dark);
        builderSingle.setCancelable(false);
        builderSingle.setTitle("Add photo with");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.list_dialog);
        arrayAdapter.add("Camera");
        arrayAdapter.add("Gallery");
        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (arrayAdapter.getItem(which)=="Camera") {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent,camera_intent);
                        }
                        else {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent,gal_intent);
                        }
                    }
                });
        builderSingle.show();
    }

    private boolean validateEmail(TextInputEditText mEmailView, TextInputLayout mTextEmailView) {
        mTextEmailView.setError(null);
        String email = mEmailView.getText().toString();
        boolean valid = true;
        View focusView = null;
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

    private boolean validateNickname(TextInputEditText mNickname, TextInputLayout mTextNicknameView) {
        mTextNicknameView.setError(null);
        String nickname = mNickname.getText().toString();
        boolean valid = true;
        View focusView = null;
        if (!isValidName(nickname)) {
            mTextNicknameView.setError(getString(R.string.error_invalid_nickname));
            focusView = mNickname;
            valid = false;
        }
        if (!valid) {
            focusView.requestFocus();
        }
        return valid;
    }

    private boolean validatePass(TextInputEditText mPasswordView, TextInputLayout mTextPassView) {
        mTextPassView.setError(null);
        String password = mPasswordView.getText().toString();
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
        if (!valid) {
            focusView.requestFocus();
        }
        return valid;
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
    private boolean isEmailValid(String email) {
        return limitEmailCharacters(email);
    }

    private static boolean limitEmailCharacters(String about) {
        final Pattern USER_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
        return USER_NAME_PATTERN.matcher(about).matches();
    }
    private static boolean limitNameCharacters(String about) {
        final Pattern USER_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._]+$");
        return USER_NAME_PATTERN.matcher(about).matches();
    }
    private static boolean limitPasswordCharacters(String about) {
        final Pattern USER_NAME_PATTERN = Pattern.compile("\\p{ASCII}*$");
        return USER_NAME_PATTERN.matcher(about).matches();
    }
}
