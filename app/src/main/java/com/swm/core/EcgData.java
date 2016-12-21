package com.swm.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzhenyu on 2016/9/27.
 */

public class EcgData {
    public List<Integer> values = new ArrayList<>();

    EcgData() {

    }

    EcgData(List<Integer> values){
        this.values = values;
    }

}
