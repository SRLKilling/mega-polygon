package com.srl.test;

import android.view.MotionEvent;

abstract class Element {
    ColorWrapper mBackColor;
    boolean mDebugOutput;
    protected float mHeight;
    protected float[] mMargin;
    protected float[] mPadding;
    protected Element mParent;
    boolean mToCompute;
    protected UI mUI;
    protected float mWidth;
    protected float mX;
    protected float mY;

    abstract boolean compute();

    public abstract void draw();

    Element(UI ui) {
        this.mUI = ui;
        this.mParent = null;
        this.mMargin = new float[4];
        this.mPadding = new float[4];
        for (int i = 0; i < 4; i++) {
            this.mMargin[i] = 0.0f;
            this.mPadding[i] = 0.0f;
        }
        this.mWidth = 1.0f;
        this.mHeight = 1.0f;
        this.mDebugOutput = false;
        this.mBackColor = new ColorWrapper();
        this.mToCompute = true;
    }

    public void setParent(Element e) {
        this.mParent = e;
    }

    public void drawElement() {
        computeElement();
        if (this.mBackColor.a() != 0.0f) {
            this.mUI.push();
            this.mUI.translate(this.mMargin[3], this.mMargin[0]);
            this.mUI.scale(this.mWidth, this.mHeight);
            this.mUI.setColor(this.mBackColor);
            UI ui = this.mUI;
            UnitSquare.draw(UI.mPositionLoc);
            this.mUI.pop();
        }
        this.mUI.push();
        this.mUI.translate(this.mMargin[3] + this.mPadding[3], this.mMargin[0] + this.mPadding[0]);
        this.mUI.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        draw();
        this.mUI.pop();
        if (this.mDebugOutput) {
            this.mUI.push();
            this.mUI.setColor(1.0f, 0.0f, 0.0f, 1.0f);
            this.mUI.scale(getMWidth(), getMHeight());
            ui = this.mUI;
            UnitSquare.drawLines(UI.mPositionLoc);
            this.mUI.pop();
            this.mUI.push();
            this.mUI.setColor(0.0f, 0.0f, 1.0f, 1.0f);
            this.mUI.translate(this.mMargin[3], this.mMargin[0]);
            this.mUI.scale(this.mWidth, this.mHeight);
            ui = this.mUI;
            UnitSquare.drawLines(UI.mPositionLoc);
            this.mUI.pop();
            this.mUI.push();
            this.mUI.setColor(0.0f, 1.0f, 0.0f, 1.0f);
            this.mUI.translate(this.mMargin[3] + this.mPadding[3], this.mMargin[0] + this.mPadding[0]);
            this.mUI.scale(getPWidth(), getPHeight());
            ui = this.mUI;
            UnitSquare.drawLines(UI.mPositionLoc);
            this.mUI.pop();
        }
    }

    public boolean computeElement() {
        if ((!this.mUI.hasChanged() && !this.mToCompute) || !compute()) {
            return false;
        }
        this.mToCompute = false;
        if (this.mParent != null) {
            this.mParent.childChanged(this);
        }
        return true;
    }

    public void setMWidth(float w) {
        this.mWidth = (w - this.mMargin[1]) - this.mMargin[3];
        this.mToCompute = true;
    }

    public void setMHeight(float h) {
        this.mHeight = (h - this.mMargin[0]) - this.mMargin[2];
        this.mToCompute = true;
    }

    public void setWidth(float w) {
        this.mWidth = w;
        this.mToCompute = true;
    }

    public void setHeight(float h) {
        this.mHeight = h;
        this.mToCompute = true;
    }

    public void setPWidth(float w) {
        this.mWidth = (this.mPadding[1] + w) + this.mPadding[3];
        this.mToCompute = true;
    }

    public void setPHeight(float h) {
        this.mHeight = (this.mPadding[0] + h) + this.mPadding[2];
        this.mToCompute = true;
    }

    public float getPWidth() {
        return (this.mWidth - this.mPadding[1]) - this.mPadding[3];
    }

    public float getPHeight() {
        return (this.mHeight - this.mPadding[0]) - this.mPadding[2];
    }

    public float getWidth() {
        return this.mWidth;
    }

    public float getHeight() {
        return this.mHeight;
    }

    public float getMWidth() {
        return (this.mWidth + this.mMargin[1]) + this.mMargin[3];
    }

    public float getMHeight() {
        return (this.mHeight + this.mMargin[0]) + this.mMargin[2];
    }

    public void setX(float x) {
        this.mX = x;
        this.mToCompute = true;
    }

    public void setY(float y) {
        this.mY = y;
        this.mToCompute = true;
    }

    public float getMX() {
        return this.mX;
    }

    public float getMY() {
        return this.mY;
    }

    public float getX() {
        return this.mX + this.mMargin[3];
    }

    public float getY() {
        return this.mY + this.mMargin[0];
    }

    public float getPX() {
        return (this.mX + this.mMargin[3]) + this.mPadding[3];
    }

    public float getPY() {
        return (this.mY + this.mMargin[0]) + this.mPadding[0];
    }

    public void setPadding(float top, float left, float bottom, float right) {
        this.mPadding[0] = top;
        this.mPadding[1] = left;
        this.mPadding[2] = bottom;
        this.mPadding[3] = right;
        this.mToCompute = true;
    }

    public void setMargin(float top, float left, float bottom, float right) {
        this.mMargin[0] = top;
        this.mMargin[1] = left;
        this.mMargin[2] = bottom;
        this.mMargin[3] = right;
        this.mToCompute = true;
    }

    public void setBackColor(ColorWrapper color) {
        this.mBackColor = color;
    }

    public boolean inside(float x, float y) {
        return x >= getX() && x <= getX() + this.mWidth && y >= getY() && y <= getY() + this.mHeight;
    }

    public boolean inside(MotionEvent e) {
        return inside(this.mUI.normaliseX(e.getX()), this.mUI.normaliseY(e.getY()));
    }

    public boolean eventHandler(MotionEvent e) {
        if (inside(e)) {
            return event(e);
        }
        return false;
    }

    public boolean event(MotionEvent e) {
        return false;
    }

    public void childChanged(Element e) {
    }
}
