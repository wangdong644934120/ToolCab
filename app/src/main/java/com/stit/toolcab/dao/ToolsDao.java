package com.stit.toolcab.dao;

import com.stit.toolcab.db.DataBaseExec;
import com.stit.toolcab.entity.Tools;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2020-08-05.
 */

public class ToolsDao {

    /**
     * 删除所有工具
     */
    public void deleteAllTools(){
        String sql="delete from tools";
        DataBaseExec.execOther(sql,null);
    }

    /**
     * 批量添加工具
     * @param list
     */
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

    /**
     * 获取所有工具
     * @return
     */
    public List<Tools> getAllTools(){
        String sql="select * from tools";
        return DataBaseExec.execQueryForBean(sql,null,Tools.class);
    }

    /**
     * 清空标签位置
     */
    public void clearWZ(){
        String sql="update tools set wz=''";
        DataBaseExec.execOther(sql,null);
    }

    /**
     * 更新所有工具位置
     * @param list
     */
    public void updateAllToolsWZ(List<HashMap<String,String>> list){
        StringBuilder sql=new StringBuilder("insert into tools (id,gg,mc,epc,wz) values ");
        for(HashMap<String,String> map : list){
            sql.append("('").append(map.get("id").toString()).append("','");
            sql.append(map.get("gg").toString()).append("','");
            sql.append(map.get("mc").toString()).append("','");
            sql.append(map.get("epc").toString().toUpperCase()).append("','");
            sql.append(map.get("wz").toString()).append("'),");
        }
        sql.deleteCharAt(sql.length()-1);

        DataBaseExec.execOther(sql.toString(),null);
    }


}
