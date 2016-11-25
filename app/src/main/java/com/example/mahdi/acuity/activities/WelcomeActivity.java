package com.example.mahdi.acuity.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.mahdi.acuity.R;

public class WelcomeActivity extends AppCompatActivity {

    private TextView SignUpView;
    private Button SignInView;
    private static final int REQUEST_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_welcome);
        SignUpView = (TextView) findViewById(R.id.link_to_sign_up);
        SignInView = (Button) findViewById(R.id.sign_in_button);
        allowPermissions();
        SignUpView.setOnClickListener(new View.OnClickListener(){
            Intent intent;
            @Override
            public void onClick(View view) {
                intent=new Intent(WelcomeActivity.this,SignUpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        });
        SignInView.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                intent=new Intent(getApplicationContext(),SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
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
                                ActivityCompat.requestPermissions(WelcomeActivity.this,
                                        new String[]{Manifest.permission
                                                .READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                        REQUEST_PERMISSIONS);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(WelcomeActivity.this,
                        new String[]{Manifest.permission
                                .READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        REQUEST_PERMISSIONS);
            }
        }
    }
}
