package com.stit.toolcab.utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cloudwalk.gldisplay.GLFrameSurface;
import com.stit.toolcab.activity.FaceActivity;
import com.stit.toolcab.manager.CacheManager;

import org.apache.log4j.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cn.cloudwalk.component.liveness.entity.face.CWLiveFaceParam;
import cn.cloudwalk.component.liveness.entity.face.CWLiveFaceRect;
import cn.cloudwalk.component.liveness.entity.face.CWLiveImage;
import cn.cloudwalk.midware.camera.entity.CWCameraInfos;
import cn.cloudwalk.midware.engine.CWCameraConfig;
import cn.cloudwalk.midware.engine.CWEngine;
import cn.cloudwalk.midware.engine.CWGeneralApi;
import cn.cloudwalk.midware.engine.CWLivenessConfig;
import cn.cloudwalk.midware.engine.CWPreivewConfig;
import cn.cloudwalk.midware.engine.utils.FileUtil;

/**
 * Created by Administrator on 2020-08-12.
 */

public class YCCamera {
    private static ArrayList<CWCameraInfos> cameraInfoList = new ArrayList<>();
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
    public static CWPreivewConfig cwVisPreivewConfig;
    public static boolean isInit=false; // 是否初始化成功


    private static  Logger logger;



    public static void init(){
        if(isInit){
            return;
        }
        logger=Logger.getLogger(YCCamera.class);
        //requestPermission();
        initConfig();
    }



    private static void initConfig() {
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

        cwVisPreivewConfig = CWPreivewConfig.getInstance();
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
        long ret = CWEngine.getInstance().cwInit(Cache.myContext, cwCameraConfig, cwLivenessConfig, cwVisPreivewConfig);
        logger.info("初始化摄像头耗时："+(System.currentTimeMillis()-start));
        if (ret == 0L) {
            isInit=false;
            logger.info("摄像头初始化失败");
        } else {
            isInit=true;
            logger.info("摄像头初始化成功");
        }

        CWEngine.getInstance().setLogLevel(cn.cloudwalk.midware.engine.utils.Logger.WARN, ",");

//        CWEngine.getInstance().cwSetFrameCallback(new CWFrameCallback() {
//            @Override
//            public void onBgrFrame(byte[] data) {
//                System.out.println("personactivity bgr:"+System.currentTimeMillis()+"  "+data.length);
//            }
//
//            @Override
//            public void onIrFrame(byte[] data) {
//                System.out.println("personactivity irframe:"+System.currentTimeMillis()+"  "+data.length);
//            }
//
//            @Override
//            public void onDepthFrame(byte[] data) {
//
//                System.out.println("personactivity depth:"+System.currentTimeMillis()+"  "+data.length);
//            }
//        });
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

                        break;
                    case MSG_SAVE_BGRIMAGE:

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



}
