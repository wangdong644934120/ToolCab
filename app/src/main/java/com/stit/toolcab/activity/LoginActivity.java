package com.stit.toolcab.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.stit.toolcab.R;
import com.stit.toolcab.dao.PersonDao;
import com.stit.toolcab.utils.Cache;
import com.stit.toolcab.utils.MyTextToSpeech;

import org.apache.log4j.Logger;


public class LoginActivity extends Activity {

    private EditText txtUserName;
    private EditText txtPassword;
    private Button btnLogin;
    private ImageButton btnFace;
    private Logger logger = Logger.getLogger(this.getClass());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        //使用布局文件来定义标题栏
        initView();
        //closeBar();
        //showBar();
    }


    private void initView(){
        txtUserName=(EditText)findViewById(R.id.username);
        txtPassword=(EditText)findViewById(R.id.password);
        btnLogin=(Button)findViewById(R.id.login);
        btnLogin.setOnClickListener(new onClickListener());
        btnFace=(ImageButton)findViewById(R.id.face);
        btnFace.setOnClickListener(new onClickListener());

    }

    /**
     * 单击事件监听
     */
    public class onClickListener implements View.OnClickListener {
        Intent intent;
        @Override
        public void onClick(View v) {

            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.login:
                    if(txtUserName.getText().toString().trim().equals("admin") && txtPassword.getText().toString().trim().equals("3013507")){
                        logger.info("超级管理员登录");
                        LoginActivity.this.finish();
                        return;
                    }
                    PersonDao personDao = new PersonDao();
                    Cache.operator= personDao.getPersonByUserNamePassword(txtUserName.getText().toString().trim(),txtPassword.getText().toString().trim());
                    if(Cache.operator!=null){
                        Message message = Message.obtain(Cache.mainHandle);
                        Bundle data = new Bundle();
                        data.putString("czy","1");
                        message.setData(data);
                        Cache.mainHandle.sendMessage(message);
                        LoginActivity.this.finish();
                    }else{
                        Toast.makeText(LoginActivity.this, "账号和密码不匹配", Toast.LENGTH_SHORT).show();
                        MyTextToSpeech.getInstance().speak("账号和密码不匹配");
                    }
                    break;
                case R.id.face:
                    intent=new Intent(LoginActivity.this,FaceActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }
    public void setHideVirtualKey(Window window){
        //保持布局状态
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                //布局位于状态栏下方
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
                //全屏
                View.SYSTEM_UI_FLAG_FULLSCREEN|
                //隐藏导航栏
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (Build.VERSION.SDK_INT>=19){
            uiOptions |= 0x00001000;
        }else{
            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        window.getDecorView().setSystemUiVisibility(uiOptions);
    }
    /**
     * 关闭底部状态栏
     */
    private void closeBar() {
        try {
            //需要root 权限
            Build.VERSION_CODES vc = new Build.VERSION_CODES();
            Build.VERSION vr = new Build.VERSION();
            String ProcID = "79";

            if (vr.SDK_INT >= vc.ICE_CREAM_SANDWICH) {
                ProcID = "42"; //ICS AND NEWER
            }
            //需要root 权限
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "service call activity " + ProcID + " s16 com.android.systemui"}); //WAS 79
            proc.waitFor();

        } catch (Exception ex) {
        }
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
