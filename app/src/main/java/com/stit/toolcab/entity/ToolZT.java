package com.stit.toolcab.entity;

/**
 * Created by Administrator on 2020-08-03.
 */

public class ToolZT extends Tools{


    //工具id
    private String id;
    //工具规格
    private String gg;
    //工具名称
    private String mc;
    //工具epc
    private String epc;
    //工具位置
    private String wz;
    //工具借用状态
    private String jyzt;
    //工具维修状态
    private String wxzt;
    //工具报修状态
    private String bxzt;
    //工具借用人员id
    private String jypersonid;
    //借用人员姓名
    private String jyname;
    //工具借用时间
    private String jytimepoke;
    //工具报修人员id
    private String bxpersonid;
    //工具报修人员姓名
    private String bxname;
    //工具报修时间
    private String bxtimepoke;
    //工具维修人员id
    private String wxpersonid;
    //工具维修人员姓名
    private String wxname;
    //工具维修时间
    private String wxtimepoke;


    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getGg() {
        return gg;
    }

    @Override
    public void setGg(String gg) {
        this.gg = gg;
    }

    @Override
    public String getMc() {
        return mc;
    }

    @Override
    public void setMc(String mc) {
        this.mc = mc;
    }

    @Override
    public String getEpc() {
        return epc;
    }

    @Override
    public void setEpc(String epc) {
        this.epc = epc;
    }

    @Override
    public String getWz() {
        return wz;
    }

    @Override
    public void setWz(String wz) {
        this.wz = wz;
    }

    @Override
    public String getJyzt() {
        return jyzt;
    }

    @Override
    public void setJyzt(String jyzt) {
        this.jyzt = jyzt;
    }

    @Override
    public String getWxzt() {
        return wxzt;
    }

    @Override
    public void setWxzt(String wxzt) {
        this.wxzt = wxzt;
    }

    @Override
    public String getJypersonid() {
        return jypersonid;
    }

    @Override
    public void setJypersonid(String jypersonid) {
        this.jypersonid = jypersonid;
    }

    public String getJyname() {
        return jyname;
    }

    public void setJyname(String jyname) {
        this.jyname = jyname;
    }

    @Override
    public String getJytimepoke() {
        return jytimepoke;
    }

    @Override
    public void setJytimepoke(String jytimepoke) {
        this.jytimepoke = jytimepoke;
    }

    @Override
    public String getBxpersonid() {
        return bxpersonid;
    }

    @Override
    public void setBxpersonid(String bxpersonid) {
        this.bxpersonid = bxpersonid;
    }

    public String getBxname() {
        return bxname;
    }

    public void setBxname(String bxname) {
        this.bxname = bxname;
    }

    @Override
    public String getBxtimepoke() {
        return bxtimepoke;
    }

    @Override
    public void setBxtimepoke(String bxtimepoke) {
        this.bxtimepoke = bxtimepoke;
    }

    @Override
    public String getWxpersonid() {
        return wxpersonid;
    }

    @Override
    public void setWxpersonid(String wxpersonid) {
        this.wxpersonid = wxpersonid;
    }

    public String getWxname() {
        return wxname;
    }

    public void setWxname(String wxname) {
        this.wxname = wxname;
    }

    @Override
    public String getWxtimepoke() {
        return wxtimepoke;
    }

    @Override
    public void setWxtimepoke(String wxtimepoke) {
        this.wxtimepoke = wxtimepoke;
    }

    @Override
    public String getBxzt() {
        return bxzt;
    }

    @Override
    public void setBxzt(String bxzt) {
        this.bxzt = bxzt;
    }
}
