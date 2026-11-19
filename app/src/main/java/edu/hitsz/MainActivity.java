package edu.hitsz;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.application.BaseGame;
import edu.hitsz.dao.GameDifficulty;

public class MainActivity extends AppCompatActivity {

    private BaseGame baseGame;
    private OnBackPressedCallback backPressedCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 实验二：默认以简单难度启动（难度选择页面属于后续实验）
        baseGame = new BaseGame(this, GameDifficulty.EASY);
        setContentView(baseGame);

        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }
}
