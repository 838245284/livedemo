package com.myylook.mall.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.myylook.common.adapter.RefreshAdapter;
import com.myylook.common.custom.CommonRefreshView;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.views.AbsCommonViewHolder;
import com.myylook.mall.R;
import com.myylook.mall.activity.GoodsDetailActivity;
import com.myylook.mall.adapter.SellerDaimaiAdapter;
import com.myylook.mall.bean.GoodsManageBean;
import com.myylook.mall.http.MallHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 商品管理 代卖平台商品
 */
public class SellerDaimaiViewHolder extends AbsCommonViewHolder implements SellerDaimaiAdapter.ActionListener {

    private CommonRefreshView mRefreshView;
    private SellerDaimaiAdapter mAdapter;

    public SellerDaimaiViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_seller_manage_goods;
    }

    @Override
    public void init() {
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_goods_seller);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<GoodsManageBean>() {
            @Override
            public RefreshAdapter<GoodsManageBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new SellerDaimaiAdapter(mContext);
                    mAdapter.setActionListener(SellerDaimaiViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MallHttpUtil.getManagePlatGoods(p, callback);
            }

            @Override
            public List<GoodsManageBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), GoodsManageBean.class);
            }

            @Override
            public void onRefreshSuccess(List<GoodsManageBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<GoodsManageBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }


    @Override
    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }


    @Override
    public void onItemClick(GoodsManageBean bean) {
        GoodsDetailActivity.forward(mContext, bean.getId(), bean.getType());
    }



}
