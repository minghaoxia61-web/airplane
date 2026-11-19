package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class ScatteredShoot implements ShootStrategy {

    private int power;
    private int direction;
    private int bulletCount;

    public ScatteredShoot(int power, int direction, int bulletCount) {
        this.power = power;
        this.direction = direction;
        this.bulletCount = bulletCount;
    }

    @Override
    public List<BaseBullet> doShoot(AbstractAircraft aircraft) {
        List<BaseBullet> res = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + direction * 2;
        int speedY = aircraft.getSpeedY() + direction * 5;
        
        int spreadAngle = 4;
        int startX = -((bulletCount - 1) * spreadAngle) / 2;
        
        for (int i = 0; i < bulletCount; i++) {
            int speedX = startX + i * spreadAngle;
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
