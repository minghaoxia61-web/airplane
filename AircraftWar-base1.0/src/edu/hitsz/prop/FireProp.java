package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.strategy.ScatteredShoot;

/**
 * 火力道具
 * @author hitsz
 */
public class FireProp extends AbstractProp {

    public FireProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void active() {
        System.out.println("FireSupply active!");
        HeroAircraft heroAircraft = HeroAircraft.getInstance();
        heroAircraft.setShootStrategy(new ScatteredShoot(30, -1, 5));
        System.out.println("火力道具生效：弹道切换为散射模式！");
    }

}
