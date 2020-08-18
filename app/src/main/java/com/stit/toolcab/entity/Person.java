package com.stit.toolcab.entity;


/**
 * Created by Administrator on 2020-07-03.
 */


public class Person {

    private String id;

    private String name;

    private String path;

    private byte[] featuredata;

    private String username;

    private String password;

    private String tzz;

    private String code;

    private String role;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getFeaturedata() {
        return featuredata;
    }

    public void setFeaturedata(byte[] featuredata) {
        this.featuredata = featuredata;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTzz() {
        return tzz;
    }

    public void setTzz(String tzz) {
        this.tzz = tzz;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
