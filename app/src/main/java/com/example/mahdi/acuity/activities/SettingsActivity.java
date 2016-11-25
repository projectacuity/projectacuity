package com.example.mahdi.acuity.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mahdi.acuity.R;
import com.example.mahdi.acuity.models.Post;
import com.example.mahdi.acuity.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;
import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


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
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private DatabaseReference mUserRef;
    private DatabaseReference userPostRef;
    private ValueEventListener userListner;
    private ValueEventListener photoListner1;
    private ValueEventListener photoListner2;
    private ValueEventListener nameListner1;
    private ValueEventListener nameListner2;
    private DatabaseReference postRef;
    private ProgressDialog mProgdial;
    private static final int REQUEST_PERMISSIONS = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgdial = new ProgressDialog(this);
        mProgdial.setCancelable(false);
        mProgdial.setIndeterminate(true);
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
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userPostRef = mDatabase.child("user-posts").child(mAuth.getCurrentUser().getUid());
        postRef = mDatabase.child("posts");

    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (userListner!=null) {
            mUserRef.removeEventListener(userListner);
        }
        if (photoListner1!=null) {
            userPostRef.removeEventListener(photoListner1);
        }
        if (nameListner1!=null) {
            userPostRef.removeEventListener(nameListner1);
        }
        if (photoListner2!=null) {
            postRef.removeEventListener(photoListner2);
        }
        if (nameListner2!=null) {
            postRef.removeEventListener(nameListner2);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void allowPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.CAMERA)) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Please Grant Permissions",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(SettingsActivity.this,
                                        new String[]{Manifest.permission
                                                .READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                        REQUEST_PERMISSIONS);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(SettingsActivity.this,
                        new String[]{Manifest.permission
                                .READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        REQUEST_PERMISSIONS);
            }
        }
        else {
            takePhotoAction();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if ((grantResults.length > 0) && (grantResults[0] +
                        grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    takePhotoAction();

                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Enable Permissions from settings",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(intent);
                                }
                            }).show();
                }
                return;
            }
        }
    }

    private void setUserInfo() {
        final Context contextUserPhoto = userPhoto.getContext();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        userListner = mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getUsername()!=null) {
                    userNickname.setText(user.getUsername());
                }
                if (user.getEmail()!=null) {
                    userEmail.setText(user.getEmail());
                }
                if (user.getPhotoUrl() != null) {
                    Glide.with(contextUserPhoto).load(user.getPhotoUrl()).bitmapTransform(new CropCircleTransformation(contextUserPhoto)).into(userPhoto);
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
            allowPermissions();
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
                                String nickname = mNickname.getText().toString();
                                updateNickname(nickname);
                                Toast.makeText(getApplicationContext(),"Nickname is updated!",Toast.LENGTH_LONG).show();
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
                                String email = mEmailView.getText().toString();
                                updateMail(email);
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
                                String pass = mPasswordView.getText().toString();
                                updatePass(pass);
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
        builderSingle.setIcon(R.drawable.add_a_photo_black);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == camera_intent || requestCode == gal_intent) && resultCode == RESULT_OK){

            Uri uri = data.getData();
            mProgdial.setMessage("Uploading photo...");
            mProgdial.show();
            StorageReference filepath = mStorage.child("photos")
                    .child(mAuth.getCurrentUser().getUid()).child("profile");
            filepath.child(UUID.randomUUID().toString()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgdial.dismiss();
                    String imgUrl = taskSnapshot.getDownloadUrl().toString();
                    updatePhoto(imgUrl);
                    Toast.makeText(getApplicationContext(),"Photo is updated!",Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private void updatePhoto(final String imgUrl) {
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("photoUrl").setValue(imgUrl);
        photoListner1 = userPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String postId = postSnapshot.getKey();
                    userPostRef.child(postId).child("authorUrl").setValue(imgUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        photoListner2 = postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String postId = postSnapshot.getKey();
                    Post post = postSnapshot.getValue(Post.class);
                    if ((post.uid).equals(mAuth.getCurrentUser().getUid())) {
                        postRef.child(postId).child("authorUrl").setValue(imgUrl);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void updateNickname(final String nickname) {
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("username").setValue(nickname);
        nameListner1 = userPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String postId = postSnapshot.getKey();
                    userPostRef.child(postId).child("author").setValue(nickname);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        nameListner2 = postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String postId = postSnapshot.getKey();
                    Post post = postSnapshot.getValue(Post.class);
                    if ((post.uid).equals(mAuth.getCurrentUser().getUid())) {
                        postRef.child(postId).child("author").setValue(nickname);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void updateMail(final String email) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.i("nizar4",email);
        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("email").setValue(email);
                            Toast.makeText(getApplicationContext(),"Email is updated!",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Failed to update email!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }
    private void updatePass(final String pass) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(pass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Password is updated!",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Failed to update password!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
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
