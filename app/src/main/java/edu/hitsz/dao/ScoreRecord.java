package edu.hitsz.dao;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 排行榜单条记录（数值对象 VO）。
 */
public class ScoreRecord {

    private final long id;
    private final String playerName;
    private final int score;
    private final LocalDateTime recordTime;

    public ScoreRecord(long id, String playerName, int score, LocalDateTime recordTime) {
        this.id = id;
        this.playerName = Objects.requireNonNull(playerName);
        this.score = score;
        this.recordTime = Objects.requireNonNull(recordTime);
    }

    public long getId() {
        return id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public LocalDateTime getRecordTime() {
        return recordTime;
    }
}
