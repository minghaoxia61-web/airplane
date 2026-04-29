package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.strategy.StraightShoot;

import java.util.LinkedList;
import java.util.List;

/**
 * 精英敌机
 * 可射击，发射单排子弹
 * @author hitsz
 */
public class EliteEnemy extends AbstractAircraft {

    private int power = 1;
    private int direction = 1;

    private boolean isFrozen = false;
    private long freezeStartTime = 0;
    private final long freezeDuration = 4000;
    private int originalSpeedX;
    private int originalSpeedY;

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootStrategy = new StraightShoot(power, direction, 1);
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