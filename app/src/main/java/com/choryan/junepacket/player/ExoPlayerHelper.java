package com.choryan.junepacket.player;

import android.content.Context;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.choryan.junepacket.interf.IMediaFramePositionListener;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.android.exoplayer2.video.VideoSize;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author: ChoRyan Quan
 * @date: 6/10/21
 */
public class ExoPlayerHelper implements LifecycleObserver {

    private static final String TAG = "ExoPlayerHelper";
    private SimpleDateFormat sdfMediaPosition = new SimpleDateFormat("mm:ss", Locale.ENGLISH);

    private static ExoPlayerHelper instance = new ExoPlayerHelper();

    private ExoPlayerHelper() {
    }

    public static ExoPlayerHelper getInstance() {
        return instance;
    }


    public SimpleExoPlayer mExoPlayer;
    private boolean repeatMode;

    public class ExoPlayerHelperParam {
        public String mediaUri;
        public long mediaDuration;
        public Surface targetSurface;
    }

    public ExoPlayerHelper initExoPlayer(Context context, ExoPlayerHelperParam param) {
        mExoPlayer = new SimpleExoPlayer.Builder(context)
                .setSeekParameters(SeekParameters.EXACT)
                .build();
        setRepeatMode(false);
        mExoPlayer.setPlayWhenReady(true);
        mExoPlayer.setThrowsWhenUsingWrongThread(true);
        mExoPlayer.addVideoListener(new VideoListener() {
            @Override
            public void onVideoSizeChanged(VideoSize videoSize) {
                Log.d(TAG, "onVideoSizeChanged: ");
            }

            @Override
            public void onSurfaceSizeChanged(int width, int height) {
                Log.d(TAG, "onSurfaceSizeChanged: ");
            }

            @Override
            public void onRenderedFirstFrame() {
                Log.d(TAG, "onRenderedFirstFrame: ");
            }
        });
        mExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.d(TAG, "onPlayerError: ");
            }

            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {
                Log.d(TAG, "onTimelineChanged: " + sdfMediaPosition.format(mExoPlayer.getDuration()));
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                Log.d(TAG, "onPlaybackStateChanged: " + state);
            }
        });
        mExoPlayer.setVideoFrameMetadataListener((presentationTimeUs, releaseTimeNs, format, mediaFormat) -> {
            Log.d(TAG, "setVideoFrameMetadataListener: ");
        });
        mExoPlayer.setMediaItem(MediaItem.fromUri(param.mediaUri));
        mExoPlayer.setVideoSurface(param.targetSurface);
        mExoPlayer.prepare();
        return this;
    }

    public void setRepeatMode(boolean repeat) {
        this.repeatMode = repeat;
        if (null != mExoPlayer) {
            if (repeat) {
                mExoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
            } else {
                mExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
            }

        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void releaseExoPlayer() {
        if (null != mExoPlayer) {
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

}
