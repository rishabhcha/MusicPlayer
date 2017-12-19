package com.stare.out.olamusicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.stare.out.olamusicplayer.R;
import com.stare.out.olamusicplayer.models.Music;
import com.stare.out.olamusicplayer.utils.DBHelper;
import com.stare.out.olamusicplayer.utils.DownloadSong;
import com.stare.out.olamusicplayer.utils.ExoMusicPlayer;
import com.stare.out.olamusicplayer.utils.UpdateBottomPlayerUI;
import com.stare.out.olamusicplayer.utils.customfonts.MyTextViewBold;
import com.stare.out.olamusicplayer.utils.customfonts.MyTextViewRegular;
import com.stare.out.olamusicplayer.utils.customfonts.MyTextViewSemibold;

import java.util.ArrayList;
import java.util.List;


public class FavSongAdapter extends RecyclerView.Adapter<FavSongAdapter.FavSongsViewHolder> {

    private static final String TAG = "FavSongsAdapter";
    private Context context;
    private List<Music> musicList;
    private ExoMusicPlayer exoMusicPlayer;
    private UpdateBottomPlayerUI updateBottomPlayerUI;

    public FavSongAdapter() {

    }

    public FavSongAdapter(Context context, List<Music> musicList, ExoMusicPlayer exoMusicPlayer, UpdateBottomPlayerUI updateBottomPlayerUI) {
        this.context = context;
        this.musicList = musicList;
        this.exoMusicPlayer = exoMusicPlayer;
        this.updateBottomPlayerUI = updateBottomPlayerUI;
    }

    @Override
    public FavSongsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_song_list, parent, false);
        return new FavSongsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FavSongsViewHolder holder, int position) {

        final Music music = musicList.get(position);
        Glide.with(context).load(music.getCoverImage()).thumbnail(0.01f).into(holder.songImageView);
        holder.songNameTextView.setText(music.getSong());
        holder.songArtistTextView.setText(music.getArtists());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBottomPlayerUI.setMusicPlayerDetails(music.getSong(), music.getArtists(), music.getCoverImage(), music.getUrl());
                exoMusicPlayer.playMusic(music.getUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }


    public class FavSongsViewHolder extends RecyclerView.ViewHolder{

        ImageView songImageView;
        MyTextViewSemibold songNameTextView;
        MyTextViewRegular songArtistTextView;

        public FavSongsViewHolder(View itemView) {
            super(itemView);

            songImageView = (ImageView) itemView.findViewById(R.id.fav_song_image);
            songNameTextView = (MyTextViewSemibold) itemView.findViewById(R.id.fav_song_name);
            songArtistTextView = (MyTextViewRegular) itemView.findViewById(R.id.fav_song_artist);

        }
    }
}
