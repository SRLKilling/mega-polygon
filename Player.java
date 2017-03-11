package com.srl.test;

public class Player {
    private float mAngle;
    private Game mGame;
    private int mMove;
    private float mSection;
    private float mSensibility;

    public Player(Game g) {
        this.mGame = g;
        this.mAngle = 0.0f;
        this.mSensibility = 0.0f;
        this.mMove = 0;
    }

    public float angle() {
        return this.mAngle;
    }

    public float section() {
        return this.mSection;
    }

    public void move(int m) {
        this.mMove = m;
    }

    public void draw(long t) {
        float d = (((float) this.mMove) * (this.mGame.board().sensibility() + this.mSensibility)) * ((float) t);
        float s = ((this.mAngle + d) * ((float) this.mGame.board().polysize())) / 6.2831855f;
        if (s < 0.0f) {
            s = ((float) this.mGame.board().polysize()) - s;
        } else if (s > ((float) this.mGame.board().polysize())) {
            s -= (float) this.mGame.board().polysize();
        }
        if (this.mGame.board().playerCan(this.mSection, s)) {
            this.mAngle += d;
            this.mSection = s;
        }
        if (this.mGame.board().isColliding(this)) {
            this.mMove = 0;
            this.mGame.stop();
        }
        if (this.mAngle <= 0.0f) {
            this.mAngle += 6.2831855f;
        } else if (((double) this.mAngle) >= 6.283185307179586d) {
            this.mAngle = (float) (((double) this.mAngle) - 6.283185307179586d);
        }
        this.mGame.polygon().draw(this);
    }
}
