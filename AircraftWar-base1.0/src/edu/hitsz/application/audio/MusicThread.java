package edu.hitsz.application.audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Objects;

/**
 * 音频播放线程（继承 Thread，满足实验五要求）。
 *
 * <p>支持：单次播放 / 循环播放 / 主动停止。</p>
 */
public class MusicThread extends Thread {

    private final String resourcePath;
    private final boolean loop;
    private volatile boolean stopRequested = false;
    private volatile Clip clip;

    public MusicThread(String resourcePath, boolean loop) {
        super("music-" + Objects.requireNonNull(resourcePath));
        setDaemon(true);
        this.resourcePath = resourcePath;
        this.loop = loop;
    }

    @Override
    public void run() {
        try (InputStream raw = MusicThread.class.getResourceAsStream(resourcePath)) {
            if (raw == null) {
                return;
            }
            try (BufferedInputStream in = new BufferedInputStream(raw);
                 AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(in)) {

                Clip c = AudioSystem.getClip();
                this.clip = c;
                c.open(audioInputStream);

                if (loop) {
                    c.loop(Clip.LOOP_CONTINUOUSLY);
                } else {
                    c.start();
                }

                while (!stopRequested && c.isOpen() && (loop || c.isRunning())) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ignored) {
                        interrupt();
                        break;
                    }
                }
            }
        } catch (Exception ignored) {
            // 资源缺失或音频格式不支持时，静默跳过，避免影响游戏主流程
        } finally {
            closeClip();
        }
    }

    public void requestStop() {
        stopRequested = true;
        closeClip();
        interrupt();
    }

    private void closeClip() {
        Clip c = this.clip;
        if (c != null) {
            try {
                c.stop();
            } catch (Exception ignored) {
            }
            try {
                c.close();
            } catch (Exception ignored) {
            }
        }
    }
}

