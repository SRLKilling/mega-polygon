package com.srl.test;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;

public class MainActivity extends Activity {
    private GameGLView mGLView;
    Game mGame;
    private IntentFilter mIntentFilter;
    private GameGLRenderer mRenderer;
    WifiHandler mWifiHandler;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mIntentFilter = new IntentFilter();
        this.mIntentFilter.addAction("android.net.wifi.p2p.STATE_CHANGED");
        this.mIntentFilter.addAction("android.net.wifi.p2p.PEERS_CHANGED");
        this.mIntentFilter.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
        this.mIntentFilter.addAction("android.net.wifi.p2p.THIS_DEVICE_CHANGED");
        this.mWifiHandler = new WifiHandler(this);
        this.mWifiHandler.create();
        this.mGame = new Game(this);
        this.mGLView = new GameGLView(this.mGame, this);
        setContentView(this.mGLView);
    }

    protected void onResume() {
        super.onResume();
        registerReceiver(this.mWifiHandler.mReceiver, this.mIntentFilter);
    }

    protected void onPause() {
        super.onPause();
        unregisterReceiver(this.mWifiHandler.mReceiver);
    }
}
