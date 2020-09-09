package com.stit.toolcab.dao;

import com.stit.toolcab.db.DataBaseExec;
import com.stit.toolcab.entity.Tools;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2020-08-05.
 */

public class ToolsStoreDao {

    /**
     * 删除所有工具
     */
    public void deleteAllTools(){
        String sql="delete from toolsstore";
        DataBaseExec.execOther(sql,null);
    }

    /**
     * 批量添加工具
     * @param list
     */
    public void addTools(List<Tools> list){
        StringBuilder sql=new StringBuilder("insert into toolsstore (id,gg,mc,epc) values ");
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
        String sql="select * from toolsstore";
        return DataBaseExec.execQueryForBean(sql,null,Tools.class);
    }

    public List<HashMap<String,String>> getAllToolsByHCCS(Set<String> cards){
        StringBuilder sql=new StringBuilder("select id,gg,mc,epc from toolsstore " +
                "where epc in(");
        for(String card : cards){
            sql.append("'").append(card.toUpperCase()).append("',");
        }
        sql.deleteCharAt(sql.length()-1);
        sql.append(")");

        List<HashMap<String,String>> list=DataBaseExec.execQueryForMap(sql.toString(),null);
        return list;
    }




}
