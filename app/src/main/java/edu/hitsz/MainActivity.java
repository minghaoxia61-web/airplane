package edu.hitsz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.dao.GameDifficulty;

/**
 * 实验三：难度选择页面（主界面）
 * 采用 XML 静态布局（res/layout/activity_menu.xml）+ 控件基于监听的事件处理
 * 音乐开关状态通过 SharedPreferences 持久化，由 MyMediaPlayer 统一管理
 */
public class MainActivity extends AppCompatActivity {

    private edu.hitsz.music.MyMediaPlayer musicManager;
    private android.widget.Switch musicSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        musicManager = edu.hitsz.music.MyMediaPlayer.getInstance(this);

        // 音乐开关：基于监听的事件处理（匿名内部类方式）
        musicSwitch = findViewById(R.id.switch_music);
        musicSwitch.setChecked(musicManager.isMusicOn());
        musicSwitch.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                musicManager.setMusicOn(isChecked);
            }
        });

        // 难度选择按钮：基于监听的事件处理（匿名内部类方式），
        // 通过 Intent 携带难度参数跳转到 GameActivity
        android.widget.Button btnEasy = findViewById(R.id.btn_easy);
        android.widget.Button btnNormal = findViewById(R.id.btn_normal);
        android.widget.Button btnHard = findViewById(R.id.btn_hard);

        android.view.View.OnClickListener launchListener = new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                int id = v.getId();
                if (id == R.id.btn_easy) {
                    intent.putExtra(GameActivity.EXTRA_DIFFICULTY, GameDifficulty.EASY.name());
                } else if (id == R.id.btn_normal) {
                    intent.putExtra(GameActivity.EXTRA_DIFFICULTY, GameDifficulty.NORMAL.name());
                } else if (id == R.id.btn_hard) {
                    intent.putExtra(GameActivity.EXTRA_DIFFICULTY, GameDifficulty.HARD.name());
                }
                startActivity(intent);
            }
        };
        btnEasy.setOnClickListener(launchListener);
        btnNormal.setOnClickListener(launchListener);
        btnHard.setOnClickListener(launchListener);
    }
}
