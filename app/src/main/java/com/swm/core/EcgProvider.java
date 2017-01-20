package com.swm.core;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by yangzhenyu on 2017/1/19.
 */

class EcgProvider implements EcgRawDataListener{
    private static final String LOG_TAG = "EcgProvider";
    private List<Filter> mFilters;
    private Thread mCallbackWorker;
    private BlockingQueue<EcgData> mClientDataQueue;
    private Vector<EcgProviderClient> mClients;

    static class Builder {
        private List<Filter> mFilters;

        public Builder addFilter(Filter filter) {
            if (mFilters == null)
                mFilters = new ArrayList<>();

            mFilters.add(filter);
            return this;
        }

        public EcgProvider build() {
            EcgProvider ecgProvider = new EcgProvider();
            ecgProvider.setFilters(mFilters);
            return ecgProvider;
        }
    }

    private EcgProvider() {
        mClientDataQueue = new LinkedBlockingQueue<>();
        mCallbackWorker = new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    if (!SwmCore.sRunning)
                        return;

                    try {
                        EcgData ecgData = mClientDataQueue.take();
                        if(mClients != null) {
                            for (EcgProviderClient client : mClients) {
                                EcgData copy = new EcgData(ecgData.values);
                                client.onEcgDataAvailable(copy);
                            }
                        }
                    } catch (InterruptedException e) {
                        Log.d(LOG_TAG, e.getMessage(), e);
                    }

                }
            }
        });
        mCallbackWorker.start();
    }

    private void setFilters(List<Filter> filters) {
        mFilters = filters;
    }

    @Override
    public void onEcgRawDataAvailable(EcgRawData ecgRawData) {
        int len = ecgRawData.ecgData.size();
        Integer[] pFilteredOut = new Integer[len];
        for(int i = 0; i < len; i++) {
            Short value = ecgRawData.ecgData.get(i);
            pFilteredOut[i] = Integer.valueOf(value);

            if (mFilters != null) {
                for (Filter filter : mFilters) {
                    pFilteredOut[i] = Integer.valueOf(filter.filter(pFilteredOut[i]));
                }
            }
        }

        EcgData ecgData = new EcgData(pFilteredOut);
        mClientDataQueue.offer(ecgData);
    }

    synchronized void addClient(EcgProviderClient client) {
        if (mClients == null)
            mClients = new Vector<>();
        mClients.add(client);
    }

    synchronized void removeClient(EcgProviderClient client) {
        mClients.remove(client);
    }
}
