package com.stare.out.olamusicplayer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.stare.out.olamusicplayer.models.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rishabh on 16/12/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SQLiteMusic.db";
    private static final int DATABASE_VERSION = 1;
    public static final String FAVORITE_TABLE_NAME = "favorite";
    public static final String FAVORITE_COLUMN_ID = "_id";
    public static final String FAVORITE_COLUMN_NAME = "name";
    public static final String FAVORITE_COLUMN_IMAGE = "image";
    public static final String FAVORITE_COLUMN_ARTIST = "artist";
    public static final String FAVORITE_COLUMN_URL = "url";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + FAVORITE_TABLE_NAME + "(" +
                FAVORITE_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                FAVORITE_COLUMN_NAME + " TEXT, " +
                FAVORITE_COLUMN_IMAGE + " TEXT, " +
                FAVORITE_COLUMN_ARTIST + " TEXT, " +
                FAVORITE_COLUMN_URL + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITE_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertFavoriteMusic(String name, String image, String artist, String url) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FAVORITE_COLUMN_NAME, name);
        contentValues.put(FAVORITE_COLUMN_IMAGE, image);
        contentValues.put(FAVORITE_COLUMN_ARTIST, artist);
        contentValues.put(FAVORITE_COLUMN_URL, url);
        db.insert(FAVORITE_TABLE_NAME, null, contentValues);
        return true;
    }

    public Integer deleteFavoriteMusic(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(FAVORITE_TABLE_NAME,
                FAVORITE_COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }



    public List<Music> getAllFavoriteMusic() {
        List<Music> musics = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery( "SELECT * FROM " + FAVORITE_TABLE_NAME, null );
        if (cursor.moveToFirst()) {
            do {
                Music music = new Music(cursor.getString(1), cursor.getString(4), cursor.getString(3), cursor.getString(2));
                musics.add(music);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return musics;
    }
}
