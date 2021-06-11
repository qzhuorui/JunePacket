package com.choryan.junepacket.view

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import com.choryan.junepacket.filter.VOOutputFilter
import com.choryan.junepacket.filter.VideoRenderFilter
import com.choryan.junepacket.util.BufferUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author: ChoRyan Quan
 * @date: 6/10/21
 */
class VideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs), GLSurfaceView.Renderer {

    private val mTextureId: Int = 0
    private var mSurfaceTexture: SurfaceTexture? = null //纹理ID和display的承接

    private var mSurface: Surface? = null

    private lateinit var inputBaseFilter: VideoRenderFilter
    private var outputFilter = VOOutputFilter()

    private var oesMatrix = FloatArray(16)
    private var cubeBuffer = BufferUtil.getVertexBuffer()
    private var fragBuffer = BufferUtil.getFragBuffer()


    init {
        setEGLContextClientVersion(3)
        mSurfaceTexture = SurfaceTexture(mTextureId)
        mSurfaceTexture?.setOnFrameAvailableListener {
            requestRender()
        }
        mSurface = Surface(mSurfaceTexture)
        setRenderer(this)
        renderMode = RENDERMODE_WHEN_DIRTY
        inputBaseFilter = VideoRenderFilter(context)
    }

    fun provideDisplay(): Surface {
        return mSurface!!
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d(TAG, "onSurfaceCreated: ")
        inputBaseFilter.onCreate()
        outputFilter.ifNeedInit()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceChanged: ")
        if (width > 0 && height > 0) {
            inputBaseFilter.onSizeChange(width, height)
            outputFilter.onOutputSizeChanged(width, height)
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glViewport(0, 0, width, height)
        GLES30.glClearColor(1f, 1f, 1f, 0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        drawOther()
    }

    private fun drawOther() {
        mSurfaceTexture?.updateTexImage()
        mSurfaceTexture?.getTransformMatrix(oesMatrix)
        val screenTextureId = inputBaseFilter.onDrawFrame(
            oesMatrix,
            mTextureId,
            cubeBuffer,
            fragBuffer
        )

        if (screenTextureId != -1) {
            GLES30.glViewport(0, 0, width, height)
            outputFilter.onDraw(
                screenTextureId,
                cubeBuffer,
                fragBuffer
            )
        }
    }

    companion object {
        private const val TAG = "VideoView"
    }
}