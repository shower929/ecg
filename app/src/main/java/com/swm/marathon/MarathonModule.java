package com.swm.marathon;

/**
 * Created by yangzhenyu on 2016/11/7.
 */

public class MarathonModule {
    private static MarathonModule mMarathonModule;

    public synchronized static MarathonModule getIns() {
        if (mMarathonModule == null)
            mMarathonModule = new MarathonModule();

        return mMarathonModule;
    }

    public void queryAllMarathon(MarathonCallback marathonCallback) {

    }
}
