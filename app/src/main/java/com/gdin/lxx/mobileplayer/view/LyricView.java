package com.gdin.lxx.mobileplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.gdin.lxx.mobileplayer.R;
import com.gdin.lxx.mobileplayer.bean.LyricItem;
import com.gdin.lxx.mobileplayer.util.Logger;

import java.util.ArrayList;


public class LyricView extends View {

    private final static int DEFAULT_COLOR = Color.WHITE;
    private final static int HEIGHT_LIGHT_COLOR = Color.GREEN;

    private float defaultSize;
    private float heightLightSize;
    private int heightLightLine;
    private ArrayList<LyricItem> mLyricItems;
    private Paint mPaint;
    private int heightLightHeight;
    private int currentPosition;

    public LyricView(Context context) {
        super(context);
        init();
    }

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        defaultSize = getResources().getDimension(R.dimen.default_size);
        heightLightSize = getResources().getDimension(R.dimen.height_light_size);

        mLyricItems = new ArrayList<>();
        heightLightLine = 4;
        for (int i = 0; i < 9; i++) {
            String s = "假装这是第" + i + "行歌词哈";
            mLyricItems.add(new LyricItem(i * 2000, s));
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(HEIGHT_LIGHT_COLOR);
        mPaint.setTextSize(heightLightSize);
        LyricItem lyricItem = mLyricItems.get(heightLightLine);
        canvasTranslateY(canvas, lyricItem);
        String heightLightStr = lyricItem.getText();
        DrawHeightLightLine(canvas, heightLightStr);

        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setTextSize(defaultSize);
        int rowHeight = getRect("测试行高").height() + 16;

        for (int i = 0; i < heightLightLine; i++) {
            LyricItem item = mLyricItems.get(i);
            String str = item.getText();
            DrawOtherLine(canvas, str, heightLightHeight - (heightLightLine - i) * rowHeight);
        }

        for (int i = mLyricItems.size() - 1; i > heightLightLine; i--) {
            LyricItem item = mLyricItems.get(i);
            String str = item.getText();
            DrawOtherLine(canvas, str, heightLightHeight + (i - heightLightLine) * rowHeight);
        }
    }


    /**
     * 画布在Y方向偏移
     *
     * @param canvas 画布
     * @param lyricItem 歌词bean
     */
    private void canvasTranslateY(Canvas canvas, LyricItem lyricItem) {
        if (heightLightLine == mLyricItems.size() - 1) {
            return;
        }
        int rowHeight = getRect(lyricItem.getText()).height();
        long nextStartShowTime = mLyricItems.get(heightLightLine + 1).getStartShowTime();
        long showTime = currentPosition - lyricItem.getStartShowTime();
        long duration = nextStartShowTime - lyricItem.getStartShowTime();
        float scale = (float) showTime / duration;
        float translateY = rowHeight * scale;
        Logger.i("lxuanx", scale + ":" + translateY);
        canvas.translate(0, -translateY);
    }

    private void DrawHeightLightLine(Canvas canvas, String heightLightStr) {
        heightLightHeight = getY(heightLightStr);
        DrawOtherLine(canvas, heightLightStr, heightLightHeight);
    }

    private void DrawOtherLine(Canvas canvas, String heightLightStr, int y) {
        int x = getX(heightLightStr);
        canvas.drawText(heightLightStr, x, y, mPaint);
    }

    private int getX(String heightLightStr) {
        Rect bounds = getRect(heightLightStr);
        return getWidth() / 2 - bounds.width() / 2;
    }

    private int getY(String heightLightStr) {
        Rect bounds = getRect(heightLightStr);
        return getHeight() / 2 + bounds.height() / 2;
    }

    @NonNull
    private Rect getRect(String heightLightStr) {
        Rect bounds = new Rect();
        mPaint.getTextBounds(heightLightStr, 0, heightLightStr.length(), bounds);
        return bounds;
    }

    public void setCurrentPosition(int position) {
        currentPosition = position;

        heightLightLine = 0;
        for (int i = 0; i < mLyricItems.size(); i++) {
            long startShowTime = mLyricItems.get(i).getStartShowTime();
            if (startShowTime < position) {
                heightLightLine = i;
            } else {
                break;
            }
        }
        invalidate();
    }
}
