package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/3/10.
 */

public class SwmServiceProvider extends ServiceProvider{
    private static SwmServiceProvider provider;
    private SwmEngine bleService;

    private SwmServiceProvider() {

    }

    public synchronized static SwmServiceProvider getIns() {
        if(provider == null)
            provider = new SwmServiceProvider();

        return provider;
    }

    @Override
    public synchronized SwmEngine getService(SwmClient client)  {
        if(bleService == null)
            bleService = new BleEngine(client);

        return bleService;
    }

    public BleEngine internal() {
        return (BleEngine) bleService;
    }
}
