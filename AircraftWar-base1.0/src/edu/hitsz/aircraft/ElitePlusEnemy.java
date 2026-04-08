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

    //子弹威力
    private int power = 25;

    //子弹射击方向 (向上发射：-1，向下发射：1)
    private int direction = 1;

    // 左右移动的计数器，用于控制移动方向
    private int moveCounter = 0;
    private final int moveInterval = 50;

    public ElitePlusEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootStrategy = new DoubleShoot(power, direction);
    }

    @Override
    public void forward() {
        super.forward();
        
        // 左右移动逻辑
        moveCounter++;
        if (moveCounter >= moveInterval) {
            moveCounter = 0;
            // 随机改变左右移动方向
            speedX = (int) (Math.random() * 6 - 3); // -3 到 2 之间的随机值
        }
        
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

}