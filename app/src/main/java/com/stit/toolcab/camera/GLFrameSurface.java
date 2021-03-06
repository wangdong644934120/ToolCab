package com.stit.toolcab.camera;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;


public class GLFrameSurface extends GLSurfaceView {

    private ICallback _cb = null;
    private GLFrameRenderer mRenderer;

    public GLFrameSurface(Context context) {
        super(context);
        cwInit();
    }

    public GLFrameSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        cwInit();
    }

    private synchronized void cwInit(){
        if(mRenderer == null){
            setEGLContextClientVersion(2);
            mRenderer = new GLFrameRenderer(this);
            this.setRenderer(mRenderer);
            setRenderMode(RENDERMODE_WHEN_DIRTY);
        }else{
            if(this.getCurrentRenderer() == null){
                this.setRenderer(mRenderer);
                setRenderMode(RENDERMODE_WHEN_DIRTY);
            }
        }
    }

    public GLFrameRenderer getCurrentRenderer(){
        return mRenderer;
    }

    public void setCallback(ICallback cb) {
        _cb = cb;
    }

    @Override
    public void setRenderer(Renderer renderer) {
        super.setRenderer(renderer);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        //Utils.LOGD("surface onAttachedToWindow()");
        super.onAttachedToWindow();
        cwInit();
        // setRenderMode() only takes effectd after SurfaceView attached to window!
        // note that on this mode, surface will not render util GLSurfaceView.requestRender() is
        // called, it's good and efficient -v-
        //“WHEN_DIRTY”是由用户调用requestRenderer()绘制
        //Utils.LOGD("surface setRenderMode RENDERMODE_WHEN_DIRTY");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w > 0 && h > 0) {
            if (null != _cb)
                _cb.onSizeChanged(w, h);
        }
    }

    public interface ICallback {
        void onSizeChanged(int w, int h);
    }
}
