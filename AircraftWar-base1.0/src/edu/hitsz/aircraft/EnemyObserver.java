package edu.hitsz.aircraft;

public interface EnemyObserver {
    void onBomb();    // 炸弹触发时执行
    void onFreeze();  // 冰冻触发时执行
}
