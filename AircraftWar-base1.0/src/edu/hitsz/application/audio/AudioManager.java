package edu.hitsz.application.audio;

/**
 * 统一音频管理类：封装播放/循环/停止，降低模块耦合（实验五建议）。
 *
 * <p>资源路径默认按指导书使用：{@code /videos/*.wav}。</p>
 */
public final class AudioManager {

    private static volatile MusicThread bgm;
    private static volatile MusicThread bossBgm;

    private AudioManager() {
    }

    public static synchronized void playBgmLoop() {
        stopBgm();
        bgm = new MusicThread("/videos/bgm.wav", true);
        bgm.start();
    }

    public static synchronized void stopBgm() {
        if (bgm != null) {
            bgm.requestStop();
            bgm = null;
        }
    }

    public static synchronized void playBossBgmLoop() {
        stopBossBgm();
        bossBgm = new MusicThread("/videos/bgm_boss.wav", true);
        bossBgm.start();
    }

    public static synchronized void stopBossBgm() {
        if (bossBgm != null) {
            bossBgm.requestStop();
            bossBgm = null;
        }
    }

    public static void playSfxOnce(String resourcePath) {
        new MusicThread(resourcePath, false).start();
    }

    public static synchronized void stopAll() {
        stopBossBgm();
        stopBgm();
    }
}

