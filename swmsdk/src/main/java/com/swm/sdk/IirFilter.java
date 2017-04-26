package com.swm.sdk;

import com.swm.sdk.Filter;

/**
 * Created by yangzhenyu on 2017/1/19.
 */

public class IirFilter extends Filter {
    private int i32PrevSample = 0;
    private int i32PrevDCSample = 0;
    private int gi16_FirstECGIIRFlag = 1;
    private final double fIIRFilterCoeff;

    public IirFilter(double fIIRFilterCoeff) {
        this.fIIRFilterCoeff = fIIRFilterCoeff;
    }

    @Override
    public Integer filter(Integer processData) {
        Integer pFilteredOut;

        if (gi16_FirstECGIIRFlag == 1) {
            i32PrevDCSample = 0;
            gi16_FirstECGIIRFlag = 0;
            i32PrevSample = processData;
            pFilteredOut = i32PrevDCSample;
        }

        i32PrevDCSample = (int) (processData - i32PrevSample + fIIRFilterCoeff * i32PrevDCSample);
        i32PrevSample = processData;
        pFilteredOut = i32PrevDCSample;
        return pFilteredOut;

    }
}
