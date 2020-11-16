package com.stit.toolcab.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import com.stit.toolcab.R;
import com.stit.toolcab.utils.QRCodeManager;
import com.stit.toolcab.utils.ZXUtils;

public class QRCodeDialog extends AlertDialog {

    private ImageView ivQRCode;

    protected QRCodeDialog(Context context) {
        super(context);

    }

    protected QRCodeDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected QRCodeDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_dialog);
        System.out.println("a");
        ivQRCode=(ImageView)findViewById(R.id.ivqrcode);
        String all="";
        for(int i=0;i<140;i++){
            all=all+"123456789abcdef-"+i;
        }
        Bitmap bitmap = ZXUtils.createQRCode(all);
        if(bitmap!=null){
            ivQRCode.setImageBitmap(bitmap);
        }

    }
}
