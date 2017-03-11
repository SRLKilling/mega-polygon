package com.srl.test;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Vector;

public class Board {
    float[][] mColors;
    Vector<Effect> mEffects;
    Game mGame;
    float mHidderDist;
    PatternFactory mPatternFactory;
    float mPlayerDist;
    float mPlayerOrientation;
    float mPlayerSize;
    boolean mPlayingZoomEffect;
    int mPolySize;
    ArrayDeque<PolyRow> mPolygons;
    float mRotation;
    float mRotationSpeed;
    float mSensibility;
    float mSpeed;
    float mZoom;

    public Board(Game g) {
        this.mGame = g;
        this.mPolygons = new ArrayDeque();
        this.mPolySize = 6;
        this.mEffects = new Vector();
        this.mPlayingZoomEffect = true;
        this.mSpeed = 0.0125f;
        this.mRotationSpeed = 0.0f;
        this.mRotation = 0.0f;
        this.mSensibility = 0.01f;
        this.mZoom = 1.0f;
        this.mHidderDist = 0.0f;
        this.mColors = (float[][]) Array.newInstance(Float.TYPE, new int[]{3, 4});
        this.mColors[0][0] = 0.0f;
        this.mColors[0][1] = 0.0f;
        this.mColors[0][2] = 0.0f;
        this.mColors[0][3] = 1.0f;
        this.mColors[1][0] = 0.0f;
        this.mColors[1][1] = 0.0f;
        this.mColors[1][2] = 0.0f;
        this.mColors[1][3] = 1.0f;
        this.mColors[2][0] = 1.0f;
        this.mColors[2][1] = 1.0f;
        this.mColors[2][2] = 1.0f;
        this.mColors[2][3] = 1.0f;
        this.mPlayerDist = 3.0f;
        this.mPlayerSize = 0.5f;
        this.mPlayerOrientation = -1.5707964f;
        this.mPatternFactory = new PatternFactory(this.mGame);
        Pattern spiral = new Pattern(this.mGame, 6).p(54, 2).p(36, 2).p(9, 2).p(18, 2).p(36, 2).p(9, 2).p(18, 2).p(54, 2);
        Pattern tripleU = new Pattern(this.mGame, 6).p(62)._(4).p(55)._(3).p(21)._(3).p(62);
        Pattern zigzag = new Pattern(this.mGame, 6).p(21)._(2).p(42)._(2).p(21)._(2).p(42)._(2).p(21)._(2).p(42);
        Pattern loop = new Pattern(this.mGame, 6).p(61).p(1, 5).p(31).p(1, 5).p(61).p(1, 5)._(31);
        Pattern stair1 = new Pattern(this.mGame, 6).p(62)._(2).p(61)._(2).p(59)._(2).p(55)._(2).p(47)._(2).p(31);
        Pattern lol = new Pattern(this.mGame, 6).p(55, 2)._(3).p(28).p(62, 2).p(8, 2).p(9, 2).p(43, 2).p(8, 2).p(28, 2);
        this.mPatternFactory.addPattern(spiral);
        this.mPatternFactory.addPattern(tripleU);
        this.mPatternFactory.addPattern(zigzag);
        this.mPatternFactory.addPattern(loop);
        this.mPatternFactory.addPattern(stair1);
        this.mPatternFactory.addPattern(lol);
    }

    public boolean isColliding(Player player) {
        return isColliding(player.angle(), player.section());
    }

    public boolean isColliding(float playerAngle, float s) {
        double a = 3.141592653589793d / ((double) this.mPolySize);
        double cosa = Math.cos(a);
        float px = this.mPlayerDist * ((float) Math.cos((double) playerAngle));
        float py = this.mPlayerDist * ((float) Math.sin((double) playerAngle));
        Iterator itr = this.mPolygons.iterator();
        while (itr.hasNext()) {
            PolyRow r = (PolyRow) itr.next();
            int section = (int) s;
            float f = (float) cosa;
            if ((r.dist() + 1.0f) / r0 > this.mPlayerDist) {
                if (r.dist() < this.mPlayerDist + this.mPlayerSize && ((r.mask() >> section) & 1) == 1) {
                    double a1 = ((double) (section * 2)) * a;
                    double a2 = ((double) ((section + 1) * 2)) * a;
                    float dist = r.dist() / ((float) cosa);
                    float x1 = dist * ((float) Math.cos(a1));
                    float y1 = dist * ((float) Math.sin(a1));
                    float x2 = dist * ((float) Math.cos(a2));
                    float y2 = dist * ((float) Math.sin(a2));
                    if (x2 - x1 != 0.0f) {
                        float coef = (y2 - y1) / (x2 - x1);
                        float ys1 = (((y2 - y1) / (x2 - x1)) * (px - x1)) + y1;
                        dist = r.dist() / ((float) cosa);
                        x1 = (1.01f + dist) * ((float) Math.cos(a1));
                        y1 = (1.01f + dist) * ((float) Math.sin(a1));
                        x2 = (1.01f + dist) * ((float) Math.cos(a2));
                        float ys2 = (((((1.01f + dist) * ((float) Math.sin(a2))) - y1) / (x2 - x1)) * (px - x1)) + y1;
                        if ((ys2 <= py && py <= ys1) || (ys1 <= py && py <= ys2)) {
                            return true;
                        }
                    } else if (x1 >= 0.0f) {
                        if (x1 <= px && px <= 1.0f + x1) {
                            return true;
                        }
                    } else if (x1 <= 1.0f + px && px <= x1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void draw(long t) {
        if (this.mGame.playing()) {
            this.mRotation += this.mRotationSpeed * ((float) t);
            Iterator i$ = this.mEffects.iterator();
            while (i$.hasNext()) {
                ((Effect) i$.next()).doEffect(t);
            }
            if (this.mPlayingZoomEffect) {
                if (this.mZoom > 1.0f) {
                    this.mZoom = (float) (((double) this.mZoom) - (((double) t) * 0.02d));
                } else {
                    this.mPlayingZoomEffect = false;
                }
            }
            this.mGame.polygon().drawBG(this.mRotation, this.mZoom);
            PolygonShape polygon = this.mGame.polygon();
            polygon.mCameraAngle = (float) (((double) polygon.mCameraAngle) + (0.001d * ((double) t)));
            if (this.mSpeed >= 0.0f) {
                if (this.mGame.playing() && (this.mPolygons.peekLast() == null || ((PolyRow) this.mPolygons.peekLast()).dist() <= 25.0f)) {
                    generateNewPattern();
                }
                while (this.mPolygons.peekFirst() != null && ((PolyRow) this.mPolygons.peekFirst()).dist() < 1.0f) {
                    this.mPolygons.removeFirst();
                }
            } else {
                if (this.mGame.playing() && (this.mPolygons.peekLast() == null || ((PolyRow) this.mPolygons.peekLast()).dist() >= 0.0f)) {
                    generateNewPattern();
                }
                while (this.mPolygons.peekFirst() != null && ((PolyRow) this.mPolygons.peekFirst()).dist() > 25.0f) {
                    this.mPolygons.removeFirst();
                }
            }
            Iterator itr = this.mPolygons.iterator();
            while (itr.hasNext()) {
                PolyRow r = (PolyRow) itr.next();
                r.changeDist((this.mGame.playing() ? -this.mSpeed : 0.0f) * ((float) t));
                if (r.dist() >= this.mHidderDist) {
                    r.draw();
                }
            }
            this.mGame.polygon().drawHidder(this.mHidderDist);
            this.mGame.polygon().drawOrigin();
            return;
        }
        if (this.mZoom < 3.0f) {
            this.mZoom = (float) (((double) this.mZoom) + (((double) t) * 0.02d));
        }
        this.mGame.polygon().drawBG(this.mRotation, this.mZoom);
        this.mGame.polygon().drawHidder(this.mHidderDist);
        this.mGame.polygon().drawOrigin();
    }

    public void clearEffects() {
        this.mEffects.clear();
    }

    public boolean playerCan(float baseSection, float toSection) {
        if (((int) baseSection) == ((int) toSection)) {
            return true;
        }
        Iterator itr = this.mPolygons.iterator();
        while (itr.hasNext()) {
            PolyRow r = (PolyRow) itr.next();
            if ((r.dist() + 1.0f) / ((float) Math.cos(3.141592653589793d / ((double) this.mPolySize))) >= this.mPlayerDist && r.dist() <= this.mPlayerDist + this.mPlayerSize && ((r.mask() >> ((int) toSection)) & 1) == 1) {
                return false;
            }
        }
        return true;
    }

    public void addEffect(Effect eff) {
        this.mEffects.add(eff);
        eff.setBoard(this);
    }

    public void generateNewPattern() {
        Iterator itr = this.mPatternFactory.getNewPattern().rows().iterator();
        int decal = (int) (Math.random() * 6.0d);
        while (itr.hasNext()) {
            this.mPolygons.addLast(((PolyRow) itr.next()).instance(this.mSpeed >= 0.0f, decal, this.mPolySize));
        }
    }

    public PatternFactory patfac() {
        return this.mPatternFactory;
    }

    public void start() {
        this.mPlayingZoomEffect = true;
    }

    public void stop() {
        clear();
        Iterator i$ = this.mEffects.iterator();
        while (i$.hasNext()) {
            ((Effect) i$.next()).endEffect();
        }
    }

    public void clear() {
        this.mPolygons.clear();
    }

    public int polysize() {
        return this.mPolySize;
    }

    public float[] color(int i) {
        return this.mColors[i];
    }

    public float sensibility() {
        return this.mSensibility;
    }

    public float playerDist() {
        return this.mPlayerDist;
    }

    public float playerOrientation() {
        return this.mPlayerOrientation;
    }
}
