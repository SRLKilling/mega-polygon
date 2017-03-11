package com.srl.test;

import android.util.Log;

class Label extends Element {
    Font mFont;
    float mFontSize;
    Listener mListener;
    String mText;
    ColorWrapper mTextColor;

    interface Listener {
        void onClick();
    }

    public Label(UI ui) {
        super(ui);
        this.mText = "Test";
        this.mTextColor = new ColorWrapper(1.0f, 1.0f, 1.0f, 1.0f);
        this.mFont = Font.getDefault();
        this.mFontSize = 0.04f;
        float[] fArr = this.mPadding;
        float f = 0.1f * this.mFontSize;
        this.mPadding[2] = f;
        fArr[0] = f;
        fArr = this.mPadding;
        f = 0.4f * this.mFontSize;
        this.mPadding[3] = f;
        fArr[1] = f;
    }

    public void setTextSize(float size) {
        this.mFontSize = size;
    }

    public void setTextColor(ColorWrapper color) {
        this.mTextColor = color;
    }

    public void setText(String str) {
        this.mText = str;
    }

    public void draw() {
        this.mUI.setColor(this.mTextColor);
        this.mFont.draw(this.mUI, this.mText, (int) this.mUI.toGlobalY(this.mFontSize), false, false);
    }

    boolean compute() {
        if (!this.mUI.hasChanged() || this.mHeight == (this.mPadding[0] + this.mFont.getTextHeight((int) this.mUI.toGlobalY(this.mFontSize))) + this.mPadding[2] || this.mWidth == (this.mPadding[1] + this.mFont.getTextWidth(this.mText, (int) this.mUI.toGlobalY(this.mFontSize))) + this.mPadding[3]) {
            return false;
        }
        Log.v("Test", "           Changing Label h/w to : " + this.mWidth + " / " + this.mHeight);
        this.mHeight = (this.mPadding[0] + this.mFont.getTextHeight((int) this.mUI.toGlobalY(this.mFontSize))) + this.mPadding[2];
        this.mWidth = (this.mPadding[1] + this.mFont.getTextWidth(this.mText, (int) this.mUI.toGlobalY(this.mFontSize))) + this.mPadding[3];
        return true;
    }
}
