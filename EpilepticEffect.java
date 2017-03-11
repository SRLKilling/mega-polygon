package com.srl.test;

/* compiled from: Effect */
class EpilepticEffect extends ModeEffect {
    public EpilepticEffect(float minTime, float proba) {
        super(0, minTime, proba);
    }

    public boolean startEffect() {
        this.mBoard.mGame.polygon().setEpilepticMode(true);
        return true;
    }

    public boolean endEffect() {
        this.mBoard.mGame.polygon().setEpilepticMode(false);
        return true;
    }
}
