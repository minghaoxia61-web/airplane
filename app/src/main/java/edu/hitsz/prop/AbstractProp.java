package edu.hitsz.prop;

import edu.hitsz.basic.AbstractFlyingObject;

/**
 * 道具抽象父类
 * @author hitsz
 */
public abstract class AbstractProp extends AbstractFlyingObject {

    public AbstractProp(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    /**
     * 道具激活方法
     */
    public abstract void active();

}
