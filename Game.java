package com.srl.test;

import android.media.MediaPlayer;
import android.os.SystemClock;
import android.view.MotionEvent;

public class Game {
    long lastDrawTime;
    MainActivity mActivity;
    Board mBoard;
    ColorFactory mColorFactory;
    Font mFont;
    LevelLoader mLevelLoader;
    MediaPlayer mMediaPlayer;
    Player mPlayer;
    boolean mPlaying;
    PolygonShape mPolygon;
    GameGLRenderer mRenderer;
    long mTimer;
    UI mUI;
    UIMaker mUIMaker;

    public Game(MainActivity activity) {
        this.mActivity = activity;
        this.lastDrawTime = SystemClock.elapsedRealtime();
        this.mBoard = new Board(this);
        this.mPlayer = new Player(this);
        this.mBoard.addEffect(new HidderEffect(4.0f, 10.0f, 10.0f));
        this.mBoard.addEffect(new EpilepticEffect(7.0f, 10.0f));
        this.mBoard.addEffect(new InvertedColorEffect(7.0f, 10.0f));
        this.mColorFactory = new ColorFactory();
        this.mMediaPlayer = MediaPlayer.create(this.mActivity, R.raw.music);
        this.mMediaPlayer.setLooping(true);
        this.mLevelLoader = new LevelLoader(this);
    }

    public void initGL(GameGLRenderer r) {
        this.mRenderer = r;
        this.mPolygon = new PolygonShape(this, r, 1.0f);
        this.mFont = Font.getDefault();
        this.mFont.renderFacet(300);
        this.mUI = new UI(this, r);
        this.mUIMaker = new UIMaker(this);
        this.mUIMaker.startMenu();
    }

    public void resized() {
        this.mUI.resized();
    }

    public PolygonShape polygon() {
        return this.mPolygon;
    }

    public Board board() {
        return this.mBoard;
    }

    public Player player(int i) {
        return this.mPlayer;
    }

    public void draw() {
        long time = SystemClock.elapsedRealtime() - this.lastDrawTime;
        this.lastDrawTime = SystemClock.elapsedRealtime();
        this.mBoard.draw(time);
        this.mPlayer.draw(time);
        if (this.mPlaying) {
            this.mTimer += time;
        }
        float chrono = ((float) this.mTimer) / 1000.0f;
        this.mFont.draw("Time : " + String.format("%.2f", new Object[]{Float.valueOf(chrono)}), 1.0f, 0.0f, 75, true, true);
        this.mUI.draw(time);
    }

    public void event(MotionEvent e) {
        if (!this.mUI.event(e) && this.mPlaying) {
            switch (e.getAction()) {
                case 0:
                case 2:
                    if (e.getX() <= ((float) (this.mRenderer.width() / 2))) {
                        player(0).move(1);
                    } else {
                        player(0).move(-1);
                    }
                case 1:
                    player(0).move(0);
                default:
            }
        }
    }

    public boolean playing() {
        return this.mPlaying;
    }

    public void startstop() {
        if (this.mPlaying) {
            stop();
        } else {
            start();
        }
    }

    public void start() {
        this.mPlaying = true;
        this.mTimer = 0;
        this.mBoard.start();
        this.mMediaPlayer.seekTo((((int) (Math.random() * 4.0d)) * this.mMediaPlayer.getDuration()) / 4);
        this.mUIMaker.inGame();
    }

    public void stop() {
        this.mPlaying = false;
        this.mBoard.stop();
        this.mUIMaker.soloLevelSelector();
    }
}
