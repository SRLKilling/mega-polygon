package com.srl.test;

/* compiled from: Effect */
class InvertedColorEffect extends ModeEffect {
    public InvertedColorEffect(float minTime, float proba) {
        super(0, minTime, proba);
    }

    public boolean startEffect() {
        this.mBoard.mGame.polygon().setInvertedColorMode(true);
        return true;
    }

    public boolean endEffect() {
        this.mBoard.mGame.polygon().setInvertedColorMode(false);
        return true;
    }
}
