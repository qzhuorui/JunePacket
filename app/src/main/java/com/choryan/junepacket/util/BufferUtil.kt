package com.choryan.junepacket.util

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * @author Secret
 * @since 2020/10/13
 */
object BufferUtil {

    @JvmStatic
    fun getVertexBuffer(rotation:Int = 0): FloatBuffer {
        val vertexBuffer =
            ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        val vertexArray = rotateVertex(rotation,floatArrayOf(-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f))
        vertexBuffer.clear()
        vertexBuffer.put(vertexArray)
        vertexBuffer.position(0)
        return vertexBuffer
    }

    @JvmOverloads
    @JvmStatic
    fun getFragBuffer(rotation:Int = 0,flipVertical:Boolean = false,flipHorizontal:Boolean = false): FloatBuffer {
        val fragBuffer =
            ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        val fragArray = rotateVertex(rotation,floatArrayOf(0f, 0f, 1f, 0f, 0f, 1f, 1f, 1f),flipVertical,flipHorizontal)
        fragBuffer.clear()
        fragBuffer.put(fragArray)
        fragBuffer.position(0)
        return fragBuffer
    }

    private fun rotateVertex(rotation: Int, srcArray: FloatArray,flipVertical:Boolean = false,flipHorizontal:Boolean = false): FloatArray {
        var floats = when (rotation) {
            90 -> floatArrayOf(
                srcArray[4], srcArray[5],
                srcArray[0], srcArray[1],
                srcArray[6], srcArray[7],
                srcArray[2], srcArray[3]
            )
            180 -> floatArrayOf(
                srcArray[6], srcArray[7],
                srcArray[4], srcArray[5],
                srcArray[2], srcArray[3],
                srcArray[0], srcArray[1]
            )
            270 -> floatArrayOf(
                srcArray[2], srcArray[3],
                srcArray[6], srcArray[7],
                srcArray[0], srcArray[1],
                srcArray[4], srcArray[5]
            )
            else -> srcArray
        }
        if(flipVertical){
            floats = floatArrayOf(
                floats[0], flip(floats[1]),
                floats[2], flip(floats[3]),
                floats[4], flip(floats[5]),
                floats[6], flip(floats[7])
            )
        }
        if(flipHorizontal){
            floats = floatArrayOf(
                flip(floats[0]), floats[1],
                flip(floats[2]), floats[3],
                flip(floats[4]), floats[5],
                flip(floats[6]), floats[7]
            )
        }
        return floats
    }

    private fun flip(i: Float): Float {
        return if (i == 0.0f) {
            1.0f
        } else 0.0f
    }

}