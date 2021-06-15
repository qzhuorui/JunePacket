package com.choryan.junepacket.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import com.choryan.junepacket.bean.VideoThumbnailFrame
import com.google.android.exoplayer2.C
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
        uri: String,
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

                        val jobList = LinkedList<Deferred<Bitmap?>>()//Deferred有返回值，job没有
                        val perFrameInterval = duration / frameCount

                        for (i in 0 until frameCount) {
                            var timeUs = perFrameInterval * i * 1000 + startPositionMs * 1000
                            if (timeUs > duration * 1000) {
                                timeUs = duration * 1000
                            }
                            jobList.add(async {
                                val src = mmr.getFrameAtTime(
                                    timeUs,
                                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                                )
                                if (null != src) {
                                    val dst = Bitmap.createScaledBitmap(
                                        src,
                                        frameWidth,
                                        frameHeight,
                                        true
                                    )
                                    if (dst != src) {
                                        if (!src.isRecycled) {
                                            src.recycle()
                                        }
                                    }

                                    if (frameHeight != displayHeight) {
                                        val transformMatrix = Matrix()
                                        val scale: Float

                                        val resizeBitmap = if (frameHeight > displayHeight) {
                                            val offset =
                                                ((frameHeight - displayHeight) / 2f).toInt()
                                            Bitmap.createBitmap(
                                                dst,
                                                0,
                                                offset,
                                                dst.width,
                                                frameHeight - offset,
                                                null,
                                                true
                                            )
                                        } else {
                                            val offset = (displayHeight - frameHeight) / 2f
                                            scale = displayHeight / frameHeight.toFloat()
                                            transformMatrix.postScale(scale, scale)
                                            transformMatrix.postTranslate(0f, offset)
                                            Bitmap.createBitmap(
                                                dst,
                                                0,
                                                0,
                                                dst.width,
                                                dst.height,
                                                transformMatrix,
                                                true
                                            )
                                        }
                                        if (resizeBitmap != dst) {
                                            if (!dst.isRecycled) {
                                                dst.recycle()
                                            }
                                        }
                                        return@async resizeBitmap
                                    }
                                    return@async dst
                                } else {
                                    return@async null
                                }
                            })
                        }
                        jobList.indices.forEach {
                            val bitmap = jobList[it].await()
                            emit(VideoThumbnailFrame(bitmap, it, 1))
                        }
                    }
                }
            } catch (e: Exception) {
                println("extractVideoThumbnail: " + e.message)
            } finally {
                mmr.release()
            }
        }
    }
}