package edu.hitsz.dao;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 得分排行榜数据访问对象接口（DAO）。
 */
public interface ScoreRecordDao {

    /**
     * 新增一条游戏成绩记录。
     */
    void insert(String playerName, int score, LocalDateTime recordTime);

    /**
     * 按主键删除一条记录。
     */
    boolean deleteById(long id);

    /**
     * 查询当前难度下全部记录，按得分降序、时间降序（同分取较新）。
     */
    List<ScoreRecord> findAllSortedByScoreDesc();

    GameDifficulty getDifficulty();
}
