/**
 * You can modify and use this source freely
 * only for the development of application related Live2D.
 * <p>
 * (c) Live2D Inc. All rights reserved.
 */

package cn.wittyneko.live2d.app;

import cn.wittyneko.live2d.utils.FileManager;
import cn.wittyneko.live2d.utils.SoundManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * 使用示例
 */
public class SampleActivity extends Activity {

    private LAppLive2DManager live2DMgr;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);


        FileManager.init(getApplicationContext());
        SoundManager.init(this);
        //live2DMgr = new LAppLive2DManager();
        setupGUI();
    }


    @Override
    protected void onResume() {
        live2DMgr.onResume();
        super.onResume();
    }


    @Override
    protected void onPause() {
        live2DMgr.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        SoundManager.release();
        super.onDestroy();
    }


    void setupGUI() {
        FrameLayout rootView = new FrameLayout(this);
        setContentView(rootView, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));


        LAppView live2dView = live2DMgr.createView(this);
        rootView.addView(live2dView, 0, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));


        Button btnChange = new Button(this);
        btnChange.setText("change");
        btnChange.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "change model", Toast.LENGTH_SHORT).show();
                live2DMgr.changeModel();//Live2D Event
            }
        });
        FrameLayout.LayoutParams btnChangeLayoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        btnChangeLayoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        rootView.addView(btnChange, btnChangeLayoutParams);
    }
}
