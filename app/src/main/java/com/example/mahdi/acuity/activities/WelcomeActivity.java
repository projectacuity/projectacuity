package com.example.mahdi.acuity.activities;

import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_welcome);
        SignUpView = (TextView) findViewById(R.id.link_to_sign_up);
        SignInView = (Button) findViewById(R.id.sign_in_button);
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
}
