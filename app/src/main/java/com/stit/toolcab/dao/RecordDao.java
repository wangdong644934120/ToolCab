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

    public void addRecord(){
        StringBuilder sb = new StringBuilder("insert into record (id,name,code,starttime,zt,mc,gg,epc,endtime) values ");

    }


//    public void updateAllToolsWZ(List<HashMap<String,String>> list){
//        StringBuilder sql=new StringBuilder("insert into tools (id,gg,mc,epc,wz,jyzt,wxzt,personid,timepoke,bxpersonid,bxtimepoke,wxpersonid,wxtimepoke) values ");
//        for(HashMap<String,String> map : list){
//            sql.append("('").append(map.get("id").toString()).append("','");
//            sql.append(map.get("gg").toString()).append("','");
//            sql.append(map.get("mc").toString()).append("','");
//            sql.append(map.get("epc").toString().toUpperCase()).append("','");
//            sql.append(map.get("wz").toString()).append("','");
//            sql.append(Record.ZAIGUI).append("','");
//            sql.append(Record.ZHENGCHANG).append("','");
//            sql.append("").append("','");
//            sql.append("").append("','");
//            sql.append("").append("','");
//            sql.append("").append("','");
//            sql.append("").append("','");
//            sql.append("").append("'),");
//
//        }
//        sql.deleteCharAt(sql.length()-1);
//
//        DataBaseExec.execOther(sql.toString(),null);
//    }

}
