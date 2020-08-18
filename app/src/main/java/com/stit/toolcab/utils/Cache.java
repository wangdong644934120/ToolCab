package com.stit.toolcab.utils;

import android.content.Context;
import android.os.Handler;

import com.stit.toolcab.entity.PeiZhi;
import com.stit.toolcab.entity.Person;
import com.stit.toolcab.entity.ToolZT;

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

    public static List<ToolZT> listJY=null; //借用信息缓存
    public static List<ToolZT> listBX=null; //报修信息缓存
    public static List<ToolZT> listWX=null; //维修信息缓存

    public static String gx="Ⅰ型"; //柜型
    public static String cpxlh="";//产品序列号
    public static String yjbbh="";//硬件版本号
    public static String gjbbh="";//固件版本号
    public static String apkversion="";//apk版本号
    public static int pc=1; //盘存方式  0-全盘，1-触发

    public static boolean deviceCommunication=true;
    public static int isPCNow=0;//正在盘存标签，0-未盘存，1-正在盘存
    public static boolean zwlrNow=false;   //正在录入指纹

}
