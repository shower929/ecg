package com.swm.sdk;

import android.content.Context;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yangzhenyu on 2017/3/29.
 */

abstract class SwmModule {
    static void checkPermission(Context context) {

        String pkgName = context.getPackageName();
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {

        }

        if (digest == null)
            try {
                digest = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

        digest.update(pkgName.getBytes());
        byte[] client = digest.digest();
        //@Fixme use engine id to generate digest key
        //digest.update(SwmConfig.ENGINE_ID);
        byte[] apiKey = digest.digest();

        //@Fixme check api key
        //if(!MessageDigest.isEqual(SwmConfig.API_KEY, apiKey))
            //throw new RuntimeException("Key is not the same");
    }
}
