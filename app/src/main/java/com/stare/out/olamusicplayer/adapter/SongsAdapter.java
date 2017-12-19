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
import com.stare.out.olamusicplayer.MainActivity;
import com.stare.out.olamusicplayer.R;
import com.stare.out.olamusicplayer.models.Music;
import com.stare.out.olamusicplayer.utils.DBHelper;
import com.stare.out.olamusicplayer.utils.DownloadSong;
import com.stare.out.olamusicplayer.utils.ExoMusicPlayer;
import com.stare.out.olamusicplayer.utils.UpdateBottomPlayerUI;
import com.stare.out.olamusicplayer.utils.customfonts.MyTextViewBold;
import com.stare.out.olamusicplayer.utils.customfonts.MyTextViewSemibold;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rishabh on 16/12/17.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> {

    private static final String TAG = "SongsAdapter";
    private Context context;
    private List<Music> musicList;
    private List<Music> favoriteMusicList;
    private DBHelper dbHelper;
    private ExoMusicPlayer exoMusicPlayer;
    private UpdateBottomPlayerUI updateBottomPlayerUI;

    public SongsAdapter() {

    }

    public SongsAdapter(Context context, List<Music> musicList, ExoMusicPlayer exoMusicPlayer, UpdateBottomPlayerUI updateBottomPlayerUI) {
        this.context = context;
        this.musicList = musicList;
        this.exoMusicPlayer = exoMusicPlayer;
        this.updateBottomPlayerUI = updateBottomPlayerUI;
        favoriteMusicList = new ArrayList<>();
        dbHelper = new DBHelper(context);
        getFavoriteMusic();
    }

    @Override
    public SongsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_list, parent, false);
        return new SongsViewHolder(itemView);
    }

    private void getFavoriteMusic(){
        favoriteMusicList = dbHelper.getAllFavoriteMusic();
    }

    @Override
    public void onBindViewHolder(final SongsViewHolder holder, int position) {

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
        holder.favoriteBtn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        for (Music music1:favoriteMusicList
             ) {
            Log.d(TAG, "favorite musics: "+ music1.getSong());
            if (music1.getSong().equals(music.getSong())){
                holder.favoriteBtn.setImageResource(R.drawable.ic_favorite_24dp);
                break;
            }
        }
        holder.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFavorite = false;
                for (Music music1:favoriteMusicList
                        ) {
                    if (music1.getSong().equals(music.getSong())){
                        isFavorite = true;
                        dbHelper.deleteFavoriteMusic(music.getSong());
                        break;
                    }
                }
                if (isFavorite){
                    holder.favoriteBtn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }else {
                    boolean isSuccess = dbHelper.insertFavoriteMusic(music.getSong(), music.getCoverImage(), music.getArtists(), music.getUrl());
                    holder.favoriteBtn.setImageResource(R.drawable.ic_favorite_24dp);
                    Log.d(TAG, "insertion success: "+ isSuccess);
                }
                getFavoriteMusic();
                updateBottomPlayerUI.setFavoriteSongAdapter();
            }
        });

        holder.downloadSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadSong downloadSong = new DownloadSong(context);
                downloadSong.startDownload(music.getUrl(), music.getSong());
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }


    public class SongsViewHolder extends RecyclerView.ViewHolder{

        ImageView songImageView;
        MyTextViewBold songNameTextView;
        MyTextViewSemibold songArtistTextView;
        ImageButton favoriteBtn, downloadSongBtn;

        public SongsViewHolder(View itemView) {
            super(itemView);

            songImageView = (ImageView) itemView.findViewById(R.id.song_iamge);
            songNameTextView = (MyTextViewBold) itemView.findViewById(R.id.song_name);
            songArtistTextView = (MyTextViewSemibold) itemView.findViewById(R.id.song_artist);
            favoriteBtn = (ImageButton) itemView.findViewById(R.id.favoriteBtn);
            downloadSongBtn = (ImageButton) itemView.findViewById(R.id.downloadSongBtn);
        }
    }
}
