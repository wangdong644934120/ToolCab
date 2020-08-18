package com.stit.toolcab.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.stit.toolcab.R;
import com.stit.toolcab.utils.Cache;

public class SelectDialog extends AlertDialog {

    private Button btnRY;
    private Button btnGJ;
    private Button btnKZ;
    private Button btnPZ;
    private Button btnSBXX;
    private Button btnTCCX;
    private View btnRYView;
    private View btnHCView;

    public SelectDialog() {
        super(null);
    }

    public SelectDialog(Context context, int theme) {
        super(context, theme);
    }

    public SelectDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_dialog);
        initView();
    }

    private void initView() {
        btnRY=(Button)findViewById(R.id.btnRY);
        btnRY.setOnClickListener(new onClickListener());
        btnGJ=(Button)findViewById(R.id.btnGJ);
        btnGJ.setOnClickListener(new onClickListener());
        btnKZ=(Button)findViewById(R.id.btnKZ);
        btnKZ.setOnClickListener(new onClickListener());
        btnPZ=(Button)findViewById(R.id.btnPZ);
        btnPZ.setOnClickListener(new onClickListener());
        btnSBXX=(Button)findViewById(R.id.btnSBXX);
        btnSBXX.setOnClickListener(new onClickListener());
        btnTCCX=(Button)findViewById(R.id.btnTCCX);
        btnTCCX.setOnClickListener(new onClickListener());
        btnRYView=(View)findViewById(R.id.btnRYView);
        btnHCView=(View)findViewById(R.id.btnHCView);
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
                case R.id.btnRY:
                    openui("ry");
                    break;
                case R.id.btnGJ:
                    openui("gj");
                    break;
                case R.id.btnPZ:
                    openui("pz");
                    break;
                case R.id.btnKZ:
                    openui("kz");
                    break;
                case R.id.btnSBXX:
                    openui("sbxx");
                    break;
                case R.id.btnTCCX:
                    openui("tccx");
                    break;
                default:
                    break;
            }
        }
    }
    private void openui(String ui){
        Message message = Message.obtain(Cache.mainHandle);
        Bundle data = new Bundle();
        data.putString("ui",ui);
        message.setData(data);
        Cache.mainHandle.sendMessage(message);
        SelectDialog.this.dismiss();
    }
}
