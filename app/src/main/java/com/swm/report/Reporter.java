package com.swm.report;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by yangzhenyu on 2016/11/4.
 */

public class Reporter {
    private static final String SCENARIO = "scenario";
    private static final String SCENARIO_TRAINING = "training";
    public static void reportStartTraining(Context context) {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);
        Bundle data = new Bundle();
        data.putString(SCENARIO, SCENARIO_TRAINING);
        analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, data);
    }
}
