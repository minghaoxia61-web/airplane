package edu.hitsz.application;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.AircraftFactory;
import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.aircraft.BossEnemyFactory;
import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.aircraft.EliteEnemyFactory;
import edu.hitsz.aircraft.ElitePlusEnemy;
import edu.hitsz.aircraft.ElitePlusEnemyFactory;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.MobEnemyFactory;
import edu.hitsz.aircraft.UltimateEnemy;
import edu.hitsz.aircraft.UltimateEnemyFactory;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.dao.GameDifficulty;
import edu.hitsz.music.MyMediaPlayer;
import edu.hitsz.music.MySoundPool;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.BombProp;
import edu.hitsz.prop.FreezeProp;
import edu.hitsz.prop.Prop;
import edu.hitsz.prop.PropFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 游戏主面板，游戏启动（Android 版本）
 * Windows 端的 Game(JPanel + Swing Timer) 在 Android 上移植为
 * SurfaceView + SurfaceHolder.Callback + 独立绘制线程：
 * 逻辑更新与画面绘制都在子线程中按固定周期执行，触控在 UI 线程回调。
 */
public class BaseGame extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final String TAG = "BaseGame";

    private int backGroundTop = 0;
    private final Bitmap backgroundImage;

    //时间间隔(ms)，控制刷新频率（与 Windows 端 timeInterval 保持一致）
    private static final int TIME_INTERVAL = 40;

    private final HeroAircraft heroAircraft;
    private final List<AbstractAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<AbstractProp> props;

    private final Prop propSubject = new Prop();

    //屏幕中出现的敌机最大数量
    private final int enemyMaxNumber = 5;

    //敌机生成周期
    protected double enemySpawnCycle = 20;
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

    /** 本局难度 */
    private final GameDifficulty difficulty;

    private AbstractGame difficultyStrategy;

    //绘制线程控制标志
    private boolean running = false;
    private Thread gameThread;

    //主线程 UI Handler：由 GameActivity 注入，用于子线程通知主线程更新 UI（实验三 Handler 知识点）
    private android.os.Handler uiHandler;

    /** Game Over 消息标识（与 GameActivity.MSG_GAME_OVER 保持一致） */
    public static final int MSG_GAME_OVER = 1;

    //画笔：绘制得分和生命值
    private final Paint textPaint;

    public BaseGame(Context context, GameDifficulty difficulty) {
        super(context);
        this.difficulty = difficulty;

        getHolder().addCallback(this);
        setFocusable(true);

        // 用屏幕真实尺寸初始化窗口常量与图片资源（一次性解码+缩放）
        int screenW = context.getResources().getDisplayMetrics().widthPixels;
        int screenH = context.getResources().getDisplayMetrics().heightPixels;
        Main.WINDOW_WIDTH = screenW;
        Main.WINDOW_HEIGHT = screenH;
        ImageManager.init(context.getResources(), screenW, screenH);

        this.backgroundImage = ImageManager.getBackground(difficulty);
        heroAircraft = HeroAircraft.getInstance();
        // 实验三修复：单例英雄机恢复初始状态（满血、居中、清大招状态），保证重开局正常
        heroAircraft.reset();
        Log.i(TAG, "诊断: screen=" + screenW + "x" + screenH
                + ", WINDOW=" + Main.WINDOW_WIDTH + "x" + Main.WINDOW_HEIGHT
                + ", hero=(" + heroAircraft.getLocationX() + "," + heroAircraft.getLocationY() + ")"
                + ", heroHp=" + heroAircraft.getHp());

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(22 * Math.max(1f, screenW / 512f));

        initDifficultyStrategy();
    }

    /**
     * 注入主线程 Handler（实验三）：游戏子线程通过它向 UI 线程发送消息
     */
    public void setUiHandler(android.os.Handler uiHandler) {
        this.uiHandler = uiHandler;
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

    //***********************
    //      游戏主循环
    //***********************

    /**
     * 游戏启动入口：由 surfaceCreated 调起绘制线程后执行一次性初始化
     */
    private void startGame() {
        difficultyStrategy.action();
        // 实验三：预加载游戏音效（SoundPool.load 为异步，尽早调用）
        MySoundPool.getInstance(getContext());
    }

    /**
     * 单个逻辑帧，对应 Windows 端 TimerTask 的执行体
     */
    private void logicFrame() {
        enemySpawnCounter++;
        if (enemySpawnCounter >= enemySpawnCycle) {
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
                        Log.i(TAG, "Boss敌机血量倍率:" + String.format("%.2f", (double) hardGame.getBossHpMultiplier()));
                        hardGame.onBossSpawned();
                    }

                    enemyAircrafts.add(boss);
                    propSubject.addObserver(boss);
                    bossSpawned = true;
                    // 实验三：Boss 出现时暂停游戏 BGM，切换 Boss BGM（课件要求）
                    MyMediaPlayer.getInstance(getContext()).switchToBossBgm();
                    Log.i(TAG, "产生BOSS敌机");
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
        // 游戏结束检查
        checkResultAction();
    }

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
                // 实验三：敌机子弹击中英雄机音效
                MySoundPool.getInstance(getContext()).playBulletHit();
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
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    // 实验三：英雄机子弹击中敌机音效
                    MySoundPool.getInstance(getContext()).playBulletHit();
                    if (enemyAircraft.notValid()) {
                        // 获得分数，产生道具补给
                        score += 10;
                        // Boss坠毁时随机掉落3个道具
                        if (enemyAircraft instanceof BossEnemy) {
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
                    propSubject.triggerBomb();
                    // 实验三：炸弹爆炸音效
                    MySoundPool.getInstance(getContext()).playBombExplosion();
                } else {
                    if (prop instanceof FreezeProp) {
                        propSubject.triggerFreeze();
                    }
                    // 实验三：获取补给道具音效
                    MySoundPool.getInstance(getContext()).playGetSupply();
                }
                prop.vanish();
            }
        }
    }

    /**
     * 后处理：删除无效的子弹、敌机、道具
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(enemy -> {
            if (enemy.notValid()) {
                propSubject.removeObserver(enemy);
                // 实验三：Boss 坠毁时恢复游戏 BGM
                if (enemy instanceof BossEnemy) {
                    MyMediaPlayer.getInstance(getContext()).backToGameBgm();
                }
                return true;
            }
            return false;
        });
        props.removeIf(AbstractFlyingObject::notValid);
    }

    /**
     * 检查游戏是否结束
     */
    private void checkResultAction() {
        if (heroAircraft.getHp() <= 0 && !gameOverFlag) {
            gameOverFlag = true;
            running = false; // 终止绘制线程（对应 Windows 端 timer.cancel()）
            // 实验三：游戏结束，停止背景音乐并播放结束音效
            MyMediaPlayer.getInstance(getContext()).stopAll();
            MySoundPool.getInstance(getContext()).playGameOver();
            Log.i(TAG, "Game Over! score = " + score);
            // 子线程不能直接更新 UI，通过 Handler 把消息发回主线程（实验三 Handler 知识点）
            if (uiHandler != null) {
                android.os.Message msg = android.os.Message.obtain();
                msg.what = MSG_GAME_OVER;
                msg.obj = score;
                uiHandler.sendMessage(msg);
            }
        }
    }

    //***********************
    //      Paint 各部分
    //***********************

    /**
     * 绘制一帧，对应 Windows 端 paint(Graphics)
     */
    private void drawFrame(Canvas canvas) {
        // 绘制背景,图片滚动
        canvas.drawBitmap(backgroundImage, 0, backGroundTop - Main.WINDOW_HEIGHT, null);
        canvas.drawBitmap(backgroundImage, 0, backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 先绘制子弹，后绘制飞机，这样子弹显示在飞机的下层
        paintImageWithPosition(canvas, enemyBullets);
        paintImageWithPosition(canvas, heroBullets);
        paintImageWithPosition(canvas, enemyAircrafts);
        paintImageWithPosition(canvas, props);

        Bitmap heroImage = ImageManager.HERO_IMAGE;
        canvas.drawBitmap(heroImage,
                heroAircraft.getLocationX() - heroImage.getWidth() / 2f,
                heroAircraft.getLocationY() - heroImage.getHeight() / 2f, null);

        //绘制得分和生命值
        paintScoreAndLife(canvas);
    }

    private void paintImageWithPosition(Canvas canvas, List<? extends AbstractFlyingObject> objects) {
        for (AbstractFlyingObject object : objects) {
            Bitmap image = object.getImage();
            if (image == null) {
                continue;
            }
            canvas.drawBitmap(image,
                    object.getLocationX() - image.getWidth() / 2f,
                    object.getLocationY() - image.getHeight() / 2f, null);
        }
    }

    private void paintScoreAndLife(Canvas canvas) {
        int x = 10;
        int y = (int) (textPaint.getTextSize() + 5);
        canvas.drawText("SCORE: " + this.score, x, y, textPaint);
        y += textPaint.getTextSize() + 5;
        canvas.drawText("LIFE: " + this.heroAircraft.getHp(), x, y, textPaint);
    }

    //***********************
    //      SurfaceView 生命周期与绘制线程
    //***********************

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        startGame();
        // 实验三：进入游戏，开始播放背景音乐
        MyMediaPlayer.getInstance(getContext()).startGameBgm();
        running = true;
        gameThread = new Thread(this, "game-draw-thread");
        gameThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        Main.WINDOW_WIDTH = width;
        Main.WINDOW_HEIGHT = height;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // 停止绘制线程并等待其真正退出
        running = false;
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // 实验三：退出游戏界面，释放音乐资源
        MyMediaPlayer.getInstance(getContext()).stopAll();
    }

    @Override
    public void run() {
        while (running) {
            long frameStart = System.currentTimeMillis();
            synchronized (getHolder()) {
                if (!gameOverFlag) {
                    logicFrame();
                }
                Canvas canvas = getHolder().lockCanvas();
                if (canvas != null) {
                    try {
                        drawFrame(canvas);
                    } finally {
                        getHolder().unlockCanvasAndPost(canvas);
                    }
                }
            }
            long elapsed = System.currentTimeMillis() - frameStart;
            long sleep = TIME_INTERVAL - elapsed;
            if (sleep > 0) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    //***********************
    //      触控（替代 Windows 端 HeroController 鼠标拖动）
    //***********************

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                heroAircraft.setLocation(event.getX(), event.getY());
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }
}
