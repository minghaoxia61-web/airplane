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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageManager {

    // 背景图
    public static BufferedImage BACKGROUND_IMAGE;
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
        try {
            BACKGROUND_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/bg.jpg"));
            HERO_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/hero.png"));
            MOB_ENEMY_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/mob.png"));
            ELITE_ENEMY_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/elite.png"));
            ELITE_PLUS_ENEMY_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/elitePlus.png"));
            BOSS_ENEMY_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/boss.png"));
            ULTIMATE_ENEMY_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/elitePro.png"));
            HERO_BULLET_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/bullet_hero.png"));
            ENEMY_BULLET_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/bullet_enemy.png"));
            HP_PROP_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/prop_blood.png"));
            FIRE_PROP_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/prop_bullet.png"));
            FIRE_PLUS_PROP_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/prop_bulletPlus.png"));
            BOMB_PROP_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/prop_bomb.png"));
            FREEZE_PROP_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/prop_freeze.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
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