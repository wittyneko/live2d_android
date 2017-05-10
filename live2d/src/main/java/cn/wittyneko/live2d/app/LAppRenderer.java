/**
 * You can modify and use this source freely
 * only for the development of application related Live2D.
 * <p>
 * (c) Live2D Inc. All rights reserved.
 */
package cn.wittyneko.live2d.app;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.live2d.framework.L2DViewMatrix;
import cn.wittyneko.live2d.utils.FileManager;
import cn.wittyneko.live2d.utils.OffscreenImage;
import cn.wittyneko.live2d.utils.SimpleImage;

import android.opengl.GLSurfaceView;

/**
 * Live2D View 渲染器
 */
public class LAppRenderer implements GLSurfaceView.Renderer {

    private LAppLive2DManager delegate;

    private SimpleImage bg;

    private float accelX = 0;
    private float accelY = 0;


    public LAppRenderer(LAppLive2DManager live2DMgr) {
        this.delegate = live2DMgr;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {

        //setupBackground(gl);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        delegate.onSurfaceChanged(gl, width, height);//Live2D Event


        gl.glViewport(0, 0, width, height);


        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        L2DViewMatrix viewMatrix = delegate.getViewMatrix();

        gl.glOrthof(
                viewMatrix.getScreenLeft(),
                viewMatrix.getScreenRight(),
                viewMatrix.getScreenBottom(),
                viewMatrix.getScreenTop(),
                0.5f, -0.5f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);


        OffscreenImage.createFrameBuffer(gl, width, height, 0);
        return;
    }


    @Override
    public void onDrawFrame(GL10 gl) {

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // 加载模型
        delegate.update(gl);


        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();


        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glDisable(GL10.GL_CULL_FACE);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);


        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        gl.glColor4f(1, 1, 1, 1);


        gl.glPushMatrix();
        {

            L2DViewMatrix viewMatrix = delegate.getViewMatrix();
            gl.glMultMatrixf(viewMatrix.getArray(), 0);


            // 绘制背景
            if (delegate.getBg() != null) {
                gl.glPushMatrix();
                {
                    float SCALE_X = 0.25f;
                    float SCALE_Y = 0.1f;
                    gl.glTranslatef(-SCALE_X * accelX, SCALE_Y * accelY, 0);

                    delegate.getBg().draw(gl);
                }
                gl.glPopMatrix();
            }

            // 绘制模型
            for (int i = 0; i < delegate.getModelNum(); i++) {
                LAppModel model = delegate.getModel(i);
                if (model.isInitialized() && !model.isUpdating()) {
                    model.update();
                    model.draw(gl);
                }
            }
        }
        gl.glPopMatrix();


    }


    public void setAccel(float x, float y, float z) {
        accelX = x;
        accelY = y;
    }

    //初始化背景
//    private void setupBackground(GL10 gl) {
//        try {
//            InputStream in = FileManager.open(LAppDefine.BACK_IMAGE_NAME);
//            bg = new SimpleImage(gl, in);
//
//            bg.setDrawRect(
//                    LAppDefine.VIEW_LOGICAL_MAX_LEFT,
//                    LAppDefine.VIEW_LOGICAL_MAX_RIGHT,
//                    LAppDefine.VIEW_LOGICAL_MAX_BOTTOM,
//                    LAppDefine.VIEW_LOGICAL_MAX_TOP);
//
//
//            bg.setUVRect(0.0f, 1.0f, 0.0f, 1.0f);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
