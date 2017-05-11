package cn.wittyneko.live2dsample.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 *  参数调节View
 * Created by wittytutu on 17-5-11.
 */

public class ParamView extends LinearLayout {
    private TextView tvName;
    private SeekBar seekBar;
    ProgressChangedListener mProgressChangedListener;

    private float min = 0f;
    private float max = 1f;
    private float def = 0f;
    float cur = def;
    private String name;

    public ParamView(Context context) {
        this(context, null);
    }

    public ParamView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParamView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ParamView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setOrientation(LinearLayout.VERTICAL);
        tvName = new TextView(context);
        tvName.setTextColor(Color.WHITE);
        seekBar = new SeekBar(context);
        seekBar.setMax((int) (Math.abs(min) * 100  + max * 100));
        setDef(def);
        addView(tvName, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(seekBar, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mProgressChangedListener != null) {
                    int minValue = (int) (min * 100);
                    int maxValue = (int) (max * 100);
                    progress = progress + minValue;
                    cur = progress / 100f;
                    //Log.e("progress", progress + ", " + cur);
                    update();
                    mProgressChangedListener.onProgressChanged(ParamView.this, cur);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
        seekBar.setMax((int) (Math.abs(min) * 100  + max * 100));
        update();
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
        seekBar.setMax((int) (Math.abs(min) * 100  + max * 100));
        update();
    }

    public float getDef() {
        return def;
    }

    public void setDef(float def) {
        this.def = def;
        cur = def;
        seekBar.setProgress((int) (def * 100));
        update();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        update();
    }

    private void update() {
        tvName.setText(name + "\n" + min + ", " + max + ", " + def+ ", "  + cur);
    }

    public ProgressChangedListener getProgressChangedListener() {
        return mProgressChangedListener;
    }

    public void setProgressChangedListener(ProgressChangedListener progressChangedListener) {
        this.mProgressChangedListener = progressChangedListener;
    }

    public interface ProgressChangedListener {
        void onProgressChanged(ParamView paramView,  float progress);
    }
}
