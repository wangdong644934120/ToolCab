package com.stit.toolcab.activity;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.stit.toolcab.R;
import com.stit.toolcab.utils.Cache;
import com.stit.toolcab.view.PercentCircle;

public class ProgressDialog extends Activity {

    PercentCircle percentCircle2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_dialog);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        percentCircle2 = (PercentCircle) findViewById(R.id.percentCircle2);
        percentCircle2.setTargetPercent(1);


        Cache.myHandleProgress = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                //提示信息
                if (bundle.getString("pd") != null) {
                    if(bundle.getString("pd").equals("closedpd")){
                        ProgressDialog.this.finish();
                    }else{
                        //显示提示信息
                        percentCircle2.setTargetPercent(Integer.valueOf(bundle.getString("pd")));
                        percentCircle2.update();
                    }

                }
            }
        };

    }
    protected void onDestroy() {
        super.onDestroy();
    };

}
