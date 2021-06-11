package com.choryan.junepacket.util

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES30
import android.util.Log
import androidx.annotation.WorkerThread
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * @author Secret
 * @since 2020/2/22
 */
object OpenGLUtil {

    interface ISaveVideoAndSavePhotoListener {
        @WorkerThread
        fun hasVideoFirstFrame(videoFirstFrame: Bitmap)
    }

    @JvmStatic
    fun testBitmap(width: Int, height: Int, inter: ISaveVideoAndSavePhotoListener) {
        val byteBuffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder())
        GLES30.glReadPixels(
            0,
            0,
            width,
            height,
            GLES30.GL_RGBA,
            GLES30.GL_UNSIGNED_BYTE,
            byteBuffer
        )
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(byteBuffer)
        inter.hasVideoFirstFrame(bitmap)
        bitmap.recycle()
    }

    private fun compileShader(strSource: String?, iType: Int): Int {
        val compiled = IntArray(1)
        val iShader = GLES30.glCreateShader(iType)
        GLES30.glShaderSource(iShader, strSource)
        GLES30.glCompileShader(iShader)
        GLES30.glGetShaderiv(iShader, GLES30.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            Log.e(
                "CJY==Load Shader Failed",
                """
                    Compilation
                    ${GLES30.glGetShaderInfoLog(iShader)}
                    """.trimIndent()
            )
            return 0
        }
        return iShader
    }

    @JvmStatic
    fun loadProgram(strVSource: String?, strFSource: String?): Int {
        val link = IntArray(1)
        val iVShader: Int = compileShader(strVSource, GLES30.GL_VERTEX_SHADER)
        if (iVShader == 0) {
            return 0
        }
        val iFShader: Int = compileShader(strFSource, GLES30.GL_FRAGMENT_SHADER)
        if (iFShader == 0) {
            return 0
        }
        val programId: Int = GLES30.glCreateProgram()
        GLES30.glAttachShader(programId, iVShader)
        GLES30.glAttachShader(programId, iFShader)
        GLES30.glLinkProgram(programId)
        GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, link, 0)
        if (link[0] <= 0) {
            return 0
        }
        GLES30.glDeleteShader(iVShader)
        GLES30.glDeleteShader(iFShader)
        return programId
    }

    fun getGLSLString(context: Context, resourceId: Int): String {
        val reader = context.resources.openRawResource(resourceId).reader()
        val string = reader.readText()
        IOUtil.closeStream(reader)
        return string
    }

    fun getShaderStatus(shaderIndex: Int) {
        val statusArray = intArrayOf(1)
        GLES30.glGetShaderiv(shaderIndex, GLES30.GL_COMPILE_STATUS, statusArray, 0)
        if (statusArray[0] != GLES30.GL_TRUE) {
            throw RuntimeException(GLES30.glGetShaderInfoLog(shaderIndex))
        }
    }

    fun getProgramStatus(programId: Int) {
        val statusArray = intArrayOf(1)
        GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, statusArray, 0)
        if (statusArray[0] != GLES30.GL_TRUE) {
            throw RuntimeException(GLES30.glGetProgramInfoLog(programId))
        }
    }

    @JvmStatic
    fun glGenTextures(target: Int, textures: IntArray) {
        GLES30.glGenTextures(textures.size, textures, 0)
        textures.forEach {
            GLES30.glBindTexture(target, it)
            GLES30.glTexParameteri(
                target,
                GLES30.GL_TEXTURE_MAG_FILTER,
                GLES30.GL_LINEAR
            )
            GLES30.glTexParameteri(
                target,
                GLES30.GL_TEXTURE_MIN_FILTER,
                GLES30.GL_NEAREST
            )
            GLES30.glTexParameteri(
                target,
                GLES30.GL_TEXTURE_WRAP_S,
                GLES30.GL_CLAMP_TO_EDGE
            )
            GLES30.glTexParameteri(
                target,
                GLES30.GL_TEXTURE_WRAP_T,
                GLES30.GL_CLAMP_TO_EDGE
            )
            GLES30.glBindTexture(target, 0)
        }
    }

    /**
     * Checks if OpenGL ES 2.0 is supported on the current device.
     *
     * @param context the context
     * @return true, if successful
     */
    fun supportsOpenGLES2(context: Context): Boolean {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        return configurationInfo.reqGlEsVersion >= 0x00020000
    }


}