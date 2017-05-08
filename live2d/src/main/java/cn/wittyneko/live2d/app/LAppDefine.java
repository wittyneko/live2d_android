/**
 * You can modify and use this source freely
 * only for the development of application related Live2D.
 * <p>
 * (c) Live2D Inc. All rights reserved.
 */
package cn.wittyneko.live2d.app;

/**
 * 模型配置
 */
public class LAppDefine {

    public static boolean DEBUG_LOG = true;
    public static boolean DEBUG_TOUCH_LOG = false;
    public static boolean DEBUG_DRAW_HIT_AREA = false;


    // 最大最小缩放尺寸
    public static final float VIEW_MAX_SCALE = 2f;
    public static final float VIEW_MIN_SCALE = 0.8f;

    public static final float VIEW_LOGICAL_LEFT = -1;
    public static final float VIEW_LOGICAL_RIGHT = 1;

    public static final float VIEW_LOGICAL_MAX_LEFT = -2;
    public static final float VIEW_LOGICAL_MAX_RIGHT = 2;
    public static final float VIEW_LOGICAL_MAX_BOTTOM = -2;
    public static final float VIEW_LOGICAL_MAX_TOP = 2;

    //================================================//


    public static final String SOURCES_DIR = "sources";

    public static final String BACK_IMAGE_NAME = "image/back_class_normal.png";


    public static final String MODEL_HARU = "live2d/haru/haru.model.json";
    public static final String MODEL_HARU_A = "live2d/haru/haru_01.model.json";
    public static final String MODEL_HARU_B = "live2d/haru/haru_02.model.json";
    public static final String MODEL_SHIZUKU = "live2d/shizuku/shizuku.model.json";
    public static final String MODEL_WANKO = "live2d/wanko/wanko.model.json";

    //===================================================//

    // 动作组 key
    public static final String MOTION_GROUP_INDEX = "index"; //默认
    public static final String MOTION_GROUP_IDLE = "idle"; //闲置
    public static final String MOTION_GROUP_SHAKE = "shake"; //摇晃
    public static final String MOTION_GROUP_PINCH_IN = "pinch_in"; //缩小
    public static final String MOTION_GROUP_PINCH_OUT = "pinch_out"; //放大

    // 事件
    public static final String EVENT_TAP = "tap_"; // 点击
    public static final String EVENT_FLICK = "drag_"; //滑动
    public static final String EVENT_LONG_PRESS = "longpress_"; //滑动


    static final String HIT_AREA_HEAD = "head";
    static final String HIT_AREA_BODY = "body";

    //===============================================//

    // 动作执行优先级
    public static final int PRIORITY_NONE = 0; // 不执行
    public static final int PRIORITY_IDLE = 1; //空闲执行
    public static final int PRIORITY_NORMAL = 2; //默认执行, 上一个>=优先级动作结束才可触发
    public static final int PRIORITY_FORCE = 3; //强制执行, 多个动作会同时触发

}
