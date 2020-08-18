package com.stit.toolcab.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.stit.toolcab.R;
import com.stit.toolcab.dao.MyPersonDao;
import com.stit.toolcab.device.HCProtocol;
import com.stit.toolcab.utils.MyTextToSpeech;

import org.apache.log4j.Logger;

public class KZActivity extends Activity {

    private TextView tvfh;
    private TextView tvtitle;
    private Button btnKM1;
    private Button btnKM2;
    private Button btnKM3;
    private Button btnKM4;
    private Button btnKM5;
    private Button btnKM6;
    private Button btnKM7;
    private Button btnSCSYZW;
    private Logger logger= Logger.getLogger(this.getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_kz);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        initView();

    }

    private void initView(){
        tvfh=(TextView)findViewById(R.id.fh);
        tvfh.setOnClickListener(new onClickListener());
        tvtitle=(TextView)findViewById(R.id.title);
        tvtitle.setText("控制管理");
        btnKM1=(Button)findViewById(R.id.km1);
        btnKM1.setOnClickListener(new onClickListener());
        btnKM2=(Button)findViewById(R.id.km2);
        btnKM2.setOnClickListener(new onClickListener());
        btnKM3=(Button)findViewById(R.id.km3);
        btnKM3.setOnClickListener(new onClickListener());
        btnKM4=(Button)findViewById(R.id.km4);
        btnKM4.setOnClickListener(new onClickListener());
        btnKM5=(Button)findViewById(R.id.km5);
        btnKM5.setOnClickListener(new onClickListener());
        btnKM6=(Button)findViewById(R.id.km6);
        btnKM6.setOnClickListener(new onClickListener());
        btnKM7=(Button)findViewById(R.id.km7);
        btnKM7.setOnClickListener(new onClickListener());
        btnSCSYZW=(Button)findViewById(R.id.scsyzw);
        btnSCSYZW.setOnClickListener(new onClickListener());
    }


    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.km1:
                    logger.info("点击开门按钮1");
                    boolean bl1= HCProtocol.ST_OpenDoor();
//                    if(bl1){
//                        sendTS("门已开");
//                    }else{
//                        sendTS("开门失败");
//                    }
                    break;
                case R.id.km2:
                    logger.info("点击开门按钮2");
                    boolean bl2= HCProtocol.ST_OpenDoor();
//                    if(bl2){
//                        sendTS("门已开");
//                    }else{
//                        sendTS("开门失败");
//                    }
                    break;
                case R.id.km3:
                    logger.info("点击开门按钮3");
                    boolean bl3= HCProtocol.ST_OpenDoor();
//                    if(bl3){
//                        sendTS("门已开");
//                    }else{
//                        sendTS("开门失败");
//                    }
                    break;
                case R.id.km4:
                    logger.info("点击开门按钮4");
                    boolean bl4= HCProtocol.ST_OpenDoor();
//                    if(bl4){
//                        sendTS("门已开");
//                    }else{
//                        sendTS("开门失败");
//                    }
                    break;
                case R.id.km5:
                    logger.info("点击开门按钮5");
                    boolean bl5= HCProtocol.ST_OpenDoor();
//                    if(bl){
//                        sendTS("门已开");
//                    }else{
//                        sendTS("开门失败");
//                    }
                    break;
                case R.id.km6:
                    logger.info("点击开门按钮6");
                    boolean bl6= HCProtocol.ST_OpenDoor();
//                    if(bl){
//                        sendTS("门已开");
//                    }else{
//                        sendTS("开门失败");
//                    }
                    break;
                case R.id.km7:
                    logger.info("点击开门按钮7");
                    boolean bl7= HCProtocol.ST_OpenDoor();
//                    if(bl){
//                        sendTS("门已开");
//                    }else{
//                        sendTS("开门失败");
//                    }
                    break;

                case R.id.scsyzw:
                    btnSCSYZW.setPressed(true);
                    final AlertDialog alertDialog = new AlertDialog.Builder(KZActivity.this)
                            .setTitle("确认提示框")
                            .setMessage("确认删除所有指纹？")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    logger.info("点击并确认删除所有指纹按钮");
                                    boolean bl=HCProtocol.ST_DeleteZW(1,0);
                                    if(bl){
                                        //将人员表数据库中的所有指纹清空
                                        MyPersonDao myPersonDao = new MyPersonDao();
                                        myPersonDao.deleteAllZW();
                                        sendTS("删除所有指纹完成");
                                    }else{
                                        sendTS("删除所有指纹失败");
                                    }
                                }
                            })

                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }) .create();
                    alertDialog.show();
                    btnSCSYZW.setPressed(false);

                    break;
                case R.id.fh:
                    KZActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }

    private  void sendTS(String value){
        MyTextToSpeech.getInstance().speak(value);
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

}
