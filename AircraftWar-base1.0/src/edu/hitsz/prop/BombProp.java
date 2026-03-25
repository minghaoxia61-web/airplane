package edu.hitsz.prop;

/**
 * 炸弹道具
 * @author hitsz
 */
public class BombProp extends AbstractProp {

    public BombProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void active() {
        // 炸弹逻辑
        System.out.println("BombSupply active!");
    }

}
