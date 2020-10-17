package com.stit.toolcab.entity;


/**
 * Created by Administrator on 2020-07-26.
 */


public class Record {
    public static String BAOXIAO="0";//报销状态
    public static String JYZAIGUI="1"; //借用--在柜状态
    public static String JYJIEYONG="2";//借用--借用状态

    public static String BXZHENGCHANG="3";//报修--正常状态
    public static String BXBAOXIU="4";//报修--报修状态

    public static String WXZHENGCHANG="5";//维修--正常状态
    public static String WXWEIXIU="6";//维修--维修状态

    private String id;
    private String name;
    private String code;
    private String starttime;
    private String zt;
    private String mc;
    private String gg;
    private String epc;
    private String endtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getZt() {
        return zt;
    }

    public void setZt(String zt) {
        this.zt = zt;
    }

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public String getGg() {
        return gg;
    }

    public void setGg(String gg) {
        this.gg = gg;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }
}
