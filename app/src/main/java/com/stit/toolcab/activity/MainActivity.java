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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.stit.toolcab.R;
import com.stit.toolcab.dao.RecordDao;
import com.stit.toolcab.dao.PZDao;
import com.stit.toolcab.db.DatabaseManager;
import com.stit.toolcab.db.UpdateDB;
import com.stit.toolcab.device.DeviceCom;
import com.stit.toolcab.device.HCProtocol;
import com.stit.toolcab.entity.ToolZT;
import com.stit.toolcab.utils.Cache;
import com.stit.toolcab.utils.CrashHandler;
import com.stit.toolcab.utils.LogUtil;
import com.stit.toolcab.utils.MySpeechUtil;
import com.stit.toolcab.utils.MyTextToSpeech;
import com.stit.toolcab.utils.YCCamera;

import org.apache.log4j.Logger;


public class MainActivity extends Activity {

    private ImageButton btnPD;
    private ImageButton btnMenu;
    private RelativeLayout rl;
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
    private TextView tvappcode;
    private LinearLayout.LayoutParams params=null;
    private SmartTable table;

    Column<String> column1 = new Column<>("名称", "mc");
    Column<String> column2 = new Column<>("规格", "gg");
    Column<String> column3 = new Column<>("人员", "name");
    Column<String> column4 = new Column<>("时间", "time");

    private Button btnLoginOut;

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
        tvappcode=(TextView)findViewById(R.id.appcode);
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
//                SelectDialog.Builder builder = new SelectDialog.Builder(MainActivity.this);
//                AlertDialog selectDialog =  builder.create();
//                selectDialog.show();
//                WindowManager.LayoutParams  lp= selectDialog.getWindow().getAttributes();
//                lp.width=200;//定义宽度
//                lp.height=500;//定义高度
//                lp.x = 580;//设置x坐标
//                lp.y = -50;//设置y坐标
//
//                selectDialog.getWindow().setAttributes(lp);

                SelectDialog selectDialog = new SelectDialog(MainActivity.this,R.style.dialog);//创建Dialog并设置样式主题
                Window win = selectDialog.getWindow();
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.x = 580;//设置x坐标
                params.y = -50;//设置y坐标
                win.setAttributes(params);
                selectDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
                selectDialog.show();
//
//               final String[] items = {"item 1", "item 2", "item 3", "item 4", "item 5", "item 6"};
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                View viewd = View.inflate(MainActivity.this,R.layout.slt_cnt_type, null);
//                builder.setView(viewd);
////                Button btnry=(Button)findViewById(R.id.btnRY);
////                btnry.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View view) {
////                        Toast.makeText(MainActivity.this, "你点击的内容为：1 " , Toast.LENGTH_LONG).show();
////                    }
////                });
//                //builder.setTitle("");
//                builder.setItems(items, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Toast.makeText(MainActivity.this, "你点击的内容为： " + items[i], Toast.LENGTH_LONG).show();
//
//                            }
//                        });
//
//
//
//                AlertDialog alertDialog =builder.create();
//                alertDialog.show();
//                WindowManager.LayoutParams  lp= alertDialog.getWindow().getAttributes();
//                lp.width=200;//定义宽度
//                lp.height=500;//定义高度
//                lp.x = 580;//设置x坐标
//                lp.y = -50;//设置y坐标
//
//                alertDialog.getWindow().setAttributes(lp);

            }
        });

        btnLoginOut=(Button)findViewById(R.id.loginout);
        btnLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

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
        RecordDao recordDao =new RecordDao();
        recordDao.initJYBXWX();
        tvjcgs.setText("（"+Cache.listJY.size()+"）");
        tvbxgs.setText("（"+Cache.listBX.size()+"）");
        tvwxgs.setText("("+Cache.listWX.size()+")");
        initJC();
    }

    private void initPZ(){
        PZDao PZDao = new PZDao();
        Cache.peiZhi = PZDao.getPZ();
        tvappcode.setText(Cache.peiZhi==null?"":Cache.peiZhi.getAppcode());
        tvappname.setText(Cache.peiZhi==null?"":Cache.peiZhi.getAppname());
    }

    private void initJC(){
        try{
            //表格数据 datas是需要填充的数据
            TableData<ToolZT> tableData = new TableData<ToolZT>("", Cache.listJY, column1, column2, column3, column4);
            //设置数据
            table.setTableData(tableData);
            column1.setWidth(300);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void initBX(){
        try{

            //表格数据 datas是需要填充的数据
            TableData<ToolZT> tableData = new TableData<ToolZT>("", Cache.listBX, column1, column2, column3, column4);
            table.setTableData(tableData);
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    private void initWX(){
        try{
            //表格数据 datas是需要填充的数据
            TableData<ToolZT> tableData = new TableData<ToolZT>("", Cache.listWX, column1, column2, column3, column4);
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
                    //MyTextToSpeech.getInstance().speak("大家好");
//                    Intent intent = new Intent(MainActivity.this, ProgressDialog.class);
//                    startActivity(intent);
                    HCProtocol.ST_GetAllCard();
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
//                                Intent intent = new Intent(MainActivity.this, AccessConActivity.class);
//                                startActivity(intent);
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
                        }
                        if(bundle.getString("appname")!=null){
                            tvappname.setText(Cache.peiZhi==null?"智能工具柜":Cache.peiZhi.getAppname());
                            tvappcode.setText(Cache.peiZhi==null?"01号柜":Cache.peiZhi.getAppcode());
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