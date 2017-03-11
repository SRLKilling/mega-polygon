package com.srl.test;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class PolygonShape {
    ColorDistributor mBackColorDistributor;
    int mBackColorOffset;
    float mCameraAngle;
    float mCameraDist;
    boolean mEpilepticMode;
    Game mGame;
    boolean mInvertedColorMode;
    float mLineSize;
    float mPolyAngle;
    int mPolySize;
    GameGLRenderer mRenderer;
    ColorDistributor mRowColorDistributor;
    int mRowColorOffset;
    int mVBO;

    public PolygonShape(Game g, GameGLRenderer renderer, float lineSize) {
        this.mGame = g;
        this.mRenderer = renderer;
        this.mLineSize = lineSize;
        this.mEpilepticMode = false;
        this.mInvertedColorMode = false;
        float[] baseData = new float[]{-0.2f, -0.25f, 0.0f, 0.0f, 0.25f, 0.0f, 0.0f, -0.25f, 0.0f, 0.2f, -0.25f, 0.0f, -1.0f, 1.01f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 1.01f, 0.0f, 1.0f, 0.0f, 0.0f};
        ByteBuffer bb = ByteBuffer.allocateDirect((baseData.length + 9) * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bb.asFloatBuffer();
        buffer.put(baseData);
        buffer.position(0);
        int[] i = new int[]{0};
        GLES20.glGenBuffers(1, i, 0);
        this.mVBO = i[0];
        GLES20.glBindBuffer(34962, this.mVBO);
        GLES20.glBufferData(34962, (baseData.length + 9) * 4, buffer, 35048);
        GLES20.glEnableVertexAttribArray(this.mRenderer.mPositionAttribLoc);
        GLES20.glVertexAttribPointer(this.mRenderer.mPositionAttribLoc, 3, 5126, false, 0, 0);
        GLES20.glBindBuffer(34962, 0);
        ColorSet set = this.mGame.mColorFactory.newSet("a");
        set.addColor(new ColorWrapper(0.0f, 0.0f, 0.0f, 1.0f));
        this.mBackColorDistributor = set;
        set = this.mGame.mColorFactory.newSet("b");
        set.addColor(new ColorWrapper(1.0f, 1.0f, 1.0f, 1.0f));
        this.mRowColorDistributor = set;
        this.mCameraDist = 0.015f;
        this.mCameraAngle = 0.4f;
        setPolygonSize(6);
    }

    public void setBackColors(ColorDistributor c) {
        this.mBackColorDistributor = c;
    }

    public void setRowColors(ColorDistributor c) {
        this.mRowColorDistributor = c;
    }

    public void setBackColorOffset(int offset) {
        this.mBackColorOffset = offset % this.mGame.mBoard.mPolySize;
    }

    public void setRowColorOffset(int offset) {
        this.mRowColorOffset = offset % this.mGame.mBoard.mPolySize;
    }

    public void setPolygonSize(int size) {
        this.mPolySize = size;
        this.mPolyAngle = 3.1415927f / ((float) this.mPolySize);
        float[] data = new float[]{(float) (-Math.tan((double) this.mPolyAngle)), 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, (float) Math.tan((double) this.mPolyAngle), 1.0f, 0.0f};
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bb.asFloatBuffer();
        buffer.put(data);
        buffer.position(0);
        GLES20.glBindBuffer(34962, this.mVBO);
        GLES20.glBufferSubData(34962, 96, 36, buffer);
    }

    public void drawBG(float rot, float zoom) {
        GLES20.glBindBuffer(34962, this.mVBO);
        GLES20.glEnableVertexAttribArray(this.mRenderer.mPositionAttribLoc);
        GLES20.glVertexAttribPointer(this.mRenderer.mPositionAttribLoc, 3, 5126, false, 0, 0);
        this.mRenderer.lookAt(this.mCameraDist * ((float) Math.cos((double) this.mCameraAngle)), this.mCameraDist * ((float) Math.sin((double) this.mCameraAngle)), 1.0f, 0.0f, 0.0f, 0.0f);
        this.mRenderer.rotate(rot, 0.0f, 0.0f, 1.0f);
        drawPolyRow(0.0f, 25.0f, false);
        this.mRenderer.scale(zoom, zoom, zoom);
    }

    public void drawOrigin() {
        this.mRenderer.pushModelview();
        drawPolyRow(0.0f, 2.0f, true);
        drawPolyRow(0.0f, 1.9f, false);
        this.mRenderer.popModelview();
    }

    public void drawHidder(float dist) {
        drawPolyRow(dist, 1.2f, true);
    }

    public void draw(PolyRow row) {
        GLES20.glBindBuffer(34962, this.mVBO);
        float angle = -this.mPolyAngle;
        for (int i = 0; i < this.mPolySize; i++) {
            if (((row.mask() >> i) & 1) == 1) {
                if (this.mInvertedColorMode) {
                    useBackColor(this.mRowColorOffset + i);
                } else {
                    useRowColor(this.mRowColorOffset + i);
                }
                drawPolySide(row.dist(), angle, this.mLineSize);
            }
            angle += this.mPolyAngle;
        }
    }

    private void drawPolyRow(float dist, float size, boolean mode) {
        this.mRenderer.pushModelview();
        GLES20.glBindBuffer(34962, this.mVBO);
        float angle = -this.mPolyAngle;
        for (int i = 0; i < this.mPolySize; i++) {
            if ((this.mInvertedColorMode ^ mode) != 0) {
                useRowColor(this.mRowColorOffset + i);
            } else {
                useBackColor(this.mBackColorOffset + i);
            }
            drawPolySide(dist, angle, size);
            angle += this.mPolyAngle;
        }
        this.mRenderer.popModelview();
    }

    private void drawPolySide(float dist, float a, float size) {
        GLES20.glBindBuffer(34962, this.mVBO);
        this.mRenderer.pushModelview();
        this.mRenderer.rotate(2.0f * a, 0.0f, 0.0f, 1.0f);
        this.mRenderer.translate(0.0f, dist, 0.0f);
        this.mRenderer.scale(size, size, 1.0f);
        float rd = (((float) Math.tan((double) this.mPolyAngle)) * dist) / size;
        this.mRenderer.translate(-rd, 0.0f, 0.0f);
        if (this.mEpilepticMode) {
            GLES20.glDrawArrays(2, 8, 3);
        } else {
            GLES20.glDrawArrays(4, 8, 3);
        }
        this.mRenderer.translate(2.0f * rd, 0.0f, 0.0f);
        if (this.mEpilepticMode) {
            GLES20.glDrawArrays(2, 8, 3);
        } else {
            GLES20.glDrawArrays(4, 8, 3);
        }
        this.mRenderer.translate(-rd, 0.0f, 0.0f);
        this.mRenderer.scale(rd, 1.0f, 1.0f);
        if (this.mEpilepticMode) {
            GLES20.glDrawArrays(2, 4, 3);
            GLES20.glDrawArrays(2, 5, 3);
        } else {
            GLES20.glDrawArrays(5, 4, 4);
        }
        this.mRenderer.popModelview();
    }

    public void draw(Player p) {
        GLES20.glBindBuffer(34962, this.mVBO);
        this.mRenderer.useRowColor();
        this.mRenderer.pushModelview();
        this.mRenderer.rotate(p.angle(), 0.0f, 0.0f, 1.0f);
        this.mRenderer.translate(this.mGame.board().playerDist(), 0.0f, 0.0f);
        this.mRenderer.rotate(this.mGame.board().playerOrientation(), 0.0f, 0.0f, 1.0f);
        if (this.mInvertedColorMode) {
            useBackColor(this.mBackColorOffset + ((int) p.section()));
        } else {
            useRowColor(this.mRowColorOffset + ((int) p.section()));
        }
        if (this.mEpilepticMode) {
            GLES20.glDrawArrays(2, 0, 3);
            GLES20.glDrawArrays(2, 1, 3);
        } else {
            GLES20.glDrawArrays(5, 0, 4);
        }
        this.mRenderer.popModelview();
    }

    public void useBackColor(int i) {
        this.mRenderer.useColor(this.mBackColorDistributor.get(i));
    }

    public void useRowColor(int i) {
        this.mRenderer.useColor(this.mRowColorDistributor.get(i));
    }

    public void setEpilepticMode(boolean mode) {
        this.mEpilepticMode = mode;
    }

    public void setInvertedColorMode(boolean mode) {
        this.mInvertedColorMode = mode;
    }
}
