package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.strategy.RingShoot;

/**
 * 超级火力道具
 * @author hitsz
 */
public class FirePlusProp extends AbstractProp {

    private static final long DURATION_MS = 5000;

    public FirePlusProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void active() {
        System.out.println("FirePlusPlus active!");
        HeroAircraft heroAircraft = HeroAircraft.getInstance();
        heroAircraft.applyFirepowerBuff(new RingShoot(30, -1, 12), DURATION_MS);
        System.out.println("超级火力道具生效：弹道切换为环射模式！");
    }

}
