package com.srl.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;

/* compiled from: WifiHandler */
class WifiBroadcaster extends BroadcastReceiver {
    private MainActivity mActivity;
    private Channel mChannel;
    private WifiP2pManager mManager;

    class 1 implements PeerListListener {
        1() {
        }

        public void onPeersAvailable(WifiP2pDeviceList peers) {
            WifiBroadcaster.this.mActivity.mGame.mUIMaker.updatePeerList(peers);
        }
    }

    public WifiBroadcaster(WifiP2pManager manager, Channel channel, MainActivity activity) {
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("android.net.wifi.p2p.STATE_CHANGED".equals(action)) {
            if (intent.getIntExtra("wifi_p2p_state", -1) != 2) {
            }
        } else if ("android.net.wifi.p2p.PEERS_CHANGED".equals(action)) {
            if (this.mManager != null) {
                this.mManager.requestPeers(this.mChannel, new 1());
            }
        } else if (!"android.net.wifi.p2p.CONNECTION_STATE_CHANGE".equals(action) && "android.net.wifi.p2p.THIS_DEVICE_CHANGED".equals(action)) {
        }
    }
}
