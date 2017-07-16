package com.gdin.lxx.mobileplayer.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.gdin.lxx.mobileplayer.R;

public class SplashActivity extends BaseActivity {

    private Handler mHandler;

    @Override
    public void initViews() {

    }

    @Override
    public void loadData() {
        delayedEnterHome();
    }

    private void delayedEnterHome() {
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                enterHome();
            }
        }, 3000);
    }

    private void enterHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public int getLayoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeCallbacksAndMessages(null);
                enterHome();
                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onClick(View v, int id) {

    }
}
