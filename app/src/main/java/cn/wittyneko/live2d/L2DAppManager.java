package cn.wittyneko.live2d;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;

import javax.microedition.khronos.opengles.GL10;

import cn.wittyneko.live2d.app.LAppDefine;
import cn.wittyneko.live2d.app.LAppLive2DManager;
import cn.wittyneko.live2d.app.LAppModel;

/**
 * Live2d 管理器
 * Created by wittytutu on 17-3-22.
 */

public class L2DAppManager extends LAppLive2DManager {
    private AppModelListener.LoadListener mLoadListener; //模型载入监听
    private AppModelListener.UpdateListener mUpdateListener; //模型刷新监听

    SimpleDateFormat mFormat =new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS毫秒");
    public L2DAppManager(Context context) {
        super(context);
    }

    @Override
    public void update(GL10 gl) {
        //Log.e("load update",  "-> " + mFormat.format(System.currentTimeMillis()) + ", " + System.currentTimeMillis());
        super.update(gl);
    }

    // 加载模型
    @Override
    public void loadModels(GL10 gl, String path) throws Throwable {

        L2DAppModel appModel = new L2DAppModel();
        appModel.setLoadListener(mLoadListener);
        appModel.setUpdateListener(mUpdateListener);
        Log.e("load begin",  "-> " + mFormat.format(System.currentTimeMillis()) + ", " + System.currentTimeMillis());
        appModel.load(gl, path);
        Log.e("load end",  "-> " + mFormat.format(System.currentTimeMillis()) + ", " + System.currentTimeMillis());
        appModel.feedIn();
        models.add(appModel);
    }

    // 点击
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
                    model.startRandomMotion(motionGroup, LAppDefine.PRIORITY_NORMAL);
                    break;
                }
            }
        }
        return true;
    }

    // 滑动
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
                    model.startRandomMotion(motionGroup, LAppDefine.PRIORITY_NORMAL);
                    break;
                }
            }
        }

    }

    // 长按
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

                // 模型动作是否存在
                //int exist = models.get(i).getModelSetting().getMotionNum(motionGroup);
                //int exist = models.get(i).getModelSetting().getMotionNum(LAppDefine.MOTION_GROUP_INDEX);
                //if (exist != 0 && hasNextMotion(models.get(i).getMainMotionManager(), LAppDefine.PRIORITY_NORMAL)) {
                if (hasNextMotion(model.getMainMotionManager(), LAppDefine.PRIORITY_NORMAL)) {
                    model.startRandomMotion(motionGroup, LAppDefine.PRIORITY_NORMAL);
                    break;
                }
            }
        }

    }

    // 点击区域名称转换(去除左右区分)
    private String getAreaName(String modelArea) {
        String areaName = modelArea;
        if (true && (modelArea.endsWith("_l") || modelArea.endsWith("_r"))) {
            areaName = modelArea.substring(0, modelArea.length() - 2);
        }
        return areaName;
    }

    public AppModelListener.LoadListener getLoadListener() {
        return mLoadListener;
    }

    public void setLoadListener(AppModelListener.LoadListener loadListener) {
        this.mLoadListener = loadListener;

        for (LAppModel appModel : models) {
            L2DAppModel model = (L2DAppModel) appModel;
            model.setLoadListener(mLoadListener);
        }
    }

    public AppModelListener.UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    public void setUpdateListener(AppModelListener.UpdateListener updateListener) {
        this.mUpdateListener = updateListener;

        for (LAppModel appModel : models) {
            L2DAppModel model = (L2DAppModel) appModel;
            model.setUpdateListener(mUpdateListener);
        }
    }
}
