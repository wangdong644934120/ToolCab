package com.stit.toolcab.dao;

import com.stit.toolcab.db.DataBaseExec;
import com.stit.toolcab.entity.Record;
import com.stit.toolcab.entity.Tools;
import com.stit.toolcab.utils.Cache;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    public List<Tools> getToolsByCFHWXC(List<String> list){
        StringBuilder sb=new StringBuilder("select * from tools "
                + " where wz in ( ");
        for (String wz : list) {
            sb.append("'").append(wz).append("'").append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return DataBaseExec.execQueryForBean(sb.toString(),null,Tools.class);
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
        StringBuilder sql=new StringBuilder("insert into tools (id,gg,mc,epc,wz,jyzt,wxzt,personid,timepoke,bxpersonid,bxtimepoke,wxpersonid,wxtimepoke) values ");
        for(HashMap<String,String> map : list){
            sql.append("('").append(map.get("id").toString()).append("','");
            sql.append(map.get("gg").toString()).append("','");
            sql.append(map.get("mc").toString()).append("','");
            sql.append(map.get("epc").toString().toUpperCase()).append("','");
            sql.append(map.get("wz").toString()).append("','");
            sql.append(Record.ZAIGUI).append("','");
            sql.append(Record.ZHENGCHANG).append("','");
            sql.append("").append("','");
            sql.append("").append("','");
            sql.append("").append("','");
            sql.append("").append("','");
            sql.append("").append("','");
            sql.append("").append("'),");

        }
        sql.deleteCharAt(sql.length()-1);

        DataBaseExec.execOther(sql.toString(),null);
    }

    /**
     * 更新为报修状态
     * @param id
     */
    public void updateBX(String id){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql="update tools set wxzt="+Record.BAOXIU +",bxpersonid='"+Cache.operator.getId()+"',bxtimepoke='"+sdf.format(new Date())+"' where id='"+id+"'";
        DataBaseExec.execOther(sql,null);
    }

    public void getBXGJ(){
        String sql="select * from tools where wxzt="+Record.BAOXIU;
        List<Tools> list = DataBaseExec.execQueryForBean(sql,null,Tools.class);
        if(list!=null && !list.isEmpty()){
            //Cache.listBX=list;
        }else{
            //Cache.listBX.clear();
        }

    }


}
