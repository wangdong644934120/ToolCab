package com.stit.toolcab.entity;


/**
 * Created by Administrator on 2020-07-26.
 */


public class Record {
    public static String BAOXIAO="0";//报销状态
    public static String ZAIGUI="1"; //在柜状态
    public static String JIEYONG="2";//借用状态
    public static String BAOXIU="3";//报修状态
    public static String WEIXIU="4";//维修状态


    private String id;

    private String toolsid;

    private String personid;

    private long time;

    private String zt;


    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToolsid() {
        return toolsid;
    }

    public void setToolsid(String toolsid) {
        this.toolsid = toolsid;
    }

    public String getPersonid() {
        return personid;
    }

    public void setPersonid(String personid) {
        this.personid = personid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getZt() {
        return zt;
    }

    public void setZt(String zt) {
        this.zt = zt;
    }
}
