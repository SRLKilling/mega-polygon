package com.srl.test;

import java.util.HashMap;
import java.util.Vector;

class ColorFactory {
    HashMap<String, ColorDistributor> mDistributors;

    static /* synthetic */ class 1 {
        static final /* synthetic */ int[] $SwitchMap$com$srl$test$ColorFactory$Type;

        static {
            $SwitchMap$com$srl$test$ColorFactory$Type = new int[Type.values().length];
            try {
                $SwitchMap$com$srl$test$ColorFactory$Type[Type.UI.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$srl$test$ColorFactory$Type[Type.BACK.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$srl$test$ColorFactory$Type[Type.ROW.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    interface ColorDistributor {
        ColorWrapper get(int i);
    }

    public class ColorScheme {
        private HashMap<String, ColorWrapper> mColorMap;
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

    class ColorSet implements ColorDistributor {
        private Vector<ColorWrapper> mColors;

        public ColorSet() {
            this.mColors = new Vector();
        }

        public void addColor(ColorWrapper color) {
            this.mColors.add(color);
        }

        public ColorWrapper get(int i) {
            return (ColorWrapper) this.mColors.get(i % this.mColors.size());
        }
    }

    enum Mode {
        PARITY,
        UNIT,
        PERCOL
    }

    enum Type {
        UI,
        BACK,
        ROW
    }

    public ColorFactory() {
        this.mDistributors = new HashMap();
    }

    public ColorSet newSet(String name) {
        ColorSet set = new ColorSet();
        this.mDistributors.put(name, set);
        return set;
    }

    public ColorDistributor getDistributor(String name) {
        return (ColorDistributor) this.mDistributors.get(name);
    }
}
