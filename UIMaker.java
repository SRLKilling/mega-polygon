package com.srl.test;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.os.Environment;
import com.srl.test.Button.Listener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Vector;

class UIMaker implements Listener {
    private Vector<Button> mButtonList;
    private Vector<String> mFileList;
    private Game mGame;
    private String mHomeFilepath;
    private VerticalScrollList mLevelList;
    private String mLoadedLevel;
    private Button mLoadedLevelButton;
    private VerticalLayout mMainMenu;
    private Label mMainTitle;
    private Button mMultiButton;
    private VerticalLayout mMultiMenu;
    private Button mSoloButton;
    State mState;
    private Button mWifiButton;
    private VerticalScrollList mWifiMenu;

    public enum State {
        START_MENU,
        LEVEL_SELECTION,
        MULTI_MENU,
        WIFI_MENU,
        IN_GAME
    }

    public UIMaker(Game g) {
        this.mGame = g;
        this.mButtonList = new Vector();
        this.mFileList = new Vector();
        this.mHomeFilepath = Environment.getExternalStorageDirectory().toString() + "/MegaPolygon";
        this.mMainTitle = new Label(this.mGame.mUI);
        this.mMainTitle.setText("MEGAPolygon");
        this.mMainTitle.setTextSize(0.2f);
        this.mMainTitle.setMargin(0.1f, 0.0f, 0.1f, 0.0f);
        this.mSoloButton = new Button(this.mGame.mUI);
        this.mSoloButton.setText("Solo");
        this.mSoloButton.setTextSize(0.05f);
        this.mSoloButton.setListener(this);
        this.mMultiButton = new Button(this.mGame.mUI);
        this.mMultiButton.setText("Multiplayer");
        this.mMultiButton.setTextSize(0.05f);
        this.mMultiButton.setListener(this);
        this.mMainMenu = new VerticalLayout(this.mGame.mUI);
        this.mMainMenu.addElement(this.mMainTitle);
        this.mMainMenu.addElement(this.mSoloButton);
        this.mMainMenu.addElement(this.mMultiButton);
        this.mWifiButton = new Button(this.mGame.mUI);
        this.mWifiButton.setText("Wi-Fi Direct");
        this.mWifiButton.setTextSize(0.05f);
        this.mWifiButton.setListener(this);
        this.mMultiMenu = new VerticalLayout(this.mGame.mUI);
        this.mMultiMenu.addElement(this.mMainTitle);
        this.mMultiMenu.addElement(this.mWifiButton);
        this.mWifiMenu = new VerticalScrollList(this.mGame.mUI);
        this.mLoadedLevelButton = null;
        this.mLoadedLevel = "";
    }

    public void startMenu() {
        this.mGame.mUI.setMainElement(this.mMainMenu);
        this.mState = State.START_MENU;
    }

    public void multiMenu() {
        this.mGame.mUI.setMainElement(this.mMultiMenu);
        this.mState = State.MULTI_MENU;
    }

    public void wifiMenu() {
        this.mGame.mActivity.mWifiHandler.searchPeers();
        this.mGame.mUI.setMainElement(this.mWifiMenu);
        this.mState = State.WIFI_MENU;
    }

    public void soloLevelSelector() {
        updateLevelSelector();
        this.mGame.mUI.setMainElement(this.mLevelList);
        this.mState = State.LEVEL_SELECTION;
    }

    public void updateLevelSelector() {
        String[] files = new File(this.mHomeFilepath + "/levels").list();
        this.mButtonList.clear();
        this.mFileList.clear();
        this.mLevelList = new VerticalScrollList(this.mGame.mUI);
        this.mLevelList.setPadding(0.0f, 0.1f, 0.0f, 0.1f);
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                Button button;
                if (this.mLoadedLevel.equals(files[i])) {
                    button = this.mLoadedLevelButton;
                } else {
                    button = new Button(this.mGame.mUI);
                    button.setText("Load");
                    button.setTextSize(0.05f);
                    button.setListener(this);
                }
                Label label = new Label(this.mGame.mUI);
                label.setText(files[i]);
                label.setTextSize(0.05f);
                label.setTextColor(new ColorWrapper(0.0f, 0.0f, 0.0f, 1.0f));
                HorizontalLayout layout = new HorizontalLayout(this.mGame.mUI);
                layout.setMargin(0.05f, 0.0f, 0.0f, 0.0f);
                layout.setPadding(0.01f, 0.01f, 0.01f, 0.01f);
                layout.setBackColor(new ColorWrapper(1.0f, 1.0f, 1.0f, 1.0f));
                layout.addElement(button);
                layout.addElement(label);
                this.mLevelList.addElement(layout);
                this.mButtonList.add(button);
                this.mFileList.add(files[i]);
            }
        }
    }

    public void updatePeerList(WifiP2pDeviceList deviceList) {
        if (this.mState == State.WIFI_MENU) {
            for (WifiP2pDevice device : deviceList.getDeviceList()) {
                Label label = new Label(this.mGame.mUI);
                label.setText(device.deviceName);
                label.setTextSize(0.05f);
                label.setTextColor(new ColorWrapper(0.0f, 0.0f, 0.0f, 1.0f));
                HorizontalLayout layout = new HorizontalLayout(this.mGame.mUI);
                layout.setMargin(0.05f, 0.0f, 0.0f, 0.0f);
                layout.setPadding(0.01f, 0.01f, 0.01f, 0.01f);
                layout.setBackColor(new ColorWrapper(1.0f, 1.0f, 1.0f, 1.0f));
                layout.addElement(label);
                this.mWifiMenu.addElement(layout);
            }
        }
    }

    public void inGame() {
        this.mGame.mUI.setMainElement(null);
        this.mState = State.IN_GAME;
    }

    public void ButtonClicked(Button b) {
        switch (this.mState) {
            case START_MENU:
                if (b == this.mSoloButton) {
                    soloLevelSelector();
                } else if (b == this.mMultiButton) {
                    multiMenu();
                }
            case LEVEL_SELECTION:
                int i = 0;
                while (i < this.mButtonList.size()) {
                    try {
                        if (this.mButtonList.get(i) == b) {
                            if (b == this.mLoadedLevelButton) {
                                this.mGame.start();
                            } else {
                                this.mGame.mLevelLoader.loadLevelXML(new FileReader(this.mHomeFilepath + "/levels/" + ((String) this.mFileList.get(i))));
                                b.setText("Play !");
                                if (this.mLoadedLevelButton != null) {
                                    this.mLoadedLevelButton.setText("Load");
                                }
                                this.mLoadedLevelButton = b;
                                this.mLoadedLevel = (String) this.mFileList.get(i);
                            }
                        }
                        i++;
                    } catch (FileNotFoundException e) {
                        return;
                    }
                }
            case MULTI_MENU:
                if (b == this.mWifiButton) {
                    wifiMenu();
                }
            default:
        }
    }
}
