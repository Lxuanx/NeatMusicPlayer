package com.gdin.lxx.mobileplayer.util;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gdin.lxx.mobileplayer.interfaces.Constants;

import java.util.Calendar;

import static android.content.Context.WINDOW_SERVICE;


public class Utils {
    public static void findButtonSetOnClickListener(View rootView, View.OnClickListener onClickListener) {
        if (rootView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) rootView;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View view = viewGroup.getChildAt(i);
                if (view instanceof ViewGroup) {
                    findButtonSetOnClickListener(view, onClickListener);
                } else if (view instanceof Button || view instanceof ImageButton) {
                    view.setOnClickListener(onClickListener);
                }
            }
        }
    }

    public static void showToast(Context context, String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        int ScreenWidth = windowManager.getDefaultDisplay().getWidth();
        return ScreenWidth;
    }

    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        int ScreenHeight = windowManager.getDefaultDisplay().getHeight();
        return ScreenHeight;
    }

    public static void printCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String columnName = cursor.getColumnName(i);
                String str = cursor.getString(i);
                Logger.i("数据", columnName+ "=" + str);
            }
        }
    }
    /**
     *     设置TextView跑马灯效果
     */
    public static void setTextMarquee(TextView textView) {
        if (textView != null) {
            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textView.setSingleLine(true);
            textView.setSelected(true);
            textView.setFocusable(true);
            textView.setFocusableInTouchMode(true);
        }
    }
    public static CharSequence formatMillis(long duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.add(Calendar.MILLISECOND, (int) duration);
        String pattern = (duration / Constants.MINUTEMILLIS )> 60 ? "HH:mm:ss" : "mm:ss";

        return android.text.format.DateFormat.format(pattern,calendar);
    }
}
