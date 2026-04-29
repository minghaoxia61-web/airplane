package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.bullet.BaseBullet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 英雄机及父类 {@link AbstractAircraft} 相关方法的单元测试（JUnit5）。
 */
class HeroAircraftTest {

    private HeroAircraft hero;

    @BeforeAll
    static void stubMissingImages() {
        BufferedImage stub = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        if (ImageManager.HERO_IMAGE == null) {
            ImageManager.HERO_IMAGE = stub;
        }
        if (ImageManager.HERO_BULLET_IMAGE == null) {
            ImageManager.HERO_BULLET_IMAGE = stub;
        }
    }

    @BeforeEach
    void setUp() {
        HeroAircraft.resetInstanceForTest();
        hero = HeroAircraft.getInstance();
    }

    @AfterEach
    void tearDown() {
        HeroAircraft.resetInstanceForTest();
    }

    @Test
    @DisplayName("父类 decreaseHp：部分扣血后生命值正确减少且仍存活")
    void decreaseHp_partialDamage() {
        int before = hero.getHp();
        hero.decreaseHp(30);
        assertEquals(before - 30, hero.getHp());
        assertFalse(hero.notValid());
    }

    @Test
    @DisplayName("父类 getHp：初始生命值为 100")
    void getHp_initialValue() {
        assertEquals(100, hero.getHp());
    }

    @Test
    @DisplayName("父类 shoot：应返回非空子弹列表（直线射击策略）")
    void shoot_returnsBullets() {
        List<BaseBullet> bullets = hero.shoot();
        assertNotNull(bullets);
        assertFalse(bullets.isEmpty());
    }

    @Test
    @DisplayName("子类 forward：英雄机由鼠标控制，forward 不改变位置")
    void forward_doesNotMove() {
        int x = hero.getLocationX();
        int y = hero.getLocationY();
        hero.forward();
        assertEquals(x, hero.getLocationX());
        assertEquals(y, hero.getLocationY());
    }
}
