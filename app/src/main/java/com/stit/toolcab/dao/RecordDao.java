package com.stit.toolcab.dao;

import com.stit.toolcab.entity.Record;
import com.stit.toolcab.db.DataBaseExec;
import com.stit.toolcab.entity.ToolZT;
import com.stit.toolcab.utils.Cache;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Administrator on 2020-08-03.
 */

public class RecordDao {
    private Logger logger = Logger.getLogger(this.getClass());
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

            tool.setTimepoke(map.get("timepoke")==null?"":map.get("timepoke").toString());
            tool.setJyzt("借出");
            list.add(tool);
        }
        Cache.listJY=list;
    }
    public void initBX(){
        List<ToolZT> list = new ArrayList<ToolZT>();
        String sql="select t.mc,t.gg,t.epc,t.wz,t.jyzt,t.wxzt, p.code,p.name,t.bxtimepoke  from tools t left join person p on t.personid=p.id where  t.wxzt="+ Record.BAOXIU;
        List<HashMap<String,String>> listmap =DataBaseExec.execQueryForMap(sql,null);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
        for(HashMap map : listmap){
            ToolZT tool = new ToolZT();
            tool.setMc(map.get("mc")==null?"":map.get("mc").toString());
            tool.setGg(map.get("gg")==null?"":map.get("gg").toString());
            tool.setEpc(map.get("epc")==null?"":map.get("epc").toString());
            tool.setCode(map.get("code")==null?"":map.get("code").toString());
            tool.setName(map.get("name")==null?"":map.get("name").toString());
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
