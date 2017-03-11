package com.srl.test;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.Arrays;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameGLRenderer implements Renderer {
    private static GameGLRenderer CurrentRenderer = null;
    private static final String MSAAfsCode = "varying vec2 pos;uniform sampler2D tex;void main() {gl_FragColor = texture2D(tex, pos);}";
    private static final String MSAAvsCode = "attribute vec4 vPosition;varying vec2 pos;void main() {pos.x = (vPosition.x+1.0)/2.0;pos.y = (vPosition.y+1.0)/2.0;gl_Position = vPosition;}";
    private static final String fragmentShaderCode = "precision mediump float;uniform vec4 vColor;void main() {  gl_FragColor = vColor;}";
    private static final String vertexShaderCode = "attribute vec4 vPosition;uniform mat4 projectionMat;uniform mat4 modelviewMat;void main() {  gl_Position = projectionMat * modelviewMat * vPosition;}";
    private boolean mAntiAliasing;
    private int mColorLoc;
    private int mFBO;
    private Game mGame;
    private int mHeight;
    private int mMSAAPosLoc;
    private int mMSAAProgram;
    private int mMSAATexLoc;
    private int mMSAApower;
    private FloatBuffer mMSAAva;
    private float[] mModelviewMatrix;
    private int mModelviewMatrixLoc;
    private ArrayDeque<float[]> mModelviewStack;
    public int mPositionAttribLoc;
    private int mProgram;
    private float[] mProjectionMatrix;
    private int mProjectionMatrixLoc;
    private int mTex;
    private int mWidth;

    static /* synthetic */ class 1 {
        static final /* synthetic */ int[] $SwitchMap$com$srl$test$ColorFactory$Mode;

        static {
            $SwitchMap$com$srl$test$ColorFactory$Mode = new int[Mode.values().length];
            try {
                $SwitchMap$com$srl$test$ColorFactory$Mode[Mode.PARITY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$srl$test$ColorFactory$Mode[Mode.UNIT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$srl$test$ColorFactory$Mode[Mode.PERCOL.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    static GameGLRenderer get() {
        return CurrentRenderer;
    }

    public GameGLRenderer(Game g) {
        this.mGame = g;
        this.mProjectionMatrix = new float[16];
        this.mModelviewMatrix = new float[16];
        this.mModelviewStack = new ArrayDeque();
        this.mAntiAliasing = false;
        this.mMSAApower = 2;
        CurrentRenderer = this;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(3042);
        GLES20.glBlendFunc(770, 771);
        GLES20.glDisable(2884);
        GLES20.glDisable(2929);
        this.mProgram = getLinkedProgram(vertexShaderCode, fragmentShaderCode);
        GLES20.glUseProgram(this.mProgram);
        this.mPositionAttribLoc = GLES20.glGetAttribLocation(this.mProgram, "vPosition");
        this.mColorLoc = GLES20.glGetUniformLocation(this.mProgram, "vColor");
        this.mProjectionMatrixLoc = GLES20.glGetUniformLocation(this.mProgram, "projectionMat");
        this.mModelviewMatrixLoc = GLES20.glGetUniformLocation(this.mProgram, "modelviewMat");
        Matrix.setIdentityM(this.mProjectionMatrix, 0);
        Matrix.setIdentityM(this.mModelviewMatrix, 0);
        if (this.mAntiAliasing) {
            int[] tex = new int[1];
            GLES20.glGenTextures(1, tex, 0);
            this.mTex = tex[0];
            GLES20.glBindTexture(3553, this.mTex);
            GLES20.glTexImage2D(3553, 0, 6408, 1, 1, 0, 6408, 5121, null);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLES20.glTexParameteri(3553, 10240, 9728);
            GLES20.glTexParameteri(3553, 10241, 9728);
            int[] fbo = new int[1];
            GLES20.glGenFramebuffers(1, fbo, 0);
            this.mFBO = fbo[0];
            GLES20.glBindFramebuffer(36160, this.mFBO);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.mTex, 0);
            GLES20.glBindTexture(3553, 0);
            int status = GLES20.glCheckFramebufferStatus(36160);
            if (status != 36053) {
                Log.v("Test", Integer.toString(status));
            }
            this.mMSAAProgram = getLinkedProgram(MSAAvsCode, MSAAfsCode);
            GLES20.glUseProgram(this.mMSAAProgram);
            this.mMSAATexLoc = GLES20.glGetUniformLocation(this.mMSAAProgram, "tex");
            this.mMSAAPosLoc = GLES20.glGetAttribLocation(this.mMSAAProgram, "vPosition");
            GLES20.glUniform1i(this.mMSAATexLoc, 0);
            float[] data = new float[]{1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f};
            ByteBuffer bb = ByteBuffer.allocateDirect(data.length * 4);
            bb.order(ByteOrder.nativeOrder());
            this.mMSAAva = bb.asFloatBuffer();
            this.mMSAAva.put(data);
            this.mMSAAva.position(0);
        }
        GLES20.glUseProgram(0);
        this.mGame.initGL(this);
    }

    public void onDrawFrame(GL10 unused) {
        if (this.mAntiAliasing) {
            GLES20.glBindFramebuffer(36160, this.mFBO);
            GLES20.glClear(16384);
            GLES20.glViewport(0, 0, this.mWidth * this.mMSAApower, this.mHeight * this.mMSAApower);
            GLES20.glUseProgram(this.mProgram);
            GLES20.glUniformMatrix4fv(this.mProjectionMatrixLoc, 1, false, this.mProjectionMatrix, 0);
            this.mGame.draw();
            GLES20.glBindFramebuffer(36160, 0);
            GLES20.glBindBuffer(34962, 0);
            GLES20.glViewport(0, 0, this.mWidth, this.mHeight);
            GLES20.glClear(16384);
            GLES20.glUseProgram(this.mMSAAProgram);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.mTex);
            GLES20.glGenerateMipmap(3553);
            GLES20.glEnableVertexAttribArray(this.mMSAAPosLoc);
            GLES20.glVertexAttribPointer(this.mMSAAPosLoc, 2, 5126, false, 0, this.mMSAAva);
            GLES20.glDrawArrays(5, 0, 4);
            return;
        }
        GLES20.glClear(16384);
        GLES20.glUseProgram(this.mProgram);
        this.mGame.draw();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        float ratio = ((float) width) / ((float) height);
        Matrix.frustumM(this.mProjectionMatrix, 0, (-1084227584) * ratio, 5.0f * ratio, -1084227584, 5.0f, 0.5f, 100.0f);
        GLES20.glUseProgram(this.mProgram);
        GLES20.glUniformMatrix4fv(this.mProjectionMatrixLoc, 1, false, this.mProjectionMatrix, 0);
        this.mWidth = width;
        this.mHeight = height;
        if (this.mAntiAliasing) {
            Log.v("Test", "Resolution set to " + Integer.toString(width) + "x" + Integer.toString(height));
            GLES20.glBindTexture(3553, this.mTex);
            int[] tex = new int[]{this.mTex};
            GLES20.glDeleteTextures(1, tex, 0);
            GLES20.glGenTextures(1, tex, 0);
            this.mTex = tex[0];
            GLES20.glBindTexture(3553, this.mTex);
            GLES20.glTexImage2D(3553, 0, 6408, this.mWidth * this.mMSAApower, this.mHeight * this.mMSAApower, 0, 6408, 5121, null);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLES20.glTexParameteri(3553, 10240, 9728);
            GLES20.glTexParameteri(3553, 10241, 9728);
            GLES20.glBindFramebuffer(36160, this.mFBO);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.mTex, 0);
            int status = GLES20.glCheckFramebufferStatus(36160);
            if (status != 36053) {
                Log.v("Test", Integer.toString(status));
            }
            GLES20.glBindFramebuffer(36160, 0);
            GLES20.glBindTexture(3553, 0);
        } else {
            GLES20.glViewport(0, 0, width, height);
        }
        this.mGame.resized();
    }

    public static int getCompiledShader(int type, String source) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, 35713, compiled, 0);
        if (compiled[0] != 0) {
            return shader;
        }
        Log.e("Test", "Could not compile shader " + (type == 35633 ? "VertexShader" : "FragmentShader") + ":");
        Log.e("Test", GLES20.glGetShaderInfoLog(shader));
        GLES20.glDeleteShader(shader);
        return 0;
    }

    public static int getLinkedProgram(String vsSource, String fsSource) {
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, getCompiledShader(35633, vsSource));
        GLES20.glAttachShader(program, getCompiledShader(35632, fsSource));
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, 35714, linkStatus, 0);
        if (linkStatus[0] == 1) {
            return program;
        }
        Log.e("Test", "Could not link program: ");
        Log.e("Test", GLES20.glGetProgramInfoLog(program));
        GLES20.glDeleteProgram(program);
        return 0;
    }

    public void pushModelview() {
        this.mModelviewStack.push(Arrays.copyOf(this.mModelviewMatrix, 16));
    }

    public void popModelview() {
        this.mModelviewMatrix = (float[]) this.mModelviewStack.pop();
        GLES20.glUniformMatrix4fv(this.mModelviewMatrixLoc, 1, false, this.mModelviewMatrix, 0);
    }

    public void translate(float x, float y, float z) {
        Matrix.translateM(this.mModelviewMatrix, 0, x, y, z);
        GLES20.glUniformMatrix4fv(this.mModelviewMatrixLoc, 1, false, this.mModelviewMatrix, 0);
    }

    public void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(this.mModelviewMatrix, 0, (float) Math.toDegrees((double) angle), x, y, z);
        GLES20.glUniformMatrix4fv(this.mModelviewMatrixLoc, 1, false, this.mModelviewMatrix, 0);
    }

    public void scale(float x, float y, float z) {
        Matrix.scaleM(this.mModelviewMatrix, 0, x, y, z);
        GLES20.glUniformMatrix4fv(this.mModelviewMatrixLoc, 1, false, this.mModelviewMatrix, 0);
    }

    public void setMVIdentity() {
        Matrix.setIdentityM(this.mModelviewMatrix, 0);
        GLES20.glUniformMatrix4fv(this.mModelviewMatrixLoc, 1, false, this.mModelviewMatrix, 0);
    }

    public void lookAt(float posX, float posY, float posZ, float targetX, float targetY, float targetZ) {
        Matrix.setLookAtM(this.mModelviewMatrix, 0, posX, posY, posZ, targetX, targetY, targetZ, 0.0f, 1.0f, 0.0f);
        GLES20.glUniformMatrix4fv(this.mModelviewMatrixLoc, 1, false, this.mModelviewMatrix, 0);
    }

    public void useOddColor() {
        float[] c = this.mGame.board().color(0);
        GLES20.glUniform4f(this.mColorLoc, c[0], c[1], c[2], c[3]);
    }

    public void useEvenColor() {
        float[] c = this.mGame.board().color(1);
        GLES20.glUniform4f(this.mColorLoc, c[0], c[1], c[2], c[3]);
    }

    public void useBTColor() {
        float[] c1 = this.mGame.board().color(0);
        float[] c2 = this.mGame.board().color(1);
        GLES20.glUniform4f(this.mColorLoc, (c1[0] + c2[0]) / 2.0f, (c1[1] + c2[1]) / 2.0f, (c1[2] + c2[2]) / 2.0f, (c1[3] + c2[3]) / 2.0f);
    }

    public void useRowColor() {
        float[] c = this.mGame.board().color(2);
        GLES20.glUniform4f(this.mColorLoc, c[0], c[1], c[2], c[3]);
    }

    public void useColor(float r, float g, float b) {
        GLES20.glUniform4f(this.mColorLoc, r, g, b, 1.0f);
    }

    public void useColor(ColorWrapper color) {
        GLES20.glUniform4f(this.mColorLoc, color.r(), color.g(), color.b(), color.a());
    }

    public int width() {
        return this.mWidth;
    }

    public int height() {
        return this.mHeight;
    }
}
