package com.biubiu.miku.util;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.biubiu.miku.BuildConfig;

/**
 * Configurations.
 */
public class Config {
    public static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String REFUSE_READ_CONTACTS = "refuse_read_contacts";
    private static final String GENERIC_CONFIG_PREFERENCE_NAME = "gaia_config";
    private static final String DEBUG_CHANNEL = "gaia_debug";
    private static final String USER_TOKEN = "user_token";
    private static final String GETUI_TOKEN = "getui_token";
    private static final String USER_DATA = "user_data";
    private static final String HAS_INVITED_FRIEND = "has_invited_friend";
    private static final String SYNCHRO_CONTACT_TIME = "synchro_contact_time";
    private static final String SAVE_VIDEO = "save_video";
    private static final String AUTO_SOUND = "auto_sound";
    private static final String GPRS_PLAY = "gprs_play";
    private static final String GPRS_TIP_PLAY = "gprs_tip_play";
    private static final String LOCAL_AVATAR_PATH = "local_avatar_path";

    //视频比例
    public static final float VIDEO_RATIO_4_3 = 4f / 3f;
    public static final float VIDEO_RATIO_1_1 = 1.0f;
    public static final float VIDEO_RATIO_16_9 = 16f / 9f;
    private static SharedPreferences genericSharedPrefs;
    private static String lastChannel;


    //元素默认时长
    //  气泡、综艺、tag、sticker默认2s
    //  字幕类：默认3s
    //  剪辑特效：暂停、慢动作、重复默认1s，快进默认3s
    public static final int SUBTITLE_TIME = 3000;
    public static final int STICKER_TIME = 2000;
    public static final int CHAT_BOX_TIME = 2000;
    public static final int VIDEO_TAG_TIME = 2000;
    public static final int RUN_MAN_TIME = 2000;
    public static final int MONTAGE_PAUSE_TIME = 1000;
    public static final int MONTAGE_SLOW_TIME = 1000;
    public static final int MONTAGE_REPEAT_TIME = 1000;
    public static final int MONTAGE_SPEED_TIME = 3000;

    private Config() {
    }

    /**
     * Get the last channel name, which will change if upgrading from different channel.
     *
     * @return the first channel name
     */
    public static synchronized String getLastChannel() {
        if (TextUtils.isEmpty(lastChannel)) {
            if (BuildConfig.DEBUG) {
                lastChannel = DEBUG_CHANNEL;
            } else {
                lastChannel = BuildConfig.FLAVOR;
            }
        }

        return lastChannel;
    }

    public static synchronized void saveVideo(boolean saveVideo) {
        SharedPreferences.Editor editor = genericSharedPrefs.edit();
        editor.putBoolean(SAVE_VIDEO, saveVideo);
        editor.apply();
    }

    public static synchronized boolean isSaveVideo() {
        return genericSharedPrefs.getBoolean(SAVE_VIDEO, true);
    }

}
