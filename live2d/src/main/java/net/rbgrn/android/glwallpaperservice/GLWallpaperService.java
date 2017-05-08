/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.rbgrn.android.glwallpaperservice;

import java.io.Writer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

import net.rbgrn.android.glwallpaperservice.BaseConfigChooser.ComponentSizeChooser;
import net.rbgrn.android.glwallpaperservice.BaseConfigChooser.SimpleEGLConfigChooser;

import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

// Original code provided by Robert Green
// GitHub https://github.com/markfguerra/GLWallpaperService
// http://www.rbgrn.net/content/354-glsurfaceview-adapted-3d-live-wallpapers
public class GLWallpaperService extends WallpaperService {
    private static final String TAG = "GLWallpaperService";

    @Override
    public Engine onCreateEngine() {
        return new GLEngine();
    }

    public class GLEngine extends Engine {
        public final static int RENDERMODE_WHEN_DIRTY = 0;
        public final static int RENDERMODE_CONTINUOUSLY = 1;

        private GLThread mGLThread;
        private GLSurfaceView.EGLConfigChooser mEGLConfigChooser;
        private GLSurfaceView.EGLContextFactory mEGLContextFactory;
        private GLSurfaceView.EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
        private GLSurfaceView.GLWrapper mGLWrapper;
        private int mDebugFlags;

        public GLEngine() {
            super();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                onResume();
            } else {
                onPause();
            }
            super.onVisibilityChanged(visible);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            // Log.d(TAG, "GLEngine.onCreate()");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            // Log.d(TAG, "GLEngine.onDestroy()");
            mGLThread.requestExitAndWait();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // Log.d(TAG, "onSurfaceChanged()");
            mGLThread.onWindowResize(width, height);
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "onSurfaceCreated()");
            mGLThread.surfaceCreated(holder);
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "onSurfaceDestroyed()");
            mGLThread.surfaceDestroyed();
            super.onSurfaceDestroyed(holder);
        }

        /**
         * An EGL helper class.
         */
        public void setGLWrapper(GLSurfaceView.GLWrapper glWrapper) {
            mGLWrapper = glWrapper;
        }

        public void setDebugFlags(int debugFlags) {
            mDebugFlags = debugFlags;
        }

        public int getDebugFlags() {
            return mDebugFlags;
        }

        public void setRenderer(GLSurfaceView.Renderer renderer) {
            checkRenderThreadState();
            if (mEGLConfigChooser == null) {
                mEGLConfigChooser = new SimpleEGLConfigChooser(true);
            }
            if (mEGLContextFactory == null) {
                mEGLContextFactory = new DefaultContextFactory();
            }
            if (mEGLWindowSurfaceFactory == null) {
                mEGLWindowSurfaceFactory = new DefaultWindowSurfaceFactory();
            }
            mGLThread = new GLThread(renderer, mEGLConfigChooser, mEGLContextFactory, mEGLWindowSurfaceFactory, mGLWrapper);
            mGLThread.start();
        }

        public void setEGLContextFactory(GLSurfaceView.EGLContextFactory factory) {
            checkRenderThreadState();
            mEGLContextFactory = factory;
        }

        public void setEGLWindowSurfaceFactory(GLSurfaceView.EGLWindowSurfaceFactory factory) {
            checkRenderThreadState();
            mEGLWindowSurfaceFactory = factory;
        }

        public void setEGLConfigChooser(GLSurfaceView.EGLConfigChooser configChooser) {
            checkRenderThreadState();
            mEGLConfigChooser = configChooser;
        }

        public void setEGLConfigChooser(boolean needDepth) {
            setEGLConfigChooser(new SimpleEGLConfigChooser(needDepth));
        }

        public void setEGLConfigChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize,
                                        int stencilSize) {
            setEGLConfigChooser(new ComponentSizeChooser(redSize, greenSize, blueSize, alphaSize, depthSize,
                    stencilSize));
        }

        public void setRenderMode(int renderMode) {
            mGLThread.setRenderMode(renderMode);
        }

        public int getRenderMode() {
            return mGLThread.getRenderMode();
        }

        public void requestRender() {
            mGLThread.requestRender();
        }

        public void onPause() {
            mGLThread.onPause();
        }

        public void onResume() {
            mGLThread.onResume();
        }

        public void queueEvent(Runnable r) {
            mGLThread.queueEvent(r);
        }

        private void checkRenderThreadState() {
            if (mGLThread != null) {
                throw new IllegalStateException("setRenderer has already been called for this instance.");
            }
        }
    }

    /**
     * Empty wrapper for {@link GLSurfaceView.Renderer}.
     *
     * @deprecated Use {@link GLSurfaceView.Renderer} instead.
     */
    @Deprecated
    public interface Renderer extends GLSurfaceView.Renderer {
    }
}

// ----------------------------------------------------------------------

/**
 * Empty wrapper for {@link GLSurfaceView.EGLContextFactory}.
 *
 * @deprecated Use {@link GLSurfaceView.EGLContextFactory} instead.
 */
@Deprecated
interface EGLContextFactory extends GLSurfaceView.EGLContextFactory {
}

/**
 * Empty wrapper for {@link GLSurfaceView.EGLWindowSurfaceFactory}.
 *
 * @deprecated Use {@link GLSurfaceView.EGLWindowSurfaceFactory} instead.
 */
@Deprecated
interface EGLWindowSurfaceFactory extends GLSurfaceView.EGLWindowSurfaceFactory {
}

/**
 * Empty wrapper for {@link GLSurfaceView.GLWrapper}.
 *
 * @deprecated Use {@link GLSurfaceView.GLWrapper} instead.
 */
@Deprecated
interface GLWrapper extends GLSurfaceView.GLWrapper {
}

/**
 * Empty wrapper for {@link GLSurfaceView.EGLConfigChooser}.
 *
 * @deprecated Use {@link GLSurfaceView.EGLConfigChooser} instead.
 */
@Deprecated
interface EGLConfigChooser extends GLSurfaceView.EGLConfigChooser {
}

