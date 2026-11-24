package edu.hitsz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.application.BaseGame;
import edu.hitsz.dao.GameDifficulty;

/**
 * 实验三：游戏主界面 Activity
 * 通过 Intent 接收菜单页传递的难度参数，构造对应难度的游戏画布；
 * 通过 Handler 接收游戏子线程的 Game Over 消息（子线程不能直接更新 UI）。
 */
public class GameActivity extends AppCompatActivity {

    /** BaseGame 子线程发来的消息：游戏结束 */
    public static final int MSG_GAME_OVER = 1;
    public static final String EXTRA_DIFFICULTY = "difficulty";

    private BaseGame baseGame;

    /**
     * 主线程 Handler：接收 BaseGame（SurfaceView 绘制子线程）发送的消息，
     * 按 msg.what 分发处理（参考课件 HandlerDemo 的写法）
     */
    @SuppressLint("HandlerLeak")
    private final Handler uiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_GAME_OVER) {
                int score = (int) msg.obj;
                Toast.makeText(GameActivity.this,
                        "Game Over! SCORE: " + score, Toast.LENGTH_LONG).show();
                // 排行榜页面的跳转属于实验四，本实验先返回菜单页
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 读取菜单页通过 Intent putExtra 传递的难度
        Intent intent = getIntent();
        GameDifficulty difficulty;
        String difficultyName = intent.getStringExtra(EXTRA_DIFFICULTY);
        if (GameDifficulty.HARD.name().equals(difficultyName)) {
            difficulty = GameDifficulty.HARD;
        } else if (GameDifficulty.NORMAL.name().equals(difficultyName)) {
            difficulty = GameDifficulty.NORMAL;
        } else {
            difficulty = GameDifficulty.EASY;
        }

        baseGame = new BaseGame(this, difficulty);
        // 把主线程 Handler 交给游戏子线程，用于 Game Over 时通知 UI 线程
        baseGame.setUiHandler(uiHandler);
        setContentView(baseGame);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除未处理的消息，防止泄漏
        uiHandler.removeCallbacksAndMessages(null);
    }
}
