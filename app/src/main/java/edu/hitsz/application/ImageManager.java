package edu.hitsz.application;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.res.Resources;

import edu.hitsz.R;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.aircraft.ElitePlusEnemy;
import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.aircraft.UltimateEnemy;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.HpProp;
import edu.hitsz.prop.FireProp;
import edu.hitsz.prop.FirePlusProp;
import edu.hitsz.prop.BombProp;
import edu.hitsz.prop.FreezeProp;
import edu.hitsz.dao.GameDifficulty;

/**
 * 图片资源管理器（Android 版本）
 * Windows 端使用 ImageIO 读取，Android 端使用 BitmapFactory 读取 res/drawable 资源。
 * 所有图片只在 init() 时解码一次并按屏幕比例缩放，游戏循环中仅复用缓存。
 */
public class ImageManager {

    /** Windows 端设计分辨率宽度，用于统一缩放比例 */
    private static final int DESIGN_WIDTH = 512;

    // 背景图
    public static Bitmap BACKGROUND_IMAGE;
    public static Bitmap BACKGROUND_EASY_IMAGE;
    public static Bitmap BACKGROUND_NORMAL_IMAGE;
    public static Bitmap BACKGROUND_HARD_IMAGE;
    // 英雄机
    public static Bitmap HERO_IMAGE;
    // 普通敌机
    public static Bitmap MOB_ENEMY_IMAGE;
    // 精英敌机
    public static Bitmap ELITE_ENEMY_IMAGE;
    // 精锐敌机
    public static Bitmap ELITE_PLUS_ENEMY_IMAGE;
    // Boss敌机
    public static Bitmap BOSS_ENEMY_IMAGE;
    // 王牌敌机
    public static Bitmap ULTIMATE_ENEMY_IMAGE;
    // 英雄机子弹
    public static Bitmap HERO_BULLET_IMAGE;
    // 敌机子弹
    public static Bitmap ENEMY_BULLET_IMAGE;
    // 道具
    public static Bitmap HP_PROP_IMAGE;
    public static Bitmap FIRE_PROP_IMAGE;
    public static Bitmap FIRE_PLUS_PROP_IMAGE;
    public static Bitmap BOMB_PROP_IMAGE;
    public static Bitmap FREEZE_PROP_IMAGE;

    /**
     * 启动时一次性解码全部图片资源。
     * @param res 资源对象
     * @param screenW 屏幕宽（像素）
     * @param screenH 屏幕高（像素）
     */
    public static void init(Resources res, int screenW, int screenH) {
        // 精灵图按 屏幕宽/设计宽 的比例统一放大，保持与 Windows 端相对布局一致
        float scale = Math.max(1f, screenW / (float) DESIGN_WIDTH);

        // 背景图拉伸到全屏
        BACKGROUND_IMAGE = load(res, R.drawable.bg, screenW, screenH);
        BACKGROUND_EASY_IMAGE = load(res, R.drawable.bg2, screenW, screenH);
        BACKGROUND_NORMAL_IMAGE = load(res, R.drawable.bg3, screenW, screenH);
        BACKGROUND_HARD_IMAGE = load(res, R.drawable.bg4, screenW, screenH);

        HERO_IMAGE = load(res, R.drawable.hero, scale, scale);
        MOB_ENEMY_IMAGE = load(res, R.drawable.mob, scale, scale);
        ELITE_ENEMY_IMAGE = load(res, R.drawable.elite, scale, scale);
        ELITE_PLUS_ENEMY_IMAGE = load(res, R.drawable.elite_plus, scale, scale);
        BOSS_ENEMY_IMAGE = load(res, R.drawable.boss, scale, scale);
        ULTIMATE_ENEMY_IMAGE = load(res, R.drawable.elite_pro, scale, scale);
        HERO_BULLET_IMAGE = load(res, R.drawable.bullet_hero, scale, scale);
        ENEMY_BULLET_IMAGE = load(res, R.drawable.bullet_enemy, scale, scale);
        HP_PROP_IMAGE = load(res, R.drawable.prop_blood, scale, scale);
        FIRE_PROP_IMAGE = load(res, R.drawable.prop_bullet, scale, scale);
        FIRE_PLUS_PROP_IMAGE = load(res, R.drawable.prop_bullet_plus, scale, scale);
        BOMB_PROP_IMAGE = load(res, R.drawable.prop_bomb, scale, scale);
        FREEZE_PROP_IMAGE = load(res, R.drawable.prop_freeze, scale, scale);
    }

    /**
     * 解码并缩放图片；inScaled=false 保证拿到原始像素尺寸，避免密度缩放干扰
     */
    private static Bitmap load(Resources res, int resId, float scaleX, float scaleY) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap raw = BitmapFactory.decodeResource(res, resId, options);
        if (raw == null) {
            return Bitmap.createBitmap(8, 8, Bitmap.Config.ARGB_8888);
        }
        int w = Math.max(1, Math.round(raw.getWidth() * scaleX));
        int h = Math.max(1, Math.round(raw.getHeight() * scaleY));
        if (w == raw.getWidth() && h == raw.getHeight()) {
            return raw;
        }
        return Bitmap.createScaledBitmap(raw, w, h, true);
    }

    public static Bitmap getBackground(GameDifficulty difficulty) {
        if (difficulty == null) {
            return BACKGROUND_IMAGE;
        }
        return switch (difficulty) {
            case EASY -> (BACKGROUND_EASY_IMAGE != null ? BACKGROUND_EASY_IMAGE : BACKGROUND_IMAGE);
            case NORMAL -> (BACKGROUND_NORMAL_IMAGE != null ? BACKGROUND_NORMAL_IMAGE : BACKGROUND_IMAGE);
            case HARD -> (BACKGROUND_HARD_IMAGE != null ? BACKGROUND_HARD_IMAGE : BACKGROUND_IMAGE);
        };
    }

    public static Bitmap get(AbstractFlyingObject obj) {
        if (obj instanceof HeroAircraft) {
            return HERO_IMAGE;
        } else if (obj instanceof MobEnemy) {
            return MOB_ENEMY_IMAGE;
        } else if (obj instanceof EliteEnemy) {
            return ELITE_ENEMY_IMAGE;
        } else if (obj instanceof ElitePlusEnemy) {
            return ELITE_PLUS_ENEMY_IMAGE;
        } else if (obj instanceof BossEnemy) {
            return BOSS_ENEMY_IMAGE;
        } else if (obj instanceof UltimateEnemy) {
            return ULTIMATE_ENEMY_IMAGE;
        } else if (obj instanceof HeroBullet) {
            return HERO_BULLET_IMAGE;
        } else if (obj instanceof EnemyBullet) {
            return ENEMY_BULLET_IMAGE;
        } else if (obj instanceof HpProp) {
            return HP_PROP_IMAGE;
        } else if (obj instanceof FireProp) {
            return FIRE_PROP_IMAGE;
        } else if (obj instanceof FirePlusProp) {
            return FIRE_PLUS_PROP_IMAGE;
        } else if (obj instanceof BombProp) {
            return BOMB_PROP_IMAGE;
        } else if (obj instanceof FreezeProp) {
            return FREEZE_PROP_IMAGE;
        }
        return null;
    }
}
