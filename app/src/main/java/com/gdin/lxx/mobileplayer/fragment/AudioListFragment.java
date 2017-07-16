package com.gdin.lxx.mobileplayer.fragment;


import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gdin.lxx.mobileplayer.R;
import com.gdin.lxx.mobileplayer.activity.AudioPlayerActivity;
import com.gdin.lxx.mobileplayer.adapter.AudioListAdapter;
import com.gdin.lxx.mobileplayer.bean.AudioItem;
import com.gdin.lxx.mobileplayer.interfaces.Keys;

import java.util.ArrayList;

public class AudioListFragment extends BaseFragment {

    private ListView mListView;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_media_list;
    }

    @Override
    public void initViews() {
        mListView = (ListView) rootView;
        Log.e("lxuanx","xxxxxxxxxxxxx");
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                ArrayList<AudioItem> audioItems = getAudioItems(cursor);
                enterAudioPlayerActivity(audioItems, position);
            }
        });
    }

    private void enterAudioPlayerActivity(ArrayList<AudioItem> audioItems, int position) {
        Intent intent = new Intent(getContext(), AudioPlayerActivity.class);
        intent.putExtra(Keys.ITEM_LIST, audioItems);
        intent.putExtra(Keys.CURRENT_POSITION, position);
        startActivity(intent);
    }

    private ArrayList<AudioItem> getAudioItems(Cursor cursor) {
        ArrayList<AudioItem> audioItems = new ArrayList<>();
        cursor.moveToFirst();
        do {
            AudioItem audioItem = AudioItem.fromCursor(cursor);
            audioItems.add(audioItem);
        } while (cursor.moveToNext());
        return audioItems;
    }

    @Override
    public void loadData() {
        AsyncQueryHandler queryHandler = new AsyncQueryHandler(getActivity().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//                Utils.printCursor(cursor);
                AudioListAdapter adapter = new AudioListAdapter(getContext(), cursor);
                mListView.setAdapter(adapter);
            }
        };

        int token = 0;            // 相当于Message.what
        Object cookie = null;    // 相当于Message.obj
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        String[] projection = {            // 指定要查询哪些列
                Media._ID, Media.TITLE, Media.ARTIST, Media.DATA
        };
        String selection = null;        // 指定查询条件
        String[] selectionArgs = null;    // 指定查询条件中的参数
        String orderBy = Media.TITLE + " ASC";

        queryHandler.startQuery(token, cookie, uri, projection, selection, selectionArgs, orderBy);
    }

    @Override
    protected void onClick(View v, int id) {

    }

}
