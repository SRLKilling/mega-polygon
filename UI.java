package com.srl.test;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.MotionEvent;
import java.util.ArrayDeque;
import java.util.Arrays;

class UI {
    private static final String fragmentShaderCode = "precision mediump float;uniform vec4 Color;void main() {  gl_FragColor = Color;}";
    static int mColorLoc = 0;
    static int mModelviewLoc = 0;
    static int mPositionLoc = 0;
    static int mProgram = 0;
    private static final String vertexShaderCode = "attribute vec4 vPosition;uniform mat4 modelviewMat;void main() {  gl_Position = modelviewMat * vPosition;}";
    private ColorWrapper mCurrentColor;
    private Element mFocusedElement;
    private Game mGame;
    private boolean mHasChanged;
    private Element mMainElement;
    private float[] mModelview;
    private ArrayDeque<float[]> mModelviewStack;
    private GameGLRenderer mRenderer;

    static void initProg() {
        mProgram = GameGLRenderer.getLinkedProgram(vertexShaderCode, fragmentShaderCode);
        mPositionLoc = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mModelviewLoc = GLES20.glGetUniformLocation(mProgram, "modelviewMat");
        mColorLoc = GLES20.glGetUniformLocation(mProgram, "Color");
    }

    public UI(Game g, GameGLRenderer r) {
        initProg();
        this.mGame = g;
        this.mRenderer = r;
        this.mMainElement = null;
        this.mModelviewStack = new ArrayDeque();
        this.mModelview = new float[16];
        this.mCurrentColor = new ColorWrapper();
        this.mFocusedElement = null;
        this.mHasChanged = false;
    }

    public void setMainElement(Element e) {
        this.mMainElement = e;
        if (this.mMainElement != null) {
            this.mMainElement.setX(0.0f);
            this.mMainElement.setY(0.0f);
            this.mMainElement.setMWidth(1.0f);
            this.mMainElement.setMHeight(1.0f);
            this.mHasChanged = true;
            this.mMainElement.computeElement();
            this.mHasChanged = false;
        }
    }

    public boolean event(MotionEvent e) {
        if (this.mFocusedElement != null) {
            return this.mFocusedElement.event(e);
        }
        if (this.mMainElement != null) {
            return this.mMainElement.eventHandler(e);
        }
        return false;
    }

    public void resized() {
        if (this.mMainElement != null) {
            this.mHasChanged = true;
            this.mMainElement.computeElement();
            this.mHasChanged = false;
        }
    }

    public boolean hasChanged() {
        return this.mHasChanged;
    }

    public void requestFocus(Element e) {
        this.mFocusedElement = e;
    }

    public void looseFocus() {
        this.mFocusedElement = null;
    }

    public void draw(long t) {
        GLES20.glUseProgram(mProgram);
        setColor(1.0f, 1.0f, 1.0f, 1.0f);
        setMVIdentity();
        translate(-1.0f, 1.0f);
        scale(2.0f, -2.0f);
        if (this.mMainElement != null) {
            this.mMainElement.drawElement();
        }
        GLES20.glUseProgram(0);
    }

    public void reUseProgram() {
        GLES20.glUseProgram(mProgram);
    }

    public float[] getModelview() {
        return this.mModelview;
    }

    public void push() {
        this.mModelviewStack.push(Arrays.copyOf(this.mModelview, 16));
    }

    public void pop() {
        this.mModelview = (float[]) this.mModelviewStack.pop();
        GLES20.glUniformMatrix4fv(mModelviewLoc, 1, false, this.mModelview, 0);
    }

    public void translate(float x, float y) {
        Matrix.translateM(this.mModelview, 0, x, y, 0.0f);
        GLES20.glUniformMatrix4fv(mModelviewLoc, 1, false, this.mModelview, 0);
    }

    public void rotate(float angle, float x, float y) {
        Matrix.rotateM(this.mModelview, 0, (float) Math.toDegrees((double) angle), x, y, 0.0f);
        GLES20.glUniformMatrix4fv(mModelviewLoc, 1, false, this.mModelview, 0);
    }

    public void scale(float x, float y) {
        Matrix.scaleM(this.mModelview, 0, x, y, 1.0f);
        GLES20.glUniformMatrix4fv(mModelviewLoc, 1, false, this.mModelview, 0);
    }

    public void setMVIdentity() {
        Matrix.setIdentityM(this.mModelview, 0);
        GLES20.glUniformMatrix4fv(mModelviewLoc, 1, false, this.mModelview, 0);
    }

    public void setColor(float r, float g, float b, float a) {
        this.mCurrentColor = new ColorWrapper(r, g, b, a);
        GLES20.glUniform4f(mColorLoc, r, g, b, a);
    }

    public void setColor(float[] c) {
        this.mCurrentColor = new ColorWrapper(c);
        GLES20.glUniform4f(mColorLoc, c[0], c[1], c[2], c[3]);
    }

    public void setColor(ColorWrapper c) {
        this.mCurrentColor = c;
        GLES20.glUniform4f(mColorLoc, c.r(), c.g(), c.b(), c.a());
    }

    public ColorWrapper currentColor() {
        return this.mCurrentColor;
    }

    public float normaliseX(float x) {
        return x / ((float) this.mRenderer.width());
    }

    public float normaliseY(float y) {
        return y / ((float) this.mRenderer.height());
    }

    public float toGlobalX(float x) {
        return ((float) this.mRenderer.width()) * x;
    }

    public float toGlobalY(float y) {
        return ((float) this.mRenderer.height()) * y;
    }

    public Context context() {
        return this.mGame.mContext;
    }
}
