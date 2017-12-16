package com.stare.out.olamusicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.stare.out.olamusicplayer.R;
import com.stare.out.olamusicplayer.models.Music;
import com.stare.out.olamusicplayer.utils.customfonts.MyTextViewBold;
import com.stare.out.olamusicplayer.utils.customfonts.MyTextViewSemibold;

import java.util.List;

/**
 * Created by rishabh on 16/12/17.
 */

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsViewHolder> {

    private Context context;
    private List<Music> musicList;

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

        Music music = musicList.get(position);
        Glide.with(context).load(music.getCoverImage()).thumbnail(0.01f).into(holder.songImageView);
        holder.songNameTextView.setText(music.getSong());
        holder.songArtistTextView.setText(music.getArtists());
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class SongsViewHolder extends RecyclerView.ViewHolder{

        ImageView songImageView;
        TextView songNameTextView;
        TextView songArtistTextView;

        public SongsViewHolder(View itemView) {
            super(itemView);

            songImageView = (ImageView) itemView.findViewById(R.id.song_iamge);
            songNameTextView = (TextView) itemView.findViewById(R.id.song_name);
            songArtistTextView = (TextView) itemView.findViewById(R.id.song_artist);

        }
    }

}
