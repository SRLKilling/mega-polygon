package com.srl.test;

import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import java.util.Iterator;
import java.util.LinkedList;

class VerticalLayout extends Element {
    LinkedList<Element> mElements;

    class ScrollGesture extends SimpleOnGestureListener {
        VerticalLayout mLayout;

        public ScrollGesture(VerticalLayout vlay) {
            this.mLayout = vlay;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            this.mLayout.scroll((float) ((int) distanceX), (float) ((int) distanceY));
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
            return true;
        }

        public boolean onDown(MotionEvent e) {
            if (!this.mLayout.mScroller.isFinished()) {
                this.mLayout.mScroller.forceFinished(true);
            }
            return true;
        }
    }

    public VerticalLayout(UI ui) {
        super(ui);
        this.mElements = new LinkedList();
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
        Iterator i$ = this.mElements.iterator();
        while (i$.hasNext()) {
            Element el = (Element) i$.next();
            this.mUI.translate((this.mWidth - el.getMWidth()) / 2.0f, 0.0f);
            el.drawElement();
            this.mUI.translate((-(this.mWidth - el.getMWidth())) / 2.0f, el.getMHeight());
        }
    }

    boolean compute() {
        boolean change = false;
        float h = 0.0f;
        Iterator i$ = this.mElements.iterator();
        while (i$.hasNext()) {
            Element el = (Element) i$.next();
            if (el.computeElement()) {
                change = true;
            }
            h += el.getMHeight();
        }
        if (!change && this.mPadding[0] == (this.mHeight - h) / 2.0f) {
            return change;
        }
        float[] fArr = this.mPadding;
        float f = (this.mHeight - h) / 2.0f;
        this.mPadding[2] = f;
        fArr[0] = f;
        h = 0.0f;
        i$ = this.mElements.iterator();
        while (i$.hasNext()) {
            el = (Element) i$.next();
            el.setX(getPX() + ((this.mWidth - el.getMWidth()) / 2.0f));
            el.setY(getPY() + h);
            Log.v("Test", "New X : " + (getPX() + ((this.mWidth - el.getMWidth()) / 2.0f)) + " ; New Y : " + (getPY() + h));
            h += el.getMHeight();
        }
        return true;
    }

    public boolean event(MotionEvent e) {
        Iterator i$ = this.mElements.iterator();
        while (i$.hasNext()) {
            if (((Element) i$.next()).eventHandler(e)) {
                return true;
            }
        }
        return false;
    }
}
