package edu.hitsz.application;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * 存储所有用到的图片资源
 */
public class ImageManager {

    // 背景图
    public static BufferedImage BACKGROUND_IMAGE;
    // 英雄机
    public static BufferedImage HERO_IMAGE;
    // 敌机
    public static BufferedImage ENEMY_IMAGE;
    // 英雄机子弹
    public static BufferedImage HERO_BULLET_IMAGE;
    // 敌机子弹
    public static BufferedImage ENEMY_BULLET_IMAGE;
    // 精英敌机
    public static BufferedImage ELITE_ENEMY_IMAGE;
    // 超级精英敌机
    public static BufferedImage SUPER_ENEMY_IMAGE;
    // 道具
    public static BufferedImage BOMB_PROP_IMAGE;
    public static BufferedImage FIRE_PROP_IMAGE;
    public static BufferedImage BLOOD_PROP_IMAGE;

    static {
        try {
            // 背景
            BACKGROUND_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/bg.jpg"));

            // 飞机
            HERO_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/hero.png"));
            ENEMY_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/enemy.png"));
            ELITE_ENEMY_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/elite_enemy.png"));
            SUPER_ENEMY_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/super_enemy.png"));

            // 子弹
            HERO_BULLET_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/hero_bullet.png"));
            ENEMY_BULLET_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/enemy_bullet.png"));

            // 道具
            BOMB_PROP_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/bomb_prop.png"));
            FIRE_PROP_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/fire_prop.png"));
            BLOOD_PROP_IMAGE = ImageIO.read(ImageManager.class.getResourceAsStream("/images/blood_prop.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}