package com.srl.test;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

class UnitSquare {
    private static FloatBuffer squareBuffer;

    UnitSquare() {
    }

    static {
        squareBuffer = null;
    }

    private static void init() {
        if (squareBuffer == null) {
            float[] baseData = new float[]{0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
            ByteBuffer bb = ByteBuffer.allocateDirect(baseData.length * 4);
            bb.order(ByteOrder.nativeOrder());
            squareBuffer = bb.asFloatBuffer();
            squareBuffer.put(baseData);
            squareBuffer.position(0);
        }
    }

    public static void draw(int vpointer) {
        if (squareBuffer == null) {
            init();
        }
        GLES20.glEnableVertexAttribArray(vpointer);
        GLES20.glVertexAttribPointer(vpointer, 2, 5126, false, 0, squareBuffer);
        GLES20.glDrawArrays(5, 0, 4);
    }

    public static void drawLines(int vpointer) {
        if (squareBuffer == null) {
            init();
        }
        GLES20.glEnableVertexAttribArray(vpointer);
        GLES20.glVertexAttribPointer(vpointer, 2, 5126, false, 0, squareBuffer);
        GLES20.glDrawArrays(2, 4, 4);
    }
}
