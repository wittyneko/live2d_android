/**
 * You can modify and use this source freely
 * only for the development of application related Live2D.
 * <p>
 * (c) Live2D Inc. All rights reserved.
 */
package cn.wittyneko.live2d.app;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import jp.live2d.android.Live2DModelAndroid;
import jp.live2d.base.IBaseData;
import jp.live2d.framework.L2DBaseModel;
import jp.live2d.framework.L2DEyeBlink;
import jp.live2d.framework.L2DStandardID;
import jp.live2d.framework.Live2DFramework;
import jp.live2d.model.PartsData;
import jp.live2d.motion.AMotion;
import jp.live2d.param.ParamDefFloat;
import jp.live2d.util.UtSystem;
import cn.wittyneko.live2d.utils.BufferUtil;
import cn.wittyneko.live2d.utils.FileManager;
import cn.wittyneko.live2d.utils.ModelSetting;
import cn.wittyneko.live2d.utils.ModelSettingJson;
import cn.wittyneko.live2d.utils.OffscreenImage;
import cn.wittyneko.live2d.utils.SoundManager;

import android.util.Log;

/**
 * 模型信息
 */
public abstract class LAppModel extends L2DBaseModel {

    public String TAG = "LAppModel ";

    protected ModelSetting modelSetting = null; // 模型json
    protected String modelNameDir; // 模型名称
    protected String modelHomeDir; // 模型文件目录


    static FloatBuffer debugBufferVer = null;
    static FloatBuffer debugBufferColor = null;

    static Object lock = new Object();

    //=============模型信息================//

    /**
     * TODO: 获取模型参数列表和部件信息
     */
    public void getModelInfo() {

        // 获取动作参数列表
        List<ParamDefFloat> paramList = getLive2DModel().getModelImpl().getParamDefSet().getParamDefFloatList();
        for (ParamDefFloat param : paramList) {
            Log.e("param", "-> " + param.getParamID().toString() + ", " + param.getMinValue() + ", " + param.getMaxValue() + ", " + param.getDefaultValue());
        }

        Log.e("---", "-------");
        Log.e("---", "-------");
        Log.e("---", "-------");

        // 获取部件信息
        List<PartsData> partsDataList = getLive2DModel().getModelImpl().getPartsDataList();
        for (PartsData parts : partsDataList) {
            Log.e("parts", "-> " + parts.getPartsID().toString());
            List<IBaseData> baseList = parts.getBaseData();
            for (IBaseData base : baseList) {
                // base.e()
                Log.e("parts", "base -> " + base.e());
                //Log.e("parts", "base -> " + base.d() + " --- " + base.e());
            }
            List<jp.live2d.draw.IDrawData> drawList = parts.getDrawData();
            for (jp.live2d.draw.IDrawData draw : drawList) {
                // draw.i()
                Log.e("parts", "draw -> " + draw.i());
                //Log.e("parts", "draw -> "  + draw.i() + " --- " + draw.j() + " --- " + draw.n());
            }
        }
    }

    public String getModelNameDir() {
        return modelNameDir;
    }

    public String getModelHomeDir() {
        return modelHomeDir;
    }

    public ModelSetting getModelSetting() {
        return modelSetting;
    }

    public LAppModel() {
        super();

        if (LAppDefine.DEBUG_LOG) {
            debugMode = true;
//			mainMotionManager.setMotionDebugMode(true);
        }
    }

    //============模型加载==============//


    // 释放模型
    public void release() {
        if (live2DModel == null) return;
        live2DModel.deleteTextures();
    }

    // 加载模型
    public void load(GL10 gl, String modelSettingPath) throws Exception {
        updating = true;
        initialized = false;

        modelHomeDir = modelSettingPath.substring(0, modelSettingPath.lastIndexOf("/") + 1);//live2d/model/xxx/
        modelNameDir = getModelName(modelSettingPath);
        //Log.e("modelPath", "-> " + modelHomeDir);
        PlatformManager pm = (PlatformManager) Live2DFramework.getPlatformManager();
        pm.setGL(gl);

//		if(LAppDefine.DEBUG_LOG)
//			Log.d(TAG, "json : "+modelSettingPath);

        // 解析模型Json
        try {
            InputStream in = FileManager.open(modelSettingPath);
            modelSetting = new ModelSettingJson(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();


            throw new Exception();
        }

        // 获取模型名
        if (modelSetting.getModelName() != null) {
            TAG += modelSetting.getModelName();
        }

        if (LAppDefine.DEBUG_LOG) Log.d(TAG, "Load model.");

        // 加载模型
        loadModelData(modelHomeDir + modelSetting.getModelFile());
        //getModelInfo();
        String[] texPaths = modelSetting.getTextureFiles();
        for (int i = 0; i < texPaths.length; i++) {
            loadTexture(i, modelHomeDir + texPaths[i]);
        }

        String[] expressionNames = modelSetting.getExpressionNames();
        String[] expressionPaths = modelSetting.getExpressionFiles();

        for (int i = 0; i < expressionPaths.length; i++) {
            //Log.e("expressions", "-> " + expressionNames[i]);
            loadExpression(expressionNames[i], modelHomeDir + expressionPaths[i]);
        }


        loadPhysics(modelHomeDir + modelSetting.getPhysicsFile());


        loadPose(modelHomeDir + modelSetting.getPoseFile());


        // 调整模型初始位置
        HashMap<String, Float> layout = new HashMap<String, Float>();
        if (modelSetting.getLayout(layout)) {
            if (layout.get("width") != null) modelMatrix.setWidth(layout.get("width"));
            if (layout.get("height") != null) modelMatrix.setHeight(layout.get("height"));
            if (layout.get("x") != null) modelMatrix.setX(layout.get("x"));
            if (layout.get("y") != null) modelMatrix.setY(layout.get("y"));
            if (layout.get("center_x") != null) modelMatrix.centerX(layout.get("center_x"));
            if (layout.get("center_y") != null) modelMatrix.centerY(layout.get("center_y"));
            if (layout.get("top") != null) modelMatrix.top(layout.get("top"));
            if (layout.get("bottom") != null) modelMatrix.bottom(layout.get("bottom"));
            if (layout.get("left") != null) modelMatrix.left(layout.get("left"));
            if (layout.get("right") != null) modelMatrix.right(layout.get("right"));
        }

        //Sound 加载声音列表
        //String[] soundPaths = modelSetting.getSoundPaths();
//        String modelSourceDir = modelHomeDir + LAppDefine.SOURCES_DIR;
//        String[] soundPaths = SoundManager.getSoundList(modelSourceDir);
//        for (int i = 0; i < soundPaths.length; i++) {
//            String path = soundPaths[i];
//            //Log.e("sound", "-> " + path);
//            SoundManager.load(path);
//        }


        for (int i = 0; i < modelSetting.getInitParamNum(); i++) {
            String id = modelSetting.getInitParamID(i);
            float value = modelSetting.getInitParamValue(i);
            live2DModel.setParamFloat(id, value);
        }

        for (int i = 0; i < modelSetting.getInitPartsVisibleNum(); i++) {
            String id = modelSetting.getInitPartsVisibleID(i);
            float value = modelSetting.getInitPartsVisibleValue(i);
            live2DModel.setPartsOpacity(id, value);
        }


        eyeBlink = new L2DEyeBlink();

        updating = false;
        initialized = true;
    }

    public void preloadMotionGroup(String name) {
        int len = modelSetting.getMotionNum(name);
        for (int i = 0; i < len; i++) {
            String fileName = modelSetting.getMotionFile(name, i);
            AMotion motion = loadMotion(fileName, modelHomeDir + fileName);
            motion.setFadeIn(modelSetting.getMotionFadeIn(name, i));
            motion.setFadeOut(modelSetting.getMotionFadeOut(name, i));
        }
    }

    // 截取模型名称
    private String getModelName(String model) {

        int end = model.lastIndexOf("/");
        int begin = model.lastIndexOf("/", end - 1);
        return model.substring(begin, end);
    }


    //===========绘制============//


    public void update() {
        if (live2DModel == null) {
            if (LAppDefine.DEBUG_LOG) Log.d(TAG, "Failed to update.");
            return;
        }

        long timeMSec = UtSystem.getUserTimeMSec() - startTimeMSec;
        double timeSec = timeMSec / 1000.0;
        double t = timeSec * 2 * Math.PI;


        if (mainMotionManager.isFinished()) {

            // 闲置动作
            startIdleRandomMotion();
        }

        //-----------------------------------------------------------------
        live2DModel.loadParam();

        boolean update = mainMotionManager.updateParam(live2DModel);
        if (!update) {

            eyeBlink.updateParam(live2DModel);
        }

        live2DModel.saveParam();
        //-----------------------------------------------------------------


        if (expressionManager != null) {
            //Log.e("expressions", "-> " + expressionManager.isFinished());
            if (!expressionManager.isFinished()) {
                resetMouth();
            }
            expressionManager.updateParam(live2DModel);
        }


        live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_X, dragX * 30, 1);
        live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Y, dragY * 30, 1);
        live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Z, (dragX * dragY) * -30, 1);


        live2DModel.addToParamFloat(L2DStandardID.PARAM_BODY_ANGLE_X, dragX * 10, 1);


        live2DModel.addToParamFloat(L2DStandardID.PARAM_EYE_BALL_X, dragX, 1);
        live2DModel.addToParamFloat(L2DStandardID.PARAM_EYE_BALL_Y, dragY, 1);


        live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_X, (float) (15 * Math.sin(t / 6.5345)), 0.5f);
        live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Y, (float) (8 * Math.sin(t / 3.5345)), 0.5f);
        live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Z, (float) (10 * Math.sin(t / 5.5345)), 0.5f);
        live2DModel.addToParamFloat(L2DStandardID.PARAM_BODY_ANGLE_X, (float) (4 * Math.sin(t / 15.5345)), 0.5f);
        live2DModel.setParamFloat(L2DStandardID.PARAM_BREATH, (float) (0.5f + 0.5f * Math.sin(t / 3.2345)), 1);


        live2DModel.addToParamFloat(L2DStandardID.PARAM_ANGLE_Z, 90 * accelX, 0.5f);

        if (physics != null) physics.updateParam(live2DModel);


        if (lipSync) {
            resetMouth();
            live2DModel.setParamFloat(L2DStandardID.PARAM_MOUTH_OPEN_Y, lipSyncValue, 0.8f);
        }

        if (pose != null) pose.updateParam(live2DModel);

        //live2DModel.update();
    }


    private void drawHitArea(GL10 gl) {
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glPushMatrix();
        {
            gl.glMultMatrixf(modelMatrix.getArray(), 0);
            int len = modelSetting.getHitAreasNum();
            for (int i = 0; i < len; i++) {
                String drawID = modelSetting.getHitAreaID(i);
                int drawIndex = live2DModel.getDrawDataIndex(drawID);
                if (drawIndex < 0) continue;
                float[] points = live2DModel.getTransformedPoints(drawIndex);
                float left = live2DModel.getCanvasWidth();
                float right = 0;
                float top = live2DModel.getCanvasHeight();
                float bottom = 0;

                for (int j = 0; j < points.length; j = j + 2) {
                    float x = points[j];
                    float y = points[j + 1];
                    if (x < left) left = x;
                    if (x > right) right = x;
                    if (y < top) top = y;
                    if (y > bottom) bottom = y;
                }

                float[] vertex = {left, top, right, top, right, bottom, left, bottom, left, top};
                float r = 1;
                float g = 0;
                float b = 0;
                float a = 0.5f;
                int size = 5;
                float color[] = {r, g, b, a, r, g, b, a, r, g, b, a, r, g, b, a, r, g, b, a};


                gl.glLineWidth(size);
                gl.glVertexPointer(2, GL10.GL_FLOAT, 0, BufferUtil.setupFloatBuffer(debugBufferVer, vertex));
                gl.glColorPointer(4, GL10.GL_FLOAT, 0, BufferUtil.setupFloatBuffer(debugBufferColor, color));
                gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, 5);
            }
        }
        gl.glPopMatrix();
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }


    //  渲染器绘制
    public void draw(GL10 gl) {
        ((Live2DModelAndroid) live2DModel).setGL(gl);

        alpha += accAlpha;

        if (alpha < 0) {
            alpha = 0;
            accAlpha = 0;
        } else if (alpha > 1) {
            alpha = 1;
            accAlpha = 0;
        }

        if (alpha < 0.001) return;

        if (alpha < 0.999) {


            OffscreenImage.setOffscreen(gl);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            gl.glPushMatrix();
            {
                gl.glMultMatrixf(modelMatrix.getArray(), 0);
                live2DModel.draw();
            }
            gl.glPopMatrix();


            OffscreenImage.setOnscreen(gl);
            gl.glPushMatrix();
            {
                gl.glLoadIdentity();
                OffscreenImage.drawDisplay(gl, alpha);
            }
            gl.glPopMatrix();
        } else {

            gl.glPushMatrix();
            {
                gl.glMultMatrixf(modelMatrix.getArray(), 0);
                live2DModel.draw();
            }
            gl.glPopMatrix();

            if (LAppDefine.DEBUG_DRAW_HIT_AREA) {

                drawHitArea(gl);
            }
        }
    }


    public void feedIn() {
        alpha = 0;
        accAlpha = 0.1f;
    }


    //===============动作表情处理==================//

    // 区域判断
    public boolean hitTest(String id, float testX, float testY) {
        if (alpha < 1) return false;
        if (modelSetting == null) return false;
        int len = modelSetting.getHitAreasNum();
        for (int i = 0; i < len; i++) {
            if (id.equals(modelSetting.getHitAreaName(i))) {
                return hitTestSimple(modelSetting.getHitAreaID(i), testX, testY);
            }
        }
        return false;
    }

    // 随机肢体动作
    public void startRandomMotion(String name, int priority) {
        int max = modelSetting.getMotionNum(name);
        int no = (int) (Math.random() * max);
        startMotion(name, no, priority);
    }

    // 肢体动作
    public void startMotion(String name, int no, int priority) {

        //动作有效判断
        if (0 <= no && no >= modelSetting.getMotionNum(name)) {
            return;
        }

        String motionName = modelSetting.getMotionFile(name, no);

        if (motionName == null || motionName.equals("")) {
            if (LAppDefine.DEBUG_LOG) {
                Log.d(TAG, "Failed to motion.");
            }
            return;
        }

        AMotion motion;

        if (priority == LAppDefine.PRIORITY_FORCE) {
            mainMotionManager.setReservePriority(priority);
        } else if (!mainMotionManager.reserveMotion(priority)) {
            if (LAppDefine.DEBUG_LOG) {
                Log.d(TAG, "Failed to motion.");
            }
            return;
        }

        String motionPath = modelHomeDir + motionName;
        motion = loadMotion(null, motionPath);

        if (motion == null) {
            Log.w(TAG, "Failed to load motion.");
            mainMotionManager.setReservePriority(LAppDefine.PRIORITY_NONE);
            return;
        }


        motion.setFadeIn(modelSetting.getMotionFadeIn(name, no));
        motion.setFadeOut(modelSetting.getMotionFadeOut(name, no));

        if (LAppDefine.DEBUG_LOG) Log.d(TAG, "Start motion : " + motionName);


        if (modelSetting.getMotionSound(name, no) == null) {
            mainMotionManager.startMotionPrio(motion, priority);
        } else {
            String soundName = modelSetting.getMotionSound(name, no);
            String soundPath = modelHomeDir + soundName;

            if (LAppDefine.DEBUG_LOG) Log.d(TAG, "sound : " + soundName);

            SoundManager.play(soundPath);
            mainMotionManager.startMotionPrio(motion, priority);
        }
    }

    //随机表情
    public void setRandomExpression() {
        int no = (int) (Math.random() * expressions.size());
        setExpression(no);
    }

    //执行表情动作(名称)
    public void setExpression(String name) {
        if (!expressions.containsKey(name)) return;
        if (LAppDefine.DEBUG_LOG) Log.d(TAG, "Expression : " + name);
        AMotion motion = expressions.get(name);
        expressionManager.startMotion(motion, false);
    }

    //执行表情动作(顺序)
    public void setExpression(int no) {
        //表情列表
        String[] expressions = modelSetting.getExpressionNames();
        if (0 <= no && no < expressions.length) {
            //执行表情动作
            setExpression(expressions[no]);
        }
    }

    /**
     * 重置清空嘴型
     */
    public abstract void resetMouth();

    /**
     * 随机闲置动作
     */
    public abstract void startIdleRandomMotion();
}