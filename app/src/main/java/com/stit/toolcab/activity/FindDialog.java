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
import com.stit.toolcab.entity.ToolZT;
import com.stit.toolcab.utils.Cache;

import org.apache.log4j.Logger;

public class FindDialog extends AlertDialog {

    private SmartTable findtable;
    private Button btnClose;
    private Logger logger = Logger.getLogger(this.getClass());

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
        Column<String> columnmc = new Column<String>("名称", "mc");
        Column<String> columngg = new Column<String>("规格", "gg");
        Column<String> columnjyzt = new Column<String>("借用状态", "jyzt");
        Column<String> columnjyry = new Column<String>("借用人员", "personid");
        Column<String> columnjysj = new Column<String>("借用时间", "timepoke");
        Column<String> columnwxzt = new Column<String>("维修状态", "wxzt");
        Column<String> columnbxry = new Column<String>("报修人员", "bxpersonid");
        Column<String> columnbxsj = new Column<String>("报修时间", "bxtimepoke");
        Column<String> columnwxry = new Column<String>("维修人员", "wxpersonid");
        Column<String> columnwxsj = new Column<String>("维修时间", "wxtimepoke");
        Column<String> columnwz=new Column<String>("位置","wz");

        TableData<ToolZT> tableData = new TableData<ToolZT>("", Cache.listBX, columnmc, columngg, columnjyzt, columnjyry,columnjysj,columnwxzt,columnbxry,columnbxsj,columnwxry,columnwxsj,columnwz);
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
