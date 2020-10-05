package com.stit.toolcab.dao;

import com.stit.toolcab.db.DataBaseExec;
import com.stit.toolcab.entity.Record;
import com.stit.toolcab.entity.ToolZT;
import com.stit.toolcab.entity.Tools;
import com.stit.toolcab.utils.Cache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    /**
     * 存操作，更新借用信息
     */
    public void updateSave(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("update tools set jyzt='").append(Record.ZAIGUI).append("',");
        sb.append("personid='',");
        sb.append("timepoke='").append(sdf.format(new Date())).append("'");
        sb.append(" where id in (");
        for(Tools tools : Cache.listOperaSave){
            sb.append("'").append(tools.getId()).append("',");
        }
        if(!Cache.listOperaSave.isEmpty()){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(")");
        DataBaseExec.execOther(sb.toString(),null);
    }

    /**
     * 更新为正常状态
     */
    public void updateZTToZC(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("update tools set jyzt='").append(Record.ZAIGUI).append("',");
        sb.append("personid='',wxpersonid='',");
        sb.append("wxzt='").append(Record.ZHENGCHANG).append("',");
        sb.append("timepoke='").append(sdf.format(new Date())).append("'");
        sb.append(" where id in (");
        for(Tools tools : Cache.listOperaSave){
            sb.append("'").append(tools.getId()).append("',");
        }
        if(!Cache.listOperaSave.isEmpty()){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(")");
        DataBaseExec.execOther(sb.toString(),null);
    }

    /**
     * 更新状态为维修
     */
    public void updateZTToWX(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("update tools set jyzt='',");
        sb.append("wxpersonid='").append(Cache.operator.getId()).append("',");
        sb.append("wxzt='").append(Record.WEIXIU).append("',");
        sb.append("wxtimepoke='").append(sdf.format(new Date())).append("'");
        sb.append(" where id in (");
        for(Tools tools : Cache.listOperaOut){
            sb.append("'").append(tools.getId()).append("',");
        }
        if(!Cache.listOperaOut.isEmpty()){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(")");
        DataBaseExec.execOther(sb.toString(),null);
    }

    /**
     * 取操作，更新借用信息
     */
    public void updateOut(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("update tools set jyzt='").append(Record.JIEYONG).append("',");
        sb.append("personid='").append(Cache.operator.getId()==null?"":Cache.operator.getId()).append("',");
        sb.append("timepoke='").append(sdf.format(new Date())).append("'");
        sb.append(" where id in (");
        for(Tools tools : Cache.listOperaOut){
            sb.append("'").append(tools.getId()).append("',");
        }
        if(!Cache.listOperaOut.isEmpty()){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(")");
        DataBaseExec.execOther(sb.toString(),null);

    }

    /**
     * 初始化借用报修维修信息
     * @return
     */
    public void initJYBXWX(){
        initJY();
        initBX();
        initWX();
    }

    private void initJY(){
        List<ToolZT> list = new ArrayList<ToolZT>();
        String sql="select t.mc,t.gg,t.epc,t.wz,t.wxzt,t.jyzt, p.code,p.name,t.timepoke  from tools t ,person p where t.personid=p.id  and t.jyzt="+ Record.JIEYONG;

        List<HashMap<String,String>> listmap =DataBaseExec.execQueryForMap(sql,null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
        for(HashMap map : listmap){
            ToolZT tool = new ToolZT();
            tool.setMc(map.get("mc")==null?"":map.get("mc").toString());
            tool.setGg(map.get("gg")==null?"":map.get("gg").toString());
            tool.setEpc(map.get("epc")==null?"":map.get("epc").toString());
            tool.setCode(map.get("code")==null?"":map.get("code").toString());
            tool.setName(map.get("name")==null?"":map.get("name").toString());
            tool.setWz(map.get("wz")==null?"":map.get("wz").toString());
            tool.setTimepoke(map.get("timepoke")==null?"":map.get("timepoke").toString());
            tool.setJyzt("借出");
            list.add(tool);
        }
        Cache.listJY=list;
    }

    public void initBX(){
        List<ToolZT> list = new ArrayList<ToolZT>();
        String sql="select t.mc,t.gg,t.epc,t.wz,t.jyzt,t.wxzt, p.code,p.name,t.bxtimepoke  from tools t left join person p on t.bxpersonid=p.id where  t.wxzt="+ Record.BAOXIU;
        List<HashMap<String,String>> listmap =DataBaseExec.execQueryForMap(sql,null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
        for(HashMap map : listmap){
            ToolZT tool = new ToolZT();
            tool.setMc(map.get("mc")==null?"":map.get("mc").toString());
            tool.setGg(map.get("gg")==null?"":map.get("gg").toString());
            tool.setEpc(map.get("epc")==null?"":map.get("epc").toString());
            tool.setCode(map.get("code")==null?"":map.get("code").toString());
            tool.setName(map.get("name")==null?"":map.get("name").toString());
            tool.setWz(map.get("wz")==null?"":map.get("wz").toString());
//            String time ="";
//            try{
//                time=sdf.format(new Date(Long.valueOf(map.get("bxtimepoke")==null?"":map.get("bxtimepoke").toString())));
//            }catch (Exception e){
//                logger.error("时间转换出错",e);
//            }
            tool.setBxtimepoke(map.get("bxtimepoke")==null?"":map.get("bxtimepoke").toString());
            tool.setWxzt("报修");
            list.add(tool);
        }
        Cache.listBX=list;
    }

    private void initWX(){
        List<ToolZT> list = new ArrayList<ToolZT>();
        String sql="select t.mc,t.gg,t.epc,t.wz,t.jyzt,t.wxzt, p.code,p.name,t.wxtimepoke  from tools t left join person p on t.wxpersonid=p.id  where t.wxzt="+ Record.WEIXIU;
        List<HashMap<String,String>> listmap =DataBaseExec.execQueryForMap(sql,null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
        for(HashMap map : listmap){
            ToolZT tool = new ToolZT();
            tool.setMc(map.get("mc")==null?"":map.get("mc").toString());
            tool.setGg(map.get("gg")==null?"":map.get("gg").toString());
            tool.setEpc(map.get("epc")==null?"":map.get("epc").toString());
            tool.setCode(map.get("code")==null?"":map.get("code").toString());
            tool.setName(map.get("name")==null?"":map.get("name").toString());
            tool.setWz(map.get("wz")==null?"":map.get("wz").toString());
//            String time ="";
//            try{
//                time=sdf.format(new Date(Long.valueOf(map.get("wxtimepoke")==null?"":map.get("wxtimepoke").toString())));
//            }catch (Exception e){
//                logger.error("时间转换出错",e);
//            }
            tool.setWxtimepoke(map.get("wxtimepoke")==null?"":map.get("wxtimepoke").toString());
            tool.setWxzt("维修");
            list.add(tool);
        }
        Cache.listWX=list;
    }


}
