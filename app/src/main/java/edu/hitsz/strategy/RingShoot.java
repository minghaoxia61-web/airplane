package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class RingShoot implements ShootStrategy {

    private int power;
    private int direction;
    private int bulletCount;

    public RingShoot(int power, int direction, int bulletCount) {
        this.power = power;
        this.direction = direction;
        this.bulletCount = bulletCount;
    }

    @Override
    public List<BaseBullet> doShoot(AbstractAircraft aircraft) {
        List<BaseBullet> res = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY();
        
        double angleStep = 2 * Math.PI / bulletCount;
        int radius = 10;
        int bulletSpeed = 5;
        
        for (int i = 0; i < bulletCount; i++) {
            double angle = i * angleStep;
            int speedX = (int) (bulletSpeed * Math.cos(angle));
            int speedY = (int) (bulletSpeed * Math.sin(angle));
            
            if (direction == 1) {
                speedY = Math.abs(speedY);
            } else {
                speedY = -Math.abs(speedY);
            }
            
            BaseBullet bullet;
            if (direction == -1) {
                bullet = new HeroBullet(x, y, speedX, speedY, power);
            } else {
                bullet = new EnemyBullet(x, y, speedX, speedY, power);
            }
            res.add(bullet);
        }
        
        return res;
    }
}
