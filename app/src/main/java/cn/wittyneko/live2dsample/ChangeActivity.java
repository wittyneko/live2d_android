/**
 * You can modify and use this source freely
 * only for the development of application related Live2D.
 * <p>
 * (c) Live2D Inc. All rights reserved.
 */

package cn.wittyneko.live2dsample;

import cn.wittyneko.live2d.L2DAppDefine;
import cn.wittyneko.live2d.L2DAppManager;
import cn.wittyneko.live2d.app.LAppLive2DManager;
import cn.wittyneko.live2d.app.LAppView;
import cn.wittyneko.live2d.utils.FileManager;
import cn.wittyneko.live2d.utils.SoundManager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 使用示例
 */
public class ChangeActivity extends AppCompatActivity {

    private LAppLive2DManager live2dMgr;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);


        FileManager.init(getApplicationContext());
        SoundManager.init(this);
        setupGUI();
    }


    @Override
    protected void onResume() {
        live2dMgr.onResume();
        super.onResume();
    }


    @Override
    protected void onPause() {
        live2dMgr.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        live2dMgr.onDestroy();
        super.onDestroy();
    }


    void setupGUI() {
        FrameLayout rootView = new FrameLayout(this);
        setContentView(rootView, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        live2dMgr = new L2DAppManager(this);
        //live2dMgr.addBgPath(L2DAppDefine.BACK_IMAGE_NAME); // 添加背景
        //live2dMgr.addModelPath(L2DAppDefine.MODEL_YANXI); // 添加模型
        for (String path : ModelInfo.bgPath) {
            live2dMgr.addBgPath(path); // 添加背景
        }
        for (String path : ModelInfo.modelPath) {
            live2dMgr.addModelPath(path); // 添加模型
        }

        LAppView live2dView = live2dMgr.createView(this);
        rootView.addView(live2dView, new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));


        LinearLayout vgCtrl = new LinearLayout(this);

        // 切换模型
        Button btnChange = new Button(this);
        btnChange.setText("change");
        btnChange.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "change model", Toast.LENGTH_SHORT).show();
                live2dMgr.changeModel();//Live2D Event
            }
        });
        vgCtrl.addView(btnChange, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        // 切换背景
        Button btnChangeBg = new Button(this);
        btnChangeBg.setText("change Bg");
        btnChangeBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "change background", Toast.LENGTH_SHORT).show();
                live2dMgr.changeBg();//Live2D Event
            }
        });
        vgCtrl.addView(btnChangeBg, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        FrameLayout.LayoutParams btnChangeLayoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        btnChangeLayoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        rootView.addView(vgCtrl, btnChangeLayoutParams);
    }
}
