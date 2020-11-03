package com.stit.toolcab.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stit.toolcab.R;
import com.stit.toolcab.dao.ToolsDao;
import com.stit.toolcab.entity.ToolZT;
import com.stit.toolcab.entity.Tools;
import com.stit.toolcab.utils.Cache;

import org.apache.log4j.Logger;

import java.util.List;


/**
 * Created by Administrator on 2020-10-23.
 */

public class RecycleAdapterOut extends RecyclerView.Adapter<RecycleAdapterOut.MyViewHolder>{

    private Context context;
    private List<Tools> list;
    private View inflater;
    private Logger logger = Logger.getLogger(this.getClass());


    //构造方法，传入数据
    public RecycleAdapterOut(Context context, List<Tools> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建ViewHolder，返回每一项的布局
        inflater = LayoutInflater.from(context).inflate(R.layout.item_out,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(inflater);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //将数据和控件绑定
        holder.imageViewTu.setImageResource(R.drawable.baoxiuqr);
        holder.textView.setText(list.get(position).getMc());
        holder.textView1.setText("位置："+list.get(position).getWz()+"  规格："+list.get(position).getGg());
        for(ToolZT toolZT : Cache.listBX){
            if(toolZT.getEpc().equals(Cache.listOperaOut.get(position).getEpc())){
                holder.llbxbackground.setBackgroundColor(Color.YELLOW);
                break;
            }
        }
        //holder.viewGe.setBackgroundColor(R.color.black);
        holder.imageViewBX.setImageResource(R.drawable.baoxiuqr);
        holder.imageViewYW.setImageResource(R.drawable.cuowuqr);

        holder.imageViewBX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setTitle("提示");
                builder.setMessage("确认报修？");
                builder.setCancelable(true);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("aa:"+which+"  bb:"+position);
                            logger.info("点击确认报修");
                        ToolsDao toolsDao = new ToolsDao();
                            toolsDao.updateBX(list.get(position).getId());
                            System.out.println(list.get(position).getId()+"报修");
                            toolsDao.initBX();
                            holder.llbxbackground.setBackgroundColor(Color.YELLOW);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
            }
        });

        holder.imageViewYW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setTitle("提示");
                builder.setMessage("确认有误？");
                builder.setCancelable(true);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cache.listOperaOut.remove(position);
                        if(Cache.myHandleAccess!=null){
                            Message message = Message.obtain(Cache.myHandleAccess);
                            Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
                            data.putString("youwu","out");
                            message.setData(data);
                            Cache.myHandleAccess.sendMessage(message);
                        }

                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        //返回Item总条数
        return list.size();
    }

    //内部类，绑定控件
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        TextView textView1;
        ImageView imageViewTu;
        ImageView imageViewBX;
        ImageView imageViewYW;
        LinearLayout llbxbackground;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text_view);
            textView1=(TextView)itemView.findViewById(R.id.text_view1);
            imageViewTu=(ImageView)itemView.findViewById(R.id.imageViewTU);
            imageViewBX=(ImageView)itemView.findViewById(R.id.imageViewBX);
            imageViewYW=(ImageView)itemView.findViewById(R.id.imageViewYW);
            llbxbackground=(LinearLayout)itemView.findViewById(R.id.llbxbackground);

        }

    }
}
