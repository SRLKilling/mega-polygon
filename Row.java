package com.srl.test;

class Row extends Element {
    public Row(UI ui) {
        super(ui);
    }

    public void draw(float w, float h) {
        this.mUI.scale(w, h);
        UI ui = this.mUI;
        UnitSquare.draw(UI.mPositionLoc);
    }
}
