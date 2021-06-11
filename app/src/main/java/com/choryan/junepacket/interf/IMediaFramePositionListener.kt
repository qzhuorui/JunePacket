package com.choryan.junepacket.interf

/**
 * @author Secret
 * @since 2020/11/14
 */
interface IMediaFramePositionListener {

    fun onMediaPositionChange(presentationTimeUs:Long,contentPosition:Long,percentage:Float)

    fun onMediaEnd(lastFramePresentationTimeUs:Long)

    fun onMediaClipApply(mediaStartTimeUs:Long, mediaEndTimeUs:Long)

    fun onMediaPause()

    fun onVideoSizeChanged(width:Int, height:Int, unappliedRotationDegrees:Int, pixelWidthHeightRatio:Float)

    fun onPlayBackError()

}