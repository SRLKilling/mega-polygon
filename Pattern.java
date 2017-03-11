package com.srl.test;

import java.util.ArrayDeque;

class Pattern {
    float mCurrDist;
    Game mGame;
    int mPolysize;
    ArrayDeque<PolyRow> mRows;
    Polychange mTransition;

    public enum Polychange {
        Decr,
        None,
        Incr
    }

    Pattern(Game g, int polysize) {
        this.mGame = g;
        this.mPolysize = polysize;
        this.mRows = new ArrayDeque();
        this.mCurrDist = 0.0f;
    }

    int polysize() {
        return this.mPolysize;
    }

    Pattern addRow(float dist, int row) {
        this.mRows.addLast(new PolyRow(this.mGame, this.mCurrDist + dist, row));
        this.mCurrDist += ((float) ((int) dist)) + 1.0f;
        return this;
    }

    Pattern addRows(float dist, int[] rows) {
        for (int i = 1; i < rows.length; i++) {
            addRow(dist, rows[i]);
        }
        return this;
    }

    Pattern pushRow(int row) {
        this.mRows.addLast(new PolyRow(this.mGame, this.mCurrDist, row));
        this.mCurrDist += 1.0f;
        return this;
    }

    Pattern pushRows(int[] rows) {
        for (int i = 1; i < rows.length; i++) {
            pushRow(rows[i]);
        }
        return this;
    }

    Pattern pushBlank(int count) {
        this.mCurrDist += ((float) count) * 1.0f;
        return this;
    }

    Pattern p(int row) {
        pushRow(row);
        return this;
    }

    Pattern p(int row, int count) {
        for (int i = 0; i < count; i++) {
            pushRow(row);
        }
        return this;
    }

    Pattern _() {
        pushBlank(1);
        return this;
    }

    Pattern _(int count) {
        pushBlank(count);
        return this;
    }

    ArrayDeque rows() {
        return this.mRows;
    }
}
