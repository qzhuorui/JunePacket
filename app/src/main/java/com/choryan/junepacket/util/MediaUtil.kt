package com.choryan.junepacket.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import com.choryan.junepacket.bean.VideoThumbnailFrame
import com.google.android.exoplayer2.C
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.*

/**
 * @author: ChoRyan Quan
 * @date: 6/10/21
 */
object MediaUtil {

    fun getMediaDuration(resourcePath: String): String? {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(resourcePath)
        //毫秒
        return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    }

    fun extractVideoThumbnail(
        context: Context, uri: String,
        displayWidth: Int, displayHeight: Int, frameCount: Int,
        startPositionMs: Long, endPositionMs: Long
    ): Flow<VideoThumbnailFrame> {
        return flow {
            val mmr = MediaMetadataRetriever()
            try {
                withContext(Dispatchers.IO) {
                    mmr.setDataSource(uri)
                    var duration =
                        mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
                            ?: 0L
                    if (duration != 0L) {
                        if (endPositionMs == C.TIME_END_OF_SOURCE) {
                            duration -= startPositionMs
                        } else {
                            duration = endPositionMs - startPositionMs
                        }
                    }
                    if (duration != 0L) {
                        val width =
                            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!
                                .toInt()
                        val height =
                            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!
                                .toInt()
                        val frameRatio = height / width.toFloat()

                        val frameWidth = (displayWidth / frameCount.toFloat()).toInt()
                        val frameHeight = (frameWidth * frameRatio).toInt()

                        emit(VideoThumbnailFrame(null, frameCount, 0))

                        val jobList = LinkedList<Deferred<Bitmap?>>()



                    }
                }
            }
        }
    }
}