package com.stit.toolcab.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stit.toolcab.R;
import com.stit.toolcab.camera.CWGLDisplay;
import com.stit.toolcab.camera.GLFrameSurface;
import com.stit.toolcab.dao.PersonDao;
import com.stit.toolcab.entity.Person;
import com.stit.toolcab.manager.CacheManager;
import com.stit.toolcab.manager.ThreadManager;
import com.stit.toolcab.utils.Cache;

import org.apache.log4j.Logger;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import cn.cloudwalk.component.liveness.entity.face.CWLiveFaceDetectInfo;
import cn.cloudwalk.component.liveness.entity.face.CWLiveFaceLivenessInfo;
import cn.cloudwalk.component.liveness.entity.face.CWLiveFaceParam;
import cn.cloudwalk.component.liveness.entity.face.CWLiveFaceRecogInfo;
import cn.cloudwalk.component.liveness.entity.face.CWLiveFaceRect;
import cn.cloudwalk.component.liveness.entity.face.CWLiveImage;
import cn.cloudwalk.midware.camera.entity.CWCameraInfos;
import cn.cloudwalk.midware.engine.CWCameraConfig;
import cn.cloudwalk.midware.engine.CWEngine;
import cn.cloudwalk.midware.engine.CWGeneralApi;
import cn.cloudwalk.midware.engine.CWLivenessConfig;
import cn.cloudwalk.midware.engine.CWPreivewConfig;
import cn.cloudwalk.midware.engine.callback.CWFrameCallback;
import cn.cloudwalk.midware.engine.callback.CWLiveinfoCallback;
import cn.cloudwalk.midware.engine.utils.FileUtil;

import static cn.cloudwalk.midware.engine.CWEngine.CW_CAMERA_STATE_OPENED;
import static cn.cloudwalk.midware.engine.CWEngine.CW_FUNC_MULTI_FACES;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_COLOR_PASS;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_DIST_TOO_CLOSE_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_DIST_TOO_FAR_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_FACE_CLARITY_DET_FAIL_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_IS_LIVE;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_IS_UNLIVE_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_NO_PAIR_FACE_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_POSE_DET_FAIL_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_SKIN_FAILED_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_VIS_BLACKSPEC_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_VIS_BRIGHTNESS_EXC_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_VIS_BRIGHTNESS_INS_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_VIS_EYE_CLOSE_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_VIS_FACE_LOW_CONF_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_VIS_MOUTH_OPEN_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_VIS_NO_FACE_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_VIS_OCCLUSION_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_VIS_PROCEDUREMASK_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_VIS_SUNGLASS_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_NO_FACE;

public class FaceActivity extends Activity {

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
    private Handler myHandlerFace;
    private TextView tvDJS;
    private boolean timeFlag=true;
    private TextView tvJG;
    private boolean success=false;//识别成功标志
    private Button btnTC;
    CWGLDisplay mVisGlDiaplay= new CWGLDisplay();



    private Logger logger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_face);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        initView();
        logger=Logger.getLogger(this.getClass());
        requestPermission();
        new TimeThread().start();
    }


    private void initView(){
        mSrVisView = (GLFrameSurface) findViewById(R.id.sr_vis_view);
        mTvResult = (TextView) findViewById(R.id.tv_liveness_result);
        mTvResult.setText("");
        tvDJS=(TextView)findViewById(R.id.djs);
        tvJG=(TextView)findViewById(R.id.jieguo);
        btnTC=(Button)findViewById(R.id.tc);
        btnTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = Message.obtain(myHandlerFace);
                Bundle data = new Bundle();
                data.putString("close","close");
                message.setData(data);
                myHandlerFace.sendMessage(message);
            }
        });
        myHandlerFace = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                //提示信息
                if (bundle.getString("time") != null) {
                   tvDJS.setText(bundle.getString("time")+"秒");
                    if(Integer.valueOf(bundle.getString("time"))<=10){
                        tvDJS.setTextColor(Color.RED);
                   }
                }
                if (bundle.getString("close") != null) {
                    timeFlag=false;


//                    updatePreview(false);
//                    CWEngine.getInstance().cwUninit();
                    updatePreview(false);
                    updateDetect(false);
                    try{
                        Thread.sleep(1000);
                    }catch (Exception e){}
                    Intent intent = new Intent(FaceActivity.this, LoginActivity.class);
                    startActivity(intent);
                    FaceActivity.this.finish();
                }


            }
        };

    }


    protected void onResume() {
        super.onResume();
        CWEngine.getInstance().cwSetLiveInfoCallback(mLiveinfoCallback);
        featureImageGetType = CacheManager.getInstance().loadFeatureImageGetType(1);
        CacheManager.getInstance().saveFeatureImageGetType(featureImageGetType);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //initConfig();
                }
                break;
        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            //initConfig();
            //YCCamera.cwVisPreivewConfig.setSurfaceView(mSrVisView);
            updatePreview(true);
            updateDetect(true);
            CWEngine.getInstance().cwSetFrameCallback(new CWFrameCallback() {
                @Override
                public void onBgrFrame(byte[] data) {

                    if(data.length>0){
                        mVisGlDiaplay.render(mSrVisView,90,true,data,640,480,0);
                    }

                }

                @Override
                public void onIrFrame(byte[] data) {

                }

                @Override
                public void onDepthFrame(byte[] data) {

                    System.out.println("personactivity depth:"+System.currentTimeMillis()+"  "+data.length);
                }
            });
        }
    }

    private CWLiveinfoCallback mLiveinfoCallback = new CWLiveinfoCallback() {

        @Override
        public void onFaceDetectCallback(int errorCode,
                                         long timestamp,
                                         final CWLiveImage image,
                                         ArrayList<CWLiveFaceDetectInfo> faceDetectInfoList) {
            //先判断是否为图片比对
            if (errorCode == CW_FACE_NO_FACE) {
                handler.sendEmptyMessage(MSG_SEND_STOP_LIV_TIP);
            }

            long tmp = timestamp;
            if (tmp == 0 && image.getTimestamp() != 0) {
                tmp = image.getTimestamp();
            }
            if (lastCompareImg != null && lastCompareImg.getTimestamp() == tmp) {
//                Logger.e(TAG, "比对onFaceDetectCallback  time: " + (System.currentTimeMillis() - tmp));
                lastCompareImg = null;
                synchronized (pushObj) {
                    pushObj.notify();
                }
                if (!faceDetectInfoList.isEmpty()
                        && System.currentTimeMillis() - tmp < compareFaceDetictTimeout) {
                    CWLiveFaceDetectInfo faceDetectInfo = faceDetectInfoList.get(0);
                    lastLiveImage = image;
                    if (lastLiveImage.getTimestamp() <= 0 && timestamp != 0) {
                        lastLiveImage.setTimestamp(timestamp);
                    }
                    lastLiveRect = faceDetectInfo.getRect();
                } else {
                    showToast("比对失败");
                    isCompare = false;
                }
                return;
            }
            if (image != null && featureImageGetType == 0
                    && isLiveEnable
                    && isResetLastImage
                    && faceDetectInfoList != null
                    && faceDetectInfoList.size() > 0) {
                isResetLastImage = false;
                lastLiveRect = faceDetectInfoList.get(0).getRect();
                lastLiveImage = image;
                if (lastLiveImage.getTimestamp() <= 0 && timestamp != 0) {
                    lastLiveImage.setTimestamp(timestamp);
                }
                Message msg = new Message();
                msg.what = MSG_SEND_IMAGE_FEATURE;
                msg.obj = image;
                handler.sendMessage(msg);
            }

            if (faceDetectInfoList.size() > 0 && faceDetectInfoList.get(0).getCode() == CW_FACE_LIV_POSE_DET_FAIL_ERR) {  // author: xujunke 临时解决角度不正确的问题
                showTips(faceDetectInfoList.get(0).getCode());
            }
        }

        @Override
        public void onLivenessCallback(int errorCode,
                                       long timestamp,
                                       final CWLiveImage bgrImage,
                                       CWLiveImage irImage,
                                       CWLiveImage depthImage,
                                       ArrayList<CWLiveFaceDetectInfo> faceDetectInfoList,
                                       ArrayList<CWLiveFaceLivenessInfo> faceLivenessList,
                                       CWLiveImage bestface) {



            CWLiveFaceDetectInfo detectInfo = null;
            CWLiveFaceLivenessInfo livenessInfo = null;
            if (!faceDetectInfoList.isEmpty() && !faceLivenessList.isEmpty()) {
                detectInfo = faceDetectInfoList.get(0);
                livenessInfo = faceLivenessList.get(0);

                //展示提示语
                if (detectInfo.getCode() != CW_FACE_LIV_COLOR_PASS) {
                   showTips(detectInfo.getCode());
                    return;
                } else {
                    showTips(livenessInfo.getCode());
                    if (featureImageGetType == 1 && isLiveEnable && isResetLastImage && livenessInfo.getCode() == CW_FACE_LIV_IS_LIVE) {

                        //isResetLastImage = false;
                        lastLiveRect = detectInfo.getRect();
                        lastLiveImage = bgrImage;
                        Message msg = new Message();
                        msg.what = MSG_SEND_IMAGE_FEATURE;
                        msg.obj = bgrImage;
                        handler.sendMessage(msg);
                        System.out.println("是否比对？");
                        if(!isCompare && !success){
                            System.out.println("进入比对");
                            ThreadManager.getThreadPool().execute(compareRunable);
                        }else{
                            System.out.println("不比对");
                        }

                    }

                    //最佳人脸及全景图存图
                    if (livenessInfo.getCode() == CW_FACE_LIV_IS_LIVE) {
                        if (bestface != null) {
                            Message msg = Message.obtain();
                            msg.what = MSG_SAVE_BESTFACE;
                            msg.obj = bestface;
                            handler.sendMessage(msg);
                        }
                        if (bgrImage != null) {
                            Message msg = Message.obtain();
                            msg.what = MSG_SAVE_BGRIMAGE;
                            msg.obj = bgrImage;
                            handler.sendMessage(msg);
                        }
                    }
                }
            }


        }
    };

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
        long start = System.currentTimeMillis();
        long ret = CWEngine.getInstance().cwInit(FaceActivity.this, cwCameraConfig, cwLivenessConfig, cwVisPreivewConfig);
        System.out.println("初始化耗时："+(System.currentTimeMillis()-start));
        if (ret == 0L) {
            logger.info("摄像头初始化失败");
            showToast("初始化失败");
        } else {
            logger.info("摄像头初始化成功");
            showToast("初始化成功");
        }

        CWEngine.getInstance().setLogLevel(cn.cloudwalk.midware.engine.utils.Logger.WARN, ",");

        CWEngine.getInstance().cwSetFrameCallback(new CWFrameCallback() {
            @Override
            public void onBgrFrame(byte[] data) {

                System.out.println("personactivity bgr:"+System.currentTimeMillis()+"  "+data.length);


                //mSrVisView.setRenderer();
            }

            @Override
            public void onIrFrame(byte[] data) {
                //System.out.println("222");
                System.out.println("personactivity irframe:"+System.currentTimeMillis()+"  "+data.length);
            }

            @Override
            public void onDepthFrame(byte[] data) {
                //System.out.println("333");
                System.out.println("personactivity depth:"+System.currentTimeMillis()+"  "+data.length);
            }
        });
        cwVisPreivewConfig.setSurfaceView(mSrVisView);
        updatePreview(true);
        updateDetect(true);
    }

    private void updatePreview(final boolean isEnable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isEnable) {
                    CWEngine.getInstance().cwStartCamera();
                } else {
                    CWEngine.getInstance().cwStopCamera();
                }
            }
        });
    }

    private void updateDetect(final boolean isEnable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setDetectorParams();
                if (isEnable) {
                    if (CWEngine.getInstance().cwGetCameraStatus() != CW_CAMERA_STATE_OPENED) {
                        return;
                    }
                    CWEngine.getInstance().cwSetLiveInfoCallback(mLiveinfoCallback);

                    CWEngine.getInstance().cwStartLiveDetect(1);

                    isLiveEnable = true;
                } else {
                    //比对状态关闭
                    isCompare = false;
                    isResetLastImage = false;
                    startFaceCompare = false;
                    isLiveEnable = false;
                    CWEngine.getInstance().cwStopLiveDetect();
                    handler.sendEmptyMessage(MSG_SEND_STOP_LIV_TIP);
                }
            }
        });
    }

    private void setDetectorParams() {
        boolean isInit = CacheManager.getInstance().loadIsInit(true);
        if (!isInit) {
            try {
                CWLiveFaceParam cwLiveFaceParam = CWGeneralApi.getInstance().cwGetDetectorParams();
                //Logger.d(TAG, cwLiveFaceParam == null ? "null" : CWEngine.getInstance().cwGetDetectorParams().toString());
                if (null == cwLiveFaceParam) {
                    return;
                }
//            CWLiveFaceParam cwLiveFaceParam = new CWLiveFaceParam();
                cwLiveFaceParam.setRoi_x(CacheManager.getInstance().loadRoiX(0));
                cwLiveFaceParam.setRoi_y(CacheManager.getInstance().loadRoiY(0));
                cwLiveFaceParam.setRoi_width(CacheManager.getInstance().loadRoiWidth(0));
                cwLiveFaceParam.setRoi_height(CacheManager.getInstance().loadRoiHeight(0));
                int height = CacheManager.getInstance().loadCameraHeight(480);
                int minFace = CacheManager.getInstance().loadMinFace(80);
                int maxFace = CacheManager.getInstance().loadMaxFace(400);
                if (CacheManager.getInstance().loadDefaultFace(1) == 1 && height > 0) {
                    float ration = height / 480f;
                    minFace = (int) (80 * ration);
                    maxFace = (int) (400 * ration);
                }
                cwLiveFaceParam.setMin_face_size(minFace);
                cwLiveFaceParam.setMax_face_size(maxFace);
                CacheManager.getInstance().saveMinFace(minFace);
                CacheManager.getInstance().saveMaxFace(maxFace);

                cwLiveFaceParam.setMax_face_num_perImg(CacheManager.getInstance().loadMaxFaceNum(1));
                cwLiveFaceParam.setPerfmonLevel(CacheManager.getInstance().loadPerformLevel(5));
                cwLiveFaceParam.setNir_face_compare(CacheManager.getInstance().loadIsCompare(0));
                cwLiveFaceParam.setOpen_liveness(CacheManager.getInstance().loadIsOpenLiveness(1));
                cwLiveFaceParam.setOpen_quality(CacheManager.getInstance().loadIsOpenQuality(1));
                cwLiveFaceParam.setPitch_max(CacheManager.getInstance().loadPitchMax(20.0f));
                cwLiveFaceParam.setPitch_min(CacheManager.getInstance().loadPitchMin(-20.0f));

                //Logger.d("zxy", "zxy yaw max is " + CacheManager.getInstance().loadYawMax(20.0f));
                cwLiveFaceParam.setYaw_max(CacheManager.getInstance().loadYawMax(20.0f));

                //Logger.d("zxy", "zxy yaw min is " + CacheManager.getInstance().loadYawMin(-20.0f));
                cwLiveFaceParam.setYaw_min(CacheManager.getInstance().loadYawMin(-20.0f));
                cwLiveFaceParam.setRoll_max(CacheManager.getInstance().loadRollMax(20.0f));
                cwLiveFaceParam.setRoll_min(CacheManager.getInstance().loadRollMin(-20.0f));
                cwLiveFaceParam.setClarity(CacheManager.getInstance().loadClarity(0.5f));
                cwLiveFaceParam.setSkinscore(CacheManager.getInstance().loadSkin(0.35f));
                cwLiveFaceParam.setConfidence(CacheManager.getInstance().loadConfidence(0.55f));
                cwLiveFaceParam.setEyeopen(CacheManager.getInstance().loadEyeOpen(-1f));
                cwLiveFaceParam.setMouthopen(CacheManager.getInstance().loadMouthOpen(-1f));
                cwLiveFaceParam.setOcclusion(CacheManager.getInstance().loadOcclucsion(-1f));

                cwLiveFaceParam.setHight_brightness_threshold(CacheManager.getInstance().loadBrightness(-1f));
                cwLiveFaceParam.setLow_brightness_threshold(CacheManager.getInstance().loadDarkness(-1f));
                cwLiveFaceParam.setBlackspec(CacheManager.getInstance().loadBlackSpec(-1f));
                cwLiveFaceParam.setSunglass(CacheManager.getInstance().loadSunglass(-1f));
                cwLiveFaceParam.setProceduremask(CacheManager.getInstance().loadProceduremask(0.5f));
                //Logger.d("zxy", "zxy glass is " + CacheManager.getInstance().loadBlackSpec(-1f));
                //Logger.d("zxy", "zxy sunglass is "+CacheManager.getInstance().loadSunglass(-1f));

                CWGeneralApi.getInstance().cwSetDetectorParams(cwLiveFaceParam);
                CWGeneralApi.getInstance().cwSetDetectorParams(cwLiveFaceParam);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class SafeHandler extends Handler {

        private WeakReference<FaceActivity> mWeakReference;

        public SafeHandler(FaceActivity activity) {
            mWeakReference = new WeakReference<FaceActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            FaceActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case MSG_SAVE_BESTFACE:
//                        CWLiveImage bestface = (CWLiveImage) msg.obj;
//                        if (null != bestface) {
//                            Bitmap bitmapBestface = Covert.BGRToBitmap(bestface.getData(), bestface.getWidth(),
//                                    bestface.getHeight());
//                            Covert.saveToJpeg(bitmapBestface, BESTFACE_PATH + "最佳人脸.jpg", 100);
//                            if (bitmapBestface != null && !bitmapBestface.isRecycled()) {
//                                bitmapBestface.recycle();
//                            }
//                        }
                        break;
                    case MSG_SAVE_BGRIMAGE:
//                        CWLiveImage bgrImage = (CWLiveImage) msg.obj;
//                        if (null != bgrImage) {
//                            Bitmap bitmapBestface = Covert.BGRToBitmap(bgrImage.getData(), bgrImage.getWidth(),
//                                    bgrImage.getHeight());
//                            Covert.saveToJpeg(bitmapBestface, BESTFACE_PATH + "全景图.jpg", 100);
//                            if (bitmapBestface != null && !bitmapBestface.isRecycled()) {
//                                bitmapBestface.recycle();
//                            }
//                        }
                        break;
                    case MSG_SEND_IMAGE_FEATURE:
                        break;
                    case MSG_SEND_STOP_LIV_TIP:
                        activity.mTvResult.setText("");
                        break;
                    case MSG_SEND_START_LIV_TIP:
                        if (activity.isLiveEnable && CWEngine.getInstance().cwGetCameraStatus() == CW_CAMERA_STATE_OPENED) {
                            activity.mTvResult.setText("活体");
                            activity.mTvResult.setTextColor(Color.BLUE);
                        }
                        break;
                    case MSG_SEND_TIP:
                        if (activity.isLiveEnable && CWEngine.getInstance().cwGetCameraStatus() == CW_CAMERA_STATE_OPENED) {
                            String tipsInfo = msg.getData().getString("tips");
                            activity.mTvResult.setText(tipsInfo);
                            activity.mTvResult.setTextColor(Color.RED);
                        }
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
                    Toast.makeText(FaceActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showTips(int errorCode) {
        Message message = Message.obtain();
        message.what = MSG_SEND_TIP;
        Bundle bundle = new Bundle();
        switch (errorCode) {
            case CW_FACE_LIV_IS_LIVE:
                message.what = MSG_SEND_START_LIV_TIP;
                //handler.sendEmptyMessage(2);
                break;
            case CW_FACE_LIV_IS_UNLIVE_ERR:
                bundle.putString("tips", "非活体!");
                break;
            case CW_FACE_LIV_DIST_TOO_FAR_ERR:
                bundle.putString("tips", "请离远一点!");
                break;
            case CW_FACE_LIV_DIST_TOO_CLOSE_ERR:
                bundle.putString("tips", "请离近一点!");
                break;
            case CW_FACE_LIV_SKIN_FAILED_ERR:
                bundle.putString("tips", "人脸肤色检测未通过!");
                break;
            case CW_FACE_LIV_NO_PAIR_FACE_ERR:
                bundle.putString("tips", "可见光和红外人脸不匹配!");
                break;
//                    case CW_FACE_LIV_NIS_NO_FACE_ERR:
//                        bundle.putString("tips", "红外输入没有人脸!");
//                        break;
            case CW_FACE_LIV_VIS_NO_FACE_ERR:
                bundle.putString("tips", "可见光输入没有人脸!");
                break;
            case CW_FACE_LIV_POSE_DET_FAIL_ERR:
                bundle.putString("tips", "请正对摄像头!");
                break;
            case CW_FACE_LIV_FACE_CLARITY_DET_FAIL_ERR:
                bundle.putString("tips", "请调整角度或距离!");
                break;
            case CW_FACE_LIV_VIS_EYE_CLOSE_ERR:
                bundle.putString("tips", "请睁眼!");
                break;
            case CW_FACE_LIV_VIS_MOUTH_OPEN_ERR:
                bundle.putString("tips", "请不要张嘴!");
                break;
            case CW_FACE_LIV_VIS_BRIGHTNESS_EXC_ERR:
                bundle.putString("tips", "人脸照片过亮!");
                break;
            case CW_FACE_LIV_VIS_BRIGHTNESS_INS_ERR:
                bundle.putString("tips", "人脸照片过暗!");
                break;
            case CW_FACE_LIV_VIS_FACE_LOW_CONF_ERR:
                bundle.putString("tips", "检测到人脸置信度过低!");
                break;
            case CW_FACE_LIV_VIS_OCCLUSION_ERR:
                bundle.putString("tips", "请不要遮挡您的脸部!");
                break;
            case CW_FACE_LIV_VIS_BLACKSPEC_ERR:
                bundle.putString("tips", "请取下您的眼镜");
                break;
            case CW_FACE_LIV_VIS_SUNGLASS_ERR:
                bundle.putString("tips", "请取下您的墨镜");
                break;
            case CW_FACE_LIV_VIS_PROCEDUREMASK_ERR:
                bundle.putString("tips", "请摘下您的口罩");
                break;
            case CW_FUNC_MULTI_FACES:
                bundle.putString("tips", "有多张人脸!");
                break;
//                    case CW_LIVENESS_CHANAGE_FACE_ERROR:
//                        showMessage(index + "检测到换脸！", resultObj, "red");
//                        break;
        }
        message.setData(bundle);
        handler.sendMessage(message);
    }

    public Runnable compareRunable = new Runnable() {
        @Override
        public void run() {
            long tmp;
            isCompare = true;
            PersonDao personDao = new PersonDao();
            List<Person> featureModels = personDao.getAllPerson();
            //Logger.i("loadLib", "featureLibCount: " + DBManager.getInstance().getPersonFeatureModelDao().count());
            featureModels = personDao.getAllPerson();
                if (featureModels == null || featureModels.size() < 1) {
                    showToast("当前特征库没有数据");
                    isCompare = false;
                    startFaceCompare = false;
                    return;
                }

            tmp = System.currentTimeMillis();
            final CWLiveFaceRecogInfo currentInfo = CWEngine.getInstance().cwGetFaceFeature(lastLiveImage, lastLiveRect, 1, CWEngine.getInstance().cwGetFeatureLength());
            //Logger.i(TAG, "比对 cwGetFaceFeature  time: " + (System.currentTimeMillis() - tmp));
            if (currentInfo == null) {
                showToast("获取当前图片特征失败");
                isCompare = false;
                startFaceCompare = false;
                return;
            }
            int len = 0;
            tmp = System.currentTimeMillis();
            for (Person model : featureModels) {
                len += model.getFeaturedata()==null?0:model.getFeaturedata().length;
            }
            if(len==0){
                showToast("当前特征库没有数据");
                return;
            }
            int featureCount=0;
            ByteBuffer byteBuffer = ByteBuffer.allocate(len);
            byteBuffer.position(0);
            for (Person model : featureModels) {
                if(model.getFeaturedata()!=null){
                    featureCount=featureCount+1;
                    byteBuffer.put(model.getFeaturedata());
                }

            }
            byteBuffer.position(0);
            final byte[] b1 = new byte[len];
            byteBuffer.get(b1);
            tmp = System.currentTimeMillis();
            final float[] score = CWEngine.getInstance().cwComputeSimilarity(currentInfo.getFeature_data(), 1, b1, featureCount, CWEngine.getInstance().cwGetFeatureLength());

            float minScore = CacheManager.getInstance().loadFeatureThreshold(0.8f);
            int index = -1;
            for (int i = 0; i < score.length; i++) {
                float f = score[i];
                if (f >= minScore) {
                    if (index >= 0 && f < score[index]) {
                        continue;
                    }
                    index = i;
                }
            }
            if (index != -1) {
                final Person m = featureModels.get(index);
                if (m.getPath() != null) {
                    final int finalIndex = index;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(success){
                                return;
                            }
                            success=true;
                            tvJG.setText(m.getName());
                            timeFlag=false;
                            Cache.operator=m;


                            //主界面发送操作员更新信息
                            Message message = Message.obtain(Cache.mainHandle);
                            Bundle data = new Bundle();
                            data.putString("czy","1");
                            message.setData(data);
                            Cache.mainHandle.sendMessage(message);
                            logger.info("分值:"+String.format("%.1f", score[finalIndex] * 100));
                            logger.info("姓名："+m.getName());
                            //logger.info("开始关闭预览界面");
                            updatePreview(false);
                            updateDetect(false);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                            }

//                            System.out.println("开始释放资源");
//                            try{
//                                //CWEngine.getInstance().cwUninit();
//                                Thread.sleep(1000);
//                            }catch (Exception e){
//                                logger.error("释放视频资源出错",e);
//                            }
//                            System.out.println("开始关闭界面");
                            FaceActivity.this.finish();
                            //System.out.println("界面关闭完成");
                        }
                    });
                }
            } else {
                showToast("无匹配人脸");
            }
            isCompare = false;
            startFaceCompare = false;
        }
    };

    class TimeThread extends Thread{
        public void run(){
            int time=60;
            while(timeFlag){
                try{

                    Message message = Message.obtain(myHandlerFace);
                    Bundle data = new Bundle();
                    if(time>0){
                        data.putString("time",String.valueOf(time));
                    }else{
                        data.putString("close","close");
                    }
                    message.setData(data);
                    myHandlerFace.sendMessage(message);
                }catch (Exception e){
                }
                try{
                    Thread.sleep(1000l);
                }catch (Exception e){

                }
                time=time-1;
            }


        }
    }
}
