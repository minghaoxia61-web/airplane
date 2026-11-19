package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.strategy.DoubleShoot;

import java.util.LinkedList;
import java.util.List;

/**
 * 精锐敌机
 * 向下发射双排子弹，具备左右移动能力
 * @author hitsz
 */
public class ElitePlusEnemy extends AbstractAircraft {

    private int power = 1;
    private int direction = 1;

    private int moveCounter = 0;
    private final int moveInterval = 50;

    private boolean isFrozen = false;
    private long freezeStartTime = 0;
    private final long freezeDuration = 3000;
    private int originalSpeedX;
    private int originalSpeedY;

    public ElitePlusEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootStrategy = new DoubleShoot(power, direction);
        this.originalSpeedX = speedX;
        this.originalSpeedY = speedY;
    }

    @Override
    public void forward() {
        if (isFrozen) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - freezeStartTime >= freezeDuration) {
                isFrozen = false;
                speedX = originalSpeedX;
                speedY = originalSpeedY;
            } else {
                return;
            }
        }
        super.forward();

        moveCounter++;
        if (moveCounter >= moveInterval) {
            moveCounter = 0;
            speedX = (int) (Math.random() * 6 - 3);
        }

        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

    @Override
    public void onBomb() {
        vanish();
    }

    @Override
    public void onFreeze() {
        isFrozen = true;
        freezeStartTime = System.currentTimeMillis();
        speedX = 0;
        speedY = 0;
    }

}