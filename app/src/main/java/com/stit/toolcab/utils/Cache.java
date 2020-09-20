package com.stit.toolcab.utils;

import android.content.Context;
import android.os.Handler;

import com.stit.toolcab.camera.CWGLDisplay;
import com.stit.toolcab.dao.ToolsDao;
import com.stit.toolcab.entity.PeiZhi;
import com.stit.toolcab.entity.Person;
import com.stit.toolcab.entity.ToolZT;
import com.stit.toolcab.entity.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2020-07-28.
 */

public class Cache {
    public static Context myContext;
    public static int pccs=5;   //盘存次数
    public static int pcjg=5;   //盘存时间间隔
    public static PeiZhi peiZhi;   //app名称

    public static Person operator;  //操作员
    public static Handler mainHandle; //主界面的handle
    public static Handler myHandleProgress;//更新进度条的handle
    public static Handler myHandleDevice;//设备信息界面显示handle
    public static Handler myHandlePerson; //人员管理界面handle
    public static Handler myHandleHCCS;//耗材初始化界面handle
    public static Handler myHandleAccess;//确认界面handle

    public static List<ToolZT> listJY=null; //借用信息缓存
    public static List<ToolZT> listBX=null;//报修信息缓存
    public static List<ToolZT> listWX=null; //维修信息缓存


    public static String gx="Ⅰ型"; //柜型
    public static String cpxlh="";//产品序列号
    public static String yjbbh="";//硬件版本号
    public static String gjbbh="";//固件版本号
    public static String apkversion="";//apk版本号
    public static int pc=0; //盘存方式  0-全盘，1-触发

    public static boolean deviceCommunication=true;
    public static int isPCNow=0;//正在盘存标签，0-未盘存，1-正在盘存
    public static boolean zwlrNow=false;   //正在录入指纹
    public static List<String> cfpdcs=new ArrayList<String>(); //触发盘点层数 0-全部盘存，1-只盘存第一层,2...

    public static int getHCCS=0;  //0--关门盘存，1-耗材初始时要数据线，2-主界面盘点要数据,3-加载界面盘点所有耗材
    public static boolean external=false;  //是否挂接第三方平台
    public static List<Tools> listOperaSave=new ArrayList<Tools>();  //存操作缓存
    public static List<Tools> listOperaOut=new ArrayList<Tools>();  //取操作缓存
    //public static List<Tools> listYJBX = new ArrayList<>();//报销工具缓存
    public static HashMap<String,String> HCCSMap=new HashMap<String,String>(); //key-card,value-wz

    public static boolean hwxc1=false;  //界面红外触发状态
    public static boolean hwxc2=false;
    public static boolean hwxc3=false;
    public static boolean hwxc4=false;
    public static boolean hwxc5=false;
    public static boolean hwxc6=false;
    public static boolean hwxc7=false;

    /**
     * 初始化报修工具
     */
    public static void initBXGJ(){
        ToolsDao toolsDao = new ToolsDao();
        toolsDao.getBXGJ();
    }
}
