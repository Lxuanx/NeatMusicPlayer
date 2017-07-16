package com.gdin.lxx.mobileplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;


public class AudioItem implements Serializable {
    private String title;
    private String artist;
    private String path;

    public static AudioItem fromCursor(Cursor cursor) {
        AudioItem audioItem = new AudioItem();
        audioItem.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        audioItem.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        audioItem.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
        return audioItem;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
