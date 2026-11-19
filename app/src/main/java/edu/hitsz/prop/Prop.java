package edu.hitsz.prop;

import edu.hitsz.aircraft.EnemyObserver;

public class Prop extends PropSubject {
    @Override
    public void notifyBomb() {
        for (EnemyObserver observer : observers) {
            observer.onBomb();
        }
    }

    @Override
    public void notifyFreeze() {
        for (EnemyObserver observer : observers) {
            observer.onFreeze();
        }
    }

    public void triggerBomb() { notifyBomb(); }
    public void triggerFreeze() { notifyFreeze(); }
}
