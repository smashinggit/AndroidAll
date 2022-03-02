package com.cs.android.ui;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.cs.android.R;
import com.cs.android.view.MySeekBar;
import com.cs.common.base.BaseActivity;

/**
 * @author ChenSen
 * @desc
 * @since 2022/2/16 12:30
 **/
public class SeekBarActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seekbar);


        MySeekBar seekBar = findViewById(R.id.seekBar);

        seekBar.setMax(1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                seekBar.setCurrent(500);
            }
        }, 2);


    }
}
