package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

import java.util.Random;

/**
 * 精锐敌机工厂
 * @author hitsz
 */
public class ElitePlusEnemyFactory implements AircraftFactory {

    @Override
    public AbstractAircraft createAircraft() {
        Random random = new Random();
        return new ElitePlusEnemy(
                (int) (random.nextDouble() * (Main.WINDOW_WIDTH - ImageManager.ELITE_PLUS_ENEMY_IMAGE.getWidth())),
                (int) (random.nextDouble() * Main.WINDOW_HEIGHT * 0.05),
                0,
                6,
                80
        );
    }

}