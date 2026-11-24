package edu.hitsz;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 实验三：难度选择页面（主界面）
 * 采用 XML 静态布局（res/layout/activity_menu.xml）+ 控件基于监听的事件处理
 * 音乐开关状态通过 SharedPreferences 持久化
 */
public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "aircraft_war";
    public static final String KEY_MUSIC_ON = "music_on";

    private SharedPreferences prefs;
    private android.widget.Switch musicSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // 音乐开关：基于监听的事件处理（匿名内部类方式）
        musicSwitch = findViewById(R.id.switch_music);
        musicSwitch.setChecked(prefs.getBoolean(KEY_MUSIC_ON, true));
        musicSwitch.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean(KEY_MUSIC_ON, isChecked).apply();
            }
        });

        // 难度选择按钮的跳转逻辑在实验三提交②中接入 GameActivity
        // TODO(实验三提交②): btn_easy/btn_normal/btn_hard -> Intent 跳转
    }
}
