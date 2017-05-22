package com.swm.view;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.swm.heart.R;

import static com.swm.training.TrainingListener.DANGER;
import static com.swm.training.TrainingListener.INTENSITY_10K;
import static com.swm.training.TrainingListener.INTENSITY_E;
import static com.swm.training.TrainingListener.INTENSITY_I;
import static com.swm.training.TrainingListener.INTENSITY_M;
import static com.swm.training.TrainingListener.INTENSITY_NONE;
import static com.swm.training.TrainingListener.INTENSITY_R;
import static com.swm.training.TrainingListener.INTENSITY_T;

/**
 * Created by yangzhenyu on 2017/5/17.
 */

public class IntensityView extends AppCompatTextView {
    private Animator mIntensityColorAnimator;

    public IntensityView(Context context) {
        super(context);
    }

    public IntensityView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IntensityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setIntensity(int intensity) {
        if (mIntensityColorAnimator != null && mIntensityColorAnimator.isStarted()) {
            mIntensityColorAnimator.cancel();
        }
        ArgbEvaluator evaluator = new ArgbEvaluator();

        if (intensity == INTENSITY_NONE) {
            setText(getResources().getString(R.string.swm_intensity_none));
            mIntensityColorAnimator = ObjectAnimator.ofObject(this, "textColor", evaluator, getCurrentTextColor(), getResources().getColor(R.color.swm_white));
        } else if (intensity == INTENSITY_E) {
            setText(getResources().getString(R.string.swm_intensity_easy));
            mIntensityColorAnimator = ObjectAnimator.ofObject(this, "textColor", evaluator, getCurrentTextColor(), getResources().getColor(R.color.swm_intensity_easy));
        } else if (intensity == INTENSITY_M) {
            setText(getResources().getString(R.string.swm_intensity_normal));
            mIntensityColorAnimator = ObjectAnimator.ofObject(this, "textColor", evaluator, getCurrentTextColor(), getResources().getColor(R.color.swm_intensity_normal));
        } else if (intensity == INTENSITY_T) {
            setText(getResources().getString(R.string.swm_intensity_strong));
            mIntensityColorAnimator = ObjectAnimator.ofObject(this, "textColor", evaluator, getCurrentTextColor(), getResources().getColor(R.color.swm_intensity_strong));
        } else if (intensity == INTENSITY_I) {
            setText(getResources().getString(R.string.swm_intensity_heavy));
            mIntensityColorAnimator = ObjectAnimator.ofObject(this, "textColor", evaluator, getCurrentTextColor(), getResources().getColor(R.color.swm_intensity_heavy));
        } else if (intensity == INTENSITY_10K) {
            setText(getResources().getString(R.string.swm_intensity_heavy));
            mIntensityColorAnimator = ObjectAnimator.ofObject(this, "textColor", evaluator, getCurrentTextColor(), getResources().getColor(R.color.swm_intensity_heavy));
        } else if (intensity == INTENSITY_R) {
            setText(getResources().getString(R.string.swm_intensity_hard));
            mIntensityColorAnimator = ObjectAnimator.ofObject(this, "textColor", evaluator, getCurrentTextColor(), getResources().getColor(R.color.swm_intensity_hard));
        } else if (intensity == DANGER) {
            setText(getResources().getString(R.string.swm_danger));
            mIntensityColorAnimator = ObjectAnimator.ofObject(this, "textColor", evaluator, getCurrentTextColor(), getResources().getColor(R.color.swm_intensity_danger));
        }

        if (mIntensityColorAnimator != null) {
            mIntensityColorAnimator.setDuration(500);
            mIntensityColorAnimator.start();
        }
    }
}
