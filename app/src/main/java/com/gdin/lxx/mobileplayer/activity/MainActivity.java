package com.gdin.lxx.mobileplayer.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.gdin.lxx.mobileplayer.R;
import com.gdin.lxx.mobileplayer.fragment.AudioListFragment;

public class MainActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onClick(View v, int id) {

    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fly_audio, new AudioListFragment());
        ft.commit();
    }

    @Override
    public void loadData() {

    }
}
