package com.srl.test;

/* compiled from: Effect */
class SwitchColorEffect extends Effect {
    int mCurrentNum;
    int mMaxNum;
    boolean mType;

    public SwitchColorEffect(boolean type, int number, float minTime, float proba) {
        super(-1, minTime, proba);
        this.mType = type;
        this.mMaxNum = number;
        this.mCurrentNum = 0;
    }

    public boolean startEffect() {
        if (this.mCurrentNum + 1 >= this.mMaxNum) {
            this.mCurrentNum = 0;
        } else {
            this.mCurrentNum++;
        }
        if (this.mType) {
            this.mBoard.mGame.mPolygon.setRowColorOffset(this.mCurrentNum);
        } else {
            this.mBoard.mGame.mPolygon.setBackColorOffset(this.mCurrentNum);
        }
        return false;
    }

    public boolean endEffect() {
        this.mCurrentNum = 0;
        if (this.mType) {
            this.mBoard.mGame.mPolygon.setRowColorOffset(0);
        } else {
            this.mBoard.mGame.mPolygon.setBackColorOffset(0);
        }
        return true;
    }
}
