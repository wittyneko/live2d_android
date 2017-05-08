package cn.wittyneko.live2d;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;
import com.muta.yanxi.MutaHelper;
import com.muta.yanxi.presenter.utils.GsonUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.opengles.GL10;

import cn.wittyneko.live2d.app.LAppDefine;
import cn.wittyneko.live2d.app.LAppLive2DManager;
import cn.wittyneko.live2d.app.LAppModel;

/**
 * Live2d 管理器
 * Created by wittytutu on 17-3-22.
 */

public class L2DAppManager extends LAppLive2DManager {
    // 配置文件状态
    private String preferenceState;
    private L2DAppModel.UpdateListener mUpdateListener; //界面刷新监听

    public L2DAppManager(Context context) {
        super(context);
    }

    @Override
    public void loadModels(GL10 gl) throws Throwable {

        int no = modelCount % 4;

        switch (no) {
            case 0:
                releaseModel();

                models.add(new L2DAppModel());
                models.get(0).load(gl, L2DAppDefine.MODEL_YANXI);
                models.get(0).feedIn();
                break;
            default:

                break;
        }
        setUpdateListener(mUpdateListener);
        updatePreference();
    }

    @Override
    public boolean tapEvent(float x, float y) {
        if (LAppDefine.DEBUG_LOG) Log.d(TAG, "tapEvent view x:" + x + " y:" + y);

        for (int i = 0; i < models.size(); i++) {
            L2DAppModel model = (L2DAppModel) models.get(i);
            String hitAreaName = hitTest(model, x, y);
            if (!TextUtils.isEmpty(hitAreaName)) {
                hitAreaName = getAreaName(hitAreaName);
                String motionGroup = LAppDefine.EVENT_TAP + hitAreaName;

                if (LAppDefine.DEBUG_LOG)
                    Log.e(TAG, "Tap Event." + motionGroup);

                // 模型动作是否存在
                //int exist = models.get(i).getModelSetting().getMotionNum(motionGroup);
                //int exist = models.get(i).getModelSetting().getMotionNum(LAppDefine.MOTION_GROUP_INDEX);
                //if (exist != 0 && hasNextMotion(models.get(i).getMainMotionManager(), LAppDefine.PRIORITY_NORMAL)) {
                if (hasNextMotion(model.getMainMotionManager(), LAppDefine.PRIORITY_NORMAL)) {
                    model.startPreferenceMotionExpression(motionGroup, LAppDefine.PRIORITY_NORMAL);
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void flickEvent(float x, float y) {
        if (LAppDefine.DEBUG_LOG) Log.d(TAG, "flick x:" + x + " y:" + y);

        for (int i = 0; i < models.size(); i++) {
            L2DAppModel model = (L2DAppModel) models.get(i);
            String hitAreaName = hitTest(model, x, y);
            if (!TextUtils.isEmpty(hitAreaName)) {
                hitAreaName = getAreaName(hitAreaName);
                String motionGroup = LAppDefine.EVENT_FLICK + hitAreaName;

                if (LAppDefine.DEBUG_LOG)
                    Log.e(TAG, "Flick Event." + motionGroup);

                // 模型动作是否存在
                //int exist = models.get(i).getModelSetting().getMotionNum(motionGroup);
                //int exist = models.get(i).getModelSetting().getMotionNum(LAppDefine.MOTION_GROUP_INDEX);
                //if (exist != 0 && hasNextMotion(models.get(i).getMainMotionManager(), LAppDefine.PRIORITY_NORMAL)) {
                if (hasNextMotion(model.getMainMotionManager(), LAppDefine.PRIORITY_NORMAL)) {
                    model.startPreferenceMotionExpression(motionGroup, LAppDefine.PRIORITY_NORMAL);
                    break;
                }
            }
        }

    }

    @Override
    public void longPress(float x, float y) {
        if (LAppDefine.DEBUG_LOG) Log.d(TAG, "longPress x:" + x + " y:" + y);

        for (int i = 0; i < models.size(); i++) {
            L2DAppModel model = (L2DAppModel) models.get(i);
            String hitAreaName = hitTest(model, x, y);
            if (!TextUtils.isEmpty(hitAreaName)) {
                hitAreaName = getAreaName(hitAreaName);
                String motionGroup = LAppDefine.EVENT_LONG_PRESS + hitAreaName;

                if (LAppDefine.DEBUG_LOG)
                    Log.e(TAG, "LongPress Event." + motionGroup);

                // 长按头部、脸关闭声音
                if ((L2DAppDefine.HIT_AREA_HEAD.equals(hitAreaName) ||
                        L2DAppDefine.HIT_AREA_FACE.equals(hitAreaName))) {
                    MutaHelper helper = MutaHelper.getInstance();
                    // 打开/关闭 声音
                    helper.getModel().setSettingMsgSound(!helper.getModel().getSettingMsgSound());
                    if (helper.getModel().getSettingMsgSound()) {
                        model.startPreferenceMotionExpression(L2DAppDefine.MOTION_GROUP_SOUND_ON, LAppDefine.PRIORITY_NORMAL);
                    } else {
                        model.startPreferenceMotionExpression(L2DAppDefine.MOTION_GROUP_SOUND_OFF, LAppDefine.PRIORITY_NORMAL);
                    }
                    break;
                }
            }
        }

    }

    // 点击区域名称转换
    private String getAreaName(String modelArea) {
        String areaName = modelArea;
        if (true && (modelArea.endsWith("_l") || modelArea.endsWith("_r"))) {
            areaName = modelArea.substring(0, modelArea.length() - 2);
        }
        return areaName;
    }

    // 更新模型动作配置
    public void updatePreference() {

        for (LAppModel appModel : models) {
            L2DAppModel model = (L2DAppModel) appModel;
            try {
                InputStreamReader isr = null;
                //读取下载Json
                File file = new File(mContext.getFilesDir() + "/" + model.getModelNameDir() + ".json");
                if (file.exists()) {
                    //读取更新Json配置
                    isr = new InputStreamReader(new FileInputStream(file));
                }
                if (isr == null) {
                    file = new File(mContext.getFilesDir() + "/" + model.getModelNameDir());
                    if (file.exists()) {
                        isr = new InputStreamReader(new FileInputStream(file));
                    }
                }

                //读取内置Json配置
                if (isr == null) {
                    isr = new InputStreamReader(mContext.getResources().openRawResource(R.raw.def_preference));
                }

                //字节流转字符流
                BufferedReader bfr = new BufferedReader(isr);
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bfr.readLine()) != null) {
                    stringBuilder.append(line);
                }
                //Log.e("json", "-> " + stringBuilder.toString());

                //将JSON数据转化为字符串
                //根据键名获取键值信息
                JsonObject jsonObject = GsonUtil.form(stringBuilder.toString(), JsonObject.class);
                model.setPreferenceJson(jsonObject);
                model.setPreferenceState(preferenceState);
                //MainActivity activity = (MainActivity) getActivity();
                //activity.live2DMgr.setPreferenceJson(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getPreferenceState() {
        return preferenceState;
    }

    // 更新模型状态配置
    public void setPreferenceState(String state) {
        this.preferenceState = state;
        for (LAppModel appModel : models) {
            L2DAppModel model = (L2DAppModel) appModel;
            model.setPreferenceState(state);
        }
    }

    public L2DAppModel.UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    public void setUpdateListener(L2DAppModel.UpdateListener updateListener) {
        this.mUpdateListener = updateListener;

        for (LAppModel appModel : models) {
            L2DAppModel model = (L2DAppModel) appModel;
            model.setUpdateListener(updateListener);
        }
    }
}
