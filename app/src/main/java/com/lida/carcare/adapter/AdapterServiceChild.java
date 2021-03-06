package com.lida.carcare.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.lida.carcare.R;
import com.lida.carcare.app.AppUtil;
import com.lida.carcare.bean.ServiceBean;
import com.lida.carcare.widget.BaseApiCallback;
import com.lida.carcare.widget.DialogAddClass;
import com.lida.carcare.widget.DialogChooseEditType;
import com.lida.carcare.widget.DialogIfDelete;
import com.lida.carcare.widget.DialogIfDeleteResult;
import com.midian.base.app.AppContext;
import com.midian.base.base.BaseActivity;
import com.midian.base.bean.NetResult;
import com.midian.base.util.UIHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 服务分类小类
 * Created by WeiQingFeng on 2017/4/17.
 */

public class AdapterServiceChild extends BaseAdapter {

    private Activity context;
    private String type;//用于区分需不需是否是编辑页面
    private Map<String, List<ServiceBean.DataBean>> data = new HashMap<>();
    private String flag = "";//选择的大类类别
    private AppContext ac;

    public void setFlag(String flag){
        this.flag = flag;
    }

    public AdapterServiceChild(Activity context, Map<String, List<ServiceBean.DataBean>> data, String type) {
        this.context = context;
        this.data = data;
        this.type = type;
        this.ac = (AppContext)(context.getApplication());
    }

    @Override
    public int getCount() {
        if(data.get(flag)!=null){
            return data.get(flag).size();
        }else{
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_servicechild, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(data.get(flag)!=null){
            viewHolder.tv.setText(data.get(flag).get(position).getName());
        }
        viewHolder.tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if("edit".equals(type)){
                    final DialogChooseEditType dialog=new DialogChooseEditType(context,data.get(flag).get(position).getName());
                    dialog.setOnButtonClick(new DialogChooseEditType.onButtonClick() {
                        @Override
                        public void onDelete() {
                            dialog.dismiss();
                           // final DialogIfDelete dialogIfDelete = new DialogIfDelete(context, data.get(flag).get(position).getName());
                            final DialogIfDeleteResult dialogIfDelete = new DialogIfDeleteResult(context, data.get(flag).get(position).getName(),"如果删除该服务分类，则对应的数据也会被删除");
                            final BaseActivity activity = (BaseActivity)context;
                            dialogIfDelete.setOnOkButtonClick(new DialogIfDeleteResult.onOkButtonClick() {
                                @Override
                                public void delete() {
                                    dialogIfDelete.dismiss();
                                    AppUtil.getCarApiClient(ac).deleteSerive(data.get(flag).get(position).getId(), new BaseApiCallback(){

                                        @Override
                                        public void onApiStart(String tag) {
                                            super.onApiStart(tag);
                                            activity.showLoadingDlg();
                                        }

                                        @Override
                                        public void onApiFailure(Throwable t, int errorNo, String strMsg, String tag) {
                                            super.onApiFailure(t, errorNo, strMsg, tag);
                                            activity.hideLoadingDlg();
                                        }

                                        @Override
                                        public void onApiSuccess(NetResult res, String tag) {
                                            super.onApiSuccess(res, tag);
                                            activity.hideLoadingDlg();
                                            if(res.isOK()){
                                                data.get(flag).remove(position);
                                                notifyDataSetChanged();
                                            }else {
                                                UIHelper.t(context,res.getMsg());
                                            }
                                        }
                                    });
                                }
                            });
                            dialogIfDelete.show();
                        }

                        @Override
                        public void onEdit() {
                            dialog.dismiss();
                            final DialogAddClass dialogAddClass = new DialogAddClass(context,"编辑细类");
                            dialogAddClass.getEt().setText(data.get(flag).get(position).getName());
                            dialogAddClass.setOnOkButtonClick(new DialogAddClass.onOkButtonClick() {
                                @Override
                                public void add() {
                                    final String content = dialogAddClass.getContent();
                                    if(!"".equals(content)){
                                        AppUtil.getCarApiClient(ac).updateService(data.get(flag).get(position).getId(), content,
                                                new BaseApiCallback(){
                                            @Override
                                            public void onApiSuccess(NetResult res, String tag) {
                                                super.onApiSuccess(res, tag);
                                                if(res.isOK()){
                                                    data.get(flag).get(position).setName(content);
                                                    dialogAddClass.dismiss();
                                                    notifyDataSetChanged();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                            dialogAddClass.show();
                        }
                    });
                    dialog.show();
                }
                return false;
            }
        });
        viewHolder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("choose".equals(type)){
                    Intent intent = new Intent();
                    intent.putExtra("class",data.get(flag).get(position).getName());
                    intent.putExtra("code",data.get(flag).get(position).getCode());
                    context.setResult(Activity.RESULT_OK,intent);
                    context.finish();
                }else if("edit".equals(type)){

                }

            }
        });
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv)
        TextView tv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
