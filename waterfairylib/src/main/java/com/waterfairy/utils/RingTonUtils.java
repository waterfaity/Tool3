package com.waterfairy.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/8/21 15:14
 * @info:
 */
public class RingTonUtils {
    //RingtoneManager.TYPE_NOTIFICATION;   通知声音

//RingtoneManager.TYPE_ALARM;  警告

//RingtoneManager.TYPE_RINGTONE; 铃声

    /**
     * 获取的是铃声的Uri
     *
     * @param ctx
     * @param type
     * @return
     */
    public static Uri getDefaultRingtoneUri(Context ctx, int type) {

        return RingtoneManager.getActualDefaultRingtoneUri(ctx, type);

    }

    /**
     * 获取的是铃声相应的Ringtone
     *
     * @param ctx
     * @param type
     */
    public Ringtone getDefaultRingtone(Context ctx, int type) {

        return RingtoneManager.getRingtone(ctx,
                RingtoneManager.getActualDefaultRingtoneUri(ctx, type));

    }

    /**
     * 播放铃声
     *
     * @param ctx
     * @param type
     */

    public static void PlayRingTone(Context ctx, int type) {
        MediaPlayer mMediaPlayer = MediaPlayer.create(ctx,
                getDefaultRingtoneUri(ctx, type));
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

    }

}
