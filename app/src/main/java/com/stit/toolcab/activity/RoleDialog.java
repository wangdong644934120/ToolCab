package com.stit.toolcab.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.stit.toolcab.R;
import com.stit.toolcab.utils.Cache;

public class RoleDialog extends AlertDialog {

    private Button btnJY;
    private Button btnWX;

    protected RoleDialog(Context context) {
        super(context);
    }

    protected RoleDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected RoleDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_dialog);
        initView();
    }

    private void initView(){
        btnJY=(Button)findViewById(R.id.btnJY);
        btnWX=(Button)findViewById(R.id.btnWX);
        btnJY.setOnClickListener(new onClickListener());
        btnWX.setOnClickListener(new onClickListener());
    }
    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.btnJY:
                    Cache.operatortype=0;
                    RoleDialog.this.dismiss();
                    break;
                case R.id.btnWX:
                    Cache.operatortype=1;
                    Message message = Message.obtain(Cache.mainHandle);
                    Bundle data = new Bundle();
                    data.putString("ui","wx");
                    message.setData(data);
                    Cache.mainHandle.sendMessage(message);
                    RoleDialog.this.dismiss();
                    break;
                default:
                    break;
            }
        }
    }
}
