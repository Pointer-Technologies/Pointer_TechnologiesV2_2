package com.example.pointer_technologiesv2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    Animation topAnim, bottomAnim;
    ImageView image;
    TextView slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        image = findViewById(R.id.imageStart);
        slogan = findViewById(R.id.textStart);

        image.setAnimation(topAnim);
        slogan.setAnimation(bottomAnim);

        int SPLASH_SCREEN = 5000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(StartActivity.this,SignInActivity.class);
                Pair[] pairs=new Pair[2];
                pairs[0]= new Pair<View,String>(image,"logo_image");
                ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(StartActivity.this,pairs);
                startActivity(intent,options.toBundle());
                finish();
            }
        }, SPLASH_SCREEN);
    }
}