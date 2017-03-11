package com.srl.test;

import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

public class WifiHandler {
    MainActivity mActivity;
    Channel mChannel;
    WifiP2pManager mManager;
    WifiBroadcaster mReceiver;

    class 1 implements ActionListener {
        1() {
        }

        public void onSuccess() {
            Log.v("WifiDirectTest", "Discovery Initiated");
        }

        public void onFailure(int reasonCode) {
            Log.v("WifiDirectTest", "Discovery Failed : " + reasonCode);
        }
    }

    public WifiHandler(MainActivity activity) {
        this.mActivity = activity;
    }

    public void create() {
        this.mManager = (WifiP2pManager) this.mActivity.getSystemService("wifip2p");
        this.mChannel = this.mManager.initialize(this.mActivity, this.mActivity.getMainLooper(), null);
        this.mReceiver = new WifiBroadcaster(this.mManager, this.mChannel, this.mActivity);
    }

    public void delete() {
    }

    public void searchPeers() {
        this.mManager.discoverPeers(this.mChannel, new 1());
    }
}
