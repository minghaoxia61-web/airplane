package edu.hitsz.prop;

import edu.hitsz.aircraft.EnemyObserver;
import java.util.ArrayList;
import java.util.List;

public abstract class PropSubject {
    protected List<EnemyObserver> observers = new ArrayList<>();

    public void addObserver(EnemyObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(EnemyObserver observer) {
        observers.remove(observer);
    }

    public abstract void notifyBomb();
    public abstract void notifyFreeze();
}
