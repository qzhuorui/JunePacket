package com.choryan.junepacket.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.choryan.junepacket.R
import com.choryan.junepacket.player.ExoPlayerHelper
import kotlinx.android.synthetic.main.activity_one_exoplayer.*

/**
 * @author: ChoRyan Quan
 * @date: 6/10/21
 */
class ActivityExoPlayerOne : AppCompatActivity(R.layout.activity_one_exoplayer) {

    lateinit var mExoPlayerHelper: ExoPlayerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mExoPlayerHelper = ExoPlayerHelper.getInstance()
        val exoPlayerParam = mExoPlayerHelper.ExoPlayerHelperParam()
        exoPlayerParam.mediaUri = "/sdcard/DCIM/Camera/20210519_132815.mp4"
        mExoPlayerHelper.initExoPlayer(this, exoPlayerParam)

        pv_exoplayer_pure.player = mExoPlayerHelper.mExoPlayer

    }
}