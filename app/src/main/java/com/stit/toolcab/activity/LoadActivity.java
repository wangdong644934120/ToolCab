package com.stit.toolcab.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudwalk.gldisplay.GLFrameSurface;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.stit.toolcab.R;

import com.stit.toolcab.manager.CacheManager;

import org.apache.log4j.chainsaw.Main;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.cloudwalk.component.liveness.entity.face.CWLiveFaceDetectInfo;
import cn.cloudwalk.component.liveness.entity.face.CWLiveFaceLivenessInfo;
import cn.cloudwalk.component.liveness.entity.face.CWLiveFaceRect;
import cn.cloudwalk.component.liveness.entity.face.CWLiveImage;
import cn.cloudwalk.midware.camera.entity.CWCameraInfos;
import cn.cloudwalk.midware.engine.CWCameraConfig;
import cn.cloudwalk.midware.engine.CWEngine;
import cn.cloudwalk.midware.engine.CWLivenessConfig;
import cn.cloudwalk.midware.engine.CWPreivewConfig;
import cn.cloudwalk.midware.engine.callback.CWFrameCallback;
import cn.cloudwalk.midware.engine.callback.CWLiveinfoCallback;
import cn.cloudwalk.midware.engine.utils.FileUtil;
import cn.cloudwalk.midware.engine.view.CWRectView;
import cn.cloudwalk.tool.Covert;

import static cn.cloudwalk.midware.engine.CWEngine.CW_CAMERA_STATE_OPENED;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_COLOR_PASS;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_IS_LIVE;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_POSE_DET_FAIL_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_NO_FACE;

public class LoadActivity extends Activity {


    private ArrayList<CWCameraInfos> cameraInfoList = new ArrayList<>();
    private static final int MSG_SEND_TIP = 10;
    private static final int MSG_SEND_START_LIV_TIP = 11;
    private static final int MSG_SEND_STOP_LIV_TIP = 12;
    private static final int MSG_SEND_IMAGE_FEATURE = 13;
    private static final int MSG_SAVE_BESTFACE = 14;
    private static final int MSG_SAVE_BGRIMAGE = 15;

    private GLFrameSurface mSrVisView;

    private FrameLayout mFlSurface;

    //liveness
    private TextView mTvResult;
    private boolean isLiveEnable;
    SafeHandler handler = new SafeHandler(this);
    private CWLiveFaceRect lastLiveRect;
    private CWLiveImage lastLiveImage;
    private int surfaceWidth, surfaceHeight;

    private static final String BESTFACE_PATH = "/cloudwalk/最佳人脸/";
    private static final String CURRENT_IMAGE_PATH = "/cloudwalk/当前照片/";
    private static final String ROOT_PATH = "/cloudwalk/";

    private boolean isResetLastImage = true;
    private int featureImageGetType = 1;    //获取当前图片来源类型（比对）0-人脸 1-活体
    private CWLiveImage lastCompareImg;
    private boolean isCompare = false;
    private boolean startFaceCompare = false;
    private long compareFaceDetictTimeout = 5000;
    private final Object pushObj = new Object();
    private QMUITopBarLayout topbar;
    private Handler myHandlerFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        //requestPermission();
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //首次执行
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                //首次执行
            }
        } else {
            //initConfig();
            Intent intent = new Intent(LoadActivity.this, LoginActivity.class);
            startActivity(intent);
            LoadActivity.this.finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(LoadActivity.this, LoadActivity.class);
                    startActivity(intent);
                    LoadActivity.this.finish();
                }
                break;
        }
    }

    private void initConfig() {
        cameraInfoList.clear();
        //创建存图文件夹
        FileUtil.isFolderExists(ROOT_PATH);
        //最佳人脸及全景存图(默认)
        FileUtil.isFolderExists(BESTFACE_PATH);
        //当前拍照
        FileUtil.isFolderExists(CURRENT_IMAGE_PATH);

        CWCameraConfig cwCameraConfig = CWCameraConfig.getInstance();
        int cameraType = CacheManager.getInstance().loadCameraType(1);
        cwCameraConfig.setCameraType(cameraType);

        CWCameraInfos cameraInfosVis = new CWCameraInfos();
        cameraInfosVis.setCameraWidth(CacheManager.getInstance().loadCameraWidth(640));
        cameraInfosVis.setCameraHeight(CacheManager.getInstance().loadCameraHeight(480));
        cameraInfosVis.setType(0);
        cameraInfosVis.setPid(CacheManager.getInstance().loadRgbPid(0xC053));//0xB051
        cameraInfosVis.setVid(CacheManager.getInstance().loadRgbVid(0x0C45));
        cameraInfoList.add(cameraInfosVis);

        CWCameraInfos cameraInfosNis1 = new CWCameraInfos();
        cameraInfosNis1.setCameraWidth(CacheManager.getInstance().loadCameraWidth(640));
        cameraInfosNis1.setCameraHeight(CacheManager.getInstance().loadCameraHeight(480));
        cameraInfosNis1.setType(1);
        cameraInfosNis1.setPid(CacheManager.getInstance().loadIrPid(0xB051));//0xC053
        cameraInfosNis1.setVid(CacheManager.getInstance().loadIrVid(0x0C45));
        cameraInfoList.add(cameraInfosNis1);

        CWPreivewConfig cwVisPreivewConfig = CWPreivewConfig.getInstance();
        cwVisPreivewConfig.setAngle(CacheManager.getInstance().loadRotation(90));
        cwVisPreivewConfig.setMirror(CacheManager.getInstance().loadMirror(true));
        //cwVisPreivewConfig.setSurfaceView(mSrVisView);
        cwCameraConfig.setCameraInfos(cameraInfoList);

        CWLivenessConfig cwLivenessConfig = CWLivenessConfig.getInstance();
        /* 添加背景图和存图路径 */

        cwLivenessConfig.setDefendSave(CacheManager.getInstance().loadIsSaveDebugImage(false));
        cwLivenessConfig.setDefendFile(TextUtils.isEmpty(CacheManager.getInstance().loadDebugImagePath("")) ?
                null : CacheManager.getInstance().loadDebugImagePath(""));

        cwLivenessConfig.setConfigFile(TextUtils.isEmpty(CacheManager.getInstance().loadConfigFilePath("/sdcard/assets/matrix_para.xml")) ?
                "/sdcard/assets/matrix_para.xml" : CacheManager.getInstance().loadConfigFilePath("/sdcard/assets/matrix_para.xml"));

        cwLivenessConfig.setFaceDetectFile(TextUtils.isEmpty(CacheManager.getInstance().loadFaceDetModelPath("/sdcard/assets/face_3_27_mnn_new")) ?
                "/sdcard/assets/face_3_27_mnn_new" : CacheManager.getInstance().loadFaceDetModelPath("/sdcard/assets/face_3_27_mnn_new"));
        cwLivenessConfig.setFaceKeyPointDetectFile(TextUtils.isEmpty(CacheManager.getInstance().loadKeyFacePointModelPath("/sdcard/assets/kpt_model_20200311.bin")) ?
                "/sdcard/assets/kpt_model_20200311.bin" : CacheManager.getInstance().loadKeyFacePointModelPath("/sdcard/assets/kpt_model_20200311.bin"));
        cwLivenessConfig.setFaceQualityFile(TextUtils.isEmpty(CacheManager.getInstance().loadFaceQualityFile("/sdcard/assets/faceanalyze.20200421.mnn.bin")) ?
                "/sdcard/assets/faceanalyze.20200421.mnn.bin" : CacheManager.getInstance().loadFaceQualityFile("/sdcard/assets/faceanalyze.20200421.mnn.bin"));
        cwLivenessConfig.setFaceRecogFile(TextUtils.isEmpty(CacheManager.getInstance().loadFaceRecogFile("/sdcard/assets/generic_mnn_android_mask_0305/CWR_Config2.6_1_1.xml")) ?
                "/sdcard/assets/generic_mnn_android_mask_0305/CWR_Config2.6_1_1.xml" : CacheManager.getInstance().loadFaceRecogFile("/sdcard/assets/generic_mnn_android_mask_0305/CWR_Config2.6_1_1.xml"));
//        Logger.d(TAG, cwLivenessConfig.getFaceLivenessFile());
        cwLivenessConfig.setStrategyId(CacheManager.getInstance().loadKeyId(3));  //默认第四种策略
        cwLivenessConfig.setLivenessMinutes(CacheManager.getInstance().loadKeyS(0));
        cwLivenessConfig.setLivenessCount(CacheManager.getInstance().loadKeyM(0));
        cwLivenessConfig.setLivenesSuccessCount(CacheManager.getInstance().loadKeyN(0));

        cwLivenessConfig.setModelMode(0);
        switch (cameraType) {
            case 0:
            case 1:
            case 2:
                cwLivenessConfig.setLivenessMode(2);
                cwLivenessConfig.setFaceLivenessFile(TextUtils.isEmpty(CacheManager.getInstance().loadFaceLivenessFile("/sdcard/assets/nirLiveness_model_20200216.bin")) ?
                        "/sdcard/assets/nirLiveness_model_20200216.bin" : CacheManager.getInstance().loadFaceLivenessFile("/sdcard/assets/nirLiveness_model_20200216.bin"));


                break;
            case 3:
                //结构光单独模型
                cwLivenessConfig.setLivenessMode(3);
                cwLivenessConfig.setFaceLivenessFile(TextUtils.isEmpty(CacheManager.getInstance().loadFaceLivenessFile("/sdcard/assets/nirLiveness_model_20200216.bin")) ?
                        "/sdcard/assets/nirLiveness_model_20200216.bin" : CacheManager.getInstance().loadFaceLivenessFile("/sdcard/assets/nirLiveness_model_20200216.bin"));

                break;
        }
        cwLivenessConfig.setLicenseType(4);
        cwLivenessConfig.setMultiThread(CacheManager.getInstance().loadIsMultiThread(true));
        cwLivenessConfig.setTrackPreviewHeight(CacheManager.getInstance().loadTrackPreviewHeight(640));
        cwLivenessConfig.setTrackPreviewWidth(CacheManager.getInstance().loadTrackPreviewWidth(480));
        cwLivenessConfig.setAvalableSpace(200);
        cwLivenessConfig.setFaceRatio(CacheManager.getInstance().loadBestFaceRatio(1.5f));
        cwLivenessConfig.setDefendSaveFormat(CacheManager.getInstance().loadDefineSaveFormat("jpg"));

        long ret = CWEngine.getInstance().cwInit(LoadActivity.this, cwCameraConfig, cwLivenessConfig, cwVisPreivewConfig);

        if (ret == 0L) {
            showToast("初始化失败");
        } else {
            showToast("初始化成功");
        }


    }

    private static class SafeHandler extends Handler {

        private WeakReference<LoadActivity> mWeakReference;

        public SafeHandler(LoadActivity activity) {
            mWeakReference = new WeakReference<LoadActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoadActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case MSG_SAVE_BESTFACE:
                        CWLiveImage bestface = (CWLiveImage) msg.obj;
                        if (null != bestface) {
                            Bitmap bitmapBestface = Covert.BGRToBitmap(bestface.getData(), bestface.getWidth(),
                                    bestface.getHeight());
                            Covert.saveToJpeg(bitmapBestface, BESTFACE_PATH + "最佳人脸.jpg", 100);
                            if (bitmapBestface != null && !bitmapBestface.isRecycled()) {
                                bitmapBestface.recycle();
                            }
                        }
                        break;
                    case MSG_SAVE_BGRIMAGE:
                        CWLiveImage bgrImage = (CWLiveImage) msg.obj;
                        if (null != bgrImage) {
                            Bitmap bitmapBestface = Covert.BGRToBitmap(bgrImage.getData(), bgrImage.getWidth(),
                                    bgrImage.getHeight());
                            Covert.saveToJpeg(bitmapBestface, BESTFACE_PATH + "全景图.jpg", 100);
                            if (bitmapBestface != null && !bitmapBestface.isRecycled()) {
                                bitmapBestface.recycle();
                            }
                        }
                        break;
                    case MSG_SEND_IMAGE_FEATURE:
                        break;
                    case MSG_SEND_STOP_LIV_TIP:

                        break;
                    case MSG_SEND_START_LIV_TIP:

                        break;
                    case MSG_SEND_TIP:

                        break;
                }
            }
        }
    }

    private void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    Toast.makeText(LoadActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
