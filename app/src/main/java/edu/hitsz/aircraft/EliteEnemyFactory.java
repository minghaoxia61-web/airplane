package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

import java.util.Random;

/**
 * 精英敌机工厂
 * @author hitsz
 */
public class EliteEnemyFactory implements AircraftFactory {

    @Override
    public AbstractAircraft createAircraft() {
        Random random = new Random();
        return new EliteEnemy(
                (int) (random.nextDouble() * (Main.WINDOW_WIDTH - ImageManager.ELITE_ENEMY_IMAGE.getWidth())),
                (int) (random.nextDouble() * Main.WINDOW_HEIGHT * 0.05),
                0,
                8,
                50
        );
    }

}