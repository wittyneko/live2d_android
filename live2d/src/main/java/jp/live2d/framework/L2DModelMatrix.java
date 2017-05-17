/**
 * You can modify and use this source freely
 * only for the development of application related Live2D.
 * <p>
 * (c) Live2D Inc. All rights reserved.
 */
package jp.live2d.framework;


public class L2DModelMatrix extends L2DMatrix44 {

    private float mWidth;
    private float mHeight;
    private float DEFAULT = Float.MIN_VALUE;
    private float centerX = DEFAULT;
    private float centerY = DEFAULT;
    private float width = DEFAULT;
    private float height = DEFAULT;

    public L2DModelMatrix(float w, float h) {
        mWidth = w;
        mHeight = h;
    }

    public void setPosition(float x, float y) {
        translate(x, y);
    }


    public void setCenterPosition(float x, float y) {
        centerX = x;
        centerY = y;
        float w = mWidth * getScaleX();
        float h = mHeight * getScaleY();
        translate(x - w / 2, y - h / 2);
    }


    public void top(float y) {
        setY(y);
    }


    public void bottom(float y) {
        float h = mHeight * getScaleY();
        translateY(y - h);
    }


    public void left(float x) {
        setX(x);
    }


    public void right(float x) {
        float w = mWidth * getScaleX();
        translateX(x - w);
    }


    public void centerX(float x) {
        centerX = x;
        float w = mWidth * getScaleX();
        translateX(x - w / 2);
    }


    public void centerY(float y) {
        centerY = y;
        float h = mHeight * getScaleY();
        translateY(y - h / 2);
    }


    public void setX(float x) {
        translateX(x);
    }


    public void setY(float y) {
        translateY(y);
    }


    public void setHeight(float h) {
        height = h;
        float scaleX = h / mHeight;
        float scaleY = -scaleX;
        scale(scaleX, scaleY);
    }


    public void setWidth(float w) {
        width = w;
        float scaleX = w / mWidth;
        float scaleY = -scaleX;
        scale(scaleX, scaleY);
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
