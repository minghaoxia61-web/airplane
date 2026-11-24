package edu.hitsz.music;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import edu.hitsz.R;

/**
 * 实验三：基于 MediaPlayer 的背景音乐管理类（独立音频管理类）
 *
 * 职责：游戏 BGM 与 Boss 机 BGM 的播放、切换、释放，以及音乐开关状态管理。
 * 音频逻辑与 Activity 解耦，符合单一职责原则（对应课件 4.3 节）。
 *
 * 说明：MediaPlayer 适合较长的音频（背景音乐），短促音效由 MySoundPool 负责。
 */
public class MyMediaPlayer {

    public static final String PREFS_NAME = "aircraft_war";
    public static final String KEY_MUSIC_ON = "music_on";

    private static MyMediaPlayer instance;

    private final Context context;
    private MediaPlayer bgmPlayer;
    private MediaPlayer bossPlayer;

    /** 音乐开关（与菜单页 Switch 联动，SharedPreferences 持久化） */
    private boolean musicOn;

    private MyMediaPlayer(Context context) {
        this.context = context.getApplicationContext();
        this.musicOn = readMusicSwitch();
    }

    /** 单例：全局唯一的音频管理器 */
    public static synchronized MyMediaPlayer getInstance(Context context) {
        if (instance == null) {
            instance = new MyMediaPlayer(context);
        }
        return instance;
    }

    private boolean readMusicSwitch() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_MUSIC_ON, true);
    }

    /** 菜单页 Switch 调用：更新开关状态并持久化 */
    public void setMusicOn(boolean on) {
        this.musicOn = on;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_MUSIC_ON, on).apply();
        if (!on) {
            // 关闭音乐时立即停止所有播放
            stopAll();
        }
    }

    public boolean isMusicOn() {
        return musicOn;
    }

    /**
     * 进入游戏：开始播放游戏背景音乐 bgm.wav（循环）
     */
    public void startGameBgm() {
        this.musicOn = readMusicSwitch(); // 每局开始时重新读取开关状态
        if (!musicOn) {
            return;
        }
        if (bgmPlayer == null) {
            // create 静态方法合并了构造、setDataSource、prepare 三步（本地小音频适用）
            bgmPlayer = MediaPlayer.create(context, R.raw.bgm);
            if (bgmPlayer == null) {
                return;
            }
            bgmPlayer.setLooping(true);
        }
        if (!bgmPlayer.isPlaying()) {
            bgmPlayer.start();
        }
    }

    /**
     * Boss 机出现：暂停游戏 BGM，切换播放 Boss 机 BGM（课件要求）
     */
    public void switchToBossBgm() {
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
        if (!musicOn) {
            return;
        }
        if (bossPlayer == null) {
            bossPlayer = MediaPlayer.create(context, R.raw.bgm_boss);
            if (bossPlayer == null) {
                return;
            }
            bossPlayer.setLooping(true);
        }
        if (!bossPlayer.isPlaying()) {
            bossPlayer.start();
        }
    }

    /**
     * Boss 机坠毁：停止 Boss BGM，恢复游戏 BGM
     */
    public void backToGameBgm() {
        releaseBoss();
        if (musicOn && bgmPlayer != null && !bgmPlayer.isPlaying()) {
            bgmPlayer.start();
        }
    }

    /**
     * 游戏结束或退出游戏界面：释放全部播放器资源（MediaPlayer.stop() 后不能直接 start()，直接释放重建更安全）
     */
    public void stopAll() {
        releaseBgm();
        releaseBoss();
    }

    private void releaseBgm() {
        if (bgmPlayer != null) {
            bgmPlayer.release();
            bgmPlayer = null;
        }
    }

    private void releaseBoss() {
        if (bossPlayer != null) {
            bossPlayer.release();
            bossPlayer = null;
        }
    }
}
