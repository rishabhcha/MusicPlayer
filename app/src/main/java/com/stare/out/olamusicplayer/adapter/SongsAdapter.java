package com.stare.out.olamusicplayer.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.stare.out.olamusicplayer.models.Music;
import com.stare.out.olamusicplayer.utils.customfonts.MyTextViewBold;
import com.stare.out.olamusicplayer.utils.customfonts.MyTextViewSemibold;

import java.util.List;

/**
 * Created by rishabh on 16/12/17.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> {

    private static final String TAG = "SongsAdapter";
    private Context context;
    private List<Music> musicList;
    public ExoPlayer exoPlayer;

    public SongsAdapter() {
    }

    public SongsAdapter(Context context, List<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    @Override
    public SongsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_list, parent, false);
        return new SongsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SongsViewHolder holder, int position) {

        final Music music = musicList.get(position);
        Glide.with(context).load(music.getCoverImage()).thumbnail(0.01f).into(holder.songImageView);
        holder.songNameTextView.setText(music.getSong());
        holder.songArtistTextView.setText(music.getArtists());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.setMusicDetails(music.getSong(), music.getArtists());
                playMusic(music.getUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
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
            if (exoPlayer.getPlayWhenReady()){
                MainActivity.music_palyPause_btn.setImageResource(R.drawable.ic_play_arrow_24dp);
                exoPlayer.setPlayWhenReady(false);
            }else{
                MainActivity.music_palyPause_btn.setImageResource(R.drawable.ic_pause_24dp);
                exoPlayer.setPlayWhenReady(true);
            }
        }
    }

    public void releasePlayer(){
        if (exoPlayer!=null){
            exoPlayer.release();
        }
    }

    public class SongsViewHolder extends RecyclerView.ViewHolder{

        ImageView songImageView;
        MyTextViewBold songNameTextView;
        MyTextViewSemibold songArtistTextView;

        public SongsViewHolder(View itemView) {
            super(itemView);

            songImageView = (ImageView) itemView.findViewById(R.id.song_iamge);
            songNameTextView = (MyTextViewBold) itemView.findViewById(R.id.song_name);
            songArtistTextView = (MyTextViewSemibold) itemView.findViewById(R.id.song_artist);

        }
    }

    private void playMusic(String url) {
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
                if (isLoading){
                    MainActivity.music_palyPause_btn.setVisibility(View.GONE);
                }else{
                    MainActivity.music_palyPause_btn.setVisibility(View.VISIBLE);
                }
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

}
