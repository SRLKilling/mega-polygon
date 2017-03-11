package com.srl.test;

import android.os.Environment;
import android.util.Log;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

class LevelLoader {
    private XmlPullParserFactory mFactory;
    private Game mGame;
    private String mHomeFilepath;
    private HashMap<String, Pattern> mPatterns;
    private String ns;

    public LevelLoader(Game g) {
        this.mGame = g;
        this.ns = null;
        this.mPatterns = new HashMap();
        this.mHomeFilepath = Environment.getExternalStorageDirectory().toString() + "/MegaPolygon";
        try {
            this.mFactory = XmlPullParserFactory.newInstance();
            this.mFactory.setNamespaceAware(true);
        } catch (XmlPullParserException e) {
            Log.v("Test", "XMLException");
        }
    }

    public void loadLevelXML(Reader in) {
        try {
            XmlPullParser parser = this.mFactory.newPullParser();
            parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", false);
            parser.setInput(in);
            parser.nextTag();
            parseLevel(parser);
        } catch (XmlPullParserException e) {
            Log.v("Test", "XMLException");
        } catch (IOException e2) {
            Log.v("Test", "IOEXception");
        }
    }

    private void loadPatternFile(Reader in) {
        try {
            XmlPullParser parser = this.mFactory.newPullParser();
            parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", false);
            parser.setInput(in);
            parser.nextTag();
            parser.require(2, this.ns, "patterns");
            parsePatterns(parser, true);
        } catch (XmlPullParserException e) {
            Log.v("Test", "XMLException");
        } catch (IOException e2) {
            Log.v("Test", "IOEXception");
        }
    }

    private void parseLevel(XmlPullParser parser) throws XmlPullParserException, IOException {
        this.mGame.mBoard.clearEffects();
        this.mGame.mBoard.patfac().clear();
        parser.require(2, this.ns, "level");
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                String name = parser.getName();
                if (name.equals("name")) {
                    skip(parser);
                } else if (name.equals("patterns")) {
                    parsePatterns(parser, false);
                } else if (name.equals("effects")) {
                    parseEffects(parser);
                } else if (name.equals("colors")) {
                    parseColors(parser);
                } else if (name.equals("usecolors")) {
                    String type = parser.getAttributeValue(null, "type");
                    name = parser.getAttributeValue(null, "name");
                    if (type.equals("back")) {
                        this.mGame.mPolygon.setBackColors(this.mGame.mColorFactory.getDistributor(name));
                    } else if (type.equals("rows")) {
                        this.mGame.mPolygon.setRowColors(this.mGame.mColorFactory.getDistributor(name));
                    }
                    parser.nextTag();
                } else {
                    skip(parser);
                }
            }
        }
    }

    private void parsePatterns(XmlPullParser parser, boolean internal) throws XmlPullParserException, IOException {
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                String name = parser.getName();
                if (name.equals("pattern")) {
                    parsePattern(parser, internal);
                } else if (name.equals("import")) {
                    loadPatternFile(new FileReader(this.mHomeFilepath + "/patterns/" + parser.getAttributeValue(null, "file")));
                    parser.nextTag();
                } else {
                    skip(parser);
                }
            }
        }
    }

    private void parsePattern(XmlPullParser parser, boolean internal) throws XmlPullParserException, IOException {
        String name;
        if (parser.getAttributeValue(null, "imported") != null) {
            name = parser.getAttributeValue(null, "name");
            if (name != null) {
                this.mGame.mBoard.patfac().addPattern((Pattern) this.mPatterns.get(name));
                parser.nextTag();
                return;
            }
            return;
        }
        int size = Integer.valueOf(parser.getAttributeValue(null, "size")).intValue();
        name = "";
        if (internal) {
            name = parser.getAttributeValue(null, "name");
        }
        Pattern pattern = new Pattern(this.mGame, size);
        if (parser.next() == 4) {
            for (String line : parser.getText().split(System.getProperty("line.separator"))) {
                String line2 = line2.trim();
                Log.v("Test", line2);
                int mask = 0;
                for (int i = 0; i < line2.length(); i++) {
                    if (line2.charAt(i) == '0') {
                        mask <<= 1;
                    } else if (line2.charAt(i) == '1') {
                        mask = (mask << 1) | 1;
                    }
                }
                pattern.p(mask);
            }
            parser.nextTag();
        }
        if (internal) {
            this.mPatterns.put(name, pattern);
        } else {
            this.mGame.mBoard.patfac().addPattern(pattern);
        }
    }

    private void parseEffects(XmlPullParser parser) throws XmlPullParserException, IOException {
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                if (parser.getName().equals("effect")) {
                    parseEffect(parser);
                } else {
                    skip(parser);
                }
            }
        }
    }

    private void parseEffect(XmlPullParser parser) throws XmlPullParserException, IOException {
        String type = parser.getAttributeValue(null, "type");
        float minTime = Float.valueOf(parser.getAttributeValue(null, "minimalTime")).floatValue();
        float proba = Float.valueOf(parser.getAttributeValue(null, "proba")).floatValue();
        if (type.equals("rotation")) {
            this.mGame.mBoard.addEffect(new RotationEffect(3.1415927f / (500.0f * Float.valueOf(parser.getAttributeValue(null, "speed")).floatValue()), minTime, proba));
        } else if (type.equals("hidder")) {
            this.mGame.mBoard.addEffect(new HidderEffect(Float.valueOf(parser.getAttributeValue(null, "dist")).floatValue(), minTime, proba));
        } else if (type.equals("inverseColorMode")) {
            this.mGame.mBoard.addEffect(new InvertedColorEffect(minTime, proba));
        } else if (type.equals("polygonMode")) {
            this.mGame.mBoard.addEffect(new EpilepticEffect(minTime, proba));
        } else if (type.equals("colorSwitch")) {
            int number = Integer.valueOf(parser.getAttributeValue(null, "number")).intValue();
            this.mGame.mBoard.addEffect(new SwitchColorEffect(parser.getAttributeValue(null, "mode").equals("row"), number, minTime, proba));
        }
        parser.nextTag();
    }

    private void parseColors(XmlPullParser parser) throws XmlPullParserException, IOException {
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                if (parser.getName().equals("set")) {
                    ColorSet set = this.mGame.mColorFactory.newSet(parser.getAttributeValue(null, "name"));
                    while (parser.next() != 3) {
                        if (parser.getEventType() == 2) {
                            if (parser.getName().equals("color")) {
                                set.addColor(getColorFromRGBA(parser));
                                parser.nextTag();
                            } else {
                                skip(parser);
                            }
                        }
                    }
                } else {
                    skip(parser);
                }
            }
        }
    }

    private ColorWrapper getColorFromRGBA(XmlPullParser parser) {
        String[] c = parser.getAttributeValue(null, "rgba").split(" ");
        Log.v("Test", "RGBA value : " + Float.valueOf(c[0]) + ", " + Float.valueOf(c[1]) + ", " + Float.valueOf(c[2]) + ", " + Float.valueOf(c[3]));
        return new ColorWrapper(Float.valueOf(c[0]).floatValue(), Float.valueOf(c[1]).floatValue(), Float.valueOf(c[2]).floatValue(), Float.valueOf(c[3]).floatValue());
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case 2:
                    depth++;
                    break;
                case 3:
                    depth--;
                    break;
                default:
                    break;
            }
        }
    }
}
