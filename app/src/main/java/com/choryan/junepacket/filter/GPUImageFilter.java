package com.choryan.junepacket.filter;

import android.opengl.GLES30;

import com.choryan.junepacket.util.OpenGLUtil;

import java.nio.FloatBuffer;
import java.util.LinkedList;

/**
 * @author: ChoRyan Quan
 * @date: 6/11/21
 */
public class GPUImageFilter {


    private final String vertexShader;
    private final String fragmentShader;

    private int glProgId;
    private int glAttribPosition;
    private int glUniformTexture;
    private int glAttribTextureCoordinate;

    private boolean isInitialized;

    private int outputWidth;
    private int outputHeight;

    private int[] frameBuffers;
    private int[] frameBufferTextures;

    private int selfFboId;
    private int selfFboTextureId;


    public GPUImageFilter() {
        this(NO_FILTER_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);
    }

    public GPUImageFilter(final String vertexShader, final String fragmentShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
    }

    public void ifNeedInit() {
        if (!isInitialized) {
            init();
        }
    }

    private void init() {
        onInit();
        onInitialized();
    }

    /**
     * @description link program & get variable
     * @author ChoRyan Quan
     * @time 6/11/21 4:14 PM
     */
    public void onInit() {
        glProgId = OpenGLUtil.loadProgram(vertexShader, fragmentShader);
        glAttribPosition = GLES30.glGetAttribLocation(glProgId, "position");
        glUniformTexture = GLES30.glGetUniformLocation(glProgId, "inputImageTexture");
        glAttribTextureCoordinate = GLES30.glGetAttribLocation(glProgId, "inputTextureCoordinate");
        isInitialized = true;
    }

    public void onInitialized() {
    }

    public void onOutputSizeChanged(final int width, final int height) {
        outputWidth = width;
        outputHeight = height;
        glGenFrameBuffer();
    }

    /**
     * @description fboId & fboTextureId
     * @author ChoRyan Quan
     * @time 6/11/21 4:16 PM
     */
    private void glGenFrameBuffer() {
        destroyFrameBuffers();
        frameBuffers = new int[1];
        frameBufferTextures = new int[1];
        GLES30.glGenFramebuffers(1, frameBuffers, 0);
        OpenGLUtil.glGenTextures(GLES30.GL_TEXTURE_2D, frameBufferTextures);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, frameBufferTextures[0]);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA,
                getOutputWidth(), getOutputHeight(), 0,
                GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffers[0]);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_TEXTURE_2D, frameBufferTextures[0], 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

        selfFboId = frameBuffers[0];
        selfFboTextureId = frameBufferTextures[0];
    }

    public void onDraw(int fboTextureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        GLES30.glUseProgram(glProgId);
        cubeBuffer.position(0);
        GLES30.glVertexAttribPointer(glAttribPosition, 2, GLES30.GL_FLOAT, false, 0, cubeBuffer);
        GLES30.glEnableVertexAttribArray(glAttribPosition);
        textureBuffer.position(0);
        GLES30.glVertexAttribPointer(glAttribTextureCoordinate, 2, GLES30.GL_FLOAT, false, 0,
                textureBuffer);
        GLES30.glEnableVertexAttribArray(glAttribTextureCoordinate);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, fboTextureId);
        GLES30.glUniform1i(glUniformTexture, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        GLES30.glDisableVertexAttribArray(glAttribPosition);
        GLES30.glDisableVertexAttribArray(glAttribTextureCoordinate);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
    }


    public void destroyFrameBuffers() {
        if (frameBufferTextures != null) {
            GLES30.glDeleteTextures(frameBufferTextures.length, frameBufferTextures, 0);
            frameBufferTextures = null;
        }
        if (frameBuffers != null) {
            GLES30.glDeleteFramebuffers(frameBuffers.length, frameBuffers, 0);
            frameBuffers = null;
        }
    }

    public int getOutputWidth() {
        return outputWidth;
    }

    public int getOutputHeight() {
        return outputHeight;
    }

    public int getSelfFboTextureId() {
        return selfFboTextureId;
    }

    public int getSelfFboId() {
        return selfFboId;
    }

    public static final String NO_FILTER_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";
    public static final String NO_FILTER_FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform sampler2D inputImageTexture;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}";
}
