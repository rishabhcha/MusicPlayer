package com.stare.out.olamusicplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.stare.out.olamusicplayer.api.API;
import com.stare.out.olamusicplayer.models.Music;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSongs();

    }

    private void getSongs() {
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
                            Log.d("--------", "onNext");
                            for (int i=0; i< musics.size(); i++){
                                Log.d("-------", musics.get(i).getSong());
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("--------", "onError: "+e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.d("--------", "onComplete");
                        }
                    });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
