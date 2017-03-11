package com.srl.test;

import android.view.MotionEvent;

class Button extends Label {
    boolean mClicked;
    ColorWrapper mClickedColor;
    ColorWrapper mLastBackColor;
    Listener mListener;

    public interface Listener {
        void ButtonClicked(Button button);
    }

    public Button(UI ui) {
        super(ui);
        this.mListener = null;
        this.mBackColor = new ColorWrapper(0.0f, 0.0f, 0.0f, 1.0f);
        this.mClickedColor = new ColorWrapper(0.4f, 0.4f, 0.4f, 1.0f);
    }

    public void setListener(Listener l) {
        this.mListener = l;
    }

    public boolean event(MotionEvent e) {
        switch (e.getAction()) {
            case 0:
                this.mClicked = true;
                this.mLastBackColor = this.mBackColor;
                this.mBackColor = this.mClickedColor;
                this.mUI.requestFocus(this);
                return true;
            case 1:
                if (this.mClicked) {
                    this.mClicked = false;
                    this.mBackColor = this.mLastBackColor;
                    this.mUI.looseFocus();
                    if (!inside(e) || this.mListener == null) {
                        return true;
                    }
                    this.mListener.ButtonClicked(this);
                    return true;
                }
                break;
        }
        return false;
    }
}
