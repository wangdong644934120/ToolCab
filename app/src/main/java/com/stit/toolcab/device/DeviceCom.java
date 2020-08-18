package com.stit.toolcab.device;

import android.os.Bundle;
import android.os.Message;


import com.stit.toolcab.utils.Cache;

import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/11/8.
 */

public class DeviceCom extends Thread {

    private Logger logger = Logger.getLogger(this.getClass());


    public void run(){
        openCom();
        byte[] byDevice=HCProtocol.ST_GetDeviceInfo();
        if(byDevice.length==0){
            logger.info("获取设备信息无返回数据");
        }else{
            System.out.println("返回数据："+byDevice);
        }
        JXDevice(byDevice);
        boolean bl=HCProtocol.ST_GetWorkModel();
        if(bl){
            logger.info("状态:盘存方式:"+(Cache.pc==0?"全部盘存":"触发盘存"));
            logger.info("状态:盘存次数:"+Cache.pccs);
        }else{
            logger.info("报警:获取工作模式失败");
        }
       new DataThread().start();
    }

    private void JXDevice(byte[] data){
        if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x11
                && data[3] == (byte) 0x05  ) {
            HashMap<String,String> map=new HashMap<String,String>();
            String gx="Ⅰ型";
            if(data[4]==0x01){
                //1型柜
                gx="Ⅰ型";
            }else if(data[4]==0x02){
                //11型柜
                gx="Ⅱ型";
            }
            //产品序列号
            Cache.cpxlh="";
            byte[] cpxlh = new byte[6];
            System.arraycopy(data, 5, cpxlh, 0, 6);
            for(byte b : cpxlh){
                Cache.cpxlh=Cache.cpxlh+DataTypeChange.getHeight4(b);
                Cache.cpxlh=Cache.cpxlh+DataTypeChange.getLow4(b);
            }
            logger.info("产品序列号："+Cache.cpxlh);

            //硬件版本号
            byte[] yjbbh=new byte[1];
            System.arraycopy(data,11,yjbbh,0,1);
            Cache.yjbbh="V"+DataTypeChange.getHeight4(yjbbh[0])+"."+DataTypeChange.getLow4(yjbbh[0]);
            logger.info("硬件版本号："+Cache.yjbbh);
            //固件版本号
            byte[] gjbbh=new byte[1];
            System.arraycopy(data,12,gjbbh,0,1);
            Cache.gjbbh="V"+DataTypeChange.getHeight4(gjbbh[0])+"."+DataTypeChange.getLow4(gjbbh[0]);
            logger.info("固件版本号："+Cache.gjbbh);

            String qygc=DataTypeChange.getBit(data[13]);
            logger.info("获取设备信息完成");
            logger.info("状态:柜体型号："+gx);
            logger.info("状态:柜层启用(最后为第一层抽)："+qygc);
        }else{
           logger.info("报警:获取设备信息失败");
        }
    }
    private void openCom(){
        try{
            HCProtocol.ST_OpenCom();
            Thread.sleep(4000);
        }catch (Exception e){
            logger.error("打开串口失败",e);

        }
    }




}
