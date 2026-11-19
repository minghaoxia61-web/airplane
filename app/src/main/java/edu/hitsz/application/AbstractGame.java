package edu.hitsz.application;

public abstract class AbstractGame {
    public final void action() {
        initDifficulty();
        createEnemy();
        difficultyGrow();
        checkBoss();
    }

    protected abstract void initDifficulty();
    protected abstract void createEnemy();
    protected abstract void difficultyGrow();
    protected abstract void checkBoss();

    protected boolean canSpawnBoss() {
        return true;
    }
}
