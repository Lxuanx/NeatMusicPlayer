package com.gdin.lxx.mobileplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.gdin.lxx.mobileplayer.R;
import com.gdin.lxx.mobileplayer.activity.AudioPlayerActivity;
import com.gdin.lxx.mobileplayer.bean.AudioItem;
import com.gdin.lxx.mobileplayer.interfaces.AudioService;
import com.gdin.lxx.mobileplayer.interfaces.Keys;
import com.gdin.lxx.mobileplayer.interfaces.Ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class AudioPlayerService extends Service implements AudioService {

    public static final int UI_INTERFACE = 0;
    public static final int PLAY_MODE_ORDER = 1;
    public static final int PLAY_MODE_RANDOM = 2;
    public static final int PLAY_MODE_SINGLE = 3;
    public static final int CAN_PLAY = 4;
    public static final int NOT_PLAY = 5;

    public int mCurrentPlayMode = PLAY_MODE_ORDER;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UI_INTERFACE:
                    Messenger messenger = msg.replyTo;
                    mUi = (Ui) msg.obj;
                    Message message = Message.obtain();
                    message.what = AudioPlayerActivity.SERVICE_INTERFACE;
                    message.obj = AudioPlayerService.this;
                    message.arg1 = isAgainPlay;
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private Messenger mMessenger = new Messenger(mHandler);

    private Ui mUi;

    private ArrayList<AudioItem> mAudioItems;
    private int mCurrentPosition = -1;
    private MediaPlayer mMediaPlayer;
    private AudioItem mCurrentAudioItem;
    private SharedPreferences sp;
    private Random mRandom;
    private int isAgainPlay;
    int playNotificationId = 0;
    private NotificationManager mNotificationManager;


    @Override
    public void onCreate() {
        sp = getDefaultSharedPreferences();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mRandom = new Random();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int what = intent.getIntExtra(Keys.WHAT, -1);
        switch (what) {
            case NOTIFICATION_PRE:
                pre();
                break;
            case NOTIFICATION_NEXT:
                next();
                break;
            case NOTIFICATION_ROOT:
                isAgainPlay = NOT_PLAY;
                break;
            default:
                mAudioItems = (ArrayList<AudioItem>) intent.getSerializableExtra(Keys.ITEM_LIST);
                int clickPosition = intent.getIntExtra(Keys.CURRENT_POSITION, -1);
                if (mCurrentPosition == clickPosition) {
                    isAgainPlay = NOT_PLAY;
                } else {
                    isAgainPlay = CAN_PLAY;
                    mCurrentPosition = clickPosition;
                }
                break;
        }

        mCurrentPlayMode = sp.getInt(Keys.CURRENT_PLAY_MODE, PLAY_MODE_ORDER);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mNotificationManager.cancel(playNotificationId);
    }

    private SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }


    @Override
    public void openAudio() {
        if (mAudioItems == null || mAudioItems.isEmpty() || mCurrentPosition == -1) {
            return;
        }

        mCurrentAudioItem = mAudioItems.get(mCurrentPosition);

        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        sendBroadcast(i);

        release();

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(this, Uri.parse(mCurrentAudioItem.getPath()));
            mMediaPlayer.setOnPreparedListener(preparedListener);
            mMediaPlayer.setOnCompletionListener(completionListener);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            sendNotification();
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public static final int NOTIFICATION_ROOT = 0;
    public static final int NOTIFICATION_PRE = 1;
    public static final int NOTIFICATION_NEXT = 2;

    private void sendNotification() {
        int icon = R.drawable.icon_notification;
        String tickerText = "当前正在播放:" + mCurrentAudioItem.getTitle();
        long when = System.currentTimeMillis();


        String contentTitle =  mCurrentAudioItem.getTitle();
        String contentText =  mCurrentAudioItem.getArtist();
        PendingIntent pendingIntent = getActivityPendingIntent(NOTIFICATION_ROOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(icon)
                .setTicker(tickerText)
                .setWhen(when)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setContent(getRemoteViews());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(playNotificationId, notification);
    }

    private RemoteViews getRemoteViews() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.tv_title, mCurrentAudioItem.getTitle());
        remoteViews.setTextViewText(R.id.tv_artist, mCurrentAudioItem.getArtist());
        remoteViews.setOnClickPendingIntent(R.id.ll_root, getActivityPendingIntent(NOTIFICATION_ROOT));
        remoteViews.setOnClickPendingIntent(R.id.btn_pre, getServicePendingIntent(NOTIFICATION_PRE));
        remoteViews.setOnClickPendingIntent(R.id.btn_next, getServicePendingIntent(NOTIFICATION_NEXT));
        return remoteViews;
    }

    private PendingIntent getActivityPendingIntent(int what) {
        int requestCode = 1;
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra(Keys.WHAT, what);
        return PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getServicePendingIntent(int what) {
        int requestCode = 1;
        Intent intent = new Intent(this, AudioPlayerService.class);
        intent.putExtra(Keys.WHAT, what);
        return PendingIntent.getService(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void pre() {
        switch (mCurrentPlayMode) {
            case PLAY_MODE_ORDER:
                if (mCurrentPosition != 0) {
                    mCurrentPosition--;
                } else {
                    mCurrentPosition = mAudioItems.size() - 1;
                }
                break;
            case PLAY_MODE_RANDOM:
                int position = mRandom.nextInt(mAudioItems.size());
                while (mCurrentPosition == position) {
                    position = mRandom.nextInt(mAudioItems.size());
                }
                mCurrentPosition = position;
                break;
            case PLAY_MODE_SINGLE:
                //不用任何操作
                break;
            default:
                throw new RuntimeException("未知的选择模式");
        }
        openAudio();
    }

    @Override
    public void next() {
        switch (mCurrentPlayMode) {
            case PLAY_MODE_ORDER:
                if (mCurrentPosition != mAudioItems.size() - 1) {
                    mCurrentPosition++;
                } else {
                    mCurrentPosition = 0;
                }
                break;
            case PLAY_MODE_RANDOM:
                int position = mRandom.nextInt(mAudioItems.size());
                while (mCurrentPosition == position) {
                    position = mRandom.nextInt(mAudioItems.size());
                }
                mCurrentPosition = position;
                break;
            case PLAY_MODE_SINGLE:
                //不用任何操作
                break;
            default:
                throw new RuntimeException("未知的选择模式");
        }
        openAudio();
    }

    @Override
    public void seekTo(int position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
        }
    }

    @Override
    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int switchPlayMode() {
        switch (mCurrentPlayMode) {
            case PLAY_MODE_ORDER:
                mCurrentPlayMode = PLAY_MODE_RANDOM;
                break;
            case PLAY_MODE_RANDOM:
                mCurrentPlayMode = PLAY_MODE_SINGLE;
                break;
            case PLAY_MODE_SINGLE:
                mCurrentPlayMode = PLAY_MODE_ORDER;
                break;
            default:
                throw new RuntimeException("未知的选择模式");
        }

        sp.edit().putInt(Keys.CURRENT_PLAY_MODE, mCurrentPlayMode).apply();
        return mCurrentPlayMode;
    }

    @Override
    public int getCurrentPlayMode() {
        return mCurrentPlayMode;
    }

    @Override
    public AudioItem getCurrentAudioItem() {
        return mCurrentAudioItem;
    }


    MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            start();
            mUi.updateUi(mCurrentAudioItem);
        }
    };
    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    };
}
