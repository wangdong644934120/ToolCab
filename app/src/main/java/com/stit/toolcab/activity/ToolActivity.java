package com.stit.toolcab.activity;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.stit.toolcab.R;
import com.stit.toolcab.dao.ToolsDao;
import com.stit.toolcab.dao.ToolsStoreDao;
import com.stit.toolcab.device.HCProtocol;
import com.stit.toolcab.utils.Cache;
import com.stit.toolcab.utils.ExpportDataBeExcel;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ToolActivity extends Activity {

    private TextView tvfh;
    private TextView tvtitle;
    private Button btnUp;
    private Button btnDown;
    private Button btnCS;
    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_tool);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        initView();
        Cache.myHandleHCCS = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                //提示信息
                if (bundle.getString("cshc") != null) {
                    logger.info("初始工具获取标签原始个数："+Cache.HCCSMap.size());
                    //先删除原有标签
                    ToolsDao toolsDao = new ToolsDao();
                    toolsDao.deleteAllTools();
                    //根据标签EPC更新位置
                    ToolsStoreDao toolsStoreDao = new ToolsStoreDao();

                    if(!Cache.HCCSMap.isEmpty()){
                        Set<String> cards = Cache.HCCSMap.keySet();
                        List<HashMap<String,String>> list=toolsStoreDao.getAllToolsByHCCS(cards);
                        if(list!=null && !list.isEmpty()){
                            for(String card : cards){
                                for(HashMap<String,String> map : list){
                                    if(card.toUpperCase().equals(map.get("epc").toString().toUpperCase())){
                                        map.put("wz",Cache.HCCSMap.get(card).toString());
                                        break;
                                    }
                                }
                            }
                            toolsDao.updateAllToolsWZ(list);
                        }

                    }
                    sendTotal();
                    Toast.makeText(ToolActivity.this, "初始柜内工具完成,个数："+Cache.HCCSMap.size(), Toast.LENGTH_SHORT).show();
                    Cache.HCCSMap.clear();
                    Cache.getHCCS=0;
                }

            }
        };
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
        btnCS=(Button)findViewById(R.id.cs);
        btnCS.setOnClickListener(new onClickListener());
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
                case R.id.cs:
                    Cache.getHCCS=1;

                    //下发盘点指令
                    if(HCProtocol.ST_GetAllCard()){
                        logger.info("下发指令盘存所有成功");
                    }else{
                        Cache.getHCCS=0;
                        logger.info("下发指令盘存所有失败");
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

    private  void sendTotal(){
        Message message = Message.obtain(Cache.mainHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。

        data.putString("initTotal","1");
        message.setData(data);
        Cache.mainHandle.sendMessage(message);
    }
}
