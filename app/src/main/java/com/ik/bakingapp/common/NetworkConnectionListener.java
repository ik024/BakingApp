package com.ik.bakingapp.common;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import timber.log.Timber;

public class NetworkConnectionListener extends LiveData<Boolean> {
    private Context context;
    private BroadcastReceiver receiver;

    private static NetworkConnectionListener connectionListener;

    public static NetworkConnectionListener getInstance(Context context){
        if (connectionListener == null){
            connectionListener = new NetworkConnectionListener(context);
        }

        return connectionListener;
    }

    private NetworkConnectionListener(Context context){
        this.context = context;
    }

    @Override
    protected void onActive() {
        super.onActive();
        Timber.d("onActive");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Timber.i("onReceive");
                    setValue(Util.isOnline(context));
                }
            };
        }
        context.registerReceiver(receiver, filter);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        Timber.d("onInactive");
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
    }


}
