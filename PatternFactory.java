package com.srl.test;

import java.util.Vector;

class PatternFactory {
    private Game mGame;
    private Vector<Vector<Pattern>> mPatterns;
    private int mPolysize;

    public PatternFactory(Game g) {
        this.mGame = g;
        this.mPolysize = 6;
        this.mPatterns = new Vector();
        for (int i = 0; i <= 8; i++) {
            this.mPatterns.add(new Vector());
        }
    }

    public void addPattern(Pattern p) {
        ((Vector) this.mPatterns.get(p.polysize())).add(p);
    }

    Pattern getNewPattern() {
        return (Pattern) ((Vector) this.mPatterns.get(this.mPolysize)).get((int) (Math.random() * ((double) ((Vector) this.mPatterns.get(this.mPolysize)).size())));
    }

    public void clear() {
        for (int i = 0; i <= 8; i++) {
            ((Vector) this.mPatterns.get(i)).clear();
        }
    }
}
