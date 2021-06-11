package com.choryan.junepacket.util

import java.io.Closeable

/**
 * @author Secret
 * @since 2020/10/21
 */
object IOUtil {

    @JvmStatic
    fun closeStream(vararg stream: Closeable?) {
        stream.forEach {
            try {
                it?.close()
            } catch (ignored: Exception) {
            }
        }
    }

}