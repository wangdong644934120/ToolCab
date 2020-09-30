package com.stit.toolcab.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.stit.toolcab.R;
import com.stit.toolcab.entity.ToolZT;
import com.stit.toolcab.utils.Cache;

public class BXDialog  extends AlertDialog {


    private SmartTable wxtable;


    protected BXDialog(Context context) {
        super(context);
    }

    protected BXDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected BXDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private void Builder(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bxdialog);
        initView();
        initData();
    }

    private void initView(){

        wxtable=(SmartTable)findViewById(R.id.wxtable);
        wxtable.getConfig().setShowXSequence(false);
        wxtable.getConfig().setShowYSequence(false);
        wxtable.getConfig().setShowTableTitle(false);
        wxtable.getConfig().setColumnTitleBackgroundColor(Color.BLUE);
        wxtable.getConfig().setColumnTitleStyle(new FontStyle(20,Color.WHITE));
        wxtable.getConfig().setContentStyle(new FontStyle(18,Color.BLACK));
        wxtable.getConfig().setMinTableWidth(700);

    }
    private void initData(){
        Column<String> columnmc = new Column<String>("名称", "mc");
        Column<String> columngg = new Column<String>("规格", "gg");
        Column<String> columnry = new Column<String>("报修人员", "name");
        Column<String> columnbxsj = new Column<String>("报修时间", "bxtimepoke");
        Column<String> columnwz=new Column<String>("位置","wz");

        TableData<ToolZT> tableData = new TableData<ToolZT>("", Cache.listBX, columnmc, columngg, columnry, columnbxsj,columnwz);
        wxtable.setTableData(tableData);
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


                default:
                    break;
            }
        }

    }

}
