package com.stit.toolcab.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.stit.toolcab.R;
import com.stit.toolcab.dao.RecordDao;
import com.stit.toolcab.dao.PZDao;
import com.stit.toolcab.dao.ToolsDao;
import com.stit.toolcab.db.DatabaseManager;
import com.stit.toolcab.db.UpdateDB;
import com.stit.toolcab.device.DeviceCom;
import com.stit.toolcab.device.HCProtocol;
import com.stit.toolcab.entity.ToolZT;
import com.stit.toolcab.entity.Tools;
import com.stit.toolcab.utils.Cache;
import com.stit.toolcab.utils.CrashHandler;
import com.stit.toolcab.utils.LogUtil;
import com.stit.toolcab.utils.MySpeechUtil;
import com.stit.toolcab.utils.MyTextToSpeech;
import com.stit.toolcab.utils.YCCamera;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private ImageButton btnPD;
    private ImageButton btnMenu;
    private ImageButton btnQRCode;
    private RelativeLayout rl;
    private RelativeLayout mylayoutdevice;
    private LinearLayout lljc;
    private LinearLayout llbx;
    private LinearLayout llwx;
    private TextView tvjcxx;
    private TextView tvbxxx;
    private TextView tvwxxx;
    private TextView tvjcgs;
    private TextView tvbxgs;
    private TextView tvwxgs;
    private TextView tvczy;
    private TextView tvappname;
    //private TextView tvappcode;

    private ImageView ceng1;
    private ImageView ceng2;
    private ImageView ceng3;
    private ImageView ceng4;
    private ImageView ceng5;
    private ImageView ceng6;
    private ImageView ceng7;

    private LinearLayout.LayoutParams params=null;
    private SmartTable table;
    Column<String> columnmc = new Column<String>("名称", "mc");
    Column<String> columngg = new Column<String>("规格", "gg");
    Column<String> columnjyry = new Column<String>("借用人员", "jyname");
    Column<String> columnbxry = new Column<String>("报修人员", "bxname");
    Column<String> columnwxry = new Column<String>("维修人员", "wxname");
    Column<String> columnjysj = new Column<String>("借用时间", "jytimepoke");
    Column<String> columnbxsj = new Column<String>("报修时间", "bxtimepoke");
    Column<String> columnwxsj = new Column<String>("维修时间", "wxtimepoke");


    private ImageButton btnLoginOut;
    private ImageButton btnChaZhao;

    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        //使用布局文件来定义标题栏
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
//        setContentView(R.layout.activity_main);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        //读写控制
        //requestPermission();
//        if(1==1){
//            return;
//        }
        checkPermission();
        LogUtil.initLog();// 初始log
        logger = Logger.getLogger(this.getClass());
        logger.info("程序开始启动");
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        logger.info("加载未捕获异常完成");
        initDataBase();
        initView();
        initHandler();
        //DBManager.initDataBase(this);

        initPZ();
        initTJ();
        //initGT();
        Cache.myContext=this;
        initSpeechPlug();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        new DeviceCom().start();
        YCCamera.init();

    }

    private void initView(){
        mylayoutdevice=(RelativeLayout)findViewById(R.id.mylayoutdevice);
        rl=(RelativeLayout)findViewById(R.id.mylayout);
        btnQRCode=(ImageButton)findViewById(R.id.erweima);
        btnQRCode.setOnClickListener(new onClickListener());
        btnPD=(ImageButton)findViewById(R.id.pandian);
        btnPD.setOnClickListener(new onClickListener());
        btnMenu=(ImageButton)findViewById(R.id.menu);
        lljc=(LinearLayout)findViewById(R.id.lljc);
        llbx=(LinearLayout)findViewById(R.id.llbx);
        llwx=(LinearLayout)findViewById(R.id.llwx);
        tvjcgs=(TextView)findViewById(R.id.jcxxgs);
        tvbxgs=(TextView)findViewById(R.id.bxxxgs);
        tvwxgs=(TextView)findViewById(R.id.wxxxgs);
        tvjcxx=(TextView)findViewById(R.id.jcxx);
        tvbxxx=(TextView)findViewById(R.id.bxxx);
        tvwxxx=(TextView)findViewById(R.id.wxxx);
        tvappname=(TextView)findViewById(R.id.appname);
        //tvappcode=(TextView)findViewById(R.id.appcode);
        lljc.setOnClickListener(new onClickListener());
        llbx.setOnClickListener(new onClickListener());
        llwx.setOnClickListener(new onClickListener());
        table=(SmartTable)findViewById(R.id.table);

        table.getConfig().setShowXSequence(false);
        table.getConfig().setShowYSequence(false);
        table.getConfig().setShowTableTitle(false);
        table.getConfig().setColumnTitleBackgroundColor(Color.BLUE);
        table.getConfig().setColumnTitleStyle(new FontStyle(20,Color.WHITE));
        table.getConfig().setContentStyle(new FontStyle(18,Color.BLACK));
        table.getConfig().setMinTableWidth(700);

        tvczy=(TextView)findViewById(R.id.czy);

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectDialog selectDialog = new SelectDialog(MainActivity.this,R.style.dialog);//创建Dialog并设置样式主题
                Window win = selectDialog.getWindow();
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.x = 550;//设置x坐标
                params.y = -130;//设置y坐标
                win.setAttributes(params);
                selectDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
                selectDialog.show();
            }
        });

        btnLoginOut=(ImageButton)findViewById(R.id.loginout);
        btnLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        btnChaZhao=(ImageButton)findViewById(R.id.chazhao);
        btnChaZhao.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //打开维修列表界面
                FindDialog findDialog = new FindDialog(MainActivity.this,R.style.roledialog);//创建Dialog并设置样式主题
                Window win = findDialog.getWindow();
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.x = 20;//设置x坐标
                params.y = -50;//设置y坐标
                win.setAttributes(params);
                findDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
                findDialog.setTitle("工具查找");
                findDialog.show();
            }
        });
        initDevice();

    }

    private void initDevice(){

        ceng1 = (ImageView)findViewById(R.id.ceng1);
        ceng1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HCProtocol.ST_OpenDoor();
                ceng1.setImageResource(R.drawable.opened);
            }
        });
        ceng2 = (ImageView)findViewById(R.id.ceng2);
        ceng2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HCProtocol.ST_OpenDoor();
            }
        });
        ceng3 = (ImageView)findViewById(R.id.ceng3);
        ceng3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HCProtocol.ST_OpenDoor();
            }
        });
        ceng4 = (ImageView)findViewById(R.id.ceng4);
        ceng4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HCProtocol.ST_OpenDoor();

            }
        });
        ceng5 = (ImageView)findViewById(R.id.ceng5);
        ceng5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HCProtocol.ST_OpenDoor();
            }
        });
        ceng6 = (ImageView)findViewById(R.id.ceng6);
        ceng6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HCProtocol.ST_OpenDoor();
            }
        });
        ceng7 = (ImageView)findViewById(R.id.ceng7);
        ceng7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HCProtocol.ST_OpenDoor();
            }
        });

    }


    private void initTJ(){
        logger.info("开始统计借用维修报修信息");
        ToolsDao toolsDao =new ToolsDao();
        toolsDao.initJYBXWX();
        tvjcgs.setText("（"+Cache.listJY.size()+"）");
        tvbxgs.setText("（"+Cache.listBX.size()+"）");
        tvwxgs.setText("("+Cache.listWX.size()+")");
        initJC();
    }

    private void initPZ(){
        PZDao PZDao = new PZDao();
        Cache.peiZhi = PZDao.getPZ();
        //tvappcode.setText(Cache.peiZhi==null?"":Cache.peiZhi.getAppcode());
        //tvappname.setText(Cache.peiZhi==null?"":Cache.peiZhi.getAppname());
    }

    private void initJC(){
        try{

            //表格数据 datas是需要填充的数据
//            Tools tools = new Tools();
//            tools.setMc("a");
//            List<Tools> listTest = new ArrayList<Tools>();
//            listTest.add(tools);

            TableData<ToolZT> tableData = new TableData<ToolZT>("", Cache.listJY, columnmc, columngg, columnjyry,columnjysj);
            //设置数据
            table.setTableData(tableData);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void initBX(){
        try{
            //表格数据 datas是需要填充的数据
            TableData<ToolZT> tableData = new TableData<ToolZT>("", Cache.listBX, columnmc, columngg, columnbxry, columnbxsj);
            table.setTableData(tableData);
        }catch (Exception e){
            logger.error("渲染表格出错",e);

        }
    }

    private void initWX(){
        try{
            //表格数据 datas是需要填充的数据
            TableData<ToolZT> tableData = new TableData<ToolZT>("", Cache.listWX, columnmc, columngg, columnwxry, columnwxsj);
            table.setTableData(tableData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 单击事件监听
     */
    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.pandian:
//                    Intent intent = new Intent(MainActivity.this, TestActivity.class);
//                    startActivity(intent);
//                    if(1==1){
//                        return;
//                    }
                    logger.info("点击盘点");
                    Cache.getHCCS=2;
                    if(HCProtocol.ST_GetAllCard()){
                    }else{
                        MyTextToSpeech.getInstance().speak("盘点失败");
                        Toast.makeText(MainActivity.this, "盘点失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.erweima:
                    QRCodeDialog qrCodeDialog = new QRCodeDialog(MainActivity.this,R.style.roledialog);//创建Dialog并设置样式主题
                    Window win = qrCodeDialog.getWindow();
                    WindowManager.LayoutParams paramscode = new WindowManager.LayoutParams();
                    paramscode.x = 20;//设置x坐标
                    paramscode.y = -50;//设置y坐标
                    win.setAttributes(paramscode);
                    qrCodeDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
                    qrCodeDialog.setTitle("二维码扫描");
                    qrCodeDialog.show();
                    break;
                case R.id.lljc:
                    params = new LinearLayout.LayoutParams(60,140);
                    params.gravity= Gravity.RIGHT;
                    lljc.setLayoutParams(params);

                    params = new LinearLayout.LayoutParams(50,140);
                    params.gravity= Gravity.RIGHT;
                    params.topMargin=2;
                    llbx.setLayoutParams(params);
                    llwx.setLayoutParams(params);
                    //设置linnerlayout
                    lljc.setBackgroundColor(Color.argb(255,63,81,181));
                    llbx.setBackgroundColor(Color.argb(255,115,113,113));
                    llwx.setBackgroundColor(Color.argb(255,115,113,113));
                    //设置textview
                    tvjcxx.setBackgroundColor(Color.argb(255,63,81,181));
                    tvjcgs.setBackgroundColor(Color.argb(255,63,81,181));
                    tvbxxx.setBackgroundColor(Color.argb(255,115,113,113));
                    tvbxgs.setBackgroundColor(Color.argb(255,115,113,113));
                    tvwxxx.setBackgroundColor(Color.argb(255,115,113,113));
                    tvwxgs.setBackgroundColor(Color.argb(255,115,113,113));
                    initJC();
                    break;
                case R.id.llbx:
                    params = new LinearLayout.LayoutParams(60,140);
                    params.gravity= Gravity.RIGHT;
                    params.topMargin=2;

                    llbx.setLayoutParams(params);
                    params = new LinearLayout.LayoutParams(50,140);
                    params.gravity= Gravity.RIGHT;
                    params.topMargin=2;
                    lljc.setLayoutParams(params);
                    llwx.setLayoutParams(params);
                    //设置linnerlayout
                    llbx.setBackgroundColor(Color.argb(255,63,81,181));
                    lljc.setBackgroundColor(Color.argb(255,115,113,113));
                    llwx.setBackgroundColor(Color.argb(255,115,113,113));
                    //设置textview
                    tvbxxx.setBackgroundColor(Color.argb(255,63,81,181));
                    tvbxgs.setBackgroundColor(Color.argb(255,63,81,181));
                    tvjcxx.setBackgroundColor(Color.argb(255,115,113,113));
                    tvjcgs.setBackgroundColor(Color.argb(255,115,113,113));
                    tvwxxx.setBackgroundColor(Color.argb(255,115,113,113));
                    tvwxgs.setBackgroundColor(Color.argb(255,115,113,113));
                    initBX();
                    break;
                case R.id.llwx:
                    params = new LinearLayout.LayoutParams(60,140);
                    params.gravity= Gravity.RIGHT;
                    params.topMargin=2;
                    llwx.setLayoutParams(params);

                    params = new LinearLayout.LayoutParams(50,140);
                    params.gravity= Gravity.RIGHT;
                    params.topMargin=2;
                    lljc.setLayoutParams(params);
                    llbx.setLayoutParams(params);
                    //设置linnerlayout
                    llwx.setBackgroundColor(Color.argb(255,63,81,181));
                    lljc.setBackgroundColor(Color.argb(255,115,113,113));
                    llbx.setBackgroundColor(Color.argb(255,115,113,113));
                    //设置textview
                    tvwxxx.setBackgroundColor(Color.argb(255,63,81,181));
                    tvwxgs.setBackgroundColor(Color.argb(255,63,81,181));
                    tvjcxx.setBackgroundColor(Color.argb(255,115,113,113));
                    tvjcgs.setBackgroundColor(Color.argb(255,115,113,113));
                    tvbxxx.setBackgroundColor(Color.argb(255,115,113,113));
                    tvbxgs.setBackgroundColor(Color.argb(255,115,113,113));
                    initWX();
                    break;


                default:
                    break;
            }
        }

    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        //getSupportActionBar().hide();
    }

    private void initHandler(){
        try {
            Cache.mainHandle = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Bundle bundle = null;
                    try {
                        bundle = msg.getData(); // 用来获取消息里面的bundle数据
                        //菜单栏
                        if (bundle.getString("ui") != null) {

                            if (bundle.getString("ui").toString().equals("logout")) {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                            if (bundle.getString("ui").toString().equals("ry")) {
                                Intent intent = new Intent(MainActivity.this, PersonActivity.class);
                                startActivity(intent);
                            }
                            if (bundle.getString("ui").toString().equals("gj")) {
                                Intent intent = new Intent(MainActivity.this, ToolActivity.class);
                                startActivity(intent);
                            }
                            if (bundle.getString("ui").toString().equals("pz")) {
                                Intent intent = new Intent(MainActivity.this, PZActivity.class);
                                startActivity(intent);
                            }
                            if (bundle.getString("ui").toString().equals("kz")) {
                                Intent intent = new Intent(MainActivity.this, KZActivity.class);
                                startActivity(intent);
                            }
                            if(bundle.getString("ui").toString().equals("sbxx")){
                                Intent intent = new Intent(MainActivity.this, DeviceActivity.class);
                                startActivity(intent);
                            }
                            if(bundle.getString("ui").toString().equals("access")){
                                //打开存取操作确认界面
                                Intent intent = new Intent(MainActivity.this, AccessConActivity.class);
                                startActivity(intent);
                            }
                            if(bundle.getString("ui").toString().equals("wx")){
                                //打开维修列表界面
                                BXDialog bxDialog = new BXDialog(MainActivity.this,R.style.roledialog);//创建Dialog并设置样式主题
                                Window win = bxDialog.getWindow();
                                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                                params.x = 20;//设置x坐标
                                params.y = -50;//设置y坐标
                                win.setAttributes(params);
                                bxDialog.setCanceledOnTouchOutside(false);//设置点击Dialog外部任意区域关闭Dialog
                                bxDialog.setTitle("操作选择");
                                bxDialog.show();

                            }

                            if(bundle.getString("ui").toString().equals("tccx")){
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setIcon(android.R.drawable.ic_dialog_info);
                                builder.setTitle("提示");
                                builder.setMessage("您确定要退出程序吗？");
                                builder.setCancelable(true);

                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        logger.info("点击并确认退出程序菜单");
                                        showBar();
                                        System.exit(0);
                                    }
                                });
                                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                builder.create().show();
                            }
                        }
                        if(bundle.getString("pd")!=null){
                            String value=bundle.getString("pd");
                            if(value.equals("openpd")){
                                Intent intent = new Intent(MainActivity.this, ProgressDialog.class);
                                startActivity(intent);
                            }
                        }
                        if(bundle.getString("initTotal")!=null){
                            initTJ();
                            //打开系统登录界面
//                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                            startActivity(intent);
                        }
                        //更新界面操作员
                        if(bundle.getString("czy")!=null){
                            tvczy.setText(Cache.operator.getName());
                            MyTextToSpeech.getInstance().speak("欢迎"+Cache.operator.getName());
                            if(Cache.operator.getRole().equals("维修工人")){
                                //维修工人
                                RoleDialog roleDialog = new RoleDialog(MainActivity.this,R.style.roledialog);//创建Dialog并设置样式主题
                                Window win = roleDialog.getWindow();
                                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                                params.x = 20;//设置x坐标
                                params.y = -50;//设置y坐标
                                win.setAttributes(params);
                                roleDialog.setCanceledOnTouchOutside(false);//设置点击Dialog外部任意区域关闭Dialog
                                roleDialog.setTitle("操作选择");
                                roleDialog.show();
                            }
                        }
                        if(bundle.getString("appname")!=null){
                            tvappname.setText(Cache.peiZhi==null?"智能工具柜":Cache.peiZhi.getAppname());
                            //tvappcode.setText(Cache.peiZhi==null?"01号柜":Cache.peiZhi.getAppcode());
                        }
                    } catch (Exception ex) {
                        logger.error("获取handler消息出错",ex);
                    }
                }};
        }catch( Exception e)
        {
            logger.error("初始化handler出错", e);
        }

    }

    /**
     * 初始TTS引擎
     */
    private void initSpeechPlug() {
        try {
            if (!MySpeechUtil.checkSpeechServiceInstall(MainActivity.this)) {
                MySpeechUtil.processInstall(MainActivity.this,
                        "SpeechService.apk");
            }
            MyTextToSpeech.getInstance().initial(MainActivity.this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean initDataBase() {

        try {
            DatabaseManager.createDatabaseIfNone(MainActivity.this);// 检测数据库，若不存在则创建
            // 数据库连接测试
            SQLiteDatabase db = DatabaseManager.openReadWrite();
            if (db != null && db.isDatabaseIntegrityOk()) {
                //logger.info("打开数据库连接成功");
                db.close();// 关闭数据库
            } else {
                return false;// 数据库打开失败或不可用
            }
            UpdateDB upDB = new UpdateDB(MainActivity.this);
            upDB.updata();
        } catch (Exception ex) {
            //logger.error("初始化数据库出错", ex);
            return false;
        }
        return true;
    }
    /**
     * 打开状态栏
     */
    private void showBar() {
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{
                    "am", "startservice", "-n", "com.android.systemui/.SystemUIService"});
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkPermission() {
        boolean isGranted = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //如果没有写sd卡权限
                isGranted = false;
            }
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }

            if (!isGranted) {
                this.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                                .ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        102);
            }
        }

    }
}