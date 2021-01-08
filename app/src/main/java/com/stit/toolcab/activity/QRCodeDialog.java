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
import com.stit.toolcab.entity.ToolZT;
import com.stit.toolcab.utils.Cache;
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
        Cache.listJY.clear();
        for(int i=0;i<4;i++){
            ToolZT toolZT = new ToolZT();
            toolZT.setMc("name张三"+i);
            toolZT.setGg("gg规格"+i);
            toolZT.setEpc("epc111"+i);
            Cache.listJY.add(toolZT);
        }
        String all="[";
        for(ToolZT toolZT : Cache.listJY){
            all=all+"{\"mc:\""+toolZT.getMc()+"\",\"gg\":\""+toolZT.getGg()+"\",\"epc\":\""+toolZT.getEpc()+"\"},";
            //all=all+"{\"epc\":\""+toolZT.getEpc()+"\"},";
        }
/*        for(int i=0;i<5;i++){
            all=all+all;
        }*/
        if(Cache.listJY!=null && !Cache.listJY.isEmpty()){
            all=all.substring(0,all.length()-1);
        }
        all=all+"]";
        Bitmap bitmap=QRCodeManager. createQRCodeBitmap( all, 600, 600, "UTF-8", "L", "2", Color.BLACK, Color.WHITE );
        //Bitmap bitmap = ZXUtils.createQRCode(all);
        if(bitmap!=null){
            ivQRCode.setImageBitmap(bitmap);
        }

    }
}
