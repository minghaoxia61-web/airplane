package edu.hitsz.application;

import edu.hitsz.aircraft.*;
import edu.hitsz.aircraft.AircraftFactory;
import edu.hitsz.aircraft.MobEnemyFactory;
import edu.hitsz.aircraft.EliteEnemyFactory;
import edu.hitsz.aircraft.ElitePlusEnemyFactory;
import edu.hitsz.aircraft.UltimateEnemyFactory;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.prop.*;
import edu.hitsz.prop.PropFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.*;

/**
 * 游戏主面板，游戏启动
 * @author hitsz
 */
public class Game extends JPanel {

    private int backGroundTop = 0;

    //调度器, 用于定时任务调度
    private final Timer timer;
    //时间间隔(ms)，控制刷新频率
    private final int timeInterval = 40;

    private final HeroAircraft heroAircraft;
    private final List<AbstractAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<AbstractProp> props;

    //屏幕中出现的敌机最大数量
    private final int enemyMaxNumber = 5;

    //敌机生成周期
    protected double enemySpawnCycle  =  20;
    private int enemySpawnCounter = 0;

    //英雄机和敌机射击周期
    protected double shootCycle = 20;
    private int shootCounter = 0;

    //当前玩家分数
    private int score = 0;
    
    //Boss生成标记
    private boolean bossSpawned = false;

    //游戏结束标志
    private boolean gameOverFlag = false;

    public Game() {
        heroAircraft = HeroAircraft.getInstance();

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        //启动英雄机鼠标监听
        new HeroController(this, heroAircraft);

        this.timer = new Timer("game-action-timer", true);

    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {

        // 定时任务：绘制、对象产生、碰撞判定、及结束判定
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                enemySpawnCounter++;
                if (enemySpawnCounter >=enemySpawnCycle) {
                    enemySpawnCounter = 0;
                    // 产生敌机
                    if (enemyAircrafts.size() < enemyMaxNumber) {
                        // 检查是否需要生成Boss敌机
                        if (score >= 500 && !bossSpawned) {
                            AircraftFactory factory = new BossEnemyFactory();
                            AbstractAircraft boss = factory.createAircraft();
                            enemyAircrafts.add(boss);
                            bossSpawned = true;
                        } else {
                            double random = Math.random();
                            AircraftFactory factory;
                            
                            if (random < 0.7) {
                                // 70% 概率生成普通敌机
                                factory = new MobEnemyFactory();
                            } else if (random < 0.9) {
                                // 20% 概率生成精英敌机
                                factory = new EliteEnemyFactory();
                            } else if (random < 0.98) {
                                // 8% 概率生成精锐敌机
                                factory = new ElitePlusEnemyFactory();
                            } else {
                                // 2% 概率生成王牌敌机
                                factory = new UltimateEnemyFactory();
                            }
                            
                            AbstractAircraft enemy = factory.createAircraft();
                            enemyAircrafts.add(enemy);
                        }
                    }
                }

                // 飞机发射子弹
                shootAction();
                // 子弹移动
                bulletsMoveAction();
                // 飞机移动
                aircraftsMoveAction();
                // 道具移动
                propsMoveAction();
                // 撞击检测
                crashCheckAction();
                // 后处理
                postProcessAction();
                // 重绘界面
                repaint();
                // 游戏结束检查
                checkResultAction();
            }
        };
        // 以固定延迟时间进行执行：本次任务执行完成后，延迟 timeInterval 再执行下一次
        timer.schedule(task,0,timeInterval);

    }

    //***********************
    //      Action 各部分
    //***********************

    private void shootAction() {
        shootCounter++;
        if (shootCounter >= shootCycle) {
            shootCounter = 0;
            //英雄机射击
            heroBullets.addAll(heroAircraft.shoot());
            // 敌机射击
            for (AbstractAircraft enemy : enemyAircrafts) {
                enemyBullets.addAll(enemy.shoot());
            }
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    /**
     * 道具移动
     */
    private void propsMoveAction() {
        for (AbstractProp prop : props) {
            prop.forward();
        }
    }

    /**
     * 生成道具
     */
    private void generateProp(int x, int y) {
        Random random = new Random();
        String[] propTypes = {"Hp", "Fire", "FirePlus", "Bomb", "Freeze"};
        String propType = propTypes[random.nextInt(propTypes.length)];
        
        AbstractProp prop = PropFactory.createProp(propType, x, y);
        if (prop != null) {
            props.add(prop);
        }
    }


    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // TODO 敌机子弹攻击英雄机

        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        // 获得分数，产生道具补给
                        score += 10;
                        // 敌机坠毁时生成道具
                        if (enemyAircraft instanceof BossEnemy) {
                            // Boss敌机坠毁时随机掉落3个道具
                            for (int i = 0; i < 3; i++) {
                                Random random = new Random();
                                String[] propTypes = {"Hp", "Fire", "FirePlus", "Bomb", "Freeze"};
                                String propType = propTypes[random.nextInt(propTypes.length)];
                                int offsetX = random.nextInt(60) - 30;
                                int offsetY = random.nextInt(60) - 30;
                                AbstractProp prop = PropFactory.createProp(propType, 
                                        enemyAircraft.getLocationX() + offsetX, 
                                        enemyAircraft.getLocationY() + offsetY);
                                if (prop != null) {
                                    props.add(prop);
                                }
                            }
                        } else if (Math.random() < 0.3) {
                            if (enemyAircraft instanceof EliteEnemy) {
                                generateProp(enemyAircraft.getLocationX(), enemyAircraft.getLocationY());
                            } else if (enemyAircraft instanceof ElitePlusEnemy) {
                                // 精锐敌机坠毁时随机掉落4种道具（不含冰冻）
                                Random random = new Random();
                                String[] propTypes = {"Hp", "Fire", "FirePlus", "Bomb"};
                                String propType = propTypes[random.nextInt(propTypes.length)];
                                AbstractProp prop = PropFactory.createProp(propType, 
                                        enemyAircraft.getLocationX(), enemyAircraft.getLocationY());
                                if (prop != null) {
                                    props.add(prop);
                                }
                            } else if (enemyAircraft instanceof UltimateEnemy) {
                                // 王牌敌机坠毁时随机掉落全部5种道具
                                generateProp(enemyAircraft.getLocationX(), enemyAircraft.getLocationY());
                            }
                        }
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // 我方获得道具，道具生效
        for (AbstractProp prop : props) {
            if (prop.notValid()) {
                continue;
            }
            if (prop.crash(heroAircraft) || heroAircraft.crash(prop)) {
                prop.active();
                prop.vanish();
            }
        }

    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 删除无效的道具
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    /**
     * 检查游戏是否结束，若结束：关闭线程池
     */
    private void checkResultAction(){
        // 游戏结束检查英雄机是否存活
        if (heroAircraft.getHp() <= 0) {
            timer.cancel(); // 取消定时器并终止所有调度任务
            gameOverFlag = true;
            System.out.println("Game Over!");
        }
    };

    //***********************
    //      Paint 各部分
    //***********************
    /**
     * 重写 paint方法
     * 通过重复调用paint方法，实现游戏动画
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 绘制背景,图片滚动
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 先绘制子弹，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);
        paintImageWithPositionRevised(g, enemyAircrafts);

        // 绘制道具
        paintImageWithPositionRevised(g, props);

        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        //绘制得分和生命值
        paintScoreAndLife(g);

    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.isEmpty()) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(Color.RED);
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE: " + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE: " + this.heroAircraft.getHp(), x, y);
    }

}
