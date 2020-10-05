package com.stit.toolcab.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
    Column<String> columnry = new Column<String>("人员", "name");
    Column<String> columnsj = new Column<String>("时间", "timepoke");
    Column<String> columnbxsj = new Column<String>("时间", "bxtimepoke");
    Column<String> columnwxsj = new Column<String>("时间", "wxtimepoke");


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
                params.x = 580;//设置x坐标
                params.y = -50;//设置y坐标
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
        RelativeLayout.LayoutParams params ;
        params = new RelativeLayout.LayoutParams(247, 43);
        params.setMargins(94, 100, 0, 0);
        ceng1 = new ImageView(this);
        ceng1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HCProtocol.ST_OpenDoor();
                ceng1.setImageResource(R.drawable.ceng1kai);
            }
        });
        ceng1.setImageResource(R.drawable.ceng1guan);
        ceng1.setLayoutParams(params);
        mylayoutdevice.addView(ceng1);

        params = new RelativeLayout.LayoutParams(247, 43);
        params.setMargins(94, 152, 0, 0);
        ceng2 = new ImageView(this);
        ceng2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HCProtocol.ST_OpenDoor();
            }
        });
        ceng2.setImageResource(R.drawable.ceng2guan);
        ceng2.setLayoutParams(params);
        mylayoutdevice.addView(ceng2);

        params = new RelativeLayout.LayoutParams(247, 43);
        params.setMargins(94, 204, 0, 0);
        ceng3 = new ImageView(this);
        ceng3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HCProtocol.ST_OpenDoor();
            }
        });
        ceng3.setImageResource(R.drawable.ceng3guan);
        ceng3.setLayoutParams(params);
        mylayoutdevice.addView(ceng3);

        params = new RelativeLayout.LayoutParams(247, 43);
        params.setMargins(94, 256, 0, 0);
        ceng4 = new ImageView(this);
        ceng4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HCProtocol.ST_OpenDoor();
            }
        });
        ceng4.setImageResource(R.drawable.ceng4guan);
        ceng4.setLayoutParams(params);
        mylayoutdevice.addView(ceng4);

        params = new RelativeLayout.LayoutParams(247, 43);
        params.setMargins(94, 308, 0, 0);
        ceng5 = new ImageView(this);
        ceng5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HCProtocol.ST_OpenDoor();
            }
        });
        ceng5.setImageResource(R.drawable.ceng5guan);
        ceng5.setLayoutParams(params);
        mylayoutdevice.addView(ceng5);

        params = new RelativeLayout.LayoutParams(247, 43);
        params.setMargins(94, 360, 0, 0);
        ceng6 = new ImageView(this);
        ceng6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HCProtocol.ST_OpenDoor();
            }
        });
        ceng6.setImageResource(R.drawable.ceng6guan);
        ceng6.setLayoutParams(params);
        mylayoutdevice.addView(ceng6);

        params = new RelativeLayout.LayoutParams(247, 43);
        params.setMargins(94, 412, 0, 0);
        ceng7 = new ImageView(this);
        ceng7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HCProtocol.ST_OpenDoor();
            }
        });
        ceng7.setImageResource(R.drawable.ceng7guan);
        ceng7.setLayoutParams(params);
        mylayoutdevice.addView(ceng7);
    }

//    private void requestPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
//                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
//            } else {
//                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//            }
//        }
//    }

    private void initTJ(){
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

            TableData<ToolZT> tableData = new TableData<ToolZT>("", Cache.listJY, columnmc, columngg, columnry,columnsj);
            //设置数据
            table.setTableData(tableData);

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void initBX(){
        try{
            //表格数据 datas是需要填充的数据
            TableData<ToolZT> tableData = new TableData<ToolZT>("", Cache.listBX, columnmc, columngg, columnry, columnbxsj);
            table.setTableData(tableData);
        }catch (Exception e){
            logger.error("渲染表格出错",e);

        }
    }

    private void initWX(){
        try{
            //表格数据 datas是需要填充的数据
            TableData<ToolZT> tableData = new TableData<ToolZT>("", Cache.listWX, columnmc, columngg, columnry, columnwxsj);
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
                    logger.info("点击盘点");
                    Cache.getHCCS=2;
                    if(HCProtocol.ST_GetAllCard()){
                    }else{
                        MyTextToSpeech.getInstance().speak("盘点失败");
                        Toast.makeText(MainActivity.this, "盘点失败", Toast.LENGTH_SHORT).show();
                    }
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
}