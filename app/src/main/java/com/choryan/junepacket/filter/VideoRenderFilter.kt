package com.choryan.junepacket.filter

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES30
import com.choryan.junepacket.R
import com.choryan.junepacket.util.OpenGLUtil
import java.nio.FloatBuffer

/**
 * @author: ChoRyan Quan
 * @date: 6/11/21
 */
class VideoRenderFilter @JvmOverloads constructor(
    private val context: Context,
    private val vertexSource: Int = R.raw.video_render_vert,
    private val fragSource: Int = R.raw.video_render_frag
) {

    var mGLProgramId = -1

    private var vPosition = -1
    private var vCoord = -1
    private var vMatrix = -1
    private var vTexture = -1

    private var mOutputWidth: Int = 0
    private var mOutputHeight: Int = 0

    private var mFrameBufferTextures: IntArray? = null //纹理ID
    var mFrameBuffers: IntArray? = null //FBO

    fun onCreate() {
        //shader
        val vertexString = OpenGLUtil.getGLSLString(context, vertexSource)
        val fragString = OpenGLUtil.getGLSLString(context, fragSource)//OES texture

        mGLProgramId = OpenGLUtil.loadProgram(vertexString, fragString)

        //glsl variable
        vPosition = GLES30.glGetAttribLocation(mGLProgramId, "vPosition")//顶点坐标
        vCoord = GLES30.glGetAttribLocation(mGLProgramId, "vCoord")//纹理坐标
        vMatrix = GLES30.glGetUniformLocation(mGLProgramId, "vMatrix")
        vTexture = GLES30.glGetUniformLocation(mGLProgramId, "vTexture")//纹理ID
    }

    fun onSizeChange(outputWidth: Int, outputHeight: Int) {
        mOutputWidth = outputWidth
        mOutputHeight = outputHeight
        destroy()
        mFrameBuffers = IntArray(1)
        GLES30.glGenFramebuffers(mFrameBuffers!!.size, mFrameBuffers, 0)
        mFrameBufferTextures = IntArray(1)

        //create 2D texture
        OpenGLUtil.glGenTextures(GLES30.GL_TEXTURE_2D, mFrameBufferTextures!!)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mFrameBufferTextures!![0])
        GLES30.glTexImage2D(
            GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, mOutputWidth, mOutputHeight,
            0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null
        )

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBuffers!![0])
        GLES30.glFramebufferTexture2D(
            GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
            GLES30.GL_TEXTURE_2D, mFrameBufferTextures!![0], 0
        )

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
    }

    fun onDrawFrame(
        oesMatrix: FloatArray,
        textureId: Int,
        cubeBuffer: FloatBuffer,
        fragBuffer: FloatBuffer
    ): Int {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBuffers!![0])
        GLES30.glViewport(0, 0, mOutputWidth, mOutputHeight)
        GLES30.glClearColor(0f, 0f, 0f, 1f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES30.glUseProgram(mGLProgramId)
        cubeBuffer.position(0)
        GLES30.glVertexAttribPointer(vPosition, 2, GLES30.GL_FLOAT, false, 0, cubeBuffer)
        GLES30.glEnableVertexAttribArray(vPosition)

        fragBuffer.position(0)
        GLES30.glVertexAttribPointer(vCoord, 2, GLES30.GL_FLOAT, false, 0, fragBuffer)
        GLES30.glEnableVertexAttribArray(vCoord)

        GLES30.glUniformMatrix4fv(vMatrix, 1, false, oesMatrix, 0)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)

        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
        GLES30.glUniform1i(vTexture, 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)
        GLES30.glDisableVertexAttribArray(vPosition)
        GLES30.glDisableVertexAttribArray(vCoord)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)

        //sizeChange时做过绑定，所以此时可以直接return
        return mFrameBufferTextures!![0]
    }

    fun destroy() {
        if (mFrameBufferTextures != null) {
            GLES30.glDeleteTextures(1, mFrameBufferTextures, 0)
            mFrameBufferTextures = null
        }
        if (mFrameBuffers != null) {
            GLES30.glDeleteFramebuffers(1, mFrameBuffers, 0)
            mFrameBuffers = null
        }
    }

}