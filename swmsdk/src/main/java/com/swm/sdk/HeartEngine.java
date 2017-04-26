package com.swm.sdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yangzhenyu on 2017/3/29.
 */

public abstract class HeartEngine implements SwmEngine{

    List<HeartEngineOutput> outputs;

    public synchronized void addOutput(HeartEngineOutput output) {
        if(outputs == null)
            outputs = Collections.synchronizedList(new ArrayList<HeartEngineOutput>());

        outputs.add(output);
    }

    public synchronized void removeOutput(HeartEngineOutput output) {
        if(outputs == null)
            return;

        outputs.remove(output);
    }

}
