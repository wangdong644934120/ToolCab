package com.stit.toolcab.utils;


import com.stit.toolcab.dao.MyToolsDao;
import com.stit.toolcab.entity.Tools;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by Administrator on 2018/11/27.
 */

public  class ExpportDataBeExcel {

    private static Logger logger = Logger.getLogger(ExpportDataBeExcel.class);

    public static  int ImportExcelData(File file) {
        int count=-1;
        //先将库中信息全部清空
        MyToolsDao myToolsDao = new MyToolsDao();
        myToolsDao.deleteAllTools();

        FileInputStream inFile = null;
        try {
            inFile = new FileInputStream(file);
            HSSFWorkbook book = new HSSFWorkbook(inFile);
            HSSFSheet sheet = book.getSheetAt(0);
            int row = sheet.getLastRowNum();
            if (row <= 1) {
                return -1;
            }
            List<Tools> list = new ArrayList<Tools>();
            for (int i = 1; i <= row; i++) {
                HSSFRow oneRow = null;
                try {
                    oneRow = sheet.getRow(i);
                } catch (Exception e) {
                    continue;
                }
                if (oneRow == null) {
                    continue;
                }
                Tools tool = new Tools();
                tool.setId(UUID.randomUUID().toString());
                tool.setMc(oneRow.getCell((short)0)==null?"":oneRow.getCell((short)0).toString().trim());
                tool.setGg(oneRow.getCell((short)1)==null?"":oneRow.getCell((short)1).toString().trim());
                tool.setEpc(oneRow.getCell((short)2)==null?"":oneRow.getCell((short)2).toString().trim().toUpperCase());
                tool.setWz(oneRow.getCell((short)3)==null?"":oneRow.getCell((short)3).toString().trim());
                if(tool.getMc().equals("") || tool.getGg().equals("") || tool.getEpc().equals("")){
                    break;
                }
                list.add(tool);

            }
            myToolsDao.addTools(list);
            count=list.size();
            logger.info("上传工具成功，个数："+list.size());
            return list.size();

        } catch (Exception ex) {
        logger.error("解析excel出错",ex);

        } finally {
            if (inFile != null) {
                try {
                    inFile.close();
                } catch (IOException ex) {

                }
            }
           return count;
        }

    }


    public static boolean saveExcel( File file) {
        try{
            if(!file.exists()){
                file.createNewFile();
            }
            MyToolsDao myToolsDao = new MyToolsDao();
            List<Tools> list =myToolsDao.getAllTools();
            // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
            HSSFWorkbook wb=null;
            if(wb == null){
                wb = new HSSFWorkbook();
            }
            // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
            HSSFSheet sheet = wb.createSheet("sheet1");
            // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
            HSSFRow row = sheet.createRow(0);
            // 第四步，创建单元格，并设置值表头 设置表头居中
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFDataFormat format = wb.createDataFormat();
            style.setDataFormat(format.getFormat("@"));
            sheet.setDefaultColumnStyle((short)3,style);
            //标题
            for(int i=0;i<3;i++){
                HSSFCell cell = row.getCell((short) i);
                if (cell == null) {
                    cell = row.createCell((short) i);
                }
                if(i==0){
                    cell.setCellValue(new HSSFRichTextString("名称"));
                }else if(i==1){
                    cell.setCellValue(new HSSFRichTextString("规格"));
                }else if(i==2){
                    cell.setCellValue(new HSSFRichTextString("EPC"));
                }else if(i==3){
                    cell.setCellValue(new HSSFRichTextString("位置"));
                }
            }
            //内容
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int rownum=1;
            for(Tools tool: list){
                row = sheet.createRow(rownum);
                rownum=rownum+1;
                for(int i=0;i<3;i++){
                    HSSFCell cell = row.getCell((short) i);
                    if (cell == null) {
                        cell = row.createCell((short) i);
                    }
                    if(i==0){
                        cell.setCellValue(new HSSFRichTextString(tool.getMc()));
                    }else if(i==1){
                        cell.setCellValue(new HSSFRichTextString(tool.getGg()));
                    }else if(i==2){
                        cell.setCellValue(new HSSFRichTextString(tool.getEpc()));
                    }else if(i==3){
                        cell.setCellValue(new HSSFRichTextString(tool.getWz()));
                    }
                }
            }
            try{
                FileOutputStream outFile = new FileOutputStream(file);
                wb.write(outFile);
                outFile.close();
            }catch(Exception e){
                logger.error(e);
            }
            return true;
        }catch(Exception e){
            logger.error("创建down.xls出错",e);
        }
        return true;

    }
}
