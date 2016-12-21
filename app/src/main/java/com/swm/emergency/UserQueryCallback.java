package com.swm.emergency;

import com.swm.core.SwmUser;

/**
 * Created by yangzhenyu on 2016/11/1.
 */

public interface UserQueryCallback {
    void onQueryDone(SwmUser swmUser);
}
