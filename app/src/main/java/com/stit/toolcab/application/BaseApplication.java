package com.stit.toolcab.application;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;
import com.stit.toolcab.manager.CacheManager;
import cn.cloudwalk.component.liveness.entity.face.CWLiveFaceParam;
import cn.cloudwalk.midware.engine.CWGeneralApi;
import cn.cloudwalk.midware.engine.callback.CWErrorCallback;
import cn.cloudwalk.midware.engine.utils.Logger;

import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_MINMAX_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_ROI_ERR;

/**
 * Created by Administrator on 2020-07-03.
 */

public class BaseApplication extends Application{

    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        t = System.currentTimeMillis();

//        sHA1(this);
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            System.out.println("申请权限");
//            ActivityCompat.requestPermissions(new PermissionActivity(),
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
////            if (ActivityCompat.shouldShowRequestPermissionRationale(new PermissionActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
////                    ActivityCompat.shouldShowRequestPermissionRationale(new PermissionActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
////            } else {
////                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
////                ActivityCompat.requestPermissions(new PermissionActivity(),
////                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
////            }
//        }



        //DBManager.initDataBase(BaseApplication.this);



        CWGeneralApi.getInstance().cwSetErrorCallback(new CWErrorCallback() {
            @Override
            public void cwOnError(int i, String s) {
                String tip = s;
                if (i == CWGeneralApi.CW_CAMERA_OPEN_FAIL) {

                } else if (i == CW_FACE_ROI_ERR) {
                    tip = "设置ROI参数失败";
                    CWLiveFaceParam cwLiveFaceParam = CWGeneralApi.getInstance().cwGetDetectorParams();
                    int roix = cwLiveFaceParam.getRoi_x();
                    int roiy = cwLiveFaceParam.getRoi_y();
                    int roiWidth = cwLiveFaceParam.getRoi_width();
                    int roiHeight = cwLiveFaceParam.getRoi_height();
                    CacheManager.getInstance().saveRoiX(roix);
                    CacheManager.getInstance().saveRoiY(roiy);
                    CacheManager.getInstance().saveRoiWidth(roiWidth);
                    CacheManager.getInstance().saveRoiHeight(roiHeight);
                } else if (i == CW_FACE_MINMAX_ERR) {
                    tip = "设置最大最小人脸失败";
//                    showToast(tip);
                }
                Logger.e("-----cwSetError", "error: " + i + "  tip: " + tip);
            }
        });
    }


    private long t;
    private void showToast(final String tip) {
        if (System.currentTimeMillis() - t > 2000) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BaseApplication.this, tip, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
