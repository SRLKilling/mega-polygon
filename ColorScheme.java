package com.srl.test;

import java.util.HashMap;

public class ColorScheme {
    HashMap<String, ColorWrapper> mColorMap;
    Mode mMode;
    Type mType;

    public ColorScheme(Type t, Mode m) {
        this.mColorMap = new HashMap();
        this.mType = t;
        this.mMode = m;
    }

    public ColorWrapper get(String name) {
        if (!this.mColorMap.containsKey(name)) {
            this.mColorMap.put(name, new ColorWrapper());
        }
        return (ColorWrapper) this.mColorMap.get(name);
    }
}
