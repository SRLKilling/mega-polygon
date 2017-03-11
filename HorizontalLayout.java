package com.srl.test;

import android.util.Log;
import android.view.MotionEvent;
import java.util.Iterator;
import java.util.LinkedList;

class HorizontalLayout extends Element {
    LinkedList<Element> mElements;

    public HorizontalLayout(UI ui) {
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
            el.drawElement();
            this.mUI.translate(el.getMWidth(), 0.0f);
        }
    }

    public void childChanged(Element e) {
        computeElement();
    }

    boolean compute() {
        float maxHeight = 0.0f;
        float width = 0.0f;
        Iterator i$ = this.mElements.iterator();
        while (i$.hasNext()) {
            Element el = (Element) i$.next();
            Log.v("Test", "     SettingHorizontalElement -> PX : " + getPX() + width + " ; PY : " + getPY());
            el.setX(getPX() + width);
            el.setY(getPY());
            width += el.getMWidth();
            if (el.getMHeight() > maxHeight) {
                maxHeight = el.getMHeight();
            }
        }
        this.mHeight = (this.mPadding[0] + maxHeight) + this.mPadding[2];
        boolean change = false;
        i$ = this.mElements.iterator();
        while (i$.hasNext()) {
            if (((Element) i$.next()).computeElement()) {
                change = true;
            }
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
        return false;
    }
}
