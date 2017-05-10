package cn.wittyneko.live2dsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cn.wittyneko.live2d.L2DAppDefine;
import cn.wittyneko.live2d.L2DAppManager;
import cn.wittyneko.live2d.app.LAppView;

public class MainActivity extends AppCompatActivity {

    private FrameLayout live2dFrame ;
    private L2DAppManager live2dMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        live2dFrame = (FrameLayout) findViewById(R.id.live2d_frame);
        initLive2d();
    }

    @Override
    protected void onResume() {
        super.onResume();
        live2dMgr.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        live2dMgr.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        live2dMgr.onDestroy();
    }

    private void initLive2d() {
        live2dMgr = new L2DAppManager(this);
        live2dMgr.addBgPath(L2DAppDefine.BACK_IMAGE_NAME); // 添加背景
        live2dMgr.addModelPath(L2DAppDefine.MODEL_YANXI); // 添加模型

        LAppView live2dView = live2dMgr.createView(this);
        live2dFrame.addView(live2dView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
