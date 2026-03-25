package edu.hitsz.prop;

/**
 * 超级火力道具
 * @author hitsz
 */
public class FirePlusProp extends AbstractProp {

    public FirePlusProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void active() {
        System.out.println("FirePlusPlus active!");
    }

}
