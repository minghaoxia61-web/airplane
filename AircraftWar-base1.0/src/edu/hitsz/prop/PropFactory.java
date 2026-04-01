package edu.hitsz.prop;

/**
 * 道具工厂类
 * 使用简单工厂模式创建道具
 * @author hitsz
 */
public class PropFactory {

    /**
     * 创建道具实例
     * @param type 道具类型
     * @param x 位置x坐标
     * @param y 位置y坐标
     * @return 对应的道具实例
     */
    public static AbstractProp createProp(String type, int x, int y) {
        int speedX = 0;
        int speedY = 5;
        
        switch (type) {
            case "Hp":
                return new HpProp(x, y, speedX, speedY);
            case "Fire":
                return new FireProp(x, y, speedX, speedY);
            case "FirePlus":
                return new FirePlusProp(x, y, speedX, speedY);
            case "Bomb":
                return new BombProp(x, y, speedX, speedY);
            case "Freeze":
                return new FreezeProp(x, y, speedX, speedY);
            default:
                return null;
        }
    }

}