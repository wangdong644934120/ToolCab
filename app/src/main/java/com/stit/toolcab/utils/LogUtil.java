package com.stit.toolcab.utils;

import android.os.Environment;

import org.apache.log4j.Level;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Created by Administrator on 2020-07-03.
 */

public class LogUtil {
    public static void initLog() {
        LogConfigurator logConfigurator = new LogConfigurator();
        String file= Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "STIT"
                + File.separator + "logs"+ File.separator+ "log.log";
        System.out.println("是否存在："+new File(file).exists());
        File fileTarget= new File(file);
        if(!fileTarget.exists()){
            try{
                File fileParent=fileTarget.getParentFile();
                fileParent.mkdirs();
                fileTarget.createNewFile();
            }catch (Exception e){
                e.printStackTrace();
            }


        }
        logConfigurator.setFileName(file);
        logConfigurator.setRootLevel(Level.INFO);//设置LOG级别INFO
        logConfigurator.setLevel("com.example", Level.INFO);
        logConfigurator.setFilePattern("[%-d{yyyy-MM-dd HH:mm:ss.SSS}][%5p][%-10c][%L]%m%n");
        logConfigurator.setMaxBackupSize(20);//最多记录20个
        logConfigurator.setMaxFileSize(1024 * 1024); //
        logConfigurator.setImmediateFlush(true);
        logConfigurator.configure();
    }
}
