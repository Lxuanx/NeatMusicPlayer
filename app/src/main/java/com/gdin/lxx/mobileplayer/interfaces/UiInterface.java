package com.gdin.lxx.mobileplayer.interfaces;

import android.view.View;

public interface UiInterface extends View.OnClickListener {

    int getLayoutResId();

    void initViews();

    void loadData();
}
