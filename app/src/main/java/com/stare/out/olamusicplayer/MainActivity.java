package com.stare.out.olamusicplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.stare.out.olamusicplayer.adapter.SongsAdapter;
import com.stare.out.olamusicplayer.api.API;
import com.stare.out.olamusicplayer.models.Music;
import com.stare.out.olamusicplayer.utils.Progress_Dialog;
import com.stare.out.olamusicplayer.utils.customfonts.MyTextViewLight;
import com.stare.out.olamusicplayer.utils.customfonts.MyTextViewRegular;
import com.stare.out.olamusicplayer.utils.customfonts.MyTextViewSemibold;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final int spanCount = 2;
    private static final String TAG = "MainActivity";

    private RecyclerView SongsRecyclerView;
    private SongsAdapter songsAdapter;
    private static LinearLayout playerLinearLayout;
    private static MyTextViewSemibold music_name;
    private static MyTextViewLight music_artist;
    public static ImageButton music_palyPause_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerLinearLayout = (LinearLayout) findViewById(R.id.playerLinearLayout);
        music_artist = (MyTextViewLight) findViewById(R.id.music_artist);
        music_name = (MyTextViewSemibold) findViewById(R.id.music_name);
        music_palyPause_btn = (ImageButton) findViewById(R.id.music_playPause_btn);

        SongsRecyclerView = (RecyclerView) findViewById(R.id.SongsRecyclerView);
        SongsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSongs();

    }

    @Override
    protected void onPause() {
        super.onPause();

        SongsAdapter songsAdapter = new SongsAdapter();
        songsAdapter.stopPlayer();
        songsAdapter.releasePlayer();

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
                            progress_dialog.hideProgressDialog();
                            songsAdapter = new SongsAdapter(getApplicationContext(), musics);
                            SongsRecyclerView.setAdapter(songsAdapter);
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

    public static void setMusicDetails(String songName, String songArtist){
        playerLinearLayout.setVisibility(View.VISIBLE);
        music_name.setText(songName);
        music_artist.setText(songArtist);
    }

    public void PlayPauseMusic(View view) {
        songsAdapter.playPausePlayer();
    }
}
