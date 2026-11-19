package edu.hitsz.dao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于文本文件的 {@link ScoreRecordDao} 实现，每种难度使用独立文件。
 * 行格式：{@code id|playerName|score|ISO-8601}
 */
public class ScoreRecordDaoImpl implements ScoreRecordDao {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final char SEP = '|';

    private final GameDifficulty difficulty;
    private final Path dataFile;

    public ScoreRecordDaoImpl(GameDifficulty difficulty) {
        this.difficulty = difficulty;
        Path dir = Paths.get(System.getProperty("user.dir"), "data", "leaderboard");
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new IllegalStateException("无法创建排行榜数据目录", e);
        }
        this.dataFile = dir.resolve("scores_" + difficulty.name().toLowerCase() + ".txt");
    }

    @Override
    public GameDifficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public synchronized void insert(String playerName, int score, LocalDateTime recordTime) {
        List<ScoreRecord> records = readAllRaw();
        long nextId = records.stream().mapToLong(ScoreRecord::getId).max().orElse(0L) + 1;
        records.add(new ScoreRecord(nextId, playerName, score, recordTime));
        writeAll(records);
    }

    @Override
    public synchronized boolean deleteById(long id) {
        List<ScoreRecord> records = readAllRaw();
        boolean removed = records.removeIf(r -> r.getId() == id);
        if (removed) {
            writeAll(records);
        }
        return removed;
    }

    @Override
    public synchronized List<ScoreRecord> findAllSortedByScoreDesc() {
        return readAllRaw().stream()
                .sorted(Comparator.comparingInt(ScoreRecord::getScore).reversed()
                        .thenComparing(ScoreRecord::getRecordTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    private List<ScoreRecord> readAllRaw() {
        if (!Files.isRegularFile(dataFile)) {
            return new ArrayList<>();
        }
        try {
            List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
            List<ScoreRecord> list = new ArrayList<>();
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                ScoreRecord r = parseLine(line);
                if (r != null) {
                    list.add(r);
                }
            }
            return list;
        } catch (IOException e) {
            throw new IllegalStateException("读取排行榜文件失败: " + dataFile, e);
        }
    }

    private static ScoreRecord parseLine(String line) {
        try {
            String[] p = line.split("\\" + SEP, -1);
            if (p.length != 4) {
                return null;
            }
            long id = Long.parseLong(p[0]);
            String name = p[1];
            int score = Integer.parseInt(p[2]);
            LocalDateTime t = LocalDateTime.parse(p[3], ISO);
            return new ScoreRecord(id, name, score, t);
        } catch (NumberFormatException | DateTimeParseException e) {
            return null;
        }
    }

    private void writeAll(List<ScoreRecord> records) {
        List<String> lines = records.stream()
                .map(r -> r.getId() + String.valueOf(SEP)
                        + r.getPlayerName() + SEP
                        + r.getScore() + SEP
                        + ISO.format(r.getRecordTime()))
                .collect(Collectors.toList());
        try {
            Files.write(dataFile, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("写入排行榜文件失败: " + dataFile, e);
        }
    }
}
