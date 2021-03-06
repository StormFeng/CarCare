package com.lida.carcare.data;

import android.content.Context;

import com.lida.carcare.activity.ActivityCars;
import com.lida.carcare.app.AppUtil;
import com.lida.carcare.bean.CarInShopBean;
import com.midian.base.base.BaseListDataSource;
import com.midian.base.bean.NetResult;

import java.util.ArrayList;

/**
 * 维修
 * Created by WeiQingFeng on 2017/4/13.
 */

public class FragmentCarRepairData extends BaseListDataSource {

    public FragmentCarRepairData(Context context) {
        super(context);
    }

    @Override
    protected ArrayList load(int page) throws Exception {
        CarInShopBean bean = AppUtil.getCarApiClient(ac).getCarProject("维修",ac.shopId);
        ArrayList<NetResult> models=new ArrayList<>();
        ActivityCars context = (ActivityCars) this.context;
        if (bean != null){
            models.addAll(bean.getData());
            context.refresh();
        }
        hasMore=false;
        return models;
    }
}
