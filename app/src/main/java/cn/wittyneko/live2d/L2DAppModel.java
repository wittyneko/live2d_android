package cn.wittyneko.live2d;

import android.util.Log;
import android.view.animation.AnimationUtils;

import cn.wittyneko.live2d.app.LAppDefine;
import cn.wittyneko.live2d.app.LAppModel;
import cn.wittyneko.live2d.utils.SoundManager;
import jp.live2d.framework.L2DStandardID;

/**
 * 模型实例
 * Created by wittytutu on 17-3-22.
 */

public class L2DAppModel extends LAppModel {

    private UpdateListener mUpdateListener; //界面刷新监听

    // 嘴型列表
    private String[] mouthArray;
    private int mouthIndex;
    private long mouthStartTime;

    @Override
    public void update() {
        super.update();

        // 嘴型解析
        if (mouthArray != null && mouthIndex < mouthArray.length) {
            // 停止表情
            if (!expressionManager.isFinished()) {
                expressionManager.stopAllMotions();
            }
            long timePassed = AnimationUtils.currentAnimationTimeMillis() - mouthStartTime;
            mouthIndex = (int) (timePassed * (60f / 1000f));
            //Log.e("vol", "-> " + mouthIndex + " , " + timePassed);

            if (mouthIndex < mouthArray.length) {
                resetMouth();
                // 设置嘴形状
                live2DModel.setParamFloat(L2DStandardID.PARAM_MOUTH_FORM, 0.2f);
                // 播放张嘴大小
                //Log.e("vol", "-> " + mouthArray[mouthIndex]);
                float mouth = Float.valueOf(mouthArray[mouthIndex]);
                live2DModel.setParamFloat(L2DStandardID.PARAM_MOUTH_OPEN_Y, mouth);
            } else {
                // 嘴型解析结束
                //Log.e("expressions", "-> " + expressionManager.isFinished());
                if (mainMotionManager.getCurrentPriority() <= LAppDefine.PRIORITY_IDLE) {
                    startIdleRandomMotion();
                }
            }
        }

        if (mUpdateListener != null) {
            mUpdateListener.update(this);
        }

        // 刷新显示
        live2DModel.update();
    }

    // 重置嘴型
    @Override
    public void resetMouth() {
        // 过滤贴图表情
        live2DModel.setParamFloat(L2DAppStandardID.PARAM_MOUTH_CHARTLET, 0);
        for (int i = 1; i <= 6; i++) {
            if (i < 10) {
                live2DModel.setParamFloat(L2DAppStandardID.PARAM_MOUTH_CHARTLET + "0" + i, 0);
            } else {
                live2DModel.setParamFloat(L2DAppStandardID.PARAM_MOUTH_CHARTLET + i, 0);
            }
        }
    }

    // 闲置动作
    @Override
    public void startIdleRandomMotion() {
        startRandomMotion(LAppDefine.MOTION_GROUP_IDLE, LAppDefine.PRIORITY_IDLE);
    }

    @Override
    public void release() {
        super.release();
        setUpdateListener(null);
    }

    /**
     * 加载嘴型文件
     *
     * @param volPath
     */
    public void loadMouthSync(String volPath) {
        mouthIndex = 0;
        mouthStartTime = AnimationUtils.currentAnimationTimeMillis();
        mouthArray = SoundManager.loadMouthOpen(volPath);
    }

    public UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    public void setUpdateListener(UpdateListener updateListener) {
        this.mUpdateListener = updateListener;
    }

    public interface UpdateListener {
        void update(LAppModel model);
    }
}
