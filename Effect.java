package com.srl.test;

import android.os.SystemClock;
import java.util.Vector;

abstract class Effect {
    static Vector<Boolean> mFamilyEnabled;
    protected Board mBoard;
    boolean mEnabled;
    int mFamily;
    float mLastTime;
    float mMinTime;
    float mNextTime;
    float mProba;

    abstract boolean endEffect();

    abstract boolean startEffect();

    static {
        mFamilyEnabled = null;
    }

    public Effect(int family, float minTime, float proba) {
        if (mFamilyEnabled == null) {
            mFamilyEnabled = new Vector();
        }
        if (mFamilyEnabled.size() - 1 < family) {
            mFamilyEnabled.setSize(family + 1);
            mFamilyEnabled.set(family, Boolean.valueOf(false));
        }
        this.mFamily = family;
        this.mBoard = null;
        this.mMinTime = 1000.0f * minTime;
        this.mProba = proba / 100.0f;
        this.mEnabled = false;
        this.mNextTime = ((float) SystemClock.elapsedRealtime()) + this.mMinTime;
        start();
    }

    public void doEffect(long t) {
        if (this.mBoard != null) {
            if (!this.mEnabled) {
                start();
            }
            if (this.mEnabled) {
                anim(t);
            }
        }
    }

    public void start() {
        long t = SystemClock.elapsedRealtime();
        if (((float) t) < this.mNextTime) {
            return;
        }
        if ((this.mFamily == -1 || !((Boolean) mFamilyEnabled.get(this.mFamily)).booleanValue()) && Math.random() <= ((double) this.mProba)) {
            this.mNextTime = ((float) t) + this.mMinTime;
            this.mEnabled = startEffect();
            if (this.mEnabled && this.mFamily != -1) {
                mFamilyEnabled.set(this.mFamily, Boolean.valueOf(true));
                return;
            }
            return;
        }
        this.mNextTime = (float) (250 + t);
    }

    public void setBoard(Board b) {
        this.mBoard = b;
    }

    protected void disable() {
        this.mEnabled = false;
        this.mNextTime = ((float) SystemClock.elapsedRealtime()) + this.mMinTime;
        if (this.mFamily != -1) {
            mFamilyEnabled.set(this.mFamily, Boolean.valueOf(false));
        }
    }

    void anim(long t) {
    }
}
