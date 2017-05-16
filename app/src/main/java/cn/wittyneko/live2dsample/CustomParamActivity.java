package cn.wittyneko.live2dsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import cn.wittyneko.live2d.AppModelListener;
import cn.wittyneko.live2d.L2DAppDefine;
import cn.wittyneko.live2d.L2DAppManager;
import cn.wittyneko.live2d.L2DAppModel;
import cn.wittyneko.live2d.app.LAppView;
import cn.wittyneko.live2dsample.widget.ParamView;
import jp.live2d.param.ParamDefFloat;

public class CustomParamActivity extends AppCompatActivity {

    private FrameLayout live2dFrame ;
    private L2DAppManager live2dMgr;
    private LinearLayout vgParamList;
    private View vgParamScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_param);
        live2dFrame = (FrameLayout) findViewById(R.id.live2d_frame);
        vgParamList = (LinearLayout) findViewById(R.id.param_list);
        vgParamScroll = findViewById(R.id.param_scroll);
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
        //live2dMgr.addBgPath(L2DAppDefine.BACK_IMAGE_NAME); // 添加背景
        //live2dMgr.addModelPath(L2DAppDefine.MODEL_YANXI); // 添加模型
        for (String path : ModelInfo.bgPath) {
            live2dMgr.addBgPath(path); // 添加背景
        }
        for (String path : ModelInfo.modelPath) {
            live2dMgr.addModelPath(path); // 添加模型
        }

        LAppView live2dView = live2dMgr.createView(this);
        live2dView.setMoveEnable(true);
        live2dFrame.addView(live2dView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        live2dMgr.setLoadListener(new AppModelListener.LoadListener() {
            @Override
            public void load(L2DAppModel model) {
                // 获取动作参数列表
                List<ParamDefFloat> paramList = model.getLive2DModel().getModelImpl().getParamDefSet().getParamDefFloatList();
                String modelName = model.getModelSetting().getModelName();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vgParamList.removeAllViews();
                    }
                });
                for (final ParamDefFloat param : paramList) {
                    Log.e("param", "-> " + param.getParamID().toString() + ", " + param.getMinValue() + ", " + param.getMaxValue() + ", " + param.getDefaultValue());
                    //model.customParam.put(param.getParamID().toString(), param.getDefaultValue());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 添加自定义参数View
                            ParamView paramView = new ParamView(vgParamList.getContext());
                            paramView.setName(param.getParamID().toString());
                            paramView.setMin(param.getMinValue());
                            paramView.setMax(param.getMaxValue());
                            paramView.setDef(param.getDefaultValue());
                            paramView.setProgressChangedListener(new ParamView.ProgressChangedListener() {
                                @Override
                                public void onProgressChanged(ParamView paramView, float progress) {
                                    // 修改模型参数
                                    for (int i = 0; i < live2dMgr.getModelNum(); i++) {
                                        L2DAppModel model = (L2DAppModel) live2dMgr.getModel(i);
                                        model.customParam.put(paramView.getName(), progress);
                                        model.getLive2DModel().setParamFloat(paramView.getName(), progress);
                                    }
                                }
                            });
                            vgParamList.addView(paramView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        }
                    });
                }
            }
        });

        LinearLayout vgCtrl = new LinearLayout(this);
        vgCtrl.setOrientation(LinearLayout.VERTICAL);

        // 切换模型
        Button btnChange = new Button(this);
        btnChange.setText("change");
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "change model", Toast.LENGTH_SHORT).show();
                live2dMgr.changeModel();//Live2D Event
            }
        });
        vgCtrl.addView(btnChange, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // 切换背景
        Button btnChangeBg = new Button(this);
        btnChangeBg.setText("change Bg");
        btnChangeBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "change background", Toast.LENGTH_SHORT).show();
                live2dMgr.changeBg();//Live2D Event
            }
        });
        vgCtrl.addView(btnChangeBg, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        Button btnParam = new Button(this);
        btnParam.setText("Param Set");
        btnParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "change background", Toast.LENGTH_SHORT).show();
                if (vgParamScroll.getVisibility() == View.VISIBLE) {
                    vgParamScroll.setVisibility(View.GONE);
                } else {
                    vgParamScroll.setVisibility(View.VISIBLE);
                }
            }
        });
        vgCtrl.addView(btnParam, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        FrameLayout.LayoutParams btnChangeLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnChangeLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        live2dFrame.addView(vgCtrl, btnChangeLayoutParams);
    }
}
