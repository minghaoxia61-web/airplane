package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.application.ImageManager;

/**
 * Boss敌机工厂
 * @author hitsz
 */
public class BossEnemyFactory implements AircraftFactory {

    @Override
    public AbstractAircraft createAircraft() {
        int locationX = Main.WINDOW_WIDTH / 2;
        int locationY = (int) (Main.WINDOW_HEIGHT * 0.2);
        int speedX = 2;
        int speedY = 0;
        int hp = 1000;
        
        return new BossEnemy(locationX, locationY, speedX, speedY, hp);
    }

}
