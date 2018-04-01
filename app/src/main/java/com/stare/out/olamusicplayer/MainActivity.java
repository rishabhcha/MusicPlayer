package com.stare.out.olamusicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
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

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity implements
        UpdateBottomPlayerUI,
        SearchView.OnQueryTextListener,
        SensorEventListener {

    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1111;

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

    AudioManager am;
    int max,curr,direction=0,d=0;
    static double div,result,r;
    int flag=0;
    String set;

    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        am=(AudioManager)getSystemService(AUDIO_SERVICE);
        max=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current=am.getStreamVolume(AudioManager.STREAM_MUSIC);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

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
        ViewCompat.setNestedScrollingEnabled(SlideUpRecyclerView, true);

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


    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.d(TAG,"permission is needed");

            } else {
                Log.d(TAG,"requesting permission");
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG,"permission granted");
                } else {
                    Log.d(TAG,"permission denied");
                    Toast.makeText(MainActivity.this, "This permission is required to store data!! Please grant it.", Toast.LENGTH_LONG).show();
                    checkPermission();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    //Use sensor to increase or decrease volume and to change music track
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor s=event.sensor;
        if(s.getType()==Sensor.TYPE_ACCELEROMETER) {
            float[] values = event.values;
            int x = (int) values[0];
            int y = (int) values[1];
            int z = (int) values[2];

            if(direction==0)
            {
                curr = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            }
            if(d==0) {
                if (y >= 5) {
                    direction = 1;
                    switch (y) {
                        case 5:
                            div = (y - 5) * ((15 - curr) / 4);
                            result = curr + div;
                            r=result;
                            am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) result, 0);
                            break;
                        case 6:
                            div = (y - 5) * ((15 - curr) / 4);
                            result = curr + div;
                            if(result>=r) {
                                am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) result, 0);
                                r=result;
                            }
                            else {
                                d = 1;
                            }
                            break;
                        case 7:
                            div = (y - 5) * ((15 - curr) / 4);
                            result = curr + div;
                            if(result>=r) {
                                am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) result, 0);
                                r=result;
                            }
                            else
                                d=1;
                            break;
                        case 8:
                            div = (y - 5) * ((15 - curr) / 4);
                            result = curr + div;
                            if(result>=r) {
                                am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) result, 0);
                                r=result;
                            }

                            else
                                d=1;
                            break;
                        case 9:
                            div = (y - 5) * ((15 - curr) / 4);
                            result = curr + div;
                            if(result>=r) {
                                am.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);
                                r=result;
                            }
                            else
                                d=1;
                            break;
                        default:
                            result=15;
                            am.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);
                            r=result;
                    }
                }
                else if(y<=-2)
                {
                    direction = 1;
                    switch (y) {
                        case -2:
                            div = abs(y + 2) * ((curr) / 4);
                            result = curr - div;
                            r=result;
                            am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) result, 0);
                            break;
                        case -3:
                            div = abs(y + 2) * ((curr) / 4);
                            result = curr - div;
                            if(result<=r) {
                                am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) result, 0);
                                r=result;
                            }
                            else {
                                d = 1;
                            }
                            break;
                        case -4:
                            div = abs(y + 2) * ((curr) / 4);
                            result = curr - div;
                            if(result<=r) {
                                am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) result, 0);
                                r=result;
                            }
                            else
                                d=1;
                            break;
                        case -5:
                            div = abs(y + 2) * ((curr) / 4);
                            result = curr - div;
                            if(result<=r) {
                                am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) result, 0);
                                r=result;
                            }

                            else
                                d=1;
                            break;
                        case -6:
                            div = abs(y + 2) * ((curr) / 4);
                            result = curr - div;
                            if(result<=r) {
                                am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) result, 0);
                                r=result;
                            }
                            else
                                d=1;
                            break;
                        default:
                            result=0;
                            am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) result, 0);
                            r=result;
                    }
                }
            }
            if (y <= 4 && y >= -1) {
                direction = 0;
                d=0;
            }
            if (flag == 0) {
                if (x <= -5) {
                    flag = 1;
                    set = "Next";
                    songsAdapter.playNextSong();
                }
                else if (x >= 5) {
                    flag = 1;
                    set = "previous";
                    songsAdapter.playPreviousSong();
                }
            } else if (flag == 1) {
                if (x >= -1 && x <= 1) {
                    flag = 0;
                    set = "normal";
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
