package com.stare.out.olamusicplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.stare.out.olamusicplayer.adapter.SongsAdapter;
import com.stare.out.olamusicplayer.api.API;
import com.stare.out.olamusicplayer.models.Music;
import com.stare.out.olamusicplayer.utils.Progress_Dialog;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SongsRecyclerView = (RecyclerView) findViewById(R.id.SongsRecyclerView);
        SongsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSongs();

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
                            SongsAdapter songsAdapter = new SongsAdapter(getApplicationContext(), musics);
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
}
