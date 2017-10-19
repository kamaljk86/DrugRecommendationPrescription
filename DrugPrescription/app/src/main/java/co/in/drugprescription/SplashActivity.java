package co.in.drugprescription;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.RelativeLayout;

public class SplashActivity extends Activity implements View.OnClickListener {
    RelativeLayout mBackgroundImage;
    CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mBackgroundImage = (RelativeLayout) findViewById(R.id.splash_background);
        mBackgroundImage.setOnClickListener(this);

        mCountDownTimer = new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() { goToHome(); }
        }.start();

    }
    @Override
    public void onClick(View v) { mCountDownTimer.cancel(); goToHome(); }
    public void goToHome() { startActivity(new Intent(this, HomeActivity.class)); SplashActivity.this.finish(); }
}
