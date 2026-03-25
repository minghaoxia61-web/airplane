package edu.hitsz.prop;

/**
 * 加血道具
 * @author hitsz
 */
public class HpProp extends AbstractProp {

    public HpProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void active() {
        // 加血逻辑
        System.out.println("HpSupply active!");
    }

}
