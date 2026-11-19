package edu.hitsz.aircraft;

/**
 * 飞机工厂接口
 * 使用工厂方法模式创建敌机
 * @author hitsz
 */
public interface AircraftFactory {

    /**
     * 创建飞机实例
     * @return 飞机实例
     */
    AbstractAircraft createAircraft();

}