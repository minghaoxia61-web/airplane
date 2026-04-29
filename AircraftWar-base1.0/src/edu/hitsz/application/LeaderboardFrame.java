package edu.hitsz.application;

import edu.hitsz.dao.ScoreRecord;
import edu.hitsz.dao.ScoreRecordDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 得分排行榜界面（实验五 Swing + JTable 要求）。
 */
public class LeaderboardFrame extends JFrame {

    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ScoreRecordDao dao;
    private final JTable table;
    private final DefaultTableModel model;

    public LeaderboardFrame(ScoreRecordDao dao) {
        super("Leaderboard - " + dao.getDifficulty());
        this.dao = dao;

        setSize(520, 420);
        setResizable(false);
        setLocationRelativeTo(null);

        String[] columns = {"ID", "名次", "玩家名", "得分", "记录时间"};
        this.model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.table = new JTable(model);
        this.table.getColumnModel().getColumn(0).setMinWidth(0);
        this.table.getColumnModel().getColumn(0).setMaxWidth(0);
        this.table.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);

        JButton delete = new JButton("删除选中记录");
        JButton refresh = new JButton("刷新");

        delete.addActionListener(e -> deleteSelectedRow());
        refresh.addActionListener(e -> reload());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(refresh);
        bottom.add(delete);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        reload();
    }

    private void reload() {
        model.setRowCount(0);
        List<ScoreRecord> rows = dao.findAllSortedByScoreDesc();
        int rank = 1;
        for (ScoreRecord r : rows) {
            model.addRow(new Object[]{
                    r.getId(),
                    rank++,
                    r.getPlayerName(),
                    r.getScore(),
                    DISPLAY.format(r.getRecordTime())
            });
        }
    }

    private void deleteSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选中要删除的记录。");
            return;
        }
        long id = ((Number) model.getValueAt(row, 0)).longValue();
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定删除该条记录？",
                "删除确认",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            dao.deleteById(id);
            reload();
        }
    }

    public static void showLeaderboard(ScoreRecordDao dao) {
        SwingUtilities.invokeLater(() -> {
            LeaderboardFrame f = new LeaderboardFrame(dao);
            f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            f.setVisible(true);
        });
    }
}

