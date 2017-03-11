package com.srl.test;

import android.util.Log;
import android.view.MotionEvent;
import java.util.Iterator;
import java.util.LinkedList;

class VerticalScrollList extends Element {
    LinkedList<Element> mElements;
    float mExpectedScroll;
    float mLastY;
    float mScrollSpeed;
    float mScrollY;
    boolean mScrolling;

    public VerticalScrollList(UI ui) {
        super(ui);
        this.mElements = new LinkedList();
        this.mScrolling = false;
        this.mLastY = 0.0f;
        this.mScrollY = 0.0f;
        this.mExpectedScroll = 0.0f;
        this.mScrollSpeed = 2.0f;
    }

    public void addElement(Element el) {
        this.mElements.add(el);
        el.setParent(this);
    }

    public void addElement(int row, Element el) {
        this.mElements.add(row, el);
        el.setParent(this);
    }

    public void addElements(Element[] els) {
        for (Element el : els) {
            el.setParent(this);
            this.mElements.add(el);
        }
    }

    public void draw() {
        if (this.mExpectedScroll != this.mScrollY) {
            if (((double) (this.mExpectedScroll - this.mScrollY)) > 0.001d || ((double) (this.mExpectedScroll - this.mScrollY)) < -0.001d) {
                this.mScrollY += (this.mExpectedScroll - this.mScrollY) / 20.0f;
            } else {
                this.mScrollY = this.mExpectedScroll;
            }
        }
        this.mUI.translate(0.0f, this.mScrollY);
        Iterator i$ = this.mElements.iterator();
        while (i$.hasNext()) {
            Element el = (Element) i$.next();
            el.drawElement();
            this.mUI.translate(0.0f, el.getMHeight());
        }
    }

    boolean compute() {
        boolean change = false;
        float ty = this.mScrollY;
        Iterator i$ = this.mElements.iterator();
        while (i$.hasNext()) {
            Element el = (Element) i$.next();
            el.setX(getPX());
            el.setY(getPY() + ty);
            Log.v("Test", "SettingHorizontalTruc -> PX : " + getPX() + " ; PY : " + (getPY() + ty));
            el.setMWidth(getPWidth());
            if (el.computeElement()) {
                change = true;
            }
            ty += el.getMHeight();
        }
        return change;
    }

    public boolean event(MotionEvent e) {
        Iterator i$ = this.mElements.iterator();
        while (i$.hasNext()) {
            if (((Element) i$.next()).eventHandler(e)) {
                return true;
            }
        }
        switch (e.getAction()) {
            case 0:
                this.mLastY = e.getY();
                this.mScrolling = true;
                this.mUI.requestFocus(this);
                return true;
            case 1:
                this.mScrolling = false;
                this.mUI.looseFocus();
                return true;
            case 2:
                if (!this.mScrolling) {
                    return true;
                }
                this.mExpectedScroll += this.mUI.normaliseY((-this.mLastY) + e.getY()) * this.mScrollSpeed;
                this.mLastY = e.getY();
                this.mToCompute = true;
                return true;
            default:
                return false;
        }
    }
}
