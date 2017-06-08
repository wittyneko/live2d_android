/**
 * You can modify and use this source freely
 * only for the development of application related Live2D.
 * <p>
 * (c) Live2D Inc. All rights reserved.
 */
package cn.wittyneko.live2d.wallpaper;


import android.view.MotionEvent;

import net.rbgrn.android.glwallpaperservice.*;

public class LiveWallpaperService extends GLWallpaperService {
    public LiveWallpaperService() {
        super();
    }


    public Engine onCreateEngine() {
        Live2dEngine engine = new Live2dEngine();
        return engine;
    }

    public class Live2dEngine extends GLEngine {
        Live2DRenderer renderer;

        public Live2dEngine() {
            super();
            // handle prefs, other initialization
            renderer = new Live2DRenderer(getApplicationContext());
            setRenderer(renderer);
            setRenderMode(RENDERMODE_CONTINUOUSLY);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    renderer.resetDrag();
                    break;
                case MotionEvent.ACTION_MOVE:
                    renderer.drag(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (renderer != null) {
                renderer.release();
            }
            renderer = null;
        }
    }
}
