package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

import java.util.Random;

/**
 * 普通敌机工厂
 * @author hitsz
 */
public class MobEnemyFactory implements AircraftFactory {

    @Override
    public AbstractAircraft createAircraft() {
        Random random = new Random();
        return new MobEnemy(
                (int) (random.nextDouble() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int) (random.nextDouble() * Main.WINDOW_HEIGHT * 0.05),
                0,
                10,
                30
        );
    }

}