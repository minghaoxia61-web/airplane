package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.RingShoot;

import java.util.LinkedList;
import java.util.List;

/**
 * Boss敌机
 * 仅在屏幕上方左右移动，不向下移动
 * @author hitsz
 */
public class BossEnemy extends AbstractAircraft {

    //子弹威力
    private int power = 40;

    //子弹射击方向 (向上发射：-1，向下发射：1)
    private int direction = 1;

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootStrategy = new RingShoot(power, direction, 20);
    }

    @Override
    public void forward() {
        super.forward();
        
        // 左右移动逻辑：碰到边界时反弹
        if (locationX <= 50) {
            speedX = Math.abs(speedX);
        } else if (locationX >= Main.WINDOW_WIDTH - 50) {
            speedX = -Math.abs(speedX);
        }
    }

}