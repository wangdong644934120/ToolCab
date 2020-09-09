package com.stit.toolcab.camera;

import android.opengl.GLES20;

import com.cloudwalk.gldisplay.OpenglHelper.RGBRendThread;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Administrator on 2020-08-26.
 */

public class RGB24GLProgram {
    private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nattribute vec4 vPosition;\nattribute vec2 a_texCoord;\nvarying vec2 tc;\nvoid main() {\ngl_Position = uMVPMatrix * vPosition;\ntc = a_texCoord;\n}\n";
    private static final String FRAGMENT_SHADER = "precision mediump float;\nuniform sampler2D tex_y;\nuniform sampler2D tex_u;\nuniform sampler2D tex_v;\nvarying vec2 tc;\nvoid main() {\nvec4 c = vec4((texture2D(tex_y, tc).r - 16./255.) * 1.164);\nvec4 U = vec4(texture2D(tex_u, tc).r - 128./255.);\nvec4 V = vec4(texture2D(tex_v, tc).r - 128./255.);\nc += V * vec4(1.596, -0.813, 0, 0);\nc += U * vec4(0, -0.392, 2.017, 0);\nc.a = 1.0;\ngl_FragColor = c;\n}\n";
    private static final String VERTEX_SHADER1 = "uniform mat4 uMVPMatrix;\nattribute vec4 vPosition;\nattribute vec2 a_texCoord;\nvarying vec2 tc;\nvoid main() {\ngl_Position = uMVPMatrix * vPosition;\ntc = a_texCoord;\n}\n";
    private static final String FRAGMENT_SHADER1 = "precision mediump float;\nuniform sampler2D tex_y;\nvarying vec2 tc;\nvoid main() {\ngl_FragColor = texture2D(tex_y,tc);\n}\n";
    private float[] mViewMatrix = new float[16];
    private int mVPMatrixHandle = -1;
    static float[] s0Matrix = new float[]{1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
    static float[] s0MirrorMatrix = new float[]{-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
    static float[] s90Matrix = new float[]{0.0F, -1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
    static float[] s90MirrorMatrix = new float[]{0.0F, -1.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
    static float[] s180Matrix = new float[]{-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
    static float[] s180MirrorMatrix = new float[]{1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
    static float[] s270Matrix = new float[]{0.0F, 1.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
    static float[] s270MirrorMatrix = new float[]{0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
    static float[] squareVertices = new float[]{-1.0F, -1.0F, 1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F};
    static float[] squareVertices1 = new float[]{-1.0F, 0.0F, 0.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F};
    static float[] squareVertices2 = new float[]{0.0F, -1.0F, 1.0F, -1.0F, 0.0F, 0.0F, 1.0F, 0.0F};
    static float[] squareVertices3 = new float[]{-1.0F, -1.0F, 0.0F, -1.0F, -1.0F, 0.0F, 0.0F, 0.0F};
    static float[] squareVertices4 = new float[]{0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
    private static float[] coordVertices = new float[]{0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F};
    public final int mWinPosition;
    private int mGLProgram;
    private int mGLTextureI;
    private int mGLTextureII;
    private int mGLTextureIII;
    private int mGLIndexI;
    private int mGLIndexII;
    private int mGLTIndexIII;
    private float[] mGLVertices;
    private int mPositionHandle = -1;
    private int mCoordHandle = -1;
    private int mYhandle = -1;
    private int mUhandle = -1;
    private int mVhandle = -1;
    private int mYtid = -1;
    private int mUtid = -1;
    private int mVtid = -1;
    private ByteBuffer mVerticeBuffer;
    private ByteBuffer mCoordBuffer;
    private int mVideoWidth = -1;
    private int mVideoHeight = -1;
    private boolean isProgBuilt = false;

    public RGB24GLProgram(int position) {
        if(position >= 0 && position <= 4) {
            this.mWinPosition = position;
            this.setup();
        } else {
            throw new RuntimeException("Index can only be 0 to 4");
        }
    }

    private void setup() {
        switch(this.mWinPosition) {
            case 0:
            default:
                this.mGLVertices = squareVertices;
                this.mGLTextureI = '蓀';
                this.mGLTextureII = '蓁';
                this.mGLTextureIII = '蓂';
                this.mGLIndexI = 0;
                this.mGLIndexII = 1;
                this.mGLTIndexIII = 2;
                break;
            case 1:
                this.mGLVertices = squareVertices1;
                this.mGLTextureI = '蓀';
                this.mGLTextureII = '蓁';
                this.mGLTextureIII = '蓂';
                this.mGLIndexI = 0;
                this.mGLIndexII = 1;
                this.mGLTIndexIII = 2;
                break;
            case 2:
                this.mGLVertices = squareVertices2;
                this.mGLTextureI = '蓃';
                this.mGLTextureII = '蓄';
                this.mGLTextureIII = '蓅';
                this.mGLIndexI = 3;
                this.mGLIndexII = 4;
                this.mGLTIndexIII = 5;
                break;
            case 3:
                this.mGLVertices = squareVertices3;
                this.mGLTextureI = '蓆';
                this.mGLTextureII = '蓇';
                this.mGLTextureIII = '蓈';
                this.mGLIndexI = 6;
                this.mGLIndexII = 7;
                this.mGLTIndexIII = 8;
                break;
            case 4:
                this.mGLVertices = squareVertices4;
                this.mGLTextureI = '蓉';
                this.mGLTextureII = '蓊';
                this.mGLTextureIII = '蓋';
                this.mGLIndexI = 9;
                this.mGLIndexII = 10;
                this.mGLTIndexIII = 11;
        }

    }

    public boolean isProgramBuilt() {
        return this.isProgBuilt;
    }

    public void buildProgram() {
        if(this.mGLProgram <= 0) {
            this.mGLProgram = this.createProgram("uniform mat4 uMVPMatrix;\nattribute vec4 vPosition;\nattribute vec2 a_texCoord;\nvarying vec2 tc;\nvoid main() {\ngl_Position = uMVPMatrix * vPosition;\ntc = a_texCoord;\n}\n", "precision mediump float;\nuniform sampler2D tex_y;\nvarying vec2 tc;\nvoid main() {\ngl_FragColor = texture2D(tex_y,tc);\n}\n");
        }

        try {
            this.mVPMatrixHandle = GLES20.glGetUniformLocation(this.mGLProgram, "uMVPMatrix");
            this.mPositionHandle = GLES20.glGetAttribLocation(this.mGLProgram, "vPosition");
            RGBRendThread.ShaderUtil.checkGlError("glGetAttribLocation vPosition");
            if(this.mPositionHandle == -1) {
                throw new RuntimeException("Could not get attribute location for vPosition");
            }

            this.mCoordHandle = GLES20.glGetAttribLocation(this.mGLProgram, "a_texCoord");
            RGBRendThread.ShaderUtil.checkGlError("glGetAttribLocation a_texCoord");
            if(this.mCoordHandle == -1) {
                throw new RuntimeException("Could not get attribute location for a_texCoord");
            }

            this.mYhandle = GLES20.glGetUniformLocation(this.mGLProgram, "tex_y");
            RGBRendThread.ShaderUtil.checkGlError("glGetUniformLocation tex_y");
            if(this.mYhandle == -1) {
                throw new RuntimeException("Could not get uniform location for tex_y");
            }

            this.isProgBuilt = true;
        } catch (RuntimeException var2) {
            var2.printStackTrace();
        }

    }

    public void releaseProgram() {
        GLES20.glUseProgram(0);
        if(this.mGLProgram >= 0) {
            GLES20.glDeleteProgram(this.mGLProgram);
        }

        this.mGLProgram = -1;
        this.isProgBuilt = false;
    }

    public void buildTextures(Buffer y, Buffer u, Buffer v, int width, int height) {
        boolean videoSizeChanged = width != this.mVideoWidth || height != this.mVideoHeight;
        if(videoSizeChanged) {
            this.mVideoWidth = width;
            this.mVideoHeight = height;
        }

        int[] textures;
        if(this.mYtid < 0 || videoSizeChanged) {
            if(this.mYtid >= 0) {
                GLES20.glDeleteTextures(1, new int[]{this.mYtid}, 0);
                this.checkGlError("glDeleteTextures");
            }

            textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            this.checkGlError("glGenTextures");
            this.mYtid = textures[0];
        }

        GLES20.glBindTexture(3553, this.mYtid);
        this.checkGlError("glBindTexture");
        GLES20.glTexImage2D(3553, 0, 6409, this.mVideoWidth, this.mVideoHeight, 0, 6409, 5121, y);
        this.checkGlError("glTexImage2D");
        GLES20.glTexParameterf(3553, 10241, 9728.0F);
        GLES20.glTexParameterf(3553, 10240, 9729.0F);
        GLES20.glTexParameteri(3553, 10242, '脯');
        GLES20.glTexParameteri(3553, 10243, '脯');
        if(this.mUtid < 0 || videoSizeChanged) {
            if(this.mUtid >= 0) {
                GLES20.glDeleteTextures(1, new int[]{this.mUtid}, 0);
                this.checkGlError("glDeleteTextures");
            }

            textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            this.checkGlError("glGenTextures");
            this.mUtid = textures[0];
        }

        GLES20.glBindTexture(3553, this.mUtid);
        GLES20.glTexImage2D(3553, 0, 6409, this.mVideoWidth / 2, this.mVideoHeight / 2, 0, 6409, 5121, u);
        GLES20.glTexParameterf(3553, 10241, 9728.0F);
        GLES20.glTexParameterf(3553, 10240, 9729.0F);
        GLES20.glTexParameteri(3553, 10242, '脯');
        GLES20.glTexParameteri(3553, 10243, '脯');
        if(this.mVtid < 0 || videoSizeChanged) {
            if(this.mVtid >= 0) {
                GLES20.glDeleteTextures(1, new int[]{this.mVtid}, 0);
                this.checkGlError("glDeleteTextures");
            }

            textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            this.checkGlError("glGenTextures");
            this.mVtid = textures[0];
        }

        GLES20.glBindTexture(3553, this.mVtid);
        GLES20.glTexImage2D(3553, 0, 6409, this.mVideoWidth / 2, this.mVideoHeight / 2, 0, 6409, 5121, v);
        GLES20.glTexParameterf(3553, 10241, 9728.0F);
        GLES20.glTexParameterf(3553, 10240, 9729.0F);
        GLES20.glTexParameteri(3553, 10242, '脯');
        GLES20.glTexParameteri(3553, 10243, '脯');
    }

    public void buildTextures(Buffer rgbBuffer, int width, int height, boolean hasAlpha) {
        boolean videoSizeChanged = width != this.mVideoWidth || height != this.mVideoHeight;
        if(videoSizeChanged) {
            this.mVideoWidth = width;
            this.mVideoHeight = height;
        }

        if(this.mYtid < 0 || videoSizeChanged) {
            if(this.mYtid >= 0) {
                GLES20.glDeleteTextures(1, new int[]{this.mYtid}, 0);
                RGBRendThread.ShaderUtil.checkGlError("glDeleteTextures");
            }

            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            RGBRendThread.ShaderUtil.checkGlError("glGenTextures");
            this.mYtid = textures[0];
        }

        GLES20.glBindTexture(3553, this.mYtid);
        RGBRendThread.ShaderUtil.checkGlError("glBindTexture");
        GLES20.glTexImage2D(3553, 0, hasAlpha?6408:6407, this.mVideoWidth, this.mVideoHeight, 0, hasAlpha?6408:6407, 5121, rgbBuffer);
        RGBRendThread.ShaderUtil.checkGlError("glTexImage2D");
        GLES20.glTexParameterf(3553, 10241, 9728.0F);
        GLES20.glTexParameterf(3553, 10240, 9729.0F);
        GLES20.glTexParameteri(3553, 10242, '脯');
        GLES20.glTexParameteri(3553, 10243, '脯');
    }

    public void setDisplayOrientation(int degrees, boolean needMirror) {
        if(degrees == 0) {
            if(needMirror) {
                this.mViewMatrix = s0MirrorMatrix;
            } else {
                this.mViewMatrix = s0Matrix;
            }
        } else if(degrees == 90) {
            if(needMirror) {
                this.mViewMatrix = s90MirrorMatrix;
            } else {
                this.mViewMatrix = s90Matrix;
            }
        } else if(degrees == 180) {
            if(needMirror) {
                this.mViewMatrix = s180MirrorMatrix;
            } else {
                this.mViewMatrix = s180Matrix;
            }
        } else if(degrees == 270) {
            if(needMirror) {
                this.mViewMatrix = s270MirrorMatrix;
            } else {
                this.mViewMatrix = s270Matrix;
            }
        }

    }

    public void drawFrame() {
        if(null != this.mVerticeBuffer) {
            GLES20.glUseProgram(this.mGLProgram);
            this.checkGlError("glUseProgram");
            GLES20.glUniformMatrix4fv(this.mVPMatrixHandle, 1, false, this.mViewMatrix, 0);
            GLES20.glVertexAttribPointer(this.mPositionHandle, 2, 5126, false, 8, this.mVerticeBuffer);
            this.checkGlError("glVertexAttribPointer mPositionHandle");
            GLES20.glEnableVertexAttribArray(this.mPositionHandle);
            GLES20.glVertexAttribPointer(this.mCoordHandle, 2, 5126, false, 8, this.mCoordBuffer);
            this.checkGlError("glVertexAttribPointer maTextureHandle");
            GLES20.glEnableVertexAttribArray(this.mCoordHandle);
            GLES20.glActiveTexture(this.mGLTextureI);
            GLES20.glBindTexture(3553, this.mYtid);
            GLES20.glUniform1i(this.mYhandle, this.mGLIndexI);
            GLES20.glDrawArrays(5, 0, 4);
            GLES20.glFinish();
            GLES20.glDisableVertexAttribArray(this.mPositionHandle);
            GLES20.glDisableVertexAttribArray(this.mCoordHandle);
        }
    }

    public int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = this.loadShader('謱', vertexSource);
        int pixelShader = this.loadShader('謰', fragmentSource);
        int program = GLES20.glCreateProgram();
        if(program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            this.checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            this.checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, '讂', linkStatus, 0);
            if(linkStatus[0] != 1) {
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }

        return program;
    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if(shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, '讁', compiled, 0);
            if(compiled[0] == 0) {
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }

        return shader;
    }

    void createBuffers(float[] vert) {
        this.mVerticeBuffer = ByteBuffer.allocateDirect(vert.length * 4);
        this.mVerticeBuffer.order(ByteOrder.nativeOrder());
        this.mVerticeBuffer.asFloatBuffer().put(vert);
        this.mVerticeBuffer.position(0);
        if(this.mCoordBuffer == null) {
            this.mCoordBuffer = ByteBuffer.allocateDirect(coordVertices.length * 4);
            this.mCoordBuffer.order(ByteOrder.nativeOrder());
            this.mCoordBuffer.asFloatBuffer().put(coordVertices);
            this.mCoordBuffer.position(0);
        }

    }

    private void checkGlError(String op) {
        while(GLES20.glGetError() != 0) {
            ;
        }

    }
}
