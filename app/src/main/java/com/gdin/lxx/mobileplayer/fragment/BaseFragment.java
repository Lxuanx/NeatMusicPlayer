package com.gdin.lxx.mobileplayer.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gdin.lxx.mobileplayer.R;
import com.gdin.lxx.mobileplayer.interfaces.UiInterface;
import com.gdin.lxx.mobileplayer.util.Utils;

public abstract class BaseFragment extends Fragment implements UiInterface {

    protected View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutResId(), null, false);
        Utils.findButtonSetOnClickListener(rootView, this);
        initViews();
        loadData();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                getActivity().finish();
                break;
            default:
                onClick(v, v.getId());
                break;
        }
    }

    protected abstract void onClick(View v, int id);

}
