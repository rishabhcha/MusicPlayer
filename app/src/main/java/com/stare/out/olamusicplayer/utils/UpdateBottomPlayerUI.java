package com.stare.out.olamusicplayer.utils;

/**
 * Created by rishabh on 19/12/17.
 */

public interface UpdateBottomPlayerUI {

    public void setMusicPlayerDetails(String songName, String songArtist);

    public void setPlayPauseBtnVisiblity(boolean isLoading);

    public void playPauseMusic(boolean isPlaying);

}
