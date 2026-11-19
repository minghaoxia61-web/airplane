package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

public class FullScreenShoot implements ShootStrategy {

    private int power;
    private int direction;

    public FullScreenShoot(int power, int direction) {
        this.power = power;
        this.direction = direction;
    }

    @Override
    public List<BaseBullet> doShoot(AbstractAircraft aircraft) {
        List<BaseBullet> res = new LinkedList<>();
        int y = aircraft.getLocationY() + direction * 2;
        int speedX = 0;
        int speedY = aircraft.getSpeedY() + direction * 5;
        
        int bulletWidth = 10;
        int totalBullets = Main.WINDOW_WIDTH / bulletWidth;
        
        for (int i = 0; i < totalBullets; i++) {
            int bulletX = i * bulletWidth;
            BaseBullet bullet = new HeroBullet(bulletX, y, speedX, speedY, power);
            res.add(bullet);
        }
        
        return res;
    }
}
