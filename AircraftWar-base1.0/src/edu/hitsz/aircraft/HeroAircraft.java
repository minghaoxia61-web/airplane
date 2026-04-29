package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.application.ImageManager;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.strategy.ShootStrategy;
import edu.hitsz.strategy.StraightShoot;

import java.util.LinkedList;
import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 * 单例模式实现
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {

    // 单例实例
    private static volatile HeroAircraft instance;

    //每次射击发射子弹数量
    private int shootNum = 1;

    //子弹威力
    private int power = 30;

    //子弹射击方向 (向上发射：-1，向下发射：1)
    private int direction = -1;

    /**
     * 火力道具生效控制：用于避免“后来的道具被旧线程提前恢复”。
     */
    private volatile long firepowerToken = 0;

    // 私有构造函数
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootStrategy = new StraightShoot(power, direction, shootNum);
    }

    // 双重检查锁定获取单例
    public static HeroAircraft getInstance() {
        if (instance == null) {
            synchronized (HeroAircraft.class) {
                if (instance == null) {
                    instance = new HeroAircraft(
                            Main.WINDOW_WIDTH / 2,
                            Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                            0, 0, 100
                    );
                }
            }
        }
        return instance;
    }

    /**
     * 供单元测试重置单例，避免用例之间相互影响。
     */
    static void resetInstanceForTest() {
        synchronized (HeroAircraft.class) {
            instance = null;
        }
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    /**
     * 火力道具：临时切换射击策略，指定时长后自动恢复直射（Runnable + Thread 实现）。
     */
    public void applyFirepowerBuff(ShootStrategy newStrategy, long durationMs) {
        final long token = ++firepowerToken;
        setShootStrategy(newStrategy);

        Runnable r = () -> {
            try {
                Thread.sleep(Math.max(0, durationMs));
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                return;
            }
            if (firepowerToken == token) {
                setShootStrategy(new StraightShoot(power, direction, shootNum));
            }
        };
        new Thread(r, "firepower-buff-timer").start();
    }

}
