package com.srl.test;

/* compiled from: Effect */
class RotationEffect extends Effect {
    float mSpeed;

    public RotationEffect(float speed, float minTime, float proba) {
        super(-1, minTime, proba);
        this.mSpeed = speed;
    }

    public void set(float speed) {
        this.mSpeed = speed;
    }

    public boolean startEffect() {
        this.mSpeed = -this.mSpeed;
        this.mBoard.mRotationSpeed = this.mSpeed;
        return false;
    }

    public boolean endEffect() {
        this.mBoard.mRotationSpeed = 0.0f;
        disable();
        return true;
    }
}
