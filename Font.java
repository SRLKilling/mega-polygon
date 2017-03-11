package com.srl.test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;

class Font {
    static Font defaultFont = null;
    private static final String fragmentShaderCode = "precision mediump float;varying vec2 texCoord;uniform sampler2D tex;uniform vec4 color;void main() {  gl_FragColor = vec4(color.rgb, texture2D(tex, texCoord).a);}";
    static int mColorLoc = 0;
    static int mModelviewLoc = 0;
    static int mPositionAttribLoc = 0;
    static int mProgram = 0;
    static int mTexCoordAttribLoc = 0;
    static int mTextureLoc = 0;
    private static FloatBuffer squareBuffer = null;
    private static final String vertexShaderCode = "attribute vec4 vPosition;attribute vec2 vTexCoord;varying vec2 texCoord;uniform mat4 modelviewMat;void main() {  texCoord = vTexCoord;  gl_Position = modelviewMat * vPosition;}";
    Typeface mFace;
    HashMap<Integer, Facet> mFacets;
    int mMaxSize;
    float[] mModelview;
    Paint mPaint;

    class Facet {
        float mAscent;
        char mCharNum;
        char mCharOffset;
        FloatBuffer[] mCharTexLoc;
        float[] mCharWidths;
        float mDescent;
        Font mFont;
        float mHeight;
        float mScale;
        int mTexId;

        public Facet(Font f, float scale) {
            char c;
            float maxSize;
            int texSize;
            this.mFont = f;
            this.mScale = scale;
            Paint paint = f.paint();
            FontMetrics fm = paint.getFontMetrics();
            this.mHeight = (float) Math.ceil((double) (Math.abs(fm.bottom) + Math.abs(fm.top)));
            this.mAscent = (float) Math.ceil((double) Math.abs(fm.ascent));
            this.mDescent = (float) Math.ceil((double) Math.abs(fm.descent));
            this.mCharOffset = ' ';
            this.mCharNum = (char) (127 - this.mCharOffset);
            this.mCharWidths = new float[this.mCharNum];
            char[] car = new char[2];
            float[] width = new float[2];
            float maxWidth = 0.0f;
            for (c = '\u0000'; c < this.mCharNum; c = (char) (c + 1)) {
                car[0] = (char) (this.mCharOffset + c);
                paint.getTextWidths(car, 0, 1, width);
                this.mCharWidths[c] = width[0];
                if (this.mCharWidths[c] > maxWidth) {
                    maxWidth = this.mCharWidths[c];
                }
            }
            float cellW = ((float) ((int) maxWidth)) + (2.0f * f.padX());
            float cellH = ((float) ((int) this.mHeight)) + (2.0f * f.padY());
            if (cellW >= cellH) {
                maxSize = cellW;
            } else {
                maxSize = cellH;
            }
            if (maxSize <= 24.0f) {
                texSize = 256;
            } else if (maxSize <= 40.0f) {
                texSize = 512;
            } else if (maxSize <= 80.0f) {
                texSize = 1024;
            } else {
                texSize = 2048;
            }
            Bitmap bmp = Bitmap.createBitmap(texSize, texSize, Config.ALPHA_8);
            bmp.eraseColor(0);
            Canvas canvas = new Canvas(bmp);
            int rowCnt = (int) Math.ceil((double) (((float) this.mCharNum) / ((float) ((int) (((float) texSize) / cellW)))));
            this.mCharTexLoc = new FloatBuffer[this.mCharNum];
            float[] tmpCharTexLoc = new float[8];
            float x = f.padX();
            float y = ((cellH - 1.0f) - this.mDescent) - f.padY();
            float tx = 0.0f;
            float ty = 0.0f;
            for (c = '\u0000'; c < this.mCharNum; c = (char) (c + 1)) {
                car[0] = (char) (this.mCharOffset + c);
                canvas.drawText(car, 0, 1, x, y, paint);
                tmpCharTexLoc[0] = tx / ((float) texSize);
                tmpCharTexLoc[1] = ty / ((float) texSize);
                tmpCharTexLoc[2] = (this.mCharWidths[c] + tx) / ((float) texSize);
                tmpCharTexLoc[3] = ty / ((float) texSize);
                tmpCharTexLoc[4] = tx / ((float) texSize);
                tmpCharTexLoc[5] = (ty + cellH) / ((float) texSize);
                tmpCharTexLoc[6] = (this.mCharWidths[c] + tx) / ((float) texSize);
                tmpCharTexLoc[7] = (ty + cellH) / ((float) texSize);
                ByteBuffer bb = ByteBuffer.allocateDirect(tmpCharTexLoc.length * 4);
                bb.order(ByteOrder.nativeOrder());
                this.mCharTexLoc[c] = bb.asFloatBuffer();
                this.mCharTexLoc[c].put(tmpCharTexLoc);
                this.mCharTexLoc[c].position(0);
                x += cellW;
                tx += cellW;
                if ((x + cellW) - f.padX() > ((float) texSize)) {
                    x = f.padX();
                    tx = 0.0f;
                    y += cellH;
                    ty += cellH;
                }
            }
            int[] ti = new int[1];
            GLES20.glGenTextures(1, ti, 0);
            this.mTexId = ti[0];
            GLES20.glBindTexture(3553, this.mTexId);
            GLES20.glTexParameterf(3553, 10241, 9728.0f);
            GLES20.glTexParameterf(3553, 10240, 9729.0f);
            GLES20.glTexParameterf(3553, 10242, 33071.0f);
            GLES20.glTexParameterf(3553, 10243, 33071.0f);
            GLUtils.texImage2D(3553, 0, bmp, 0);
            GLES20.glBindTexture(3553, 0);
            bmp.recycle();
        }

        void drawText(UI ui, String str, boolean reversedX, boolean reversedY) {
            Font font = this.mFont;
            GLES20.glUseProgram(Font.mProgram);
            GLES20.glBindTexture(3553, this.mTexId);
            this.mFont.mModelview = Arrays.copyOf(ui.getModelview(), 16);
            font = this.mFont;
            GLES20.glUniform4f(Font.mColorLoc, ui.currentColor().r(), ui.currentColor().g(), ui.currentColor().b(), ui.currentColor().a());
            Matrix.scaleM(this.mFont.mModelview, 0, 1.0f / ((float) GameGLRenderer.get().width()), 1.0f / ((float) GameGLRenderer.get().height()), 1.0f);
            internalDrawText(str, reversedX, reversedY);
            ui.reUseProgram();
            GLES20.glBindTexture(3553, 0);
        }

        void drawText(String str, float x, float y, boolean reversedX, boolean reversedY) {
            Font font = this.mFont;
            GLES20.glUseProgram(Font.mProgram);
            GLES20.glBindTexture(3553, this.mTexId);
            Matrix.setIdentityM(this.mFont.mModelview, 0);
            Matrix.translateM(this.mFont.mModelview, 0, -1.0f, 1.0f, 0.0f);
            Matrix.scaleM(this.mFont.mModelview, 0, 2.0f, -1.0f, 0.0f);
            Matrix.translateM(this.mFont.mModelview, 0, x, y, 0.0f);
            Matrix.scaleM(this.mFont.mModelview, 0, 1.0f / ((float) GameGLRenderer.get().width()), 1.0f / ((float) GameGLRenderer.get().height()), 1.0f);
            font = this.mFont;
            GLES20.glUniform4f(Font.mColorLoc, 1.0f, 1.0f, 1.0f, 1.0f);
            internalDrawText(str, reversedX, reversedY);
            GLES20.glUseProgram(0);
            GLES20.glBindTexture(3553, 0);
        }

        private void internalDrawText(String str, boolean reversedX, boolean reversedY) {
            Matrix.scaleM(this.mFont.mModelview, 0, this.mScale, this.mScale, 1.0f);
            if (reversedY) {
                Matrix.translateM(this.mFont.mModelview, 0, 0.0f, -this.mHeight, 1.0f);
            }
            float tx = 0.0f;
            for (int i = 0; i < str.length(); i++) {
                char c;
                if (reversedX) {
                    c = str.charAt((str.length() - i) - 1);
                    tx -= this.mCharWidths[c - this.mCharOffset];
                    Matrix.translateM(this.mFont.mModelview, 0, -this.mCharWidths[c - this.mCharOffset], 0.0f, 0.0f);
                    drawChar(c);
                } else {
                    c = str.charAt(i);
                    tx += this.mCharWidths[c - this.mCharOffset];
                    drawChar(c);
                    Matrix.translateM(this.mFont.mModelview, 0, this.mCharWidths[c - this.mCharOffset], 0.0f, 0.0f);
                }
            }
        }

        void drawChar(char c) {
            Matrix.scaleM(this.mFont.mModelview, 0, this.mCharWidths[c - this.mCharOffset], this.mHeight, 1.0f);
            Font font = this.mFont;
            GLES20.glUniformMatrix4fv(Font.mModelviewLoc, 1, false, Font.this.mModelview, 0);
            GLES20.glBindBuffer(34962, 0);
            font = this.mFont;
            GLES20.glEnableVertexAttribArray(Font.mPositionAttribLoc);
            font = this.mFont;
            GLES20.glVertexAttribPointer(Font.mPositionAttribLoc, 2, 5126, false, 0, Font.unitSquare());
            font = this.mFont;
            GLES20.glEnableVertexAttribArray(Font.mTexCoordAttribLoc);
            font = this.mFont;
            GLES20.glVertexAttribPointer(Font.mTexCoordAttribLoc, 2, 5126, false, 0, this.mCharTexLoc[c - this.mCharOffset]);
            GLES20.glDrawArrays(5, 0, 4);
            Matrix.scaleM(this.mFont.mModelview, 0, 1.0f / this.mCharWidths[c - this.mCharOffset], 1.0f / this.mHeight, 1.0f);
        }

        float getTextWidth(String str) {
            float w = 0.0f;
            for (int i = 0; i < str.length(); i++) {
                w += this.mCharWidths[str.charAt(i) - this.mCharOffset];
            }
            return (this.mScale * w) / ((float) GameGLRenderer.get().width());
        }

        float getTextHeight() {
            return (this.mHeight * this.mScale) / ((float) GameGLRenderer.get().height());
        }
    }

    class TooBigFacet {
        TooBigFacet() {
        }
    }

    static {
        mProgram = 0;
        mPositionAttribLoc = 0;
        mTexCoordAttribLoc = 0;
        mModelviewLoc = 0;
        mTextureLoc = 0;
        mColorLoc = 0;
        squareBuffer = null;
    }

    public Font() {
        if (mProgram == 0) {
            mProgram = GameGLRenderer.getLinkedProgram(vertexShaderCode, fragmentShaderCode);
            GLES20.glUseProgram(mProgram);
            mPositionAttribLoc = GLES20.glGetAttribLocation(mProgram, "vPosition");
            mTexCoordAttribLoc = GLES20.glGetAttribLocation(mProgram, "vTexCoord");
            mModelviewLoc = GLES20.glGetUniformLocation(mProgram, "modelviewMat");
            mTextureLoc = GLES20.glGetUniformLocation(mProgram, "tex");
            mColorLoc = GLES20.glGetUniformLocation(mProgram, "color");
            GLES20.glUniform1i(mTextureLoc, 0);
        }
        this.mModelview = new float[16];
        this.mPaint = new Paint();
        this.mFacets = new HashMap();
        this.mMaxSize = 100;
    }

    static FloatBuffer unitSquare() {
        if (squareBuffer == null) {
            float[] baseData = new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
            ByteBuffer bb = ByteBuffer.allocateDirect(baseData.length * 4);
            bb.order(ByteOrder.nativeOrder());
            squareBuffer = bb.asFloatBuffer();
            squareBuffer.put(baseData);
            squareBuffer.position(0);
        }
        return squareBuffer;
    }

    public Paint paint() {
        return this.mPaint;
    }

    public float padX() {
        return 0.0f;
    }

    public float padY() {
        return 0.0f;
    }

    public void loadFromFile(String path) {
        this.mFace = Typeface.createFromFile(path);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(-1);
        this.mPaint.setTypeface(this.mFace);
    }

    public void load(String path) {
        this.mFace = Typeface.create(path, 0);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(-1);
        this.mPaint.setTypeface(this.mFace);
    }

    public static Font create(String path) {
        Font f = new Font();
        f.load(path);
        return f;
    }

    public static Font getDefault() {
        if (defaultFont == null) {
            defaultFont = new Font();
            defaultFont.load(null);
        }
        return defaultFont;
    }

    public void renderFacet(int size) {
        int i;
        Paint paint = this.mPaint;
        if (size >= this.mMaxSize) {
            i = this.mMaxSize;
        } else {
            i = size;
        }
        paint.setTextSize((float) i);
        if (!this.mFacets.containsKey(Integer.valueOf(size))) {
            this.mFacets.put(Integer.valueOf(size), new Facet(this, size >= this.mMaxSize ? ((float) size) / ((float) this.mMaxSize) : 1.0f));
        }
    }

    public void draw(String str, float x, float y, int size, boolean reversedX, boolean reversedY) {
        if (!this.mFacets.containsKey(Integer.valueOf(size))) {
            renderFacet(size);
        }
        ((Facet) this.mFacets.get(Integer.valueOf(size))).drawText(str, x, y, reversedX, reversedY);
    }

    public void draw(UI ui, String str, int size, boolean reversedX, boolean reversedY) {
        if (!this.mFacets.containsKey(Integer.valueOf(size))) {
            renderFacet(size);
        }
        ((Facet) this.mFacets.get(Integer.valueOf(size))).drawText(ui, str, reversedX, reversedY);
    }

    public float getTextWidth(String str, int size) {
        if (!this.mFacets.containsKey(Integer.valueOf(size))) {
            renderFacet(size);
        }
        return ((Facet) this.mFacets.get(Integer.valueOf(size))).getTextWidth(str);
    }

    public float getTextHeight(int size) {
        if (!this.mFacets.containsKey(Integer.valueOf(size))) {
            renderFacet(size);
        }
        return ((Facet) this.mFacets.get(Integer.valueOf(size))).getTextHeight();
    }
}
