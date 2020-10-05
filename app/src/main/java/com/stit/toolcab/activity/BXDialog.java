package com.stit.toolcab.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.stit.toolcab.R;
import com.stit.toolcab.entity.ToolZT;
import com.stit.toolcab.utils.Cache;

import org.apache.log4j.Logger;


public class BXDialog  extends AlertDialog {


    private SmartTable wxtable;
    private Button btnClose;
    private Logger logger = Logger.getLogger(this.getClass());


    protected BXDialog(Context context) {
        super(context);
    }

    protected BXDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected BXDialog(Context context, int themeResId) {
        super(context, themeResId);
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
        btnClose=(Button)findViewById(R.id.btnwxclose);
        btnClose.setOnClickListener(new onClickListener());

        Cache.myHandleBX= new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据

                //显示耗材存取情况信息
                if(bundle.getString("close")!=null){
                    logger.info("开始关闭报修列表界面");
                    wxtable.setTableData(null);
                    BXDialog.this.dismiss();
                    logger.info("关闭报修列表界面完成");
                    Cache.myHandleBX=null;

                }



            }
        };

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
                case R.id.btnwxclose:
                    wxtable.setTableData(null);
                    Cache.myHandleBX=null;
                    BXDialog.this.dismiss();
                    break;
                default:
                    break;
            }
        }

    }

}
