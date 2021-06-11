package com.choryan.junepacket.view

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.C
import kotlinx.coroutines.launch
import java.util.jar.Attributes

/**
 * @author: ChoRyan Quan
 * @date: 6/11/21
 */
class ViewEffectPosition @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatSeekBar(context, attrs, defStyleAttr), SeekBar.OnSeekBarChangeListener {

    var mediaDuration = 0L
    var mediaStartPosition: Long = 0L
    var mediaEndPosition: Long = 0L

    init {
        max = 100
        setOnSeekBarChangeListener(this)
    }

    fun setClipPosition(mediaUri: String, startPositionUs: Long, endPositionUs: Long) {
        if (startPositionUs == 0L && endPositionUs == 0L) {
            return
        }
        mediaDuration = endPositionUs - startPositionUs
        this.mediaStartPosition = startPositionUs
        this.mediaEndPosition = endPositionUs
        post {
            if (context is LifecycleOwner) {
                (context as LifecycleOwner).lifecycleScope.launch {

                }
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

}