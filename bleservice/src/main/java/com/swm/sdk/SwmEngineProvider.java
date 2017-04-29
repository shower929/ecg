package com.swm.sdk;

/**
 * Created by yangzhenyu on 2017/3/10.
 */

public class SwmEngineProvider extends EngineProvider {
    private static SwmEngineProvider provider;
    private SwmEngine bleService;

    private SwmEngineProvider() {

    }

    public synchronized static SwmEngineProvider getIns() {
        if(provider == null)
            provider = new SwmEngineProvider();

        return provider;
    }

    @Override
    public synchronized SwmEngine getEngine(SwmClient client)  {
        if(bleService == null)
            bleService = new BleEngine(client);

        return bleService;
    }

    public BleEngine internal() {
        return (BleEngine) bleService;
    }
}
