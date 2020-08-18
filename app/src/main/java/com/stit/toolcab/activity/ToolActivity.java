package com.stit.toolcab.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
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
import com.stit.toolcab.utils.ExpportDataBeExcel;

import java.io.File;

public class ToolActivity extends Activity {

    private TextView tvfh;
    private TextView tvtitle;
    private Button btnUp;
    private Button btnDown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_tool);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        initView();

    }
    private void initView(){
        tvfh=(TextView)findViewById(R.id.fh);
        tvfh.setOnClickListener(new onClickListener());
        tvtitle=(TextView)findViewById(R.id.title);
        tvtitle.setText("工具管理");
        btnUp=(Button)findViewById(R.id.up);
        btnUp.setOnClickListener(new onClickListener());
        btnDown=(Button)findViewById(R.id.down);
        btnDown.setOnClickListener(new onClickListener());
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
                case R.id.up:
                    File file = new File(Environment.getExternalStorageDirectory()+File.separator+"STIT"+File.separator+"1.xls");
                    if(!file.exists()){
                        Toast.makeText(ToolActivity.this, "未找到1.xls", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        int count= ExpportDataBeExcel.ImportExcelData(file);
                        if(count>=0){
                            Toast.makeText(ToolActivity.this, "上传工具库完成，个数："+count, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ToolActivity.this, "上传工具库失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.down:
                    File fileout = new File(Environment.getExternalStorageDirectory()+File.separator+"STIT"+File.separator+"down.xls");
                    boolean bl=ExpportDataBeExcel.saveExcel(fileout);
                    if(bl){
                        Toast.makeText(ToolActivity.this, "下载耗材库完成", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ToolActivity.this, "下载耗材库失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.fh:
                    ToolActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }
}
