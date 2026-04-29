package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;

/**
 * 加血道具
 * @author hitsz
 */
public class HpProp extends AbstractProp {

    private static final int HP_INCREASE = 20;

    public HpProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void active() {
        HeroAircraft hero = HeroAircraft.getInstance();
        hero.increaseHp(HP_INCREASE);
        System.out.println("HpSupply active! +" + HP_INCREASE + " HP");
    }

}
