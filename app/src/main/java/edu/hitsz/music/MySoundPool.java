package edu.hitsz.music;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import edu.hitsz.R;

/**
 * 实验三：基于 SoundPool 的游戏音效管理类（独立音频管理类）
 *
 * 职责：播放短促、高频的游戏音效（子弹命中、道具获取、爆炸、游戏结束）。
 * SoundPool 预加载解码、延迟低、支持并发播放，适配游戏高频音效场景（对应课件 4.2 节）；
 * 背景音乐由 MyMediaPlayer 负责，两个管理类各司其职（单一职责原则）。
 */
public class MySoundPool {

    private static final String TAG = "MySoundPool";
    private static final int TOTAL_SOUNDS = 4;

    private static MySoundPool instance;

    private final Context appContext;
    private final SoundPool soundPool;
    /** 音效名 -> soundId（load 返回值） */
    private final Map<String, Integer> soundIds = new HashMap<>();
    private int loadedCount = 0;
    /** load 是异步的：全部加载完成后才允许播放，避免 play 时 soundId 未就绪 */
    private volatile boolean allLoaded = false;

    private MySoundPool(Context context) {
        this.appContext = context.getApplicationContext();
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build();

        // 工程化增强：监听加载完成事件（demo 中未处理该时序问题）
        soundPool.setOnLoadCompleteListener((sp, sampleId, status) -> {
            loadedCount++;
            if (loadedCount >= TOTAL_SOUNDS) {
                allLoaded = true;
                Log.i(TAG, "音效预加载完成");
            }
        });

        soundIds.put(SOUND_BULLET_HIT, soundPool.load(context, R.raw.bullet_hit, 1));
        soundIds.put(SOUND_BOMB_EXPLOSION, soundPool.load(context, R.raw.bomb_explosion, 1));
        soundIds.put(SOUND_GET_SUPPLY, soundPool.load(context, R.raw.get_supply, 1));
        soundIds.put(SOUND_GAME_OVER, soundPool.load(context, R.raw.game_over, 1));
    }

    public static final String SOUND_BULLET_HIT = "bullet_hit";
    public static final String SOUND_BOMB_EXPLOSION = "bomb_explosion";
    public static final String SOUND_GET_SUPPLY = "get_supply";
    public static final String SOUND_GAME_OVER = "game_over";

    /** 单例：全局唯一的音效管理器 */
    public static synchronized MySoundPool getInstance(Context context) {
        if (instance == null) {
            instance = new MySoundPool(context);
        }
        return instance;
    }

    /** 统一播放入口：leftVol/rightVol 声道音量，priority 优先级，loop 循环次数，rate 播放速率 */
    public void play(String soundKey) {
        // 音乐开关关闭时，音效一并静音
        if (!allLoaded || !MyMediaPlayer.getInstance(appContext).isMusicOn()) {
            return;
        }
        Integer soundId = soundIds.get(soundKey);
        if (soundId != null) {
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    /** 敌机子弹击中英雄机 / 英雄机子弹击中敌机 */
    public void playBulletHit() {
        play(SOUND_BULLET_HIT);
    }

    /** 英雄机与炸弹道具碰撞 */
    public void playBombExplosion() {
        play(SOUND_BOMB_EXPLOSION);
    }

    /** 英雄机与非炸弹道具碰撞 */
    public void playGetSupply() {
        play(SOUND_GET_SUPPLY);
    }

    /** 游戏结束 */
    public void playGameOver() {
        play(SOUND_GAME_OVER);
    }

    /** 退出游戏界面时释放 SoundPool 资源，并重置单例以便下次进入重建 */
    public synchronized void release() {
        soundPool.release();
        instance = null;
        allLoaded = false;
    }
}
