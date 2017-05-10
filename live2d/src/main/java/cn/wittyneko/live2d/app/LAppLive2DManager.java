/**
 * You can modify and use this source freely
 * only for the development of application related Live2D.
 * <p>
 * (c) Live2D Inc. All rights reserved.
 */
package cn.wittyneko.live2d.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import cn.wittyneko.live2d.app.data.ModelPath;
import cn.wittyneko.live2d.utils.FileManager;
import cn.wittyneko.live2d.utils.SimpleImage;
import cn.wittyneko.live2d.utils.SoundManager;
import jp.live2d.Live2D;
import jp.live2d.framework.L2DMotionManager;
import jp.live2d.framework.L2DViewMatrix;
import jp.live2d.framework.Live2DFramework;
import jp.live2d.motion.MotionQueueManager;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * Live2d 管理器
 */
public abstract class LAppLive2DManager {

    static public final String TAG = "SampleLive2DManager";
    protected Context mContext;

    protected LAppView view;

    protected ArrayList<ModelPath> mPaths; //模型路径列表
    protected ArrayList<LAppModel> models; //当前列表模型数量

    protected ModelPath mBgPaths; // 背景路径列表
    protected SimpleImage mBg; // 背景图片

    protected boolean reloadFlg;
    protected boolean reloadBg;


    public LAppLive2DManager(Context context) {
        mContext = context;
        Live2D.init();
        FileManager.init(mContext);
        SoundManager.init(mContext);
        Live2DFramework.setPlatformManager(new PlatformManager());

        mPaths = new ArrayList<>();
        models = new ArrayList<>();
        mBgPaths = new ModelPath();
    }

    //===========================================
    //=================模型加载===================
    //===========================================

    // 获取模型路径列表
    public ArrayList<ModelPath> getModelPaths() {
        return mPaths;
    }

    // 添加模型路径
    public void addModelPath(String path) {
        if (mPaths.isEmpty()) {
            mPaths.add(new ModelPath());
        }
        ModelPath modelPath = mPaths.get(0);
        modelPath.getPath().add(path);
    }

    // 获取当前加载的全部模型
    public ArrayList<LAppModel> getModels() {
        return models;
    }

    // 获取当前加载的第N个模型实例
    public LAppModel getModel(int no) {
        if (no >= models.size()) return null;
        return models.get(no);
    }

    // 获取加载的模型数量
    public int getModelNum() {
        return models.size();
    }

    //切换模型
    public void changeModel() {
        for (int i = 0; i < mPaths.size(); i++) {
            ModelPath path = mPaths.get(i);
            path.setIndex(path.getIndex() + 1);
        }
        reloadFlg = true;
    }

    // 切换第N个模型
    public void changeModel(int no) {
        if (!mPaths.isEmpty() && no < mPaths.size()) {
            ModelPath path = mPaths.get(no);
            path.setIndex(path.getIndex() + 1);
        }
        reloadFlg = true;
    }

    //释放模型
    public void releaseModel() {
//        for (int i = 0; i < mPaths.size(); i++) {
//            mPaths.get(i).setIndex(-1);
//        }

        for (int i = 0; i < models.size(); i++) {
            models.get(i).release();
        }

        models.clear();
    }

    // 获取全部背景路径
    public ModelPath getBgPaths() {
        return mBgPaths;
    }

    // 添加背景路径
    public void addBgPath(String path) {
        mBgPaths.getPath().add(path);
    }

    // 获取背景
    public SimpleImage getBg() {
        return mBg;
    }

    // 初始化背景
    private void setupBackground(GL10 gl) {

        int index = mBgPaths.getIndex();
        ArrayList<String> pathList = mBgPaths.getPath();

        if (index == -1)
            return;

        if (pathList != null && !pathList.isEmpty()) {
            if (index >= pathList.size()) {
                index = index % pathList.size();
                mBgPaths.setIndex(index);
            }
            String pathStr = pathList.get(index);

            if (TextUtils.isEmpty(pathStr)) {
                return;
            }
        } else {
            return;
        }

        try {

            InputStream in = FileManager.open(pathList.get(index));
            mBg = new SimpleImage(gl, in);

            mBg.setDrawRect(
                    LAppDefine.VIEW_LOGICAL_MAX_LEFT,
                    LAppDefine.VIEW_LOGICAL_MAX_RIGHT,
                    LAppDefine.VIEW_LOGICAL_MAX_BOTTOM,
                    LAppDefine.VIEW_LOGICAL_MAX_TOP);


            mBg.setUVRect(0.0f, 1.0f, 0.0f, 1.0f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 切换背景
    public void changeBg() {
        mBgPaths.setIndex(mBgPaths.getIndex() + 1);
        reloadBg = true;
    }

    // 释放内存
    public void release() {
        SoundManager.release();
    }

    //刷新并加载模型
    public void update(GL10 gl) {
        view.update();
        // 加载背景
        if (reloadBg) {
            reloadBg = false;
            setupBackground(gl);
        }

        // 加载模型
        if (reloadFlg) {

            reloadFlg = false;

            try {
                releaseModel();
                // 模型列表存在
                if (!mPaths.isEmpty()) {
                    for (ModelPath path : mPaths) {
                        int index = path.getIndex();
                        ArrayList<String> pathList = path.getPath();
                        // 当前模型存在一个以上路径
                        if (pathList != null && !pathList.isEmpty()) {
                            if (index >= pathList.size()) {
                                index = index % pathList.size();
                                path.setIndex(index);
                            }
                            String pathStr = null;
                            if (index != -1) {
                                 pathStr = pathList.get(index);
                            }

                            if (!TextUtils.isEmpty(pathStr)) {
                                loadModels(gl, pathStr);
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                Log.e(TAG, "Failed to load." + e.getStackTrace());
                release();
            }
        }
    }

    // 加载模型
    public abstract void loadModels(GL10 gl, String path) throws Throwable;

    //===========================================
    //=================绘制显示===================
    //===========================================

    public void setAccel(float x, float y, float z) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setAccel(x, y, z);
        }
    }

    public void setDrag(float x, float y) {
        for (int i = 0; i < models.size(); i++) {
            models.get(i).setDrag(x, y);
        }
    }

    public LAppView createView(Activity act) {

        view = new LAppView(act);
        view.setLive2DManager(this);
        view.startAccel(act);
        view.setOnTouchEnable(true);
        view.setMoveEnable(false);
        return view;
    }

    public LAppView getView() {
        return view;
    }


    public L2DViewMatrix getViewMatrix() {
        return view.getViewMatrix();
    }


    public void onResume() {
        if (LAppDefine.DEBUG_LOG) Log.d(TAG, "onResume");
        if (view != null) {
            view.onResume();
        }
    }


    public void onPause() {
        if (LAppDefine.DEBUG_LOG) Log.d(TAG, "onPause");
        if (view != null) {
            view.onPause();
        }
        //SoundManager.release();
    }


    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (LAppDefine.DEBUG_LOG) Log.d(TAG, "onSurfaceChanged " + width + " " + height);
        view.setupView(width, height);

        if (getModelNum() == 0) {
            changeModel();
        }
        if (getBg() == null) {
            changeBg();
        }
    }

    //===========================================
    //=================事件处理===================
    //===========================================

    //点击事件
    public abstract boolean tapEvent(float x, float y);

    //滑动事件
    public abstract void flickEvent(float x, float y);

    // 长按事件
    public abstract void longPress(float x, float y);

    //放大事件
    public void maxScaleEvent() {
        if (LAppDefine.DEBUG_LOG) Log.d(TAG, "Max scale event.");

        for (int i = 0; i < models.size(); i++) {
            models.get(i).startRandomMotion(LAppDefine.MOTION_GROUP_PINCH_OUT, LAppDefine.PRIORITY_NORMAL);
        }
    }

    //缩小事件
    public void minScaleEvent() {
        if (LAppDefine.DEBUG_LOG) Log.d(TAG, "Min scale event.");

        for (int i = 0; i < models.size(); i++) {
            models.get(i).startRandomMotion(LAppDefine.MOTION_GROUP_PINCH_IN, LAppDefine.PRIORITY_NORMAL);
        }
    }

    //摇动事件
    public void shakeEvent() {
        if (LAppDefine.DEBUG_LOG) Log.d(TAG, "Shake event.");

        for (int i = 0; i < models.size(); i++) {
            models.get(i).startRandomMotion(LAppDefine.MOTION_GROUP_SHAKE, LAppDefine.PRIORITY_FORCE);
        }
    }

    // 是否可以执行下一个动作
    protected boolean hasNextMotion(MotionQueueManager manager, int priority) {

        // 动作已结束
        if (manager.isFinished()) {
            return true;
        } else if (manager instanceof L2DMotionManager) {
            L2DMotionManager motionManager = (L2DMotionManager) manager;
            // 动作优先级更高
            if (motionManager.getCurrentPriority() < priority) {
                return true;
            }
        }
        return false;
    }

    // 点击区域判断
    protected String hitTest(LAppModel model, float x, float y) {

        //if (LAppDefine.DEBUG_LOG) Log.d(TAG, "hitTest x:" + x + " y:" + y);

        // 点击区域总数
        int hitAreas = model.getModelSetting().getHitAreasNum();
        for (int h = 0; h < hitAreas; h++) {
            // 区域名称
            String hitAreaName = model.getModelSetting().getHitAreaName(h);
            if (model.hitTest(hitAreaName, x, y)) {
                //hitAreaName = getAreaName(hitAreaName);
                return hitAreaName;
            }
        }
        return null;
    }
}
