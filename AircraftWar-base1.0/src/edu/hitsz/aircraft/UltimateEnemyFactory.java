package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

import java.util.Random;

/**
 * 王牌敌机工厂
 * @author hitsz
 */
public class UltimateEnemyFactory implements AircraftFactory {

    @Override
    public AbstractAircraft createAircraft() {
        Random random = new Random();
        return new UltimateEnemy(
                (int) (random.nextDouble() * (Main.WINDOW_WIDTH - ImageManager.ULTIMATE_ENEMY_IMAGE.getWidth())),
                (int) (random.nextDouble() * Main.WINDOW_HEIGHT * 0.05),
                0,
                4,
                120
        );
    }

}