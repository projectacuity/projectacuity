package com.example.mahdi.acuity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private TextView SignUpView;
    private Button SignInView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_welcome);
        SignUpView = (TextView) findViewById(R.id.link_to_sign_up);
        SignInView = (Button) findViewById(R.id.sign_in_button);
        SignUpView.setOnClickListener(new View.OnClickListener(){
            Intent intent1;
            @Override
            public void onClick(View view) {
                intent1=new Intent(WelcomeActivity.this,SignUpActivity.class);
                startActivity(intent1);
            }

        });
        SignInView.setOnClickListener(new View.OnClickListener() {
            Intent intent2;
            @Override
            public void onClick(View v) {
                intent2=new Intent(WelcomeActivity.this,SignInActivity.class);
                startActivity(intent2);
            }
        });
    }
}
