package com.swm.stuff;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.util.Log;


/**
 * Created by yangzhenyu on 2016/10/6.
 */

public class HeartBeatSound {
    private volatile long mLen = 1000L;
    private Handler mSoundHandler;
    private BeatSound mBeatSoundRunnable;
    private ToneGenerator mBeatSound;
    private int heartRate;
    private boolean mReleased = false;
    private int mCurrentVolume;
    private Context mContext;

    public void setHeartRate(int heartRate) {
        Log.d("Sound", "Heart rate: " + heartRate);

        if (heartRate <= 0)
            return;

        if (this.heartRate == heartRate)
            return;

        this.heartRate = heartRate;

        mLen = 60 * 1000 / heartRate;

        if (mBeatSoundRunnable == null) {
            mBeatSound = new ToneGenerator(AudioManager.STREAM_RING, mCurrentVolume);
            mBeatSoundRunnable = new BeatSound();
            mSoundHandler.postDelayed(mBeatSoundRunnable, mLen);
        }

    }

    private class BeatSound implements Runnable {

        @Override
        public void run() {
            if(mReleased)
                return;
            mBeatSound.startTone(ToneGenerator.TONE_PROP_BEEP, 50);
            mSoundHandler.postDelayed(this, mLen);
        }
    }

    public HeartBeatSound(Context context) {
        mContext = context;
    }

    public void prepare() {
        Log.d("Sound", "prepare");
        AudioManager audio = (AudioManager) mContext.getSystemService(mContext.AUDIO_SERVICE);
        mCurrentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
        mSoundHandler = new Handler();
        mBeatSound = new ToneGenerator(AudioManager.STREAM_RING, mCurrentVolume);
    }

    public void release() {
        mSoundHandler.removeCallbacks(mBeatSoundRunnable);
        mBeatSoundRunnable = null;
        mBeatSound.release();
    }

}
