package com.gdin.lxx.mobileplayer.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gdin.lxx.mobileplayer.view.LyricView;
import com.gdin.lxx.mobileplayer.R;
import com.gdin.lxx.mobileplayer.bean.AudioItem;
import com.gdin.lxx.mobileplayer.interfaces.AudioService;
import com.gdin.lxx.mobileplayer.interfaces.Keys;
import com.gdin.lxx.mobileplayer.interfaces.Ui;
import com.gdin.lxx.mobileplayer.service.AudioPlayerService;
import com.gdin.lxx.mobileplayer.util.Utils;

import java.util.ArrayList;

public class AudioPlayerActivity extends BaseActivity implements Ui {

    public static final int SERVICE_INTERFACE = 0;
    public static final int UPDATE_PLAY_TIME = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SERVICE_INTERFACE:
                    mAudioService = (AudioService) msg.obj;
                    int isAgainPlay = msg.arg1;
                    if (isAgainPlay == AudioPlayerService.CAN_PLAY) {
                        mAudioService.openAudio();
                    } else {
                        updateUi(mAudioService.getCurrentAudioItem());
                    }
                    break;
                case UPDATE_PLAY_TIME:
                    if (mAudioService.isPlaying()) {
                        updatePlayTime(mAudioService.getCurrentPosition());
                    } else {
                        updatePlayBtn();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private Messenger mMessenger = new Messenger(mHandler);
    private AudioService mAudioService;
    private ServiceConnection mConn;
    private Button btn_play;
    private TextView tv_title;
    private TextView tv_artist;
    private TextView tv_play_time;
    private SeekBar sb_audio;
    private Button btn_play_mode;
    private LyricView lyric_view;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_audio_player;
    }

    @Override
    public void initViews() {
        btn_play = findView(R.id.btn_play);
        tv_title = findView(R.id.tv_title);
        tv_artist = findView(R.id.tv_artist);
        tv_play_time = findView(R.id.tv_play_time);
        sb_audio = findView(R.id.sb_audio);
        btn_play_mode = findView(R.id.btn_play_mode);
        lyric_view = findView(R.id.lyric_view);
        ImageView iv_vision = findView(R.id.iv_vision);
        AnimationDrawable anim = (AnimationDrawable) iv_vision.getBackground();
        anim.start();

        sb_audio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            private int mProgress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mProgress = progress;
                    updatePlayTime(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mAudioService.seekTo(mProgress);
            }
        });
    }

    @Override
    public void loadData() {
        connectService();
    }

    private void connectService() {
        ArrayList<AudioItem> audioItems = (ArrayList<AudioItem>) getIntent().getSerializableExtra(Keys.ITEM_LIST);
        int position = getIntent().getIntExtra(Keys.CURRENT_POSITION, -1);

        Intent service = new Intent(this, AudioPlayerService.class);
        service.putExtra(Keys.ITEM_LIST, audioItems);
        service.putExtra(Keys.CURRENT_POSITION, position);
        service.putExtra(Keys.WHAT, getIntent().getIntExtra(Keys.WHAT, -1));
        startService(service);

        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                Messenger messenger = new Messenger(service);
                Message message = Message.obtain();
                message.what = AudioPlayerService.UI_INTERFACE;
                message.obj = AudioPlayerActivity.this;
                message.replyTo = mMessenger;
                try {
                    messenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(service, mConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onClick(View v, int id) {
        switch (id) {
            case R.id.btn_play:
                play();
                break;
            case R.id.btn_pre:
                pre();
                break;
            case R.id.btn_next:
                next();
                break;
            case R.id.btn_play_mode:
                switchPlayMode();
                break;
            default:
                break;
        }
    }

    private void switchPlayMode() {
        int currentPlayMode = mAudioService.switchPlayMode();
        updatePlayMode(currentPlayMode);
    }

    private void updatePlayMode(int currentPlayMode) {
        int resId;
        switch (currentPlayMode) {
            case AudioPlayerService.PLAY_MODE_ORDER:
                resId = R.drawable.selector_audio_btn_playmode_order;
                break;
            case AudioPlayerService.PLAY_MODE_RANDOM:
                resId = R.drawable.selector_audio_btn_playmode_random;
                break;
            case AudioPlayerService.PLAY_MODE_SINGLE:
                resId = R.drawable.selector_audio_btn_playmode_single;
                break;
            default:
                throw new RuntimeException("未知的选择模式");
        }
        btn_play_mode.setBackgroundResource(resId);
    }

    private void pre() {
        mAudioService.pre();
    }

    private void next() {
        mAudioService.next();
    }

    private void play() {
        if (mAudioService.isPlaying()) {
            mHandler.removeCallbacksAndMessages(null);
            mAudioService.pause();
        } else {
            mHandler.sendEmptyMessage(UPDATE_PLAY_TIME);
            mAudioService.start();
        }
        updatePlayBtn();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        unbindService(mConn);
        super.onDestroy();
    }


    @Override
    public void updateUi(AudioItem item) {
        tv_title.setText(item.getTitle());
        tv_artist.setText(item.getArtist());
        Utils.setTextMarquee(tv_artist);
        sb_audio.setMax(mAudioService.getDuration());
        updatePlayBtn();
        updatePlayTime(mAudioService.getCurrentPosition());
        updatePlayMode(mAudioService.getCurrentPlayMode());
    }


    private void updatePlayTime(int position) {
        CharSequence currentPosition = Utils.formatMillis(position);
        sb_audio.setProgress(position);
        lyric_view.setCurrentPosition(position);
        CharSequence duration = Utils.formatMillis(mAudioService.getDuration());

        tv_play_time.setText(currentPosition + "/" + duration);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessageDelayed(UPDATE_PLAY_TIME, 200);
    }

    private void updatePlayBtn() {
        int resId;
        if (mAudioService.isPlaying()) {
            resId = R.drawable.selector_audio_btn_pause;
        } else {
            resId = R.drawable.selector_audio_btn_play;
        }
        btn_play.setBackgroundResource(resId);
    }
}
