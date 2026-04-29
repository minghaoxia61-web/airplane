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
import edu.hitsz.dao.GameDifficulty;
import edu.hitsz.dao.ScoreRecordDao;
import edu.hitsz.dao.ScoreRecordDaoImpl;
import edu.hitsz.application.audio.AudioManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.Timer;

/**
 * 游戏主面板，游戏启动
 * @author hitsz
 */
public class Game extends JPanel {

    private int backGroundTop = 0;
    private final BufferedImage backgroundImage;

    //调度器, 用于定时任务调度
    private final Timer timer;
    //时间间隔(ms)，控制刷新频率
    private final int timeInterval = 40;

    private final HeroAircraft heroAircraft;
    private final List<AbstractAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<AbstractProp> props;
    
    private final Prop propSubject = new Prop();

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

    /** 本局难度，用于排行榜分文件存储 */
    private final GameDifficulty difficulty;
    
    private AbstractGame difficultyStrategy;

    /** 实验要求：使用默认玩家名保存成绩 */
    public static final String DEFAULT_PLAYER_NAME = "Player";

    public Game(GameDifficulty difficulty) {
        this.difficulty = difficulty;
        this.backgroundImage = ImageManager.getBackground(difficulty);
        heroAircraft = HeroAircraft.getInstance();

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        //启动英雄机鼠标监听
        new HeroController(this, heroAircraft);

        this.timer = new Timer("game-action-timer", true);
        
        initDifficultyStrategy();
    }
    
    private void initDifficultyStrategy() {
        switch (difficulty) {
            case EASY:
                difficultyStrategy = new EasyGame();
                break;
            case NORMAL:
                difficultyStrategy = new NormalGame();
                break;
            case HARD:
                difficultyStrategy = new HardGame();
                break;
            default:
                difficultyStrategy = new NormalGame();
        }
    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {
        difficultyStrategy.action();
        
        AudioManager.playBgmLoop();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                enemySpawnCounter++;
                if (enemySpawnCounter >=enemySpawnCycle) {
                    enemySpawnCounter = 0;
                    // 产生敌机
                    if (enemyAircrafts.size() < enemyMaxNumber) {
                        // 检查是否需要生成Boss敌机
                        if (score >= 500 && !bossSpawned && difficultyStrategy.canSpawnBoss()) {
                            AircraftFactory factory = new BossEnemyFactory();
                            AbstractAircraft boss = factory.createAircraft();
                            
                            if (difficultyStrategy instanceof HardGame) {
                                HardGame hardGame = (HardGame) difficultyStrategy;
                                boss.decreaseHp(-(boss.getHp() * (hardGame.getBossHpMultiplier() - 1)));
                                System.out.println("Boss敌机血量倍率:" + String.format("%.2f", hardGame.getBossHpMultiplier() - 1 + 1) + ".");
                                hardGame.onBossSpawned();
                            }
                            
                            enemyAircrafts.add(boss);
                            propSubject.addObserver(boss);
                            bossSpawned = true;
                            AudioManager.stopBgm();
                            AudioManager.playBossBgmLoop();
                            AudioManager.playSfxOnce("/videos/bomb_explosion.wav");
                            System.out.println("产生BOSS敌机");
                        } else {
                            double random = Math.random();
                            AircraftFactory factory;
                            
                            if (random < 0.7) {
                                factory = new MobEnemyFactory();
                            } else if (random < 0.9) {
                                factory = new EliteEnemyFactory();
                            } else if (random < 0.98) {
                                factory = new ElitePlusEnemyFactory();
                            } else {
                                factory = new UltimateEnemyFactory();
                            }
                            
                            AbstractAircraft enemy = factory.createAircraft();
                            enemyAircrafts.add(enemy);
                            propSubject.addObserver(enemy);
                        }
                    }
                }

                difficultyStrategy.difficultyGrow();
                
                shootAction();
                bulletsMoveAction();
                aircraftsMoveAction();
                propsMoveAction();
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
        // 敌机子弹攻击英雄机
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(bullet)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

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
                    // 实验五：子弹击中敌机音效
                    AudioManager.playSfxOnce("/videos/bullet_hit.wav"); // 使用现有音效
                    if (enemyAircraft.notValid()) {
                        // 获得分数，产生道具补给
                        score += 10;
                        if (enemyAircraft instanceof BossEnemy) {
                            // Boss 坠毁时停止专属 BGM，恢复普通 BGM
                            AudioManager.stopBossBgm();
                            if (!gameOverFlag) {
                                AudioManager.playBgmLoop();
                            }
                        }
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
                if (prop instanceof BombProp) {
                    AudioManager.playSfxOnce("/videos/bomb_explosion.wav");
                    propSubject.triggerBomb();
                } else if (prop instanceof FreezeProp) {
                    AudioManager.playSfxOnce("/videos/get_supply.wav");
                    propSubject.triggerFreeze();
                } else {
                    AudioManager.playSfxOnce("/videos/get_supply.wav");
                }
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
        enemyAircrafts.removeIf(enemy -> {
            if (enemy.notValid()) {
                propSubject.removeObserver(enemy);
                return true;
            }
            return false;
        });
        props.removeIf(AbstractFlyingObject::notValid);
    }

    /**
     * 检查游戏是否结束，若结束：关闭线程池
     */
    private void checkResultAction(){
        // 游戏结束检查英雄机是否存活
        if (heroAircraft.getHp() <= 0 && !gameOverFlag) {
            timer.cancel(); // 取消定时器并终止所有调度任务
            gameOverFlag = true;
            System.out.println("Game Over!");
            final int finalScore = this.score;
            SwingUtilities.invokeLater(() -> handleGameOver(finalScore));
        }
    }

    /**
     * 游戏结束：弹出输入窗口填写姓名；支持保存本局成绩；弹出排行榜界面并支持删除记录。
     */
    private void handleGameOver(int finalScore) {
        // 实验五：游戏结束停止背景音乐，并播放结束音效
        AudioManager.stopAll();
        AudioManager.playSfxOnce("/videos/game_over.wav");

        ScoreRecordDao dao = new ScoreRecordDaoImpl(difficulty);
        String name = JOptionPane.showInputDialog(
                this,
                "本局得分: " + finalScore + "\n请输入玩家姓名（取消则不保存）：",
                DEFAULT_PLAYER_NAME
        );
        if (name != null) {
            name = name.trim();
            if (name.isEmpty()) {
                name = DEFAULT_PLAYER_NAME;
            }
            dao.insert(name, finalScore, LocalDateTime.now());
        }

        LeaderboardFrame.showLeaderboard(dao);
    }

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
        g.drawImage(backgroundImage, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(backgroundImage, 0, this.backGroundTop, null);
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
