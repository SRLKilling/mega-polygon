package com.srl.test;

import android.os.SystemClock;

/* compiled from: Effect */
abstract class ModeEffect extends Effect {
    public ModeEffect(int family, float minTime, float proba) {
        super(family, minTime, proba);
    }

    public void doEffect(long t) {
        if (this.mBoard != null) {
            start(t);
        }
    }

    public void start(long t) {
        t = SystemClock.elapsedRealtime();
        if (((float) t) < this.mNextTime) {
            return;
        }
        if (!this.mEnabled || Math.random() > ((double) this.mProba)) {
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
        } else if (endEffect()) {
            disable();
        }
    }
}
