package com.stare.out.olamusicplayer;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.stare.out.olamusicplayer.adapter.FavSongAdapter;
import com.stare.out.olamusicplayer.adapter.SongsAdapter;
import com.stare.out.olamusicplayer.api.API;
import com.stare.out.olamusicplayer.fragments.FavMusicFragment;
import com.stare.out.olamusicplayer.fragments.MusicHistoryFragment;
import com.stare.out.olamusicplayer.models.Music;
import com.stare.out.olamusicplayer.utils.DBHelper;
import com.stare.out.olamusicplayer.utils.ExoMusicPlayer;
import com.stare.out.olamusicplayer.utils.PrefManager;
import com.stare.out.olamusicplayer.utils.Progress_Dialog;
import com.stare.out.olamusicplayer.utils.UpdateBottomPlayerUI;
import com.stare.out.olamusicplayer.utils.customfonts.MyTextViewLight;
import com.stare.out.olamusicplayer.utils.customfonts.MyTextViewRegular;
import com.stare.out.olamusicplayer.utils.customfonts.MyTextViewSemibold;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements
        UpdateBottomPlayerUI,
        SearchView.OnQueryTextListener{

    private static final String TAG = "MainActivity";

    private RecyclerView SongsRecyclerView, SlideUpRecyclerView;
    private LinearLayout playerLinearLayout;
    private MyTextViewSemibold music_name;
    private MyTextViewLight music_artist;
    public ImageButton music_palyPause_btn;
    private SearchView songSearchView;
    private List<Music> MusicList;
    private List<Music> SearchedMusicList;
    private ExoMusicPlayer exoMusicPlayer;
    private SongsAdapter songsAdapter;
    private ImageView playerSongImageView;
    private RecyclerView FavRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerLinearLayout = (LinearLayout) findViewById(R.id.playerLinearLayout);
        music_artist = (MyTextViewLight) findViewById(R.id.music_artist);
        music_name = (MyTextViewSemibold) findViewById(R.id.music_name);
        music_palyPause_btn = (ImageButton) findViewById(R.id.music_playPause_btn);
        playerSongImageView = (ImageView) findViewById(R.id.playerSongImageView);

        MusicList = new ArrayList<>();
        SearchedMusicList = new ArrayList<>();

        exoMusicPlayer = new ExoMusicPlayer(getApplicationContext(), MainActivity.this);

        SongsRecyclerView = (RecyclerView) findViewById(R.id.SongsRecyclerView);
        SongsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        SlideUpRecyclerView = (RecyclerView) findViewById(R.id.SlideUpRecyclerView);
        SlideUpRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FavRecyclerView = (RecyclerView) findViewById(R.id.FavRecyclerView);
        FavRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        songSearchView = (SearchView) findViewById(R.id.songSearchView);
        songSearchView.setQueryHint("Search your song...");
        songSearchView.setIconifiedByDefault(false);
        songSearchView.setFocusable(false);
        songSearchView.setOnQueryTextListener(this);

        getSongs();

        setFavoriteSongAdapter();

    }

    private void UpdateSongAdapter(String newText) {
        SearchedMusicList.clear();
        for (int i =0; i< MusicList.size(); i++){
            Music music = MusicList.get(i);
            if (music.getSong().toLowerCase().startsWith(newText.toLowerCase())){
                SearchedMusicList.add(music);
            }
        }
        songsAdapter = new SongsAdapter(getApplicationContext(), SearchedMusicList, exoMusicPlayer ,MainActivity.this);
        SongsRecyclerView.setAdapter(songsAdapter);
    }

    private void getSongs() {

        final Progress_Dialog progress_dialog = new Progress_Dialog(MainActivity.this, "Hang Tight!! Getting Songs for you");
        progress_dialog.showProgressDialog();

        try {
            API api = new API();
            api.observableAPIService.getSongs()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Music>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }
                        @Override
                        public void onNext(List<Music> musics) {
                            MusicList = musics;
                            progress_dialog.hideProgressDialog();
                            songsAdapter = new SongsAdapter(getApplicationContext(), musics, exoMusicPlayer, MainActivity.this);
                            SongsRecyclerView.setAdapter(songsAdapter);
                            SlideUpRecyclerView.setAdapter(songsAdapter);
                        }
                        @Override
                        public void onError(Throwable e) {
                            progress_dialog.hideProgressDialog();
                            Log.d(TAG, "Error: "+e.getMessage());
                            Toast.makeText(MainActivity.this, "Sorry Unable to load Songs!!", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onComplete() {
                        }
                    });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void PlayPauseMusic(View view) {
        exoMusicPlayer.playPausePlayer();
    }

    @Override
    public void setMusicPlayerDetails(String songName, String songArtist, String imageUrl, String songUrl) {
        playerLinearLayout.setVisibility(View.VISIBLE);
        music_name.setText(songName);
        music_artist.setText(songArtist);
        Glide.with(MainActivity.this).load(imageUrl).thumbnail(0.1f).into(playerSongImageView);
        PrefManager prefManager = new PrefManager(getApplicationContext());
        prefManager.setMusicDetails(songName, songArtist, songUrl, imageUrl);
    }

    @Override
    public void setPlayPauseBtnVisiblity(boolean isLoading) {
        if (isLoading){
            music_palyPause_btn.setVisibility(View.GONE);
        }else{
            music_palyPause_btn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void playPauseMusic(boolean isPlaying) {
        if (isPlaying){
            music_palyPause_btn.setImageResource(R.drawable.ic_play_arrow_24dp);
            exoMusicPlayer.stopPlayer();
        }else{
            music_palyPause_btn.setImageResource(R.drawable.ic_pause_24dp);
            exoMusicPlayer.startPlayer();
        }
    }

    @Override
    public void setFavoriteSongAdapter() {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        List<Music> favMusicList = dbHelper.getAllFavoriteMusic();
        FavSongAdapter favSongAdapter = new FavSongAdapter(getApplicationContext(), favMusicList, exoMusicPlayer, MainActivity.this);
        FavRecyclerView.setAdapter(favSongAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        UpdateSongAdapter(newText);
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        PrefManager prefManager = new PrefManager(getApplicationContext());
        String songName = prefManager.getMusicName();
        String songArtist = prefManager.getMusicArtist();
        String songUrl = prefManager.getMusicUrl();
        String songImage = prefManager.getMusicIamge();
        Log.d(TAG, "Last played song: "+songName+", "+songArtist);

        if (!TextUtils.isEmpty(songImage)){
            setMusicPlayerDetails(songName, songArtist, songImage, songUrl);
            exoMusicPlayer.playMusic(songUrl);
            exoMusicPlayer.stopPlayer();
        }

    }
}
