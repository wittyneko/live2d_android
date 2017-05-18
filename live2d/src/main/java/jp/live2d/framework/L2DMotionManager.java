/**
 * You can modify and use this source freely
 * only for the development of application related Live2D.
 * <p>
 * (c) Live2D Inc. All rights reserved.
 */
package jp.live2d.framework;

import cn.wittyneko.live2d.app.LAppDefine;
import jp.live2d.ALive2DModel;
import jp.live2d.motion.AMotion;
import jp.live2d.motion.MotionQueueManager;

/**
 * 动作队列管理
 * 动作优先级处理
 */
public class L2DMotionManager extends MotionQueueManager {


    private int currentPriority; // 当前动作优先级
    private int reservePriority; // 预备动作优先级


    public int getCurrentPriority() {
        return currentPriority;
    }


    public int getReservePriority() {
        return reservePriority;
    }


    /**
     * 预备动作测试
     *
     * @param priority
     * @return
     */
    public boolean reserveMotion(int priority) {
        if (reservePriority >= priority) {
            return false;
        }
        if (currentPriority >= priority) {
            return false;
        }
        reservePriority = priority;
        return true;
    }


    public void setReservePriority(int val) {
        reservePriority = val;
    }


    @Override
    public boolean updateParam(ALive2DModel model) {
        boolean updated = super.updateParam(model);
        if (isFinished()) {
            currentPriority = LAppDefine.PRIORITY_NONE;
        }
        return updated;
    }

    /**
     * 开始动作
     *
     * @param motion
     * @param priority
     * @return
     */
    public int startMotionPrio(AMotion motion, int priority) {
        if (priority == reservePriority) {
            reservePriority = LAppDefine.PRIORITY_NONE;
        }
        currentPriority = priority;
        return super.startMotion(motion, false);
    }
}
