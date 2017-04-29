package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/4/26.
 */

public class HrvPlugin implements HeartEngineOutput{
    public static final String ACTION_STRESS_CHANGED = "action_stress_changed";
    public static final String ACTION_PHYSICAL_AGE_CHANGED = "action_physical_age_changed";
    public static final String EXTRA_STRESS = "extra_stress";
    public static final String EXTRA_PHYSICAL_AGE = "extra_physical_age";

    public enum Stress {NORMAL, BAD, GOOD, HAPPY}
    private final double goodLevel;
    private final double normalLevel;
    private final double badLevel;
    private Stress preStress;
    private int prePhyAge;
    private volatile boolean on;

    private HrvListener hrvListener;

    private static final int	g_fSDNNToAge_Formula[] = {	76,	//SDNN	23
            67,	//SDNN	24
            62,	//SDNN	25
            58,	//SDNN	26
            54,	//SDNN	27
            51,	//SDNN	28
            49,	//SDNN	29
            46,	//SDNN	30
            44,	//SDNN	31
            42,	//SDNN	32
            40,	//SDNN	33
            38,	//SDNN	34
            36,	//SDNN	35
            34,	//SDNN	36
            33,	//SDNN	37
            31,	//SDNN	38
            29,	//SDNN	39
            28,	//SDNN	40
            26,	//SDNN	41
            25,	//SDNN	42
            23,	//SDNN	43
            22,	//SDNN	44
            21,	//SDNN	45
            19,	//SDNN	46
            18,	//SDNN	47
            17,	//SDNN	48
            16,	//SDNN	49
            14,	//SDNN	50
            13,	//SDNN	51
            12,	//SDNN	52
            11,	//SDNN	53
            10,	//SDNN	54
    };
    /**
     *
     * @param age user's real age
     */
    public HrvPlugin(int age) {
        goodLevel = age * -1.3549 + 108.74;
        normalLevel = age * -1.0582 + 79.791;
        badLevel = age * -0.756 + 52.78;
    }


    @Override
    public void onHeartDataAvailable(HeartData heartData) {
        if (hrvListener == null)
            return;

        if(!on)
            return;

        Stress stress = APPS_HRV_GetStressStatus(heartData.sdnn);

        if (stress != preStress) {
            hrvListener.onStressChanged(stress);
            preStress = stress;
        }

        int phyAge = APPS_HRV_GetAge((int) heartData.rmssd);

        if (phyAge != prePhyAge) {
            hrvListener.onPhyAgeChanged(phyAge);
            prePhyAge = phyAge;
        }
    }

    private Stress  APPS_HRV_GetStressStatus(float i16SDNN) {

        if (i16SDNN == 0)
            return Stress.NORMAL;

        //protection SDNN range
        if(i16SDNN < 2)
        {
            i16SDNN = 2;
        } else if(i16SDNN > 95)
        {
            i16SDNN = 95;
        }


        if (i16SDNN <= badLevel)
            return Stress.BAD;
        else if (i16SDNN <= normalLevel && i16SDNN > badLevel)
            return Stress.NORMAL;
        else if (i16SDNN <= goodLevel && i16SDNN > normalLevel)
            return Stress.GOOD;
        else
            return Stress.HAPPY;

    }

    private int APPS_HRV_GetAge(int i16RMSSD)
    {
        //int i16SDNN = 0;

        //Round
        //i16SDNN = (int)(fSDNN * 10 + 5) / 10;

        //protection SDNN range
        if (i16RMSSD == 0)
            return 0;
        else if (i16RMSSD < 23)
            return 0;
        else if (i16RMSSD > 54)
            return 999;
        else
            return (int)g_fSDNNToAge_Formula[(i16RMSSD - 23)];
    }

    public void setHrvListener(HrvListener hrvListener) {
        this.hrvListener = hrvListener;
    }

    public void on() {
        on = true;
    }

    public void off() {
        on = false;
    }
}
