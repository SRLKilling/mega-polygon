package com.srl.test;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

class GameGLView extends GLSurfaceView {
    private Game mGame;
    private GameGLRenderer mRenderer;

    public GameGLView(Game g, Context context) {
        super(context);
        this.mGame = g;
        this.mRenderer = new GameGLRenderer(this.mGame);
        setEGLContextClientVersion(2);
        setRenderer(this.mRenderer);
    }

    public boolean onTouchEvent(MotionEvent e) {
        this.mGame.event(e);
        return true;
    }
}
