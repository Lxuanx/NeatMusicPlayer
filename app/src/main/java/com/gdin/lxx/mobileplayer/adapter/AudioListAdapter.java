package com.gdin.lxx.mobileplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.gdin.lxx.mobileplayer.R;
import com.gdin.lxx.mobileplayer.bean.AudioItem;
import com.gdin.lxx.mobileplayer.util.Utils;

/**
 * Created by Administrator on 2017/2/15.
 */
public class AudioListAdapter extends CursorAdapter{
    public AudioListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.adapter_audio_list, null);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.tv_title = (TextView) view.findViewById(R.id.tv_title);
        viewHolder.tv_artist = (TextView) view.findViewById(R.id.tv_artist);

        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        AudioItem audioItem = AudioItem.fromCursor(cursor);
        viewHolder.tv_title.setText(audioItem.getTitle());
        viewHolder.tv_artist.setText(audioItem.getArtist());
    }

    class ViewHolder{
        public TextView tv_title;
        public TextView tv_artist;
    }
}
