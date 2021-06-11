package com.choryan.junepacket.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.choryan.junepacket.MainActivity
import com.choryan.junepacket.R
import com.choryan.junepacket.player.ExoPlayerHelper
import com.choryan.junepacket.util.MediaUtil
import kotlinx.android.synthetic.main.activity_two_exoplayer.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author: ChoRyan Quan
 * @date: 6/10/21
 */
class ActivityExoPlayerTwo : AppCompatActivity(R.layout.activity_two_exoplayer) {

    lateinit var mExoPlayerHelper: ExoPlayerHelper
    private var sourceDuration: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mExoPlayerHelper = ExoPlayerHelper.getInstance()
        lifecycle.addObserver(mExoPlayerHelper)
        initClick()
        val exoPlayerParam = buildParams()
        mExoPlayerHelper.initExoPlayer(this, exoPlayerParam)
    }

    private fun buildParams(): ExoPlayerHelper.ExoPlayerHelperParam {
        val exoPlayerParam = mExoPlayerHelper.ExoPlayerHelperParam()
        exoPlayerParam.mediaUri = RESOURCE_PATH
        exoPlayerParam.mediaDuration = sourceDuration?.toLong() ?: 0
        exoPlayerParam.targetSurface = pv_exoplayer_two.provideDisplay()
        return exoPlayerParam
    }

    private fun initClick() {
        tv_content_path.setOnClickListener {
            lifecycleScope.launch {
                sourceDuration = withContext(Dispatchers.IO) {
                    MediaUtil.getMediaDuration(RESOURCE_PATH)
                }
                tv_content_path.text = "path: $RESOURCE_PATH ; duration: $sourceDuration"
            }
        }
    }

    companion object {
        private const val RESOURCE_PATH = "/sdcard/DCIM/Camera/20210519_132815.mp4"

    }
}