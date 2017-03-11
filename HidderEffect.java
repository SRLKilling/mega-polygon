package com.srl.test;

/* compiled from: Effect */
class HidderEffect extends Effect {
    float mHidderSpeed;
    boolean mIn;
    float mMaxDist;

    public HidderEffect(float maxDist, float minTime, float proba) {
        super(-1, minTime, proba);
        this.mMaxDist = maxDist;
    }

    public boolean startEffect() {
        if (this.mBoard.mHidderDist >= this.mMaxDist) {
            this.mHidderSpeed = -this.mBoard.mSpeed;
            this.mIn = false;
        } else {
            this.mHidderSpeed = this.mBoard.mSpeed;
            this.mIn = true;
        }
        return true;
    }

    public void anim(long t) {
        Board board;
        if (this.mIn && this.mBoard.mHidderDist <= this.mMaxDist) {
            board = this.mBoard;
            board.mHidderDist += ((float) t) * this.mHidderSpeed;
        } else if (this.mIn || this.mBoard.mHidderDist < 0.0f) {
            disable();
        } else {
            board = this.mBoard;
            board.mHidderDist += ((float) t) * this.mHidderSpeed;
        }
    }

    public boolean endEffect() {
        this.mBoard.mHidderDist = 0.0f;
        disable();
        return true;
    }
}
