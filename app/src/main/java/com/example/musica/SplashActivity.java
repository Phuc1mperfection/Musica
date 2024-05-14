package com.example.musica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {
    private Handler handler;
    private Runnable runnable;
    private LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        lottieAnimationView = findViewById(R.id.lottieAnimation);
        lottieAnimationView.animate().translationY(2000).setDuration(2000).setStartDelay(0);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(),  MainActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);

    }
}