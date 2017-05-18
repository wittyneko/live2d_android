package cn.wittyneko.live2d;

import android.view.animation.AnimationUtils;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.microedition.khronos.opengles.GL10;

import cn.wittyneko.live2d.app.LAppDefine;
import cn.wittyneko.live2d.app.LAppModel;
import cn.wittyneko.live2d.utils.SoundManager;
import jp.live2d.framework.L2DStandardID;
import jp.live2d.param.ParamDefFloat;

/**
 * 模型实例
 * Created by wittytutu on 17-3-22.
 */

public class L2DAppModel extends LAppModel {

    private int listPosition; // 第几个列表
    private int listIndex; // 第几个模型
    private AppModelListener.LoadListener mLoadListener; //模型载入监听
    private AppModelListener.UpdateListener mUpdateListener; //模型刷新监听

    // 嘴型列表
    private String[] mouthArray;
    private int mouthIndex;
    private long mouthStartTime;

    // 调整参数
    public ConcurrentHashMap<String, Float> customParam = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Float> mouthChartletParam = new ConcurrentHashMap<>(); //贴图嘴形

    public L2DAppModel() {
        super();
    }

    public L2DAppModel(int position, int index) {
        super();
        this.listPosition = position;
        this.listIndex = index;
    }

    @Override
    public void load(GL10 gl, String modelSettingPath) throws Exception {
        super.load(gl, modelSettingPath);

        // 获取动作参数列表
        List<ParamDefFloat> paramList = getLive2DModel().getModelImpl().getParamDefSet().getParamDefFloatList();
        for (final ParamDefFloat param : paramList) {
            //Log.e(TAG + "param", "-> " + param.getParamID().toString() + ", " + param.getMinValue() + ", " + param.getMaxValue() + ", " + param.getDefaultValue());
            // 添加贴图嘴形到过滤列表
            if (param.getParamID().toString().startsWith(L2DAppStandardID.PARAM_MOUTH_CHARTLET)) {
                // 其它嘴型
                mouthChartletParam.put(param.getParamID().toString(), param.getDefaultValue());
            } else if (param.getParamID().toString().startsWith(L2DAppStandardID.PARAM_MOUTH_SIZE)) {
                // 嘴大小
                mouthChartletParam.put(param.getParamID().toString(), param.getDefaultValue());
            } else if (param.getParamID().toString().startsWith(L2DStandardID.PARAM_MOUTH_FORM)) {
                // 对话嘴形状
                mouthChartletParam.put(param.getParamID().toString(), 0.4f);
            }
        }

        if (mLoadListener != null) {
            mLoadListener.load(this);
        }
    }

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

        // 调整自定义参数
        Iterator<String> iterator = customParam.keySet().iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            float value = customParam.get(name);
            live2DModel.setParamFloat(name, value);
        }

        // 刷新显示
        live2DModel.update();
    }

    // 重置嘴型
    @Override
    public void resetMouth() {
        // 过滤贴图表情
        //live2DModel.setParamFloat(L2DAppStandardID.PARAM_MOUTH_CHARTLET, 0);
        Iterator<String> iterator = mouthChartletParam.keySet().iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            float value = mouthChartletParam.get(name);
            live2DModel.setParamFloat(name, value);
            //live2DModel.setParamFloat(name, 0);
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

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    public int getListIndex() {
        return listIndex;
    }

    public void setListIndex(int listIndex) {
        this.listIndex = listIndex;
    }

    public AppModelListener.LoadListener getLoadListener() {
        return mLoadListener;
    }

    public void setLoadListener(AppModelListener.LoadListener loadListener) {
        this.mLoadListener = loadListener;
    }

    public AppModelListener.UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    public void setUpdateListener(AppModelListener.UpdateListener updateListener) {
        this.mUpdateListener = updateListener;
    }
}
