package com.gdin.lxx.mobileplayer.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

import com.gdin.lxx.mobileplayer.R;
import com.gdin.lxx.mobileplayer.interfaces.UiInterface;
import com.gdin.lxx.mobileplayer.util.Utils;

public abstract class BaseActivity extends FragmentActivity implements UiInterface {

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutResId());
        rootView = findViewById(android.R.id.content);
        Utils.findButtonSetOnClickListener(rootView, this);
        initViews();
        loadData();
    }

    public <T> T findView(int id) {
        T view = (T) findViewById(id);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            default:
                onClick(v, v.getId());
                break;
        }
    }

    protected abstract void onClick(View v, int id);
}
