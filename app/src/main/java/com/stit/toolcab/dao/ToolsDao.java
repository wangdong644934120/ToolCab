package com.stit.toolcab.dao;

import com.stit.toolcab.db.DataBaseExec;
import com.stit.toolcab.entity.Record;
import com.stit.toolcab.entity.ToolZT;
import com.stit.toolcab.entity.Tools;
import com.stit.toolcab.entity.ToolsFind;
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
     * 获取所有工具
     * @return
     */
    public List<Tools> getAllTools(){
        String sql="select * from tools";
        return DataBaseExec.execQueryForBean(sql,null,Tools.class);
    }
    /**
     * 删除所有工具
     */
    public void deleteAllTools(){
        String sql="delete from tools";
        DataBaseExec.execOther(sql,null);
    }

    /**
     * 根据触发红外行程获取工具
     * @param list
     * @return
     */
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

//    /**
//     * 批量添加工具
//     * @param list
//     */
//    public void addTools(List<Tools> list){
//        StringBuilder sql=new StringBuilder("insert into tools (id,gg,mc,epc) values ");
//        for(Tools tool : list){
//            sql.append("('").append(tool.getId().toString()).append("','");
//            sql.append(tool.getGg().toString()).append("','");
//            sql.append(tool.getMc().toString()).append("','");
//            sql.append(tool.getEpc().toString().toUpperCase()).append("'),");
//        }
//        sql.deleteCharAt(sql.length()-1);
//
//        DataBaseExec.execOther(sql.toString(),null);
//    }



//    /**
//     * 清空标签位置
//     */
//    public void clearWZ(){
//        String sql="update tools set wz=''";
//        DataBaseExec.execOther(sql,null);
//    }

    /**
     * 更新所有工具位置(初始柜内耗材)
     * @param list
     */
    public void updateAllToolsWZ(List<HashMap<String,String>> list){
        StringBuilder sql=new StringBuilder("insert into tools (id,gg,mc,epc,wz,jyzt,bxzt,wxzt,jypersonid,jytimepoke,bxpersonid,bxtimepoke,wxpersonid,wxtimepoke) values ");
        for(HashMap<String,String> map : list){
            sql.append("('").append(map.get("id").toString()).append("','");
            sql.append(map.get("gg").toString()).append("','");
            sql.append(map.get("mc").toString()).append("','");
            sql.append(map.get("epc").toString().toUpperCase()).append("','");
            sql.append(map.get("wz").toString()).append("','");
            sql.append(Record.JYZAIGUI).append("','");
            sql.append(Record.BXZHENGCHANG).append("','");
            sql.append(Record.WXZHENGCHANG).append("','");
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
        String sql="update tools set bxzt="+Record.BXBAOXIU +",bxpersonid='"+Cache.operator.getId()+"',bxtimepoke='"+sdf.format(new Date())+"' where id='"+id+"'";
        DataBaseExec.execOther(sql,null);
    }

    /**
     * 存操作，更新借用信息为正常状态
     */
    public void updateSave(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("update tools set jyzt='").append(Record.JYZAIGUI).append("',");
        sb.append("jypersonid='',");
        sb.append("jytimepoke='").append(sdf.format(new Date())).append("'");
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
     * 还操作,维修报修更新为正常状态
     */
    public void updateWXToZC(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("update tools set jyzt='").append(Record.JYZAIGUI).append("',");
        sb.append("jypersonid='',bxpersonid='',wxpersonid='',");
        sb.append("bxzt='").append(Record.BXZHENGCHANG).append("',");
        sb.append("wxzt='").append(Record.WXZHENGCHANG).append("' ");
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
     * 取出操作，更新状态为维修
     */
    public void updateZTToWX(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append("update tools set ");
        sb.append("wxpersonid='").append(Cache.operator.getId()).append("',");
        sb.append("wxzt='").append(Record.WXWEIXIU).append("',");
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
        sb.append("update tools set jyzt='").append(Record.JYJIEYONG).append("',");
        sb.append("jypersonid='").append(Cache.operator.getId()==null?"":Cache.operator.getId()).append("',");
        sb.append("jytimepoke='").append(sdf.format(new Date())).append("'");
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

    /**
     * 初始化借用信息
     */
    private void initJY(){
        List<ToolZT> list = new ArrayList<ToolZT>();
        String sql="select t.mc,t.gg,t.epc,t.wz,t.jyzt,t.jytimepoke, p.code,p.name  from tools t ,person p where t.jypersonid=p.id  and t.jyzt="+ Record.JYJIEYONG+" and t.wxzt="+Record.WXZHENGCHANG;
        List<HashMap<String,String>> listmap =DataBaseExec.execQueryForMap(sql,null);
        for(HashMap map : listmap){
            ToolZT tool = new ToolZT();
            tool.setMc(map.get("mc")==null?"":map.get("mc").toString());
            tool.setGg(map.get("gg")==null?"":map.get("gg").toString());
            tool.setEpc(map.get("epc")==null?"":map.get("epc").toString());
            tool.setWz(map.get("wz")==null?"":map.get("wz").toString());
            tool.setJyzt("借出");
            tool.setJytimepoke(map.get("jytimepoke")==null?"":map.get("jytimepoke").toString());
            tool.setJyname(map.get("name")==null?"":map.get("name").toString());
            list.add(tool);
        }
        Cache.listJY=list;
    }

    /**
     * 初始化报修信息
     */
    public void initBX(){
        List<ToolZT> list = new ArrayList<ToolZT>();
        String sql="select t.mc,t.gg,t.epc,t.wz,t.bxzt,t.bxtimepoke, p.code,p.name  from tools t , person p where t.bxpersonid=p.id and  t.bxzt="+ Record.BXBAOXIU +" and t.wxzt="+Record.WXZHENGCHANG;
        List<HashMap<String,String>> listmap =DataBaseExec.execQueryForMap(sql,null);
        for(HashMap map : listmap){
            ToolZT tool = new ToolZT();
            tool.setMc(map.get("mc")==null?"":map.get("mc").toString());
            tool.setGg(map.get("gg")==null?"":map.get("gg").toString());
            tool.setEpc(map.get("epc")==null?"":map.get("epc").toString());
            tool.setWz(map.get("wz")==null?"":map.get("wz").toString());
            tool.setBxzt("报修");
            tool.setBxtimepoke(map.get("bxtimepoke")==null?"":map.get("bxtimepoke").toString());
            tool.setBxname(map.get("name")==null?"":map.get("name").toString());
            list.add(tool);
        }
        Cache.listBX=list;
    }

    /**
     * 初始化维修信息
     */
    private void initWX(){
        List<ToolZT> list = new ArrayList<ToolZT>();
        String sql="select t.mc,t.gg,t.epc,t.wz,t.wxzt,t.wxtimepoke, p.code,p.name  from tools t , person p where t.wxpersonid=p.id  and t.wxzt="+ Record.WXWEIXIU;
        List<HashMap<String,String>> listmap =DataBaseExec.execQueryForMap(sql,null);
        for(HashMap map : listmap){
            ToolZT tool = new ToolZT();
            tool.setMc(map.get("mc")==null?"":map.get("mc").toString());
            tool.setGg(map.get("gg")==null?"":map.get("gg").toString());
            tool.setEpc(map.get("epc")==null?"":map.get("epc").toString());
            tool.setWz(map.get("wz")==null?"":map.get("wz").toString());
            tool.setWxzt("维修");
            tool.setWxtimepoke(map.get("wxtimepoke")==null?"":map.get("wxtimepoke").toString());
            tool.setWxname(map.get("name")==null?"":map.get("name").toString());
            list.add(tool);
        }
        Cache.listWX=list;
    }


    /**
     * 查找界面根据层查找工具
     * @param ceng 层号
     * @return
     */
    public  List<ToolZT> findTools(String ceng){
        List<ToolZT> list = new ArrayList<ToolZT>();
        String sql="select t.mc,t.gg, t.wz,t.jyzt,jy.name as jyname,t.jytimepoke,t.bxzt,bx.name as bxname,t.bxtimepoke,t.wxzt,wx.name as wxname,t.wxtimepoke  from tools t left join person jy on t.jypersonid=jy.id " +
                "left join person bx on t.bxpersonid=bx.id left join person wx on t.wxpersonid = wx.id  where  t.wz="+ceng;
        List<HashMap<String,String>> listmap =DataBaseExec.execQueryForMap(sql,null);

        for(HashMap map : listmap){
            ToolZT tool = new ToolZT();
            tool.setMc(map.get("mc")==null?"":map.get("mc").toString());
            tool.setGg(map.get("gg")==null?"":map.get("gg").toString());
            tool.setEpc(map.get("epc")==null?"":map.get("epc").toString());
            tool.setWz(map.get("wz")==null?"":map.get("wz").toString());
            tool.setJyzt((map.get("jyzt")==null || map.get("jyzt").equals(Record.JYZAIGUI))?"在柜":"借用");
            tool.setJyname(map.get("jyname")==null?"":map.get("jyname").toString());
            tool.setJytimepoke(map.get("jytimepoke")==null?"":map.get("jytimepoke").toString());

            tool.setBxzt((map.get("bxzt")==null || map.get("bxzt").equals(Record.JYZAIGUI))?"正常":"报修");
            tool.setBxname(map.get("bxname")==null?"":map.get("bxname").toString());
            tool.setBxtimepoke(map.get("bxtimepoke")==null?"":map.get("bxtimepoke").toString());

            tool.setWxzt((map.get("wxzt")==null || map.get("wxzt").equals(Record.JYZAIGUI))?"正常":"维修");
            tool.setWxname(map.get("wxname")==null?"":map.get("wxname").toString());
            tool.setWxtimepoke(map.get("wxtimepoke")==null?"":map.get("wxtimepoke").toString());


            tool.setBxtimepoke(map.get("bxtimepoke")==null?"":map.get("bxtimepoke").toString());
            tool.setWxzt("报修");
            list.add(tool);
        }
        return list;
    }
}
