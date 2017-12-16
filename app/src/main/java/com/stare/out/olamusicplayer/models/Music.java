package com.stare.out.olamusicplayer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rishabh on 16/12/17.
 */

public class Music {

    @SerializedName("song")
    @Expose
    private String song;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("artists")
    @Expose
    private String artists;
    @SerializedName("cover_image")
    @Expose
    private String coverImage;

    /**
     * No args constructor for use in serialization
     *
     */
    public Music() {
    }

    /**
     *
     * @param artists
     * @param song
     * @param coverImage
     * @param url
     */
    public Music(String song, String url, String artists, String coverImage) {
        super();
        this.song = song;
        this.url = url;
        this.artists = artists;
        this.coverImage = coverImage;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

}


