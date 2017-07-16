package com.gdin.lxx.mobileplayer.bean;

/**
 * Created by Administrator on 2017/2/17.
 */

public class LyricItem {
    private int startShowTime;
    private String text;

    public LyricItem(int startShowTime, String text) {
        this.startShowTime = startShowTime;
        this.text = text;
    }

    @Override
    public String toString() {
        return "LyricItem{startShowTime=" + startShowTime + ", text='" + text + '\'' + '}';
    }

    public long getStartShowTime() {
        return startShowTime;
    }

    public void setStartShowTime(int startShowTime) {
        this.startShowTime = startShowTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
