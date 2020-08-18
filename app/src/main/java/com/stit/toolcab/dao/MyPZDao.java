package com.stit.toolcab.dao;

import com.stit.toolcab.db.DataBaseExec;
import com.stit.toolcab.entity.PeiZhi;

import java.util.List;

/**
 * Created by Administrator on 2020-08-05.
 */

public class MyPZDao {

    public PeiZhi getPZ(){
        String sql = "select * from peizhi";
        List<PeiZhi> list = DataBaseExec.execQueryForBean(sql,null,PeiZhi.class);
        if(list!=null && !list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    public void addPZ(PeiZhi peiZhi){
        String sql="insert into peizhi (id,appname,appcode,serverip,serverport) values (?,?,?,?,?)";
        Object[] args = new Object[]{peiZhi.getId(),peiZhi.getAppname(),peiZhi.getAppcode(),peiZhi.getServerip(),peiZhi.getServerport()};
        DataBaseExec.execOther(sql,args);
    }

    public void updatePZ(PeiZhi peiZhi){
        String sql="update peizhi set appname=?,appcode=?,serverip=?,serverport=? where id=?";
        Object[] args = new Object[]{peiZhi.getAppname(),peiZhi.getAppcode(),peiZhi.getServerip(),peiZhi.getServerport(),peiZhi.getId()};
        DataBaseExec.execOther(sql,args);
    }



}
