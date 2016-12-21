package com.swm.power;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangzhenyu on 2016/11/9.
 */
 class QueryResult {
    Map<String, Object> mResults;
    QueryResult() {
    }

    void addValues(String key, Object value) {
        if (mResults == null) {
            mResults = new HashMap<>();
        }
        mResults.put(key, value);
    }
    public int getInt(String key) {
        return (int) mResults.get(key);
    }
}
