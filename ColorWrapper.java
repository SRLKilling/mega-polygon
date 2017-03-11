package com.srl.test;

class ColorWrapper {
    private float[] mComponents;

    public ColorWrapper() {
        this.mComponents = new float[4];
        rgba(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public ColorWrapper(float[] comp) {
        this.mComponents = new float[4];
        fromArray(comp);
    }

    public ColorWrapper(float r, float g, float b) {
        this.mComponents = new float[4];
        rgba(r, g, b, 1.0f);
    }

    public ColorWrapper(float r, float g, float b, float a) {
        this.mComponents = new float[4];
        rgba(r, g, b, a);
    }

    public void setTo(ColorWrapper c) {
        this.mComponents[0] = c.mComponents[0];
        this.mComponents[1] = c.mComponents[1];
        this.mComponents[2] = c.mComponents[2];
        this.mComponents[3] = c.mComponents[3];
    }

    public float r() {
        return this.mComponents[0];
    }

    public void r(float r) {
        this.mComponents[0] = r;
    }

    public float g() {
        return this.mComponents[1];
    }

    public void g(float g) {
        this.mComponents[1] = g;
    }

    public float b() {
        return this.mComponents[2];
    }

    public void b(float b) {
        this.mComponents[2] = b;
    }

    public float a() {
        return this.mComponents[3];
    }

    public void a(float a) {
        this.mComponents[3] = a;
    }

    public void rgb(float r, float g, float b) {
        this.mComponents[0] = r;
        this.mComponents[1] = g;
        this.mComponents[2] = b;
    }

    public void rgba(float r, float g, float b, float a) {
        this.mComponents[0] = r;
        this.mComponents[1] = g;
        this.mComponents[2] = b;
        this.mComponents[3] = a;
    }

    public void fromArray(float[] comp) {
        if (comp.length >= 1) {
            this.mComponents[0] = comp[0];
        }
        if (comp.length >= 2) {
            this.mComponents[1] = comp[1];
        }
        if (comp.length >= 3) {
            this.mComponents[2] = comp[2];
        }
        if (comp.length >= 4) {
            this.mComponents[3] = comp[3];
        }
    }
}
