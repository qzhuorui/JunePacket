package com.choryan.junepacket.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.choryan.junepacket.bean.VideoThumbnailFrame
import com.choryan.junepacket.extensions.ExtensionView.dp
import com.choryan.junepacket.player.ExoPlayerHelper
import com.choryan.junepacket.util.MediaUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt

/**
 * @author: ChoRyan Quan
 * @date: 6/11/21
 */
class ViewEffectPosition @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatSeekBar(context, attrs, defStyleAttr), SeekBar.OnSeekBarChangeListener {

    var progressPercent = 0f
        set(value) {
            field = value
            progress = (value * 100).roundToInt()
            postInvalidate()
        }

    var exoPlayerHelper: ExoPlayerHelper? = null

    var mediaDuration = 0L
    var mediaStartPosition: Long = 0L
    var mediaEndPosition: Long = 0L

    private val progressRoundRectWidth = 4f.dp
    private val progressRoundCornerRadius = 2f.dp
    private var frameVerticalOffset = 3f.dp
    private var frameRoundRadius = 2f.dp

    private var thumbnailList: ArrayList<Bitmap?>? = null
    private var perFrameWidth = 0f

    private val effectPositionPaint: Paint by lazy {
        val paint = Paint()
        paint.isDither = true
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint
    }

    private val bitmapPaint: Paint by lazy {
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.isFilterBitmap = true
        paint
    }

    private val clipPath by lazy {
        val clipPath = Path()
        clipPath.addRoundRect(
            progressRoundRectWidth,
            frameVerticalOffset,
            width.toFloat() - progressRoundRectWidth,
            height - frameVerticalOffset,
            frameRoundRadius,
            frameRoundRadius,
            Path.Direction.CCW
        )
        clipPath
    }

    init {
        max = 100
        setOnSeekBarChangeListener(this)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.clipPath(clipPath)
        drawVideoThumbnail(canvas)
        canvas.restore()
        drawProgressMark(canvas)
    }

    private fun drawVideoThumbnail(canvas: Canvas) {
        thumbnailList?.indices?.forEach {
            val bitmap = thumbnailList?.get(it)
            if (null != bitmap && !bitmap.isRecycled) {
                canvas.drawBitmap(
                    bitmap,
                    it * perFrameWidth + progressRoundRectWidth,
                    frameVerticalOffset,
                    bitmapPaint
                )
            }
        }
    }

    private fun drawProgressMark(canvas: Canvas) {
        var progressMarkLeft = if (progressPercent >= 1f) {
            width - progressRoundRectWidth
        } else {
            progressPercent * width
        }
        if (progressPercent * width + progressRoundRectWidth > width) {
            progressMarkLeft = width - progressRoundRectWidth
        }
        effectPositionPaint.color = Color.WHITE
        canvas.drawRoundRect(
            progressMarkLeft,
            0f,
            progressMarkLeft + progressRoundRectWidth,
            height.toFloat(),
            progressRoundCornerRadius,
            progressRoundCornerRadius,
            effectPositionPaint
        )
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
                    MediaUtil.extractVideoThumbnail(
                        mediaUri,
                        (width - progressRoundRectWidth * 2).toInt(),
                        (height - frameVerticalOffset * 2).toInt(),
                        10,
                        startPositionUs, endPositionUs
                    ).flowOn(Dispatchers.IO).collect {
                        addFrame(it)
                    }
                }
            }
        }
    }

    private fun addFrame(videoThumbnailFrame: VideoThumbnailFrame) {
        if (videoThumbnailFrame.flag == 0) {
            clearThumbnail()
            thumbnailList = ArrayList(videoThumbnailFrame.position)
            for (i in 0 until videoThumbnailFrame.position) {
                thumbnailList?.add(null)
            }
            perFrameWidth =
                (width.toFloat() - progressRoundRectWidth * 2) / videoThumbnailFrame.position
        } else if (videoThumbnailFrame.flag == 1) {
            thumbnailList?.set(videoThumbnailFrame.position, videoThumbnailFrame.frame)
            invalidate()
        }
    }

    private fun clearThumbnail() {
        thumbnailList?.forEach { bitmap ->
            bitmap?.let {
                if (!it.isRecycled) {
                    it.recycle()
                }
            }
        }
        thumbnailList?.clear()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            progressPercent = progress / 100f
//            exoPlayerWrapper?.seekTo(progressPercent)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

}