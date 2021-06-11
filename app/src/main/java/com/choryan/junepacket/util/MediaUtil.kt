package com.choryan.junepacket.util

import android.media.MediaMetadataRetriever

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
}