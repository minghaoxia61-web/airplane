package edu.hitsz.application;

import edu.hitsz.dao.GameDifficulty;

import javax.swing.*;
import java.awt.*;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {

    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

    public static void main(String[] args) {

        System.out.println("Hello Aircraft War");

        // 获得屏幕的分辨率，初始化 Frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("Aircraft War");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(false);
        //设置窗口的大小和位置,居中放置
        frame.setBounds(((int) screenSize.getWidth() - WINDOW_WIDTH) / 2, 0,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CardLayout cardLayout = new CardLayout(0, 0);
        JPanel cardPanel = new JPanel(cardLayout);
        frame.setContentPane(cardPanel);

        StartMenuPanel startMenu = new StartMenuPanel(difficulty -> {
            Game game = new Game(difficulty);
            cardPanel.add(game, "game-" + difficulty.name());
            cardLayout.show(cardPanel, "game-" + difficulty.name());
            game.action();
        });
        cardPanel.add(startMenu, "menu");

        frame.setVisible(true);
        cardLayout.show(cardPanel, "menu");
    }
}
