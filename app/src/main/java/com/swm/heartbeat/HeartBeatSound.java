package com.swm.heartbeat;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.util.Log;

import com.swm.core.HeartBeatData;


/**
 * Created by yangzhenyu on 2016/10/6.
 */

public class HeartBeatSound implements HeartBeatListener{
    private Long mLen = 1000L;
    private Handler mSoundHandler;
    private BeatSound mBeatSoundRunnable;
    private ToneGenerator mBeatSound;
    private int mHeartBeatRate;
    private boolean mReleased = false;
    private int mCurrentVolume;
    private Context mContext;

    private class BeatSound implements Runnable {

        @Override
        public void run() {
            if(mReleased)
                return;
            mBeatSound.startTone(ToneGenerator.TONE_PROP_BEEP, 50);
            synchronized (mLen) {
                mSoundHandler.postDelayed(this, mLen);
            }
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

    @Override
    public void onHeartBeatDataAvailable(HeartBeatData heartBeatData) {
        Log.d("Sound", "onHeartBeatDataAvailable");
        int heartBeatRate = heartBeatData.heartRate;
        if (heartBeatRate <= 0)
            return;

        if (mHeartBeatRate == heartBeatRate)
            return;

        mHeartBeatRate = heartBeatRate;

        if (mBeatSoundRunnable == null) {
            mBeatSound = new ToneGenerator(AudioManager.STREAM_RING, mCurrentVolume);
            mBeatSoundRunnable = new BeatSound();
            synchronized (mLen) {
                mSoundHandler.postDelayed(mBeatSoundRunnable, mLen);
            }
        }
    }

    public void release() {
        mSoundHandler.removeCallbacks(mBeatSoundRunnable);
        mBeatSoundRunnable = null;
        mBeatSound.release();
    }

}
