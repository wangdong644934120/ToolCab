package com.stit.toolcab.device;

import android.os.Bundle;
import android.os.Message;


import com.stit.toolcab.dao.PersonDao;
import com.stit.toolcab.dao.ToolsDao;
import com.stit.toolcab.entity.Person;
import com.stit.toolcab.entity.Record;
import com.stit.toolcab.entity.Tools;
import com.stit.toolcab.utils.Cache;
import com.stit.toolcab.utils.MyTextToSpeech;

import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/12/12.
 */

public class DataThread extends Thread {
    private Logger logger = Logger.getLogger(this.getClass());

    private HashMap<String,String> map=new HashMap<String,String>();
    private int openPDFlag=0;
    private long pdstart=0l;
    private boolean dealFlag=false;
    private String pcjd="0";

    public void run(){

        while(Cache.deviceCommunication){
            try {
                    HashMap<String, String> mapWorkState = HCProtocol.ST_GetWorkState();
                    if (mapWorkState.get("skq") != null) {

                    }
                    if (mapWorkState.get("zwcgq") != null) {
                        alaZWCGQ(mapWorkState.get("zwcgq").toString());
                    }
                    if (mapWorkState.get("mztcgq") != null ) {
                       alaMZTCGQ(mapWorkState.get("mztcgq").toString());
                    }
                    if (mapWorkState.get("dks") != null ) {

                    }
                    if (mapWorkState.get("hwxckg") != null ) {
                        alaHWXCKG(mapWorkState.get("hwxckg").toString());
                    }
                    if (mapWorkState.get("zmd") != null ) {

                    }
                    if (mapWorkState.get("rfid") != null ) {
                        alaRFID(mapWorkState.get("rfid").toString());
                    }

                }catch(Exception e){
                    logger.error("获取数据出错", e);
                }

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    logger.error("获取数据线程等待出错", e);
                }


            }
            logger.info("退出设备通讯线程");
    }
    //刷卡器
    private void alaSKQ(String skq){

    }
    //指纹传感器
    private void alaZWCGQ(String zwcgq){
        zwcgq = zwcgq.substring(6, 8);

        if (zwcgq.equals("00")) {
            //未检测到指纹
        }
        if (zwcgq.equals("01")) {
            //指纹识别中忽略
        }
        if (zwcgq.equals("10")) {
            //指纹匹配失败
            logger.info("请重按手指");
            MyTextToSpeech.getInstance().speak("请重按手指");
        }
        //应该为11
        if (zwcgq.equals("11")) {
            logger.info("指纹模块有动作："+zwcgq);
            MyTextToSpeech.getInstance().speak("识别完成");
            //指纹匹配成功，下发获取指纹编号
            //刷卡器有动作，下发获取刷卡信息指令
            String card1=HCProtocol.ST_GetUser(1);
            logger.info("获取到指纹编号十六进制："+card1);
            String card2= String.valueOf(Long.parseLong(card1,  16));
            logger.info("指纹编号转十进制结果："+card2);
            //判断是否发送到第三方平台
            //String sendValue="{\"order\":\"power\",\"type\":\"1\",\"code\":\"" + Cache.appcode + "\",\"number\":\""+ UUID.randomUUID().toString()+"\",\"data\":\""+card+"\"}";

            if(Cache.isPCNow==1){
                return;
            }
            logger.info("单机使用");
            PersonDao personDao = new PersonDao();
            Person person= personDao.getPersonByCode(card1,card2);
            if(person!=null){
                Cache.operator=person;
                sendCZY(person.getName());
                MyTextToSpeech.getInstance().speak("欢迎"+person.getName());
            }else{
                MyTextToSpeech.getInstance().speak("无操作权限");
            }
//            List<HashMap<String,String>> list=personDao.getPersonByCardOrZW(card);
//            if(list !=null && list.size()>0){
//                //下发开门指令
//                if(HCProtocol.ST_OpenDoor()){
//                    logger.info("下发开门成功");
//                    sendCZY(list.get(0).get("name"));
//                    MyTextToSpeech.getInstance().speak(list.get(0).get("name")+"开门完成");
//                }else{
//                    logger.info("下发开门失败");
//                }
//
//            }else{
//                logger.info("未从数据库查到人员信息");
//            }
        }

//        //指纹传感器，下发获取指纹信息

    }
    //门状态传感器 开门 监控电控锁状态   关门  监控门状态
    private void alaMZTCGQ(String mztcgq){

//        mztcgq=mztcgq.substring(6,8);
//        if(mztcgq.contains("1")){
//
//            //初始化第一次判断
//            if(Cache.mztcgq==2){
//                logger.info("设置门开");
//                //设置门开
//                updateUI("men","","1");
//                Cache.mztcgq=1;
//
//            }
//            //门开
//            if(Cache.mztcgq==0){
//                logger.info("设置门开");
//                //设置门开
//                updateUI("men","","1");
//                Cache.mztcgq=1;
//            }
//
//        }else if(mztcgq.equals("00")){
//            //初始化第一次判断
//            if(Cache.mztcgq==2){
//                logger.info("设置门关");
//                //设置门关
//                updateUI("men","","0");
//                Cache.mztcgq=0;
//            }
//            //门关
//            if(Cache.mztcgq==1){
//                logger.info("设置门关");
//                if(Cache.myHandleAccess!=null){
//                    logger.info("关闭存取确认界面");
//                    //存取确认界面为打开状态,将其关闭
//                    Message message = Message.obtain(Cache.myHandleAccess);
//                    Bundle bund = new Bundle();
//                    bund.putString("ui","close");
//                    message.setData(bund);
//                    Cache.myHandleAccess.sendMessage(message);
//                }
//                //设置门关
//                updateUI("men","","0");
//                Cache.mztcgq=0;
//                //门关后如果启用锁屏了，并且是触发盘存模式，红外都没有被触发，那么直接锁屏
//                if(Cache.myHandleLockScreen==null){
//                    if(Cache.lockScreen.equals("1") && Cache.pc==1 && !Cache.hwxc1 && !Cache.hwxc2 && !Cache.hwxc3 && !Cache.hwxc4 && !Cache.hwxc5 && !Cache.hwxc6){
//                        //开启锁屏
//                        logger.info("开启锁屏");
//                        Message message = Message.obtain(Cache.myHandle);
//                        Bundle bund = new Bundle();
//                        bund.putString("ui","lock");
//                        message.setData(bund);
//                        Cache.myHandle.sendMessage(message);
//                    }
//                }
//            }



        //}
        //门状态传感器，下发获取门状态
    }
    //电控锁
    private void alaDKS(String dks){
    }
    //红外行程开关
    private void alaHWXCKG(String hwxckg){
        String hwxc7=hwxckg.substring(1,2);
        String hwxc6=hwxckg.substring(2,3);
        String hwxc5=hwxckg.substring(3,4);
        String hwxc4=hwxckg.substring(4,5);
        String hwxc3=hwxckg.substring(5,6);
        String hwxc2=hwxckg.substring(6,7);
        String hwxc1=hwxckg.substring(7,8);
        if(Cache.hwxc1){
            //原红外触发状态，现红外关闭状态
            if(hwxc1.equals("0")){
                logger.info("设置红外1未触发");
                updateUI("hwxc","1","0");
                Cache.hwxc1=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc1.equals("1")){
                Cache.cfpdcs.add("1");
                logger.info("设置红外1触发");
                updateUI("hwxc","1","1");
                Cache.hwxc1=true;
            }
        }
        if(Cache.hwxc2){
            //原红外触发状态，现红外关闭状态
            if(hwxc2.equals("0")){
                logger.info("设置红外2未触发");
                updateUI("hwxc","2","0");
                Cache.hwxc2=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc2.equals("1")){
                Cache.cfpdcs.add("2");
                logger.info("设置红外2触发");
                updateUI("hwxc","2","1");
                Cache.hwxc2=true;
            }
        }
        if(Cache.hwxc3){
            //原红外触发状态，现红外关闭状态
            if(hwxc3.equals("0")){
                logger.info("设置红外3未触发");
                updateUI("hwxc","3","0");
                Cache.hwxc3=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc3.equals("1")){
                Cache.cfpdcs.add("3");
                logger.info("设置红外3触发");
                updateUI("hwxc","3","1");
                Cache.hwxc3=true;
            }
        }
        if(Cache.hwxc4){
            //原红外触发状态，现红外关闭状态
            if(hwxc4.equals("0")){
                logger.info("设置红外4未触发");
                updateUI("hwxc","4","0");
                Cache.hwxc4=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc4.equals("1")){
                Cache.cfpdcs.add("4");
                logger.info("设置红外4触发");
                updateUI("hwxc","4","1");
                Cache.hwxc4=true;
            }
        }
        if(Cache.hwxc5){
            //原红外触发状态，现红外关闭状态
            if(hwxc5.equals("0")){
                logger.info("设置红外5未触发");
                updateUI("hwxc","5","0");
                Cache.hwxc5=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc5.equals("1")){
                Cache.cfpdcs.add("5");
                logger.info("设置红外5触发");
                updateUI("hwxc","5","1");
                Cache.hwxc5=true;
            }
        }
        if(Cache.hwxc6){
            //原红外触发状态，现红外关闭状态
            if(hwxc6.equals("0")){
                logger.info("设置红外6未触发");
                updateUI("hwxc","6","0");
                Cache.hwxc6=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc6.equals("1")){
                Cache.cfpdcs.add("6");
                logger.info("设置红外6触发");
                updateUI("hwxc","6","1");
                Cache.hwxc6=true;
            }
        }
    }
    //照明灯
    private void alaZMD(String zmd){


    }
    //RFID
    private void alaRFID(String rfid){
        //RFID读写器
        String zt = rfid.substring(0,2);
        String data=rfid.substring(2,4);
        String jd=rfid.substring(4,8);

        if(zt.equals("01")){
            Cache.isPCNow=1;
            //正在盘存标签
            dealFlag=true;
            if(data.equals("00")){
                //无标签数据初始
                //无标签数据，打开盘存进度
                openJD();
            }else if(data.equals("01")){
                //有标签数据
                logger.info("正在盘存标签有标签数据");
                openJD();
                getCard();
            }
            //更新进度
            updateJD(jd,true);

        }else if(zt.equals("00")){
            if(dealFlag){

                dealFlag=false;
                //处理器标签数据
                logger.info("标签盘存结束，开始处理数据");
                //有标签数据
                openJD();
                logger.info("标签盘存结束，耗时："+(System.currentTimeMillis()-pdstart));
                //更新进度
                updateJD(jd,false);
                //关闭盘存进度
                closeJD();

                logger.info("获取标签个数："+map.size());

                //对标签数据进行处理
                if(Cache.getHCCS==0){
                    //0--关门盘存
                    if(Cache.external){
                        Cache.listOperaOut.clear();
                        Cache.listOperaSave.clear();
                        //打开耗材确认界面
                        Message message = Message.obtain(Cache.mainHandle);
                        Bundle bund = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
                        bund.putString("ui","access");
                        message.setData(bund);
                        Cache.mainHandle.sendMessage(message);
                        //sendExternalProduct("product");
                    }else{
                        HashMap<String,String> mapBQ= (HashMap<String,String>)map.clone();
                        map.clear();

                        new DataDeal(mapBQ).start();
                    }
                }else if(Cache.getHCCS==1){
                    //1-耗材初始时要数据
                    Cache.HCCSMap=(HashMap<String,String>)map.clone();
                    map.clear();
                    sendHCCS();
                    Cache.getHCCS=0;
                }else if(Cache.getHCCS==2){
                    //2-主界面盘点要数据
                    if(Cache.external){
                        //打开耗材统计界面
                        Message message = Message.obtain(Cache.mainHandle);
                        Bundle bund = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
                        bund.putString("ui","access");
                        message.setData(bund);
                        Cache.mainHandle.sendMessage(message);
                        //连接第三方平台
                        //sendExternalProduct("product");
                    }else{
                        //从本地数据库读取数据进行处理
//                        Cache.HCCSMap=(HashMap<String,String>)map.clone();
//                        map.clear();
                        HashMap<String,String> mapBQ= (HashMap<String,String>)map.clone();
                        map.clear();

                        new DataDeal(mapBQ).start();
                    }
                    Cache.getHCCS=0;
                } else if(Cache.getHCCS==3){
                    //加载界面盘点所有耗材
                    if(Cache.external){
                        //sendExternalProduct("total");
                    }
                    Cache.getHCCS=0;
//                    if(Cache.lockScreen.equals("0")){
//                        logger.info("未配置锁屏");
//                        return;
//                    }
//                    if(Cache.mztcgq==1){
//                        //门开，无需锁屏
//                        logger.info("当前门状态为开，无需锁屏");
//                    }else{
//                        logger.info("当前门状态为关，开启锁屏");
//                        Message message = Message.obtain(Cache.myHandle);
//                        Bundle bund = new Bundle();
//                        bund.putString("ui","lock");
//                        message.setData(bund);
//                        Cache.myHandle.sendMessage(message);
//                    }
                }

            }
        }

    }


    //获取标签数据
    private void getCard(){
        //有标签数据
        while(true){
            HashMap<String,String> mapSingle=HCProtocol.ST_GetCard();
            logger.info("本次从底层获取标签个数:"+mapSingle.size());
            Set<String> keysCard=mapSingle.keySet();
            for(String card : keysCard){
                logger.info("EPC："+card+" 位置："+mapSingle.get(card));
            }
            //todo解析标签ID及位置，添加到map中
            map.putAll(mapSingle);
            if(mapSingle.isEmpty()){
                break;
            }
        }
    }

    /**
     * 更新盘存进度
     * @param jd
     */
    private void updateJD(String jd, boolean isJ10){

        //更新进度
        BigInteger bi = new BigInteger(jd, 2);
        int dqcs = Integer.parseInt(bi.toString());
        int zcs=Cache.pccs;
        int zjd=(int)((double)dqcs/(double)zcs*100);
        if(isJ10){
            if(zjd>=99){
                zjd=99;
            }
        }else{
            zjd=100;
        }
        if(!String.valueOf(zjd).equals(pcjd)){
            pcjd= String.valueOf(zjd);
            logger.info("开始更新进度:"+pcjd);
            sendPD(pcjd);
        }
    }
    //打开盘存进度提示
    private void openJD(){
        if(openPDFlag==0){
            sendOpenPD("openpd");
            pdstart= System.currentTimeMillis();
            openPDFlag=1;
            MyTextToSpeech.getInstance().speak("正在读取请稍候");
        }
    }
    //关闭盘存进度
    private void closeJD(){
        if(openPDFlag==1){
            Cache.isPCNow=0;
            logger.info("关闭进度条");
            sendPD("closedpd");
            openPDFlag=0;
            MyTextToSpeech.getInstance().speak("读取结束");
        }
    }
//    //发送操作员
    private  void sendCZY(String value){
        Message message = Message.obtain(Cache.mainHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("czy",value);
        message.setData(data);
        Cache.mainHandle.sendMessage(message);
    }
    //发送红外状态
//    private  void sendZT(String value){
//        Message message = Message.obtain(Cache.myHandle);
//        Bundle data = new Bundle();
//        if(value.equals("1")){
//            value="0";
//        }else if(value.equals("0")){
//            value="1";
//        }
//        data.putString("hw",value);
//        message.setData(data);
//        Cache.myHandle.sendMessage(message);
//    }
    //打开盘点界面
    private  void sendOpenPD(String value){
        Message message = Message.obtain(Cache.mainHandle);
        Bundle data = new Bundle();
        data.putString("pd",value);
        message.setData(data);
        Cache.mainHandle.sendMessage(message);
    }
//    //发送近效期
//    private  void sendJXQ(){
//        Message message = Message.obtain(Cache.myHandle);
//        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
//        data.putString("initJXQ","1");
//        message.setData(data);
//        Cache.myHandle.sendMessage(message);
//    }
//    //发送盘点进度
    private  void sendPD(String value){
        if(Cache.myHandleProgress==null){
            return;
        }
        Message message = Message.obtain(Cache.myHandleProgress);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("pd",value);
        message.setData(data);
        Cache.myHandleProgress.sendMessage(message);
    }

    //工具确认界面
    private void sendOpenAccess(){
        if(Cache.mainHandle!=null){
            Message message = Message.obtain(Cache.mainHandle);
            Bundle data = new Bundle();
            data.putString("ui","access");
            message.setData(data);
            Cache.mainHandle.sendMessage(message);
        }
    }
//
//    //盘点主界面
//    private  void sendPDZJM(){
//        try{
//            //打开盘点主界面
//            Message message = Message.obtain(Cache.myHandle);
//            Bundle data = new Bundle();
//            data.putString("pdzjm","1");
//            message.setData(data);
//            Cache.myHandle.sendMessage(message);
///*            for(int i=0;i<30;i++){
//                if(Cache.myHandlePD==null){
//                    Thread.sleep(100);
//                    continue;
//                }else{
//                    break;
//                }
//            }
//
//            //更新主界面数据
//            if(Cache.myHandlePD!=null){
//                message = Message.obtain(Cache.myHandlePD);
//                Bundle bund = new Bundle();
//                bund.putString("show","1");
//                message.setData(bund);
//                Cache.myHandlePD.sendMessage(message);
//            }*/
//        }catch (Exception e){
//            logger.error("打开盘点主界面出错",e);
//        }
//    }
//    //发送关闭锁屏
//    private  void sendCloseLockScreen(){
//        if(Cache.myHandleLockScreen!=null){
//            Message message = Message.obtain(Cache.myHandleLockScreen);
//            Bundle data = new Bundle();
//
//            data.putString("close","1");
//            message.setData(data);
//            Cache.myHandleLockScreen.sendMessage(message);
//        }
//
//    }

    /**
     * 更新界面控件
     * @param type  类型 men deng  hwxc
     * @param wz  针对红外、行程开关
     * @param zt  1 触发，0关闭触发
     */
    private  void updateUI(String type, String wz, String zt){
        Message message = Message.obtain(Cache.mainHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("type",type);
        data.putString("wz",wz);
        data.putString("zt",zt);
        message.setData(data);
        Cache.mainHandle.sendMessage(message);
    }
//
    class DataDeal extends Thread {
        HashMap<String,String> mapDeal=null;
        public DataDeal(HashMap<String,String> mapDeal){
            this.mapDeal=mapDeal;
        }
        public void run(){
            //Cache.listPR.clear();
            ToolsDao toolsDao = new ToolsDao();
           List<Tools> list =null;
            if(Cache.pc==0){
                //全部盘存
                list=toolsDao.getAllTools();
            }else if(Cache.pc==1){
                //触发盘存
                list=toolsDao.getToolsByCFHWXC(Cache.cfpdcs);
            }
//           if(Cache.sdpdcs.equals("0")){
//               //不是手动盘点
//               if(Cache.pc==0){
//                   //全部盘存
//                   list=productDao.getAllProduct();
//               }else if(Cache.pc==1){
//                   //触发盘存
//                   list=productDao.getPorductByCFHWXC(Cache.cfpdcs);
//               }
//           }else{
//               //手动盘点
//               list=productDao.getPorductBySDHWXC(Cache.sdpdcs);
//           }
//           if(Cache.cfpdcs.equals("0")){
//                list=productDao.getAllProduct();
//           }else{
//               list=productDao.getPorductByHWXC(Cache.cfpdcs);
//           }
            //Cache.sdpdcs="0";
            Cache.cfpdcs.clear();
           Set<String> dealKeys=mapDeal.keySet();//设备上报
           HashMap<String,String> mapSave=new HashMap<String,String>();
           if(Cache.operatortype==0){
               for(Tools toolsdb : list){
                   //取出标签(设备未读到,数据库是在柜，维修状态是正常)
                   if( !dealKeys.contains(toolsdb.getEpc()) && toolsdb.getJyzt().equals(Record.JYZAIGUI) ){
                       //标签被取出
                       //mapSave.put(map.get("card").toString(),"0");
                       Cache.listOperaOut.add(toolsdb);

                   }
                   //存放标签(设备读到，数据库里是借用状态)
                   if(dealKeys.contains(toolsdb.getEpc()) && toolsdb.getJyzt().equals(Record.JYJIEYONG)){
                       //标签被存放
                       Cache.listOperaSave.add(toolsdb);
                   }
                   //标签未动
                   //if(dealKeys.contains(toolsdb.getEpc()) && toolsdb.getJyzt().equals(Record.JYZAIGUI)){
//                   if(!mapDeal.get(toolsdb.getEpc()).equals(toolsdb.getWz())){
//                       //标签位置更换
//                      // mapSave.put(map.get("card").toString(),mapDeal.get(map.get("card").toString()));
//                       //Cache.listOperaOut.add(toolsdb);
//                       //Cache.listOperaSave.add(getProduct(map.get("pp").toString(),map.get("zl").toString(),map.get("gg").toString(),"存放",mapDeal.get(map.get("card").toString()).toString()));
//                   }
                   //}
               }
           }else if(Cache.operatortype==1){
               for(Tools toolsdb : list){
                   //取出标签
                   if( !dealKeys.contains(toolsdb.getEpc())  && toolsdb.getJyzt().equals(Record.JYZAIGUI) ){
                       //标签被取出
                       //mapSave.put(map.get("card").toString(),"0");
                       Cache.listOperaOut.add(toolsdb);

                   }
                   //存放标签
                   if(dealKeys.contains(toolsdb.getEpc()) && (toolsdb.getJyzt().equals(Record.JYJIEYONG) || toolsdb.getWxzt().equals(Record.WXWEIXIU))){
                       //标签被存放
                       Cache.listOperaSave.add(toolsdb);
                   }
                   //标签未动
                   //if(dealKeys.contains(toolsdb.getEpc()) && toolsdb.getWxpersonid().equals("")){
//                   if(!mapDeal.get(toolsdb.getEpc()).equals(toolsdb.getWz())){
//                       //标签位置更换
//                      // mapSave.put(map.get("card").toString(),mapDeal.get(map.get("card").toString()));
//                       //Cache.listOperaOut.add(toolsdb);
//                       //Cache.listOperaSave.add(getProduct(map.get("pp").toString(),map.get("zl").toString(),map.get("gg").toString(),"存放",mapDeal.get(map.get("card").toString()).toString()));
//                   }
                   //}
               }
           }

           //界面显示内容
            //startRecord();
            //数据库更新内容
//            Set<String> updatesKey=mapSave.keySet();
//            for(String key : updatesKey){
//                productDao.updateProductWZ(mapSave.get(key).toString(),key);
//            }
            //初始化近效期图示
            //sendJXQ();
           sendOpenAccess();
        }
    }

//    /**
//     * 添加指纹、刷卡事件记录
//     * @param code
//     * @param type
//     */
//    private void addZWSKEvent(String code, String type){
//        Event event = new Event();
//        event.setId(UUID.randomUUID().toString());
//        event.setCode(code);
//        event.setEventType(type);
//        event.setContent(code.equals("")?"失败":"成功");
//        event.setTime(System.currentTimeMillis());
//        EventDao eventDao=new EventDao();
//        eventDao.addEvent(event);
//    }
//
//
//    private  void startRecord(){
//        Message message = Message.obtain(Cache.myHandle);
//        Bundle data = new Bundle();
//        data.putString("record","1");
//        message.setData(data);
//        Cache.myHandle.sendMessage(message);
//    }
//
//
//    private  void sendKH(String value){
//        if(Cache.myHandlePerson ==null){
//            logger.info("handle卡号发送失败");
//            return;
//        }
//        Message message = Message.obtain(Cache.myHandlePerson);
//        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
//
//        data.putString("kh",value);
//        message.setData(data);
//        Cache.myHandlePerson.sendMessage(message);
//    }
//
    private  void sendHCCS(){
        if(Cache.myHandleHCCS==null){
            logger.info("初始工具发送失败");
            return;
        }
        Message message = Message.obtain(Cache.myHandleHCCS);
        Bundle data = new Bundle();

        data.putString("cshc","1");
        message.setData(data);
        Cache.myHandleHCCS.sendMessage(message);
    }
//
//    private boolean sendExternal(String sendValue){
//        boolean bl=false;
//        //发送数据到第三方平台
//       if(SocketClient.socket!=null){
//           bl=SocketClient.send(sendValue);
//       }
//        return bl;
//    }
//
    private Tools getTools(String gg, String mc, String epc, String wz){
        Tools tools = new Tools();
        tools.setGg(gg);
        tools.setMc(mc);
        tools.setEpc(epc);
        tools.setWz(wz);
        return tools;
    }
//
//    private void colseScreen(){
//        if(Cache.lockScreen.equals("1")){
//            if(Cache.myHandleLockScreen==null){
//                logger.info("handle关闭锁屏发送失败");
//                return;
//            }
//            Message message = Message.obtain(Cache.myHandleLockScreen);
//            Bundle bund = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
//            bund.putString("close","ok");
//            message.setData(bund);
//            Cache.myHandleLockScreen.sendMessage(message);
//        }
//    }
}
