package com.choryan.junepacket.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.choryan.junepacket.R
import com.choryan.junepacket.interf.IMediaFramePositionListener
import com.choryan.junepacket.player.ExoPlayerHelper
import com.choryan.junepacket.util.MediaUtil
import com.google.android.exoplayer2.C
import kotlinx.android.synthetic.main.activity_two_exoplayer.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author: ChoRyan Quan
 * @date: 6/10/21
 */
class ActivityExoPlayerTwo : AppCompatActivity(R.layout.activity_two_exoplayer),
    IMediaFramePositionListener {

    lateinit var mExoPlayerHelper: ExoPlayerHelper
    lateinit var mExoPlayerParams: ExoPlayerHelper.ExoPlayerHelperParam
    private var sourceDuration: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mExoPlayerHelper = ExoPlayerHelper.getInstance()
        lifecycle.addObserver(mExoPlayerHelper)
        init()
    }

    private fun init() {
        tv_content_path.setOnClickListener {
            lifecycleScope.launch {
                sourceDuration = withContext(Dispatchers.IO) {
                    MediaUtil.getMediaDuration(RESOURCE_PATH)
                }
                tv_content_path.text = "path: $RESOURCE_PATH ; duration: $sourceDuration"
                initParams()
                initView()
            }
        }
    }

    private fun initParams() {
        mExoPlayerParams = mExoPlayerHelper.ExoPlayerHelperParam()
        mExoPlayerParams.mediaUri = RESOURCE_PATH
        mExoPlayerParams.mediaDuration = sourceDuration?.toLong() ?: C.TIME_END_OF_SOURCE
        mExoPlayerParams.targetSurface = pv_exoplayer_two.provideDisplay()
        mExoPlayerHelper.initExoPlayer(this, mExoPlayerParams)
        mExoPlayerHelper.iMediaFramePositionListener = this
    }

    private fun initView() {
        view_video_position?.mediaDuration = mExoPlayerParams.mediaDuration
        view_video_position?.exoPlayerHelper = mExoPlayerHelper
        view_video_position?.setClipPosition(mExoPlayerParams.mediaUri, 0, C.TIME_END_OF_SOURCE)
    }

    override fun onStop() {
        super.onStop()
        mExoPlayerHelper.releaseExoPlayer()
    }

    companion object {
        private const val RESOURCE_PATH = "/sdcard/DCIM/Camera/20210519_132815.mp4"

    }

    override fun onMediaPositionChange(
        presentationTimeUs: Long,
        contentPosition: Long,
        percentage: Float
    ) {
        view_video_position?.progressPercent = percentage
    }

    override fun onMediaEnd(lastFramePresentationTimeUs: Long) {

    }

    override fun onMediaClipApply(mediaStartTimeUs: Long, mediaEndTimeUs: Long) {

    }

    override fun onMediaPause() {

    }

    override fun onVideoSizeChanged(
        width: Int,
        height: Int,
        unappliedRotationDegrees: Int,
        pixelWidthHeightRatio: Float
    ) {

    }

    override fun onPlayBackError() {

    }
}