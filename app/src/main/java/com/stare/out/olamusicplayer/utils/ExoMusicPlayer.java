package com.stare.out.olamusicplayer.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.stare.out.olamusicplayer.MainActivity;
import com.stare.out.olamusicplayer.R;

/**
 * Created by rishabh on 19/12/17.
 */

public class ExoMusicPlayer {

    private static final String TAG = "ExoMusicPlayer";
    private ExoPlayer exoPlayer;
    private Context context;
    private UpdateBottomPlayerUI updateBottomPlayerUI;

    public ExoMusicPlayer(Context context, UpdateBottomPlayerUI updateBottomPlayerUI){
        this.context = context;
        this.updateBottomPlayerUI = updateBottomPlayerUI;
    }

    public void playMusic(String url) {
        Log.d(TAG, "playMusic: "+ url);

        if (exoPlayer != null){
            stopPlayer();
            releasePlayer();
        }
        Handler mHandler = new Handler();
        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:40.0) Gecko/20100101 Firefox/40.0";
        Uri uri = Uri.parse(url);
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent, null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true);
        MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, Mp3Extractor.FACTORY,
                mHandler, null);

        TrackSelector trackSelector = new DefaultTrackSelector(mHandler);
        DefaultLoadControl loadControl = new DefaultLoadControl();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);

        exoPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onLoadingChanged(boolean isLoading) {
                updateBottomPlayerUI.setPlayPauseBtnVisiblity(isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }
        });

        exoPlayer.prepare(mediaSource);
        startPlayer();
    }

    public void stopPlayer(){
        if (exoPlayer!=null){
            exoPlayer.setPlayWhenReady(false);
        }
    }

    public void startPlayer(){
        if (exoPlayer!=null){
            exoPlayer.setPlayWhenReady(true);
        }
    }

    public void playPausePlayer(){
        if (exoPlayer!=null){
            updateBottomPlayerUI.playPauseMusic(exoPlayer.getPlayWhenReady());
        }
    }

    public void releasePlayer(){
        if (exoPlayer!=null){
            exoPlayer.release();
        }
    }

}
