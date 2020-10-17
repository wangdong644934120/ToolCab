package com.stit.toolcab.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.stit.toolcab.R;
import com.stit.toolcab.dao.ToolsDao;
import com.stit.toolcab.entity.ToolZT;
import com.stit.toolcab.entity.Tools;
import com.stit.toolcab.utils.Cache;

import org.apache.log4j.Logger;

import java.util.List;

public class FindDialog extends AlertDialog {

    private Button ceng1;
    private Button ceng2;
    private Button ceng3;
    private Button ceng4;
    private Button ceng5;
    private Button ceng6;
    private Button ceng7;
    private SmartTable findtable;
    private Button btnClose;
    private Logger logger = Logger.getLogger(this.getClass());

    Column<String> columnmc = new Column<String>("名称", "mc");
    Column<String> columngg = new Column<String>("规格", "gg");
    Column<String> columnwz=new Column<String>("位置","wz");
    Column<String> columnjyzt = new Column<String>("借用状态", "jyzt");
    Column<String> columnjyry = new Column<String>("借用人员", "jyname");
    Column<String> columnjysj = new Column<String>("借用时间", "jytimepoke");
    //Column<String> columnbxzt = new Column<String>("报修状态", "bxzt");
    Column<String> columnbxry = new Column<String>("报修人员", "bxname");
    Column<String> columnbxsj = new Column<String>("报修时间", "bxtimepoke");
    Column<String> columnwxzt = new Column<String>("维修状态", "wxzt");
    Column<String> columnwxry = new Column<String>("维修人员", "wxname");
    Column<String> columnwxsj = new Column<String>("维修时间", "wxtimepoke");


    List<ToolZT> list=null;
    TableData<ToolZT> tableData=null;
    ToolsDao toolsDao = new ToolsDao();

    protected FindDialog(Context context) {
        super(context);
    }

    protected FindDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected FindDialog(Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        initView();
        initData();
    }

    private void initView(){
        ceng1=(Button)findViewById(R.id.ceng1);
        ceng1.setOnClickListener(new onClickListener());
        ceng2=(Button)findViewById(R.id.ceng2);
        ceng2.setOnClickListener(new onClickListener());
        ceng3=(Button)findViewById(R.id.ceng3);
        ceng3.setOnClickListener(new onClickListener());
        ceng4=(Button)findViewById(R.id.ceng4);
        ceng4.setOnClickListener(new onClickListener());
        ceng5=(Button)findViewById(R.id.ceng5);
        ceng5.setOnClickListener(new onClickListener());
        ceng6=(Button)findViewById(R.id.ceng6);
        ceng6.setOnClickListener(new onClickListener());
        ceng7=(Button)findViewById(R.id.ceng7);
        ceng7.setOnClickListener(new onClickListener());

        findtable=(SmartTable)findViewById(R.id.findtable);
        btnClose=(Button)findViewById(R.id.findclose);
        findtable.getConfig().setShowXSequence(false);
        findtable.getConfig().setShowYSequence(false);
        findtable.getConfig().setShowTableTitle(false);
        findtable.getConfig().setColumnTitleBackgroundColor(Color.BLUE);
        findtable.getConfig().setColumnTitleStyle(new FontStyle(20,Color.WHITE));
        findtable.getConfig().setContentStyle(new FontStyle(18, Color.BLACK));
        findtable.getConfig().setMinTableWidth(700);
        btnClose.setOnClickListener(new onClickListener());
    }

    private void initData(){

        list= toolsDao.findTools("1");
        tableData = new TableData<ToolZT>("", list, columnmc, columngg,columnwz, columnjyzt, columnjyry,columnjysj,columnbxry,columnbxsj,columnwxzt,columnwxry,columnwxsj);
        findtable.setTableData(tableData);
    }


    /**
     * 单击事件监听
     *
     * @author dinghaoyang
     */
    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.ceng1:
                    findtable.setTableData(null);

                    list= toolsDao.findTools("1");
                    tableData = new TableData<ToolZT>("", list, columnmc, columngg,columnwz, columnjyzt, columnjyry,columnjysj,columnbxry,columnbxsj,columnwxzt,columnwxry,columnwxsj);
                    findtable.setTableData(tableData);
                    break;
                case R.id.ceng2:
                    findtable.setTableData(null);
                    list= toolsDao.findTools("2");
                    tableData = new TableData<ToolZT>("", list, columnmc, columngg,columnwz, columnjyzt, columnjyry,columnjysj,columnbxry,columnbxsj,columnwxzt,columnwxry,columnwxsj);
                    findtable.setTableData(tableData);
                    break;
                case R.id.ceng3:
                    findtable.setTableData(null);
                    list= toolsDao.findTools("3");
                    tableData = new TableData<ToolZT>("", list, columnmc, columngg,columnwz, columnjyzt, columnjyry,columnjysj,columnbxry,columnbxsj,columnwxzt,columnwxry,columnwxsj);
                    findtable.setTableData(tableData);
                    break;
                case R.id.ceng4:
                    findtable.setTableData(null);
                    list= toolsDao.findTools("4");
                    tableData = new TableData<ToolZT>("", list, columnmc, columngg,columnwz, columnjyzt, columnjyry,columnjysj,columnbxry,columnbxsj,columnwxzt,columnwxry,columnwxsj);
                    findtable.setTableData(tableData);
                    break;
                case R.id.ceng5:
                    findtable.setTableData(null);
                    list= toolsDao.findTools("5");
                    tableData = new TableData<ToolZT>("", list, columnmc, columngg,columnwz, columnjyzt, columnjyry,columnjysj,columnbxry,columnbxsj,columnwxzt,columnwxry,columnwxsj);
                    findtable.setTableData(tableData);
                    break;
                case R.id.ceng6:
                    findtable.setTableData(null);
                    list= toolsDao.findTools("6");
                    tableData = new TableData<ToolZT>("", list, columnmc, columngg,columnwz, columnjyzt, columnjyry,columnjysj,columnbxry,columnbxsj,columnwxzt,columnwxry,columnwxsj);
                    findtable.setTableData(tableData);
                    break;
                case R.id.ceng7:
                    findtable.setTableData(null);
                    list= toolsDao.findTools("7");
                    tableData = new TableData<ToolZT>("", list, columnmc, columngg,columnwz, columnjyzt, columnjyry,columnjysj,columnbxry,columnbxsj,columnwxzt,columnwxry,columnwxsj);
                    findtable.setTableData(tableData);
                    break;
                case R.id.findclose:
                    findtable.setTableData(null);
                    FindDialog.this.dismiss();
                    break;
                default:
                    break;
            }
        }

    }
}
