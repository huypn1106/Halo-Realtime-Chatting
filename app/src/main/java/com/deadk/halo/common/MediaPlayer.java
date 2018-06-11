package com.deadk.halo.common;

import android.support.annotation.RawRes;

public interface MediaPlayer {

    android.media.MediaPlayer playAudioRaw(@RawRes int rawID);
    android.media.MediaPlayer playAudioUrl(String url);
}
