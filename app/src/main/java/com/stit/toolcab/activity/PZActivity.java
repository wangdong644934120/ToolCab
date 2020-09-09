package com.stit.toolcab.activity;

import android.app.Activity;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stit.toolcab.R;
import com.stit.toolcab.dao.PZDao;
import com.stit.toolcab.entity.PeiZhi;
import com.stit.toolcab.utils.Cache;
import com.stit.toolcab.utils.MyTextToSpeech;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PZActivity extends Activity {

    private TextView tvfh;
    private TextView tvtitle;
    private Button btnSave;
    private Button btnSaveDown;
    private TextView tvxtmc;
    private TextView tvsbbh;
    private EditText etpcjg;
    private EditText etpccs;
    private EditText edfwqdz;
    private EditText edfwqdkh;

    private byte[] bysblx=new byte[1]; //设备类型
    private byte[] bycpxlh=new byte[6]; //产品序列号
    private byte[] byyjbbh=new byte[1]; //硬件版本号
    private byte[] bygjbbh=new byte[1];//固件版本号

    private Logger logger = Logger.getLogger(this.getClass());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_pz);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        initView();
        initData();

    }


    private void initView(){
        tvfh=(TextView)findViewById(R.id.fh);
        tvfh.setOnClickListener(new onClickListener());
        tvtitle=(TextView)findViewById(R.id.title);
        tvtitle.setText("配置管理");
        etpccs=(EditText)findViewById(R.id.pccs);
        etpcjg=(EditText)findViewById(R.id.pcjg);
        btnSaveDown=(Button)findViewById(R.id.btnsavedown);
        tvxtmc=(TextView)findViewById(R.id.xtmc);
        tvsbbh=(TextView)findViewById(R.id.sbbh);
        edfwqdz=(EditText)findViewById(R.id.fwqdz);
        edfwqdkh=(EditText)findViewById(R.id.fwqdkh);
        btnSaveDown.setOnClickListener(new onClickListener());
        btnSave=(Button)findViewById(R.id.btnsave);
        btnSave.setOnClickListener(new onClickListener());
    }

    /**
     * 初始化界面显示
     */
    private void initData(){
        tvxtmc.setText(Cache.peiZhi==null?"":Cache.peiZhi.getAppname());
        tvsbbh.setText(Cache.peiZhi==null?"":Cache.peiZhi.getAppcode());
        edfwqdz.setText(Cache.peiZhi==null?"":Cache.peiZhi.getServerip());
        edfwqdkh.setText(Cache.peiZhi==null?"":Cache.peiZhi.getServerport());
        etpccs.setText(String.valueOf(Cache.pccs));
        etpcjg.setText(String.valueOf(Cache.pcjg));


    }



    /**
     * 单击事件监听
     *
     * @author dinghaoyang
     */
    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.btnsavedown:
                    btnSaveDown.setPressed(true);
                    try{

                        int pccs=1;
                        try{
                            pccs=Integer.valueOf(etpccs.getText().toString());
                        }catch (Exception e){
                        }
                        int pdjg=5;
                        try{
                            pdjg=Integer.valueOf(etpcjg.getText().toString());
                        }catch (Exception e){

                        }
                        if(pdjg<5 || pdjg>255){
                            Toast.makeText(PZActivity.this, "盘点间隔为5到255", Toast.LENGTH_LONG).show();
                            return;
                        }
//                        boolean bl1=HCProtocol.ST_SetWorkModel(lightModel,pc,pccs,pdjg);
//                        if(bl1){
//                            logger.info("下发工作模式成功");
//                        }else{
//                            logger.info("下发工作模式失败");
//                        }
//                        byte[] bydata=new byte[14];
//                        if(gx==1){
//                            bydata[0]=0x01;
//                        }else{
//                            bydata[0]=0x02;
//                        }
//                        bydata[1]=bycpxlh[0];
//                        bydata[2]=bycpxlh[1];
//                        bydata[3]=bycpxlh[2];
//                        bydata[4]=bycpxlh[3];
//                        bydata[5]=bycpxlh[4];
//                        bydata[6]=bycpxlh[5];
//                        bydata[7]=byyjbbh[0];
//                        bydata[8]=bygjbbh[0];
//                        String str="00"+(gc6.isChecked()?"1":"0")+(gc5.isChecked()?"1":"0")+(gc4.isChecked()?"1":"0")+(gc3.isChecked()?"1":"0")+(gc2.isChecked()?"1":"0")+(gc1.isChecked()?"1":"0");
//                        int da=Integer.parseInt(str,2);
//                        bydata[9]=(byte)da;
//                        boolean bl2=HCProtocol.ST_SetDeviceInfo(bydata);
//                        if(bl2){
//                            logger.info("下发设备信息成功");
//                        }else{
//                            logger.info("下发设备信息失败");
//                        }
//                        btnOK.setPressed(false);
//                        if(bl1 && bl2){
//                            Cache.zmd=lightModel;
//                            Cache.pc=pc;
//                            Cache.pccs=pccs;
//                            Toast.makeText(PZActivity.this, "下传配置完成", Toast.LENGTH_LONG).show();
//                            MyTextToSpeech.getInstance().speak("下传配置完成");
//
//                            logger.info("下传配置成功："+spDK.getSelectedItem().toString()+","+spPD.getSelectedItem().toString()+"盘存次数:"+edpdcs.getText().toString());
//                        }else{
//                            Toast.makeText(PZActivity.this, "下传配置失败", Toast.LENGTH_LONG).show();
//                            MyTextToSpeech.getInstance().speak("下传配置失败");
//                            logger.info("下传配置失败："+spDK.getSelectedItem().toString()+","+spPD.getSelectedItem().toString()+"盘存次数:"+edpdcs.getText().toString());
//                        }

                    }catch (Exception e){
                        logger.error("保存出错",e);
                    }

                    break;
                case R.id.btnsave:
                    try{
                        if(!edfwqdz.getText().toString().equals("") && !isIP(edfwqdz.getText().toString())){
                            Toast.makeText(PZActivity.this, "IP地址不合法", Toast.LENGTH_LONG).show();
                            //MyTextToSpeech.getInstance().speak("IP地址不合法");
                            return;
                        }
                        if(!edfwqdkh.getText().toString().equals("") && !isPort(edfwqdkh.getText().toString())){
                            Toast.makeText(PZActivity.this, "端口号不合法", Toast.LENGTH_LONG).show();
                            //MyTextToSpeech.getInstance().speak("端口号不合法");
                            return;
                        }
                        PeiZhi peiZhi = new PeiZhi();
                        peiZhi.setId("1");
                        peiZhi.setAppname(tvxtmc.getText().toString().trim());
                        peiZhi.setAppcode(tvsbbh.getText().toString().trim());
                        peiZhi.setServerip(edfwqdz.getText().toString().trim());
                        peiZhi.setServerport(edfwqdkh.getText().toString().trim());
                        PZDao PZDao = new PZDao();
                        PeiZhi peiZhiHave = PZDao.getPZ();
                        if(peiZhiHave==null){
                            PZDao.addPZ(peiZhi);
                        }else{
                            PZDao.updatePZ(peiZhi);
                        }
                        Cache.peiZhi=peiZhi;
                        Toast.makeText(PZActivity.this, "保存配置完成", Toast.LENGTH_LONG).show();
                        MyTextToSpeech.getInstance().speak("保存配置完成");
                        sendAPPName(tvxtmc.getText().toString());
                    }catch (Exception e){
                        logger.error("保存APP名称等信息出错",e);
                    }
                    break;
                case R.id.fh:
                    PZActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }



    public boolean isIP(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        /**
             * 判断IP格式和范围
             */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();

        //============对之前的ip判断的bug在进行判断
        if (ipAddress == true) {
            String ips[] = addr.split("\\.");

            if (ips.length == 4) {
                try {
                    for (String ip : ips) {
                        if (Integer.parseInt(ip) < 0 || Integer.parseInt(ip) > 255) {
                            return false;
                        }
                    }
                } catch (Exception e) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }

        return ipAddress;
    }

    public static boolean isPort(String portStr) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(portStr);
        if (isNum.matches() && portStr.length() < 6 && Integer.valueOf(portStr) >= 1
                && Integer.valueOf(portStr) <= 65535) {
            return true;
        }
        return false;
    }
    private  void sendAPPName(String appname){
        Message message = Message.obtain(Cache.mainHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("appname",appname);
        message.setData(data);
        Cache.mainHandle.sendMessage(message);
    }
}
