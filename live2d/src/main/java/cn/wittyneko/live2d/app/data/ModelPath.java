package cn.wittyneko.live2d.app.data;

import java.util.ArrayList;

/**
 * Created by wittytutu on 17-5-9.
 */

public class ModelPath {
    private int index = -1; // 当前选中模型
    private ArrayList<String> path = new ArrayList<>(); // 模型路径

    public ArrayList<String> getPath() {
        return path;
    }

    public void setPath(ArrayList<String> path) {
        this.path = path;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
