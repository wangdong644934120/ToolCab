package com.stit.toolcab.dao;

import com.stit.toolcab.db.DataBaseExec;
import com.stit.toolcab.entity.Person;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2020-08-05.
 */

public class MyPersonDao {
    public Person getPersonByUserNamePassword(String username,String password){
        String sql="select * from person where username=? and password=?";
        String[] args = new String[]{username,password};
        List<Person> list=DataBaseExec.execQueryForBean(sql,args,Person.class);
        if(list!=null && !list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    public List<Person> getAllPerson(){
        String sql="select * from person";
        List<Person> list = DataBaseExec.execQueryForBean(sql,null,Person.class);
        return list;
    }
    public void deletePersonById(String id){
        String sql="delete from person where id=?";
        Object[] args=new Object[]{id};
        DataBaseExec.execOther(sql,args);
    }
    public void addPerson(Person person){
        String sql="insert into person (id,name,path,featuredata,username,password,tzz,code,role) values(?,?,?,?,?,?,?,?,?)";
        Object[] args = new Object[]{person.getId(),person.getName(),person.getPath(),person.getFeaturedata(),person.getUsername(),person.getPassword(),person.getTzz(),person.getCode(),person.getRole()};
        DataBaseExec.execOther(sql,args);
    }

    public void updatePerson(Person person){
        String sql="update person set name=?,path=?,featuredata=?,username=?,password=?,tzz=?,code=?,role=? where id=?";
        Object[] args = new Object[]{person.getName(),person.getPath(),person.getFeaturedata(),person.getUsername(),person.getPassword(),person.getTzz(),person.getCode(),person.getRole(),person.getId()};
        DataBaseExec.execOther(sql,args);
    }

    public List<Person> getSameCodeForUpdate(String id,String code){
        String sql="select * from person where id!=? and code=?";
        String[] args = new String[]{id,code};
        return DataBaseExec.execQueryForBean(sql,args,Person.class);
    }

    public Person getPersonByCode(String code1,String code2){
        String sql="select * from person where code=? or code=?";
        String[] args = new String[]{code1,code2};
        List<Person> personList = DataBaseExec.execQueryForBean(sql,args,Person.class);
        if(personList==null || personList.isEmpty()){
            return null;
        }
        return personList.get(0);
    }

    public void deleteAllZW(){
        String sql="update person set tzz='未录入'";
        DataBaseExec.execOther(sql,null);
    }
    public List<HashMap<String,String>> getSameCodeForAdd(String code){
        String sql="select * from person where code=?";
        String[] args = new String[]{code};
        List<HashMap<String,String>> list = DataBaseExec.execQueryForMap(sql,args);
        return list;
    }


}
