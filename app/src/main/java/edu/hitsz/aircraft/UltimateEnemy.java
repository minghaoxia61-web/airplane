package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.strategy.ScatteredShoot;

import java.util.LinkedList;
import java.util.List;

/**
 * 王牌敌机
 * 发射扇形散射弹道（一次3颗子弹），具备左右移动能力
 * @author hitsz
 */
public class UltimateEnemy extends AbstractAircraft {

    private int power = 2;
    private int direction = 1;

    private int moveCounter = 0;
    private final int moveInterval = 40;

    private boolean isSlowed = false;
    private long slowStartTime = 0;
    private final long slowDuration = 5000;
    private int originalSpeedX;
    private int originalSpeedY;

    public UltimateEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootStrategy = new ScatteredShoot(power, direction, 3);
        this.originalSpeedX = speedX;
        this.originalSpeedY = speedY;
    }

    @Override
    public void forward() {
        if (isSlowed) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - slowStartTime >= slowDuration) {
                isSlowed = false;
                speedX = originalSpeedX;
                speedY = originalSpeedY;
            }
        }
        super.forward();

        moveCounter++;
        if (moveCounter >= moveInterval) {
            moveCounter = 0;
            if (!isSlowed) {
                speedX = (int) (Math.random() * 8 - 4);
            }
        }

        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

    @Override
    public void onBomb() {
        decreaseHp(50);
    }

    @Override
    public void onFreeze() {
        isSlowed = true;
        slowStartTime = System.currentTimeMillis();
        speedX = originalSpeedX / 2;
        speedY = originalSpeedY / 2;
    }

}