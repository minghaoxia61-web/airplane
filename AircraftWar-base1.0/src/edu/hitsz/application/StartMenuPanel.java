package edu.hitsz.application;

import edu.hitsz.dao.GameDifficulty;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 难度选择界面（实验五 Swing 要求）。
 */
public class StartMenuPanel extends JPanel {

    public StartMenuPanel(Consumer<GameDifficulty> onStart) {
        Objects.requireNonNull(onStart);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Aircraft War", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(120, 60, 120, 60));

        JLabel hint = new JLabel("请选择游戏难度（排行榜将按难度分别存档）", SwingConstants.CENTER);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(hint);
        center.add(Box.createVerticalStrut(24));

        JButton easy = new JButton("简单");
        JButton normal = new JButton("普通");
        JButton hard = new JButton("困难");
        for (JButton b : new JButton[]{easy, normal, hard}) {
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(240, 48));
            center.add(b);
            center.add(Box.createVerticalStrut(16));
        }

        easy.addActionListener(e -> onStart.accept(GameDifficulty.EASY));
        normal.addActionListener(e -> onStart.accept(GameDifficulty.NORMAL));
        hard.addActionListener(e -> onStart.accept(GameDifficulty.HARD));

        add(center, BorderLayout.CENTER);
    }
}

