package edu.hitsz.application;

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

import javax.imageio.ImageIO;
import java.io.InputStream;
import java.awt.image.BufferedImage;

public class ImageManager {

    // 背景图
    public static BufferedImage BACKGROUND_IMAGE;
    public static BufferedImage BACKGROUND_EASY_IMAGE;
    public static BufferedImage BACKGROUND_NORMAL_IMAGE;
    public static BufferedImage BACKGROUND_HARD_IMAGE;
    // 英雄机
    public static BufferedImage HERO_IMAGE;
    // 普通敌机
    public static BufferedImage MOB_ENEMY_IMAGE;
    // 精英敌机
    public static BufferedImage ELITE_ENEMY_IMAGE;
    // 精锐敌机
    public static BufferedImage ELITE_PLUS_ENEMY_IMAGE;
    // Boss敌机
    public static BufferedImage BOSS_ENEMY_IMAGE;
    // 王牌敌机
    public static BufferedImage ULTIMATE_ENEMY_IMAGE;
    // 英雄机子弹
    public static BufferedImage HERO_BULLET_IMAGE;
    // 敌机子弹
    public static BufferedImage ENEMY_BULLET_IMAGE;
    // 道具
    public static BufferedImage HP_PROP_IMAGE;
    public static BufferedImage FIRE_PROP_IMAGE;
    public static BufferedImage FIRE_PLUS_PROP_IMAGE;
    public static BufferedImage BOMB_PROP_IMAGE;
    public static BufferedImage FREEZE_PROP_IMAGE;

    static {
        BACKGROUND_IMAGE = loadOrStub("/images/bg.jpg");
        BACKGROUND_EASY_IMAGE = loadOrStub("/images/bg2.jpg");
        BACKGROUND_NORMAL_IMAGE = loadOrStub("/images/bg3.jpg");
        BACKGROUND_HARD_IMAGE = loadOrStub("/images/bg4.jpg");
        HERO_IMAGE = loadOrStub("/images/hero.png");
        MOB_ENEMY_IMAGE = loadOrStub("/images/mob.png");
        ELITE_ENEMY_IMAGE = loadOrStub("/images/elite.png");
        ELITE_PLUS_ENEMY_IMAGE = loadOrStub("/images/elitePlus.png");
        BOSS_ENEMY_IMAGE = loadOrStub("/images/boss.png");
        ULTIMATE_ENEMY_IMAGE = loadOrStub("/images/elitePro.png");
        HERO_BULLET_IMAGE = loadOrStub("/images/bullet_hero.png");
        ENEMY_BULLET_IMAGE = loadOrStub("/images/bullet_enemy.png");
        HP_PROP_IMAGE = loadOrStub("/images/prop_blood.png");
        FIRE_PROP_IMAGE = loadOrStub("/images/prop_bullet.png");
        FIRE_PLUS_PROP_IMAGE = loadOrStub("/images/prop_bulletPlus.png");
        BOMB_PROP_IMAGE = loadOrStub("/images/prop_bomb.png");
        FREEZE_PROP_IMAGE = loadOrStub("/images/prop_freeze.png");
    }

    private static BufferedImage loadOrStub(String resourcePath) {
        try (InputStream in = ImageManager.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                return stubImage();
            }
            BufferedImage img = ImageIO.read(in);
            return img != null ? img : stubImage();
        } catch (Exception ignored) {
            return stubImage();
        }
    }

    private static BufferedImage stubImage() {
        return new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
    }

    public static BufferedImage getBackground(GameDifficulty difficulty) {
        if (difficulty == null) {
            return BACKGROUND_IMAGE;
        }
        return switch (difficulty) {
            case EASY -> (BACKGROUND_EASY_IMAGE != null ? BACKGROUND_EASY_IMAGE : BACKGROUND_IMAGE);
            case NORMAL -> (BACKGROUND_NORMAL_IMAGE != null ? BACKGROUND_NORMAL_IMAGE : BACKGROUND_IMAGE);
            case HARD -> (BACKGROUND_HARD_IMAGE != null ? BACKGROUND_HARD_IMAGE : BACKGROUND_IMAGE);
        };
    }

    public static BufferedImage get(AbstractFlyingObject obj) {
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