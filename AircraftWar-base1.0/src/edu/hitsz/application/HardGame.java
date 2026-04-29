package edu.hitsz.application;

public class HardGame extends AbstractGame {
    private int timeCounter = 0;
    private double enemySpeedMultiplier = 1.0;
    private int enemyHpBonus = 0;
    private double enemyFireRateMultiplier = 1.0;
    private int bossHpMultiplier = 1;

    @Override
    protected void initDifficulty() {
        timeCounter = 0;
        enemySpeedMultiplier = 1.0;
        enemyHpBonus = 0;
        enemyFireRateMultiplier = 1.0;
        bossHpMultiplier = 1;
    }

    @Override
    protected void createEnemy() {
    }

    private double eliteProbability = 0.2;
    private double enemyCycle = 20.0;

    @Override
    protected void difficultyGrow() {
        timeCounter++;
        if (timeCounter >= 80) {
            timeCounter = 0;
            enemySpeedMultiplier += 0.15;
            enemyHpBonus += 15;
            enemyFireRateMultiplier -= 0.05;
            eliteProbability += 0.02;
            enemyCycle -= 0.28;
            double attributeMultiplier = enemySpeedMultiplier;
            System.out.println(String.format("提高难度！精英机概率:%.2f,敌机周期:%.2f,敌机属性提升倍率:%.2f.", 
                    eliteProbability, enemyCycle, attributeMultiplier));
        }
    }

    @Override
    protected void checkBoss() {
    }

    public void onBossSpawned() {
        bossHpMultiplier++;
    }

    public double getEnemySpeedMultiplier() {
        return enemySpeedMultiplier;
    }

    public int getEnemyHpBonus() {
        return enemyHpBonus;
    }

    public double getEnemyFireRateMultiplier() {
        return enemyFireRateMultiplier;
    }

    public int getBossHpMultiplier() {
        return bossHpMultiplier;
    }
}
