package com.stare.out.olamusicplayer.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.stare.out.olamusicplayer.models.Music;
import com.stare.out.olamusicplayer.utils.Constants;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by rishabh on 16/12/17.
 */

public class API {

    public interface ObservableAPIService {

        @GET("/studio")
        Observable<List<Music>> getSongs(

        );

    }

    static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build();

    public ObservableAPIService observableAPIService = new Retrofit.Builder()
            .baseUrl(Constants.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ObservableAPIService.class);

}
