package com.stit.toolcab.dao;

import com.stit.toolcab.db.DataBaseExec;
import com.stit.toolcab.entity.Tools;

import java.util.List;

/**
 * Created by Administrator on 2020-08-05.
 */

public class MyToolsDao {



    public void deleteAllTools(){
        String sql="delete from tools";
        DataBaseExec.execOther(sql,null);
    }
    public void addTools(List<Tools> list){
        StringBuilder sql=new StringBuilder("insert into tools (id,gg,mc,epc) values ");
        for(Tools tool : list){
            sql.append("('").append(tool.getId().toString()).append("','");
            sql.append(tool.getGg().toString()).append("','");
            sql.append(tool.getMc().toString()).append("','");
            sql.append(tool.getEpc().toString().toUpperCase()).append("'),");
        }
        sql.deleteCharAt(sql.length()-1);

        DataBaseExec.execOther(sql.toString(),null);
    }

    public List<Tools> getAllTools(){
        String sql="select * from tools";
        return DataBaseExec.execQueryForBean(sql,null,Tools.class);
    }


}
