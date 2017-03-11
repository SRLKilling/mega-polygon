package com.srl.test;

class PolyRow {
    private float mDist;
    private Game mGame;
    private int mMask;

    public PolyRow(Game g, float dist, int mask) {
        this.mGame = g;
        this.mDist = dist;
        this.mMask = mask;
    }

    public void changeDist(float dist) {
        this.mDist += dist;
    }

    public void draw() {
        this.mGame.polygon().draw(this);
    }

    public int mask() {
        return this.mMask;
    }

    public float dist() {
        return this.mDist;
    }

    public PolyRow instance(boolean dir, int decal, int psize) {
        return new PolyRow(this.mGame, dir ? 30.0f + this.mDist : -5.0f - this.mDist, (this.mMask >>> decal) | (this.mMask << (psize - decal)));
    }
}
