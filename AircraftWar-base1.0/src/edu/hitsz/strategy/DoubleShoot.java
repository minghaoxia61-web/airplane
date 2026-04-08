package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class DoubleShoot implements ShootStrategy {

    private int power;
    private int direction;

    public DoubleShoot(int power, int direction) {
        this.power = power;
        this.direction = direction;
    }

    @Override
    public List<BaseBullet> doShoot(AbstractAircraft aircraft) {
        List<BaseBullet> res = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + direction * 2;
        int speedX = 0;
        int speedY = aircraft.getSpeedY() + direction * 5;
        
        BaseBullet bullet1;
        BaseBullet bullet2;
        
        if (direction == -1) {
            bullet1 = new HeroBullet(x - 10, y, speedX, speedY, power);
            bullet2 = new HeroBullet(x + 10, y, speedX, speedY, power);
        } else {
            bullet1 = new EnemyBullet(x - 10, y, speedX, speedY, power);
            bullet2 = new EnemyBullet(x + 10, y, speedX, speedY, power);
        }
        
        res.add(bullet1);
        res.add(bullet2);
        
        return res;
    }
}
