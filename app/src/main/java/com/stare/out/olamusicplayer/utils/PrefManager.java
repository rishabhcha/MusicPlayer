package com.stare.out.olamusicplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rishabh on 20/12/17.
 */

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = Constants.sharedPreferenceName;

    public PrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setMusicDetails(String musicName, String artistName, String musicUrl, String musicIamge){
        editor.putString("music_name", musicName);
        editor.putString("music-artist", artistName);
        editor.putString("music_url", musicUrl);
        editor.putString("music_image", musicIamge);
        editor.commit();
    }

    public String getMusicName(){
        return pref.getString("music-name", "");
    }

    public String getMusicArtist(){
        return pref.getString("music-artist", "");
    }

    public String getMusicUrl(){
        return pref.getString("music-url", "");
    }

    public String getMusicIamge(){
        return pref.getString("music-image", "");
    }

}
