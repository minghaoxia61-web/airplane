package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class StraightShoot implements ShootStrategy {

    private int power;
    private int direction;
    private int shootNum;

    public StraightShoot(int power, int direction, int shootNum) {
        this.power = power;
        this.direction = direction;
        this.shootNum = shootNum;
    }

    @Override
    public List<BaseBullet> doShoot(AbstractAircraft aircraft) {
        List<BaseBullet> res = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + direction * 2;
        int speedX = 0;
        int speedY = aircraft.getSpeedY() + direction * 5;
        
        for (int i = 0; i < shootNum; i++) {
            BaseBullet bullet;
            int bulletX = x + (i * 2 - shootNum + 1) * 10;
            
            if (direction == -1) {
                bullet = new HeroBullet(bulletX, y, speedX, speedY, power);
            } else {
                bullet = new EnemyBullet(bulletX, y, speedX, speedY, power);
            }
            res.add(bullet);
        }
        
        return res;
    }
}
