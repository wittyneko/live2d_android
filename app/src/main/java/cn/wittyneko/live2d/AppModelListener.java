package cn.wittyneko.live2d;

import cn.wittyneko.live2d.app.LAppModel;

/**
 * Created by wittytutu on 17-5-10.
 */

public interface AppModelListener {
    /**
     * 加载监听
     */
    interface LoadListener {
        void load(L2DAppModel model);
    }

    /**
     * 更新监听
     */
    interface UpdateListener {
        void update(L2DAppModel model);
    }
}
