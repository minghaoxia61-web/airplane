package edu.hitsz.dao;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 在控制台打印当前难度排行榜（名次、玩家名、得分、时间）。
 */
public final class LeaderboardPrinter {

    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LeaderboardPrinter() {
    }

    public static void printToConsole(ScoreRecordDao dao) {
        List<ScoreRecord> rows = dao.findAllSortedByScoreDesc();
        System.out.println();
        System.out.println("========== 排行榜 [" + dao.getDifficulty() + "] ==========");
        System.out.printf("%-6s%-12s%-10s%s%n", "名次", "玩家名", "得分", "记录时间");
        int rank = 1;
        for (ScoreRecord r : rows) {
            System.out.printf("%-6d%-12s%-10d%s%n",
                    rank++,
                    r.getPlayerName(),
                    r.getScore(),
                    DISPLAY.format(r.getRecordTime()));
        }
        if (rows.isEmpty()) {
            System.out.println("(暂无记录)");
        }
        System.out.println("==========================================");
        System.out.println();
    }
}
