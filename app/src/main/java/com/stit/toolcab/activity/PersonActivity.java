package com.stit.toolcab.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.Column;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.cloudwalk.cwlivenesscamera.Camera.Size;
import com.stit.toolcab.R;
import com.stit.toolcab.camera.CWGLDisplay;
import com.stit.toolcab.camera.GLFrameSurface;
import com.stit.toolcab.dao.PersonDao;
import com.stit.toolcab.device.HCProtocol;
import com.stit.toolcab.entity.Person;
import com.stit.toolcab.manager.CacheManager;
import com.stit.toolcab.manager.ThreadManager;
import com.stit.toolcab.utils.Cache;
import com.stit.toolcab.utils.MyTextToSpeech;
import org.apache.log4j.Logger;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import cn.cloudwalk.tool.Covert;

import static cn.cloudwalk.midware.engine.CWApi.CW_CAMERA_VIS_SIZES_KEYNAME;
import static cn.cloudwalk.midware.engine.CWEngine.CW_CAMERA_STATE_OPENED;

import static cn.cloudwalk.midware.engine.utils.YuvToBitmapUtils.I420ToNV21;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_COLOR_PASS;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_IS_LIVE;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_LIV_POSE_DET_FAIL_ERR;
import static cn.cloudwalk.midware.live.user.CWLiveness.CW_FACE_NO_FACE;


public class PersonActivity extends Activity {

    private TextView tvfh;
    private TextView tvtitle;
    private SmartTable table;
    private EditText etname;
    private EditText etcode;
    private EditText etusername;
    private EditText etpassword;
    private ImageView imgzhaopian;
    private Button btnadd;
    private Button btnupdate;
    private Button btndelete;
    private Button btnSPBF;
    private Button btnZPCJ;
    private Button btnZWLR;
    private Button btnEmpty;
    private RadioGroup radioGroup;
    List<Person> list=new ArrayList<>();
    private String zwzt="0";//指纹状态,0-失败，1-录入中，2-录入成功

    private ArrayList<CWCameraInfos> cameraInfoList = new ArrayList<>();
    private static final int MSG_SEND_TIP = 10;
    private static final int MSG_SEND_START_LIV_TIP = 11;
    private static final int MSG_SEND_STOP_LIV_TIP = 12;
    private static final int MSG_SEND_IMAGE_FEATURE = 13;
    private static final int MSG_SAVE_BESTFACE = 14;
    private static final int MSG_SAVE_BGRIMAGE = 15;
    private GLFrameSurface mSrVisView;
    SafeHandler handler = new SafeHandler(this);
    private static final String BESTFACE_PATH = "/sdcard/cloudwalk/最佳人脸/";
    private static final String CURRENT_IMAGE_PATH = "/sdcard/cloudwalk/当前照片/";
    private static final String ROOT_PATH = "/sdcard/cloudwalk/";
    private CWLiveImage lastCompareImg;
    private final Object pushObj = new Object();
    private long compareFaceDetictTimeout = 5000;
    private CWLiveFaceRect lastLiveRect;
    private int featureImageGetType = 1;    //获取当前图片来源类型（比对）0-人脸 1-活体
    private boolean isLiveEnable;
    private boolean isResetLastImage = true;
    private boolean cameraStatus=false;//摄像头打开状态
    private String pathTuPian="";//抓拍图片路径
    byte[] feacture=null;
    private int selectPosition=-1;
    private CWLiveImage lastLiveImage=null;
    CWGLDisplay mVisGlDiaplay= new CWGLDisplay();
    private Logger logger = Logger.getLogger(this.getClass());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_person);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        initView();
        initColums();
    }

    private void initView(){
        tvfh=(TextView)findViewById(R.id.fh);
        tvfh.setOnClickListener(new onClickListener());
        tvtitle=(TextView)findViewById(R.id.title);
        tvtitle.setText("人员管理");
        etname=(EditText)findViewById(R.id.name);
        etcode=(EditText)findViewById(R.id.code);
        etusername=(EditText)findViewById(R.id.username);
        etpassword=(EditText)findViewById(R.id.password);
        //ettzz=(EditText)findViewById(R.id.tzz);
        btnSPBF=(Button)findViewById(R.id.dakaishipin);
        btnZPCJ=(Button)findViewById(R.id.caijizhaopian);
        btnSPBF.setOnClickListener(new onClickListener());
        btnZPCJ.setOnClickListener(new onClickListener());
        mSrVisView = (GLFrameSurface) findViewById(R.id.sr_vis_view);
        imgzhaopian=(ImageView)findViewById(R.id.iv_photo);
        btnadd=(Button)findViewById(R.id.add);
        btnupdate=(Button)findViewById(R.id.update);
        btnEmpty=(Button)findViewById(R.id.empty);
        btnZWLR=(Button)findViewById(R.id.zhiwenluru);
        btnZWLR.setOnClickListener(new onClickListener());
        btnEmpty.setOnClickListener(new onClickListener());
        btndelete=(Button)findViewById(R.id.delete);
        btnadd.setOnClickListener(new onClickListener());
        btnupdate.setOnClickListener(new onClickListener());
        btndelete.setOnClickListener(new onClickListener());

        radioGroup=(RadioGroup)findViewById(R.id.radiogroup);

        Cache.myHandlePerson = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                //提示信息
                if (bundle.getString("path") != null) {
                    Bitmap bit = BitmapFactory.decodeFile(bundle.get("path").toString());
                    imgzhaopian.setImageBitmap(bit);
                }
                if (bundle.getString("zw") != null) {
                    if(bundle.getString("zw").toString().equals("ok")){
                        Cache.zwlrNow=false;
                        zwzt="2";
                        //ettzz.setText("");
                        MyTextToSpeech.getInstance().speak("指纹录入完成");
                        Toast.makeText(PersonActivity.this, "指纹录入完成", Toast.LENGTH_SHORT).show();
                    }else if(bundle.getString("zw").toString().equals("fail")){
                        zwzt="0";
                        //ettzz.setText("");
                        Cache.zwlrNow=false;
                        MyTextToSpeech.getInstance().speak("指纹录入失败");
                        Toast.makeText(PersonActivity.this, "指纹录入失败", Toast.LENGTH_SHORT).show();
                    }else if(bundle.getString("zw").toString().equals("progress")){
                        zwzt="1";
                        MyTextToSpeech.getInstance().speak("指纹录入中请勿移动手指");
                        Toast.makeText(PersonActivity.this, "指纹录入中请勿移动手指", Toast.LENGTH_SHORT).show();
                    }

                }


            }
        };
    }

    private void initColums(){
        pathTuPian="";
        selectPosition=-1;

        Column<String> name = new Column<>("姓名", "name");
        name.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String s, String s2, int i) {
                updateTableUI(i);
            }
        });

        Column<String> code = new Column<>("编号", "code");
        code.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String s, String s2, int i) {
                updateTableUI(i);
            }
        });

        Column<String> username = new Column<>("账号", "username");
        username.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String s, String s2, int i) {
                updateTableUI(i);
            }
        });
        Column<String> role = new Column<>("角色", "role");
        role.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String s, String s2, int i) {
                updateTableUI(i);
            }
        });
        Column<String> tzz = new Column<>("指纹录入", "tzz");
        tzz.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
            @Override
            public void onClick(Column<String> column, String s, String s2, int i) {
                updateTableUI(i);
            }
        });
        //list= DBManager.getInstance().getPersonDao().loadAll();
        PersonDao personDao = new PersonDao();
        list = personDao.getAllPerson();
        TableData<Person> tableData = new TableData<Person>("",list,  name,code, username,role,tzz);
        //设置数据
        table = findViewById(R.id.table);
        table.setTableData(tableData);

        table.getConfig().setShowXSequence(false);
        table.getConfig().setShowYSequence(false);
        table.getConfig().setShowTableTitle(false);
        table.getConfig().setColumnTitleBackgroundColor(Color.BLUE);
        table.getConfig().setColumnTitleStyle(new FontStyle(20,Color.WHITE));
        table.getConfig().setContentStyle(new FontStyle(18,Color.BLACK));
        //table.getConfig().setMinTableWidth(1400);
    }

    private void updateTableUI(int ii){
        pathTuPian="";
        selectPosition=ii;
        final int jj=ii;
        try{
            table.getConfig().setContentBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {     //设置隔行变色
                @Override
                public int getBackGroundColor(CellInfo cellInfo) {

                    if(jj==cellInfo.position){
                        showMessage(cellInfo.position);
                        return ContextCompat.getColor(PersonActivity.this, R.color.btn_filled_blue_bg_normal);
                    }else{
                        return TableConfig.INVALID_COLOR;
                    }


                }
            });
            table.refreshDrawableState();
            table.invalidate();
        }catch (Exception e){
            logger.error("更新存放缓存出错",e);
        }

    }

    private void showMessage(int i){
        etname.setText(list.get(i).getName());
        etcode.setText(list.get(i).getCode());
        etusername.setText(list.get(i).getUsername());
        etpassword.setText(list.get(i).getPassword());
        //ettzz.setText(list.get(i).getTzz());
        if(list.get(i).getPath()!=null){
            File file = new File(list.get(i).getPath());
            if(file.exists()){
                Bitmap bitmap=BitmapFactory.decodeFile(list.get(i).getPath());
                imgzhaopian.setImageBitmap(bitmap);
            }
        }else {
            imgzhaopian.setBackgroundColor(Color.BLACK);

        }

    }

    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.dakaishipin:
                    if(!cameraStatus){
                        cameraStatus=true;
                        btnSPBF.setText("关闭视频");
                        //YCCamera.cwVisPreivewConfig.setSurfaceView(mSrVisView);
                        updatePreview(true);
                        updateDetect(true);
                        CWEngine.getInstance().cwSetFrameCallback(new CWFrameCallback() {
                            @Override
                            public void onBgrFrame(byte[] data) {
                                System.out.println("222222222222222-----");
                                mVisGlDiaplay.render(mSrVisView,90,true,data,640,480,0);
                            }

                            @Override
                            public void onIrFrame(byte[] data) {

                            }

                            @Override
                            public void onDepthFrame(byte[] data) {


                            }
                        });
                    }else {
                        btnSPBF.setText("打开视频");
                        cameraStatus=false;
                        updatePreview(false);
                        updateDetect(false);

                    }
                    break;
                case R.id.caijizhaopian:
                    final CWLiveImage image1 = lastLiveImage;
                    final CWLiveFaceRect rect1 = lastLiveRect;
                    CWLiveFaceRecogInfo cwLiveFaceRecogInfo1 = CWEngine.getInstance().cwGetFaceFeature(image1,
                            rect1 != null ? rect1 : null, 1, CWEngine.getInstance().cwGetFeatureLength());

                    if (cwLiveFaceRecogInfo1 != null) {
                        feacture=cwLiveFaceRecogInfo1.getFeature_data();
                    }

                    if (CWEngine.getInstance().cwGetCameraStatus() == CW_CAMERA_STATE_OPENED) {
                        byte[] image = CWEngine.getInstance().cwCaptureCurrentFrame();

                        if (null == image || image.length == 0) {
                            showToast("暂无可见图像");
                            return;
                        }
                        int cameraType = CacheManager.getInstance().loadCameraType(1);
                        switch (cameraType) {
                            case 0:
                            case 1:
                            case 2: {
                                Map<String, List<Size>> sr = CWEngine.getInstance().cwGetCameraSupportedResolutions();
                                if (null == sr || sr.size() == 0) {
                                    return;
                                }
                                List<Size> previewSizeList = sr.get(CW_CAMERA_VIS_SIZES_KEYNAME);
                                if (null == previewSizeList || previewSizeList.size() == 0) {
                                    return;
                                }
                                int width = 400, height = 300;
                                if (cameraType == 1) {
                                    for (Size size : previewSizeList) {
                                        if ((size.width * size.height * 3 / 2) == image.length) {
                                            width = size.width;
                                            height = size.height;
                                            break;
                                        }
                                    }
                                } else if (cameraType == 2) {  //节点相机输出的图片大小等于width*height*2
                                    for (Size size : previewSizeList) {
                                        if ((size.width * size.height * 2) == image.length) {
                                            width = size.width;
                                            height = size.height;
                                            break;
                                        }
                                    }
                                }
                                if (width > 0 && height > 0) {   // author: xujunke 修改了保存图片主线程卡的问题，放到子线程
                                    takePicture(image, width, height);
                                }
                                break;
                            }
                            case 3: {
                                int width = 400, height = 300;
                                takePicture(image, width, height);  // author: xujunke 修改了保存图片主线程卡的问题，放到子线程
                                break;
                            }
                        }
                    }
                    break;
                case R.id.add:

                    if(etname.getText().toString().trim().equals("")){
                        MyTextToSpeech.getInstance().speak("姓名不能为空");
                        showToast("姓名不能为空");
                        return;
                    }
                   if(!checkCode()){
                       return;
                   }
                    if(etusername.getText().toString().trim().equals("")){
                        MyTextToSpeech.getInstance().speak("用户名不能为空");
                        showToast("用户名不能为空");
                        return;
                    }
                    if(etpassword.getText().toString().trim().equals("")){
                        MyTextToSpeech.getInstance().speak("密码不能为空");
                        showToast("密码不能为空");
                        return;
                    }
                    //添加信息到表中
                    Person person = new Person();
                    person.setId(UUID.randomUUID().toString());
                    person.setCode(etcode.getText().toString());
                    person.setName(etname.getText().toString());
                    person.setPassword(etpassword.getText().toString());
                    person.setUsername(etusername.getText().toString());
                    String role=((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                    person.setRole(role);
                    if(zwzt.equals("0")){
                        person.setTzz("未录入");
                    }else if(zwzt.equals("1")){
                        person.setTzz("录入中");
                    }else if(zwzt.equals("2")){
                        person.setTzz("已录入");
                    }

                    //pathTuPian=ROOT_PATH+ "feature"+File.separator+ "1595563551866.jpg";
                    person.setPath(pathTuPian);
                    if(!pathTuPian.equals("")){
                        File file = new File(pathTuPian);
                        if(file.exists()){
                            try{
                                person.setFeaturedata(feacture);
                            }catch (Exception e){
                                logger.error("读取抓拍照片出错",e);
                            }
                        }
                    }

                    PersonDao personDao = new PersonDao();
                    personDao.addPerson(person);
                    feacture=null;
                    pathTuPian="";
                    zwzt="0";
                    initColums();
                    MyTextToSpeech.getInstance().speak("添加成功");
                    showToast("添加成功");
                    break;
                case R.id.update:
                    if(selectPosition<0){
                        MyTextToSpeech.getInstance().speak("请选择一条记录");
                        showToast("请选择一条记录");
                        return;
                    }
                    if(Cache.zwlrNow){
                        return;
                    }
                   if(!checkCode()){
                        return;
                   }

                    Person personupdate = new Person();
                    personupdate.setId(list.get(selectPosition).getId());
                    personupdate.setCode(etcode.getText().toString());
                    personupdate.setName(etname.getText().toString());
                    personupdate.setPassword(etpassword.getText().toString());
                    personupdate.setUsername(etusername.getText().toString());
                    String roleupdate=((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                    personupdate.setRole(roleupdate);
                    if(!pathTuPian.equals("")){
                        File file = new File(pathTuPian);
                        if(file.exists()){
                            try{
                                personupdate.setPath(pathTuPian);
                                personupdate.setFeaturedata(feacture);
                            }catch (Exception e){
                                logger.error("读取抓拍照片出错",e);
                            }
                        }
                    }else{
                        personupdate.setFeaturedata(list.get(selectPosition).getFeaturedata());
                        personupdate.setPath(list.get(selectPosition).getPath());
                    }
                    if(zwzt.equals("0")){
                        personupdate.setTzz(list.get(selectPosition).getTzz());
                    }else if(zwzt.equals("1")){
                        personupdate.setTzz("录入中");
                    }else if(zwzt.equals("2")){
                        personupdate.setTzz("已录入");
                    }
                    PersonDao personDaoupdate = new PersonDao();
                    personDaoupdate.updatePerson(personupdate);
                    initColums();
                    MyTextToSpeech.getInstance().speak("修改成功");
                    showToast("修改成功");
                    break;
                case R.id.delete:

                    if(selectPosition>=0){
                        final AlertDialog alertDialog = new AlertDialog.Builder(PersonActivity.this)
                                .setTitle("确认提示框")
                                .setMessage("确认删除该人员？")
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        boolean blzw=HCProtocol.ST_DeleteZW(0,Integer.valueOf(list.get(selectPosition).getCode()));
                                        PersonDao personDao = new PersonDao();
                                        logger.info("删除["+list.get(selectPosition).getName()+"]指纹返回结果:"+blzw);
                                        personDao.deletePersonById(list.get(selectPosition).getId());
                                        initColums();
                                        MyTextToSpeech.getInstance().speak("删除成功");
                                        showToast("删除成功");
                                    }
                                })

                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                }) .create();
                        alertDialog.show();

                    }else{
                        showToast("请选择一条记录");
                    }
                    break;
                case R.id.empty:
                    etname.setText("");
                    etcode.setText("");
                    etusername.setText("");
                    etpassword.setText("");
                    selectPosition=-1;
                    updateTableUI(-1);
                    break;
                case R.id.zhiwenluru:
                    if(Cache.zwlrNow){
                        return;
                    }
                   if(!checkCode()){
                       return;
                   }
                   //是更新指纹
                    if(selectPosition>=0){
                        //修改后的工号是否与已经添加的工号重复
                        if(!updateCFCode()){
                            MyTextToSpeech.getInstance().speak("工号重复");
                            showToast("工号重复");
                            return;
                        }
                        AlertDialog alertDialog = new AlertDialog.Builder(PersonActivity.this)
                                .setTitle("确认提示框")
                                .setMessage("重新录入将删除原指纹")
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //先删除指纹
                                        boolean blzw=HCProtocol.ST_DeleteZW(0,Integer.valueOf(list.get(selectPosition).getCode()));
                                        logger.info("更新["+list.get(selectPosition).getName()+"]信息时删除指纹返回:"+blzw);

                                        MyTextToSpeech.getInstance().speak("请录入指纹");
                                        showToast( "请录入指纹");
                                        boolean bl= HCProtocol.ST_AddSaveZW(Integer.valueOf(etcode.getText().toString()));
                                        if(bl){
                                            Cache.zwlrNow=true;
                                            new ZWLR().start();
                                        }else{
                                            MyTextToSpeech.getInstance().speak("指纹录入失败");
                                            showToast("指纹录入失败");
                                        }

                                    }
                                })

                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                }) .create();
                        alertDialog.show();
                    }else{
                        //添加人员指纹
                        getZW();
                    }


                    break;
                case R.id.fh:
                    if(Cache.zwlrNow){
                        return;
                    }
                    PersonActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }

    private boolean checkCode(){
        if(etcode.getText().toString().trim().equals("")){
            MyTextToSpeech.getInstance().speak("请先输入工号");
            showToast("请先输入工号");
            return false;
        }
        try{
            int codeNum=Integer.valueOf(etcode.getText().toString());
        }catch (Exception e){
            MyTextToSpeech.getInstance().speak("工号必须为数字");
            showToast("工号必须为数字");
            return false;
        }
        if(Integer.valueOf(etcode.getText().toString())>710){
            MyTextToSpeech.getInstance().speak("工号不能超过710");
            showToast("工号不能超过710");
            return false;
        }
        if(Integer.valueOf(etcode.getText().toString())<=0){
            MyTextToSpeech.getInstance().speak("工号不能小于等于0");
            showToast("工号不能小于等于0");
            return false;
        }
        return true;
    }

    private boolean updateCFCode(){
        PersonDao personDao = new PersonDao();
        int codei=Integer.valueOf(etcode.getText().toString().trim());
        String codep=String.valueOf(codei);
        List<Person> listCF= personDao.getSameCodeForUpdate(list.get(selectPosition).getId(),codep);
        if(listCF==null || listCF.isEmpty()){
            return true;
        }else {
            return false;
        }
    }

    private void takePicture(final byte[] image, final int width, final int height) {
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                byte[] visByte = new byte[(width * height * 3) / 2];
                I420ToNV21(image, visByte, width, height);
                //获取image图片的特征值
                //feacture=CWEngine.getInstance().cwConvertFiledFeatureToProbeFeature(visByte,CWEngine.getInstance().cwGetFeatureLength());
                float angle = CacheManager.getInstance().loadRotation(90); //默认旋转角度为90度
                float sx = CacheManager.getInstance().loadMirror(false) ? -1 : 1;
                pathTuPian=ROOT_PATH+ "feature"+File.separator+ System.currentTimeMillis()+".jpg";
                cn.cloudwalk.midware.engine.utils.ImgUtil.saveYuvImage(pathTuPian, visByte,
                        width,
                        height, 80, Bitmap.CompressFormat.PNG, angle, sx, 1);

                Message message = Message.obtain(Cache.myHandlePerson);
                Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
                data.putString("path",pathTuPian);
                message.setData(data);
                Cache.myHandlePerson.sendMessage(message);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    initConfig();
                    // resetParams();

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
            initConfig();

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
        cwVisPreivewConfig.setSurfaceView(mSrVisView);
        //cwVisPreivewConfig.setCwRectView(mRectView);
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

        long ret = CWEngine.getInstance().cwInit(PersonActivity.this, cwCameraConfig, cwLivenessConfig, cwVisPreivewConfig);

        //mTransprantView.setFocusable(false);
        //mTransprantView.setClickable(false);
        if (ret == 0L) {
            showToast("初始化失败");
        } else {
            showToast("初始化成功");
        }

        CWEngine.getInstance().setLogLevel(cn.cloudwalk.midware.engine.utils.Logger.WARN, ",");

        CWEngine.getInstance().cwSetFrameCallback(new CWFrameCallback() {
            @Override
            public void onBgrFrame(byte[] data) {
                System.out.println("personactivity bgr:"+System.currentTimeMillis()+"  "+data.length);
            }

            @Override
            public void onIrFrame(byte[] data) {
                System.out.println("personactivity ir:"+System.currentTimeMillis()+"  "+data.length);
            }

            @Override
            public void onDepthFrame(byte[] data) {
                System.out.println("personactivity depth:"+System.currentTimeMillis()+"  "+data.length);
            }
        });

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

    private static class SafeHandler extends Handler {

        private WeakReference<PersonActivity> mWeakReference;

        public SafeHandler(PersonActivity activity) {
            mWeakReference = new WeakReference<PersonActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PersonActivity activity = mWeakReference.get();
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
                    Toast.makeText(PersonActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        CWEngine.getInstance().cwSetLiveInfoCallback(mLiveinfoCallback);
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
                    //ThreadManager.getThreadPool().execute(compareRunable);
//                    compareWithLib(lastLiveImage, lastLiveRect);
                } else {
                    showToast("比对失败");
                    //isCompare = false;
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
//                Message msg = new Message();
//                msg.what = MSG_SEND_IMAGE_FEATURE;
//                msg.obj = image;
//                handler.sendMessage(msg);
//                if (startFaceCompare) {
//                    ThreadManager.getThreadPool().execute(compareRunable);
////                    compareWithLib(lastLiveImage, lastLiveRect);
//                }
            }

            if (faceDetectInfoList.size() > 0 && faceDetectInfoList.get(0).getCode() == CW_FACE_LIV_POSE_DET_FAIL_ERR) {  // author: xujunke 临时解决角度不正确的问题
                //showTips(faceDetectInfoList.get(0).getCode());
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
                    //showTips(detectInfo.getCode());
                    return;
                } else {
                   // showTips(livenessInfo.getCode());
                    if (featureImageGetType == 1 && isLiveEnable && isResetLastImage && livenessInfo.getCode() == CW_FACE_LIV_IS_LIVE) {

                        isResetLastImage = false;
                        lastLiveRect = detectInfo.getRect();
                        lastLiveImage = bgrImage;
                    }

                }
            }

        }
    };

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
                    isResetLastImage = false;
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

    private void getZW(){
        try{
            if(Cache.zwlrNow){
                return;
            }
            if(!checkCode()){
                return;
            }
            if(!checkAddZWCF()){
                return;
            }
            //先将指纹模块中该人员编号指纹删除
            MyTextToSpeech.getInstance().speak("请录入指纹");
            Toast.makeText(this, "请录入指纹", Toast.LENGTH_SHORT).show();
            boolean bl= HCProtocol.ST_AddSaveZW(Integer.valueOf(etcode.getText().toString()));
            logger.info("添加人员编号为"+etcode.getText()+"指纹录入返回结果:"+bl);
            if(bl){
                Cache.zwlrNow=true;
                new ZWLR().start();
            }else{
                MyTextToSpeech.getInstance().speak("指纹录入失败");
                Toast.makeText(this, "指纹录入失败", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            logger.error("获取指纹出错",e);
        }
    }

    private boolean checkAddZWCF(){
        boolean bl=false;
        try{
            //若编号前有0，则将0删除
            int codei=Integer.valueOf(etcode.getText().toString().trim());
            String codep=String.valueOf(codei);
            System.out.println(codep);
            PersonDao personDao = new PersonDao();
            List<HashMap<String,String>> listCF = personDao.getSameCodeForAdd(codep);
            if(!listCF.isEmpty()){
                Toast.makeText(this, "工号重复", Toast.LENGTH_SHORT).show();
                MyTextToSpeech.getInstance().speak("工号重复");
                bl=false;
                return false;
            }
            bl=true;
            return true;
        }catch (Exception e){
            logger.error("",e);
        }
        return  bl;
    }

    class ZWLR extends Thread{
        public void run(){
            try{
                HCProtocol.ST_GetZWZT();
            }catch (Exception e){
                logger.error("指纹录入出错",e);
            }

        }
    }
}
