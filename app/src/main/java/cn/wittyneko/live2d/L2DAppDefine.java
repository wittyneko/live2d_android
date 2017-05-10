package cn.wittyneko.live2d;

import cn.wittyneko.live2d.app.LAppDefine;

/**
 * 模型配置
 * Created by wittytutu on 17-3-22.
 */

public class L2DAppDefine extends LAppDefine {

    //================================================//

    //模型 文件配置
    public static final String MODEL_YANXI = "live2d/yanxi1/model.model.json"; // 2.0
    public static final String BACK_IMAGE_NAME = "image/bg_img.jpg";
    public static final String SOURCES_DIR = "sources";

    //===================================================//

    // 动作组 key
    public static final String MOTION_GROUP_SING_ON = "sign_on"; // 签到
    public static final String MOTION_GROUP_BURDEN = "burden"; // 表白
    public static final String MOTION_GROUP_UNBURDEN = "unburden"; // 分手
    public static final String MOTION_GROUP_SOUND_ON = "sound_on"; // 声音开
    public static final String MOTION_GROUP_SOUND_OFF = "sound_off"; // 声音关

    // 可操作区域
    public static final String HIT_AREA_HEAD = "head"; // 头
    public static final String HIT_AREA_FACE = "face"; // 脸
    public static final String HIT_AREA_BODY = "body"; //身体
    public static final String HIT_AREA_HAND = "hand"; //手
    public static final String HIT_AREA_LEG = "leg"; //腿
    public static final String HIT_AREA_SKIRT = "skirt"; //裙

    // 状态
    public static final String STATE_DEFAULT = "default"; //默认状态

}
