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

    private JsonObject preferenceJson; //配置json
    private String preferenceState; // 配置状态

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

        live2DModel.update();
    }

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

    @Override
    public void startIdleRandomMotion() {
        startPreferenceMotionExpression(L2DAppDefine.MOTION_GROUP_IDLE, L2DAppDefine.PRIORITY_IDLE);
    }

    /**
     * 随机当前状态配置文件 的 肢体+表情动作
     *
     * @param name     动作名称
     * @param priority 优先级
     */
    public void startPreferenceMotionExpression(String name, int priority) {
        startPreferenceMotionExpression(preferenceState, name, priority);
    }

    /**
     * 随机指定状态配置文件 的 肢体+表情动作
     *
     * @param state    状态
     * @param name     动作名称
     * @param priority 优先级
     */
    public void startPreferenceMotionExpression(String state, String name, int priority) {
        // 配置文件存在，并且当前状态有数据
        if (preferenceJson != null && state != null) {
            //Log.e(getClass().getSimpleName(), "preference -> " + state + ", " + name + ", " + priority);
            if (preferenceJson.has(state)) {
                JsonObject stateJson = preferenceJson.getAsJsonObject(state);
                startRandomMotionExpression(stateJson, state, name, priority);
            } else if (!L2DAppDefine.STATE_DEFAULT.equals(state)) {
                // 无当前状态，读取默认状态
                startPreferenceMotionExpression(L2DAppDefine.STATE_DEFAULT, name, priority);
            }
        }
    }

    /**
     * 随机肢体+表情动作（有状态：登录、表白、分手）
     *
     * @param stateJson 状态配置信息
     * @param state     状态
     * @param name      动作名称
     * @param priority  优先级
     */
    public void startRandomMotionExpression(JsonObject stateJson, String state, String name, int priority) {
        // 动作存在
        if (stateJson.has(name) && stateJson.get(name).isJsonArray()) {
            // 动作配置信息列表
            JsonArray array = stateJson.get(name).getAsJsonArray();
            // 获取随机组合
            int no = (int) (Math.random() * array.size());
            JsonObject action = array.get(no).getAsJsonObject();
            startMotionExpression(action, name, priority);
        } else if (!L2DAppDefine.STATE_DEFAULT.equals(state)) {
            // 当前状态无动作，调用默认状态动作
            startPreferenceMotionExpression(L2DAppDefine.STATE_DEFAULT, name, priority);
        }
    }

    /**
     * 执行动作 + 表情( 无状态：活动)
     *
     * @param action   动作配置信息
     * @param name     动作名称
     * @param priority 优先级
     */
    public void startMotionExpression(JsonObject action, String name, int priority) {

        // 肢体
        if (action.has("motion") && action.get("motion").isJsonObject()) {
            int motion = action.get("motion").getAsJsonObject().get("index").getAsInt();
            //startMotion(name, motion, priority);
            // 部位动作存在调用，否则调用统一动作
            if (getModelSetting().getMotionNum(name) != 0) {
                startMotion(name, motion, priority);
            } else {
                // 调用统一动作
                startMotion(L2DAppDefine.MOTION_GROUP_INDEX, motion, priority);
            }
        }

        // 表情
        if (action.has("expression") && action.get("expression").isJsonObject()) {
            int expression = action.get("expression").getAsJsonObject().get("index").getAsInt();
            setExpression(expression);
        }

        //  对话消息
        if (action.has("dialog") && action.get("dialog").isJsonArray()) {
            JsonArray jsonArray = action.get("dialog").getAsJsonArray();
            int size = jsonArray.size();

            // 解析播放声音文件
            for (int i = 0; i < size; i++) {

                // 存在音频
                boolean hasAudio = jsonArray.get(i).getAsJsonObject().has("audio");
                if (hasAudio) {
                    // 计算播放时间
                    float delay = 0f;
                    if (i == 0) {
                        delay = 0f;
                    } else {
                        boolean hasDelay = jsonArray.get(i - 1).getAsJsonObject().has("delay");
                        if (hasDelay) {
                            delay = jsonArray.get(i - 1).getAsJsonObject().get("delay").getAsFloat();
                        } else {
                            delay = 0f;
                        }
                    }

                    // 播放音频
                    final String audio = jsonArray.get(i).getAsJsonObject().get("audio").getAsString();
                    final String audioPath = modelSourceDir + "/" + audio;
                    final String volPath = audioPath + ".vol";
                    Log.e("audio", "-> " + audioPath);
                    Log.e("vol", "-> " + volPath);
                    SoundManager.playable = MutaHelper.getInstance().getModel().getSettingMsgSound();
                    if (delay == 0f) {
                        SoundManager.play(audioPath);
                        loadMouthSync(volPath);
                    } else {
                        ThreadUtil.runOnBackgroundThread(new Runnable() {
                            @Override
                            public void run() {
                                SoundManager.play(audioPath);
                                loadMouthSync(volPath);
                            }
                        }, (long) (delay * 1000));
                    }
                }
            }

            // mini弹窗
            // 空闲动作、无对话，则不发送弹窗消息
            // 方法不一定在主线程，需要注意线程切换
            if (!L2DAppDefine.MOTION_GROUP_IDLE.equals(name) && jsonArray != null && jsonArray.size() != 0) {
                RxBus.getInstance().send(IRxBusURI.LIVE2D_SHOW_MINI_DIALOG, jsonArray);
            }
            //Log.e("live2d dialog", "-> " + GsonUtil.toJson(action.get("dialog")));
        }

        //  数值变动
        if (action.has("value") && action.get("value").isJsonObject()) {
            int min = action.get("value").getAsJsonObject().get("min").getAsInt();
            int max = action.get("value").getAsJsonObject().get("max").getAsInt();
            int len = max - min;
            int value = (int) (Math.random() * len) + min;
            RxBus.getInstance().send(IRxBusURI.LIVE2D_UPDATE_VALUE, value);
        }
    }

    // 添加默认动作和表情
    private void addDefaultMotionExpression(JsonObject preferenceJson) {
        // 添加默认闲置动作和表情
        addMotionExpression(preferenceJson, L2DAppDefine.STATE_DEFAULT,
                L2DAppDefine.MOTION_GROUP_IDLE,
                GsonUtil.form(L2DAppDefine.MOTION_IDLE, JsonArray.class));
        // 添加默认声音开动作
        addMotionExpression(preferenceJson, L2DAppDefine.STATE_DEFAULT,
                L2DAppDefine.MOTION_GROUP_SOUND_ON,
                GsonUtil.form(L2DAppDefine.MOTION_SOUND_ON, JsonArray.class));
        // 添加默认声音关动作
        addMotionExpression(preferenceJson, L2DAppDefine.STATE_DEFAULT,
                L2DAppDefine.MOTION_GROUP_SOUND_OFF,
                GsonUtil.form(L2DAppDefine.MOTION_SOUND_OFF, JsonArray.class));
//        for (int i = 1; i <= 10; i++) {
//            String state = String.valueOf(i);
//            addMotionExpression(preferenceJson, state, L2DAppDefine.MOTION_GROUP_IDLE, GsonUtil.form(L2DAppDefine.MOTION_IDLE, JsonArray.class));
//        }
    }

    /**
     * 添加动作和表情
     *
     * @param preferenceJson 配置文件
     * @param state          状态
     * @param name           动作名称
     * @param action         动作列表
     */
    private void addMotionExpression(JsonObject preferenceJson, String state, String name, JsonElement action) {

        JsonObject stateJson = new JsonObject();
        if (preferenceJson.has(state)) {
            stateJson = preferenceJson.get(state).getAsJsonObject();
        } else {
            preferenceJson.add(state, stateJson);
        }
        // 动作不存在、或为空列表
        if (!stateJson.has(name)
                || (stateJson.get(name).isJsonArray() && stateJson.getAsJsonArray(name).size() == 0)) {
            stateJson.add(name, action);
            //Log.e("state",  state + " -> " + GsonUtil.toJson(stateJson));
        }
    }

    public JsonObject getPreferenceJson() {
        return preferenceJson;
    }

    // 更新模型动作配置
    public void setPreferenceJson(JsonObject preferenceJson) {
        this.preferenceJson = preferenceJson;
        addDefaultMotionExpression(this.preferenceJson);
    }

    public String getPreferenceState() {
        return preferenceState;
    }

    // 更新状态
    public void setPreferenceState(String preferenceState) {
        //preferenceState = "1";
        this.preferenceState = preferenceState;
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
