package com.myylook.mall.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.myylook.common.adapter.RefreshAdapter;
import com.myylook.common.custom.CommonRefreshView;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.views.AbsCommonViewHolder;
import com.myylook.mall.R;
import com.myylook.mall.activity.PayContentPubActivity;
import com.myylook.mall.adapter.PayPubAdapter;
import com.myylook.mall.bean.PayContentBean;
import com.myylook.mall.http.MallHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 我上传的
 */
public class PayPubViewHolder extends AbsCommonViewHolder implements View.OnClickListener {

    private CommonRefreshView mRefreshView;
    private PayPubAdapter mAdapter;

    public PayPubViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_pay_pub;
    }

    @Override
    public void init() {
        findViewById(R.id.btn_pub).setOnClickListener(this);
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_pay_pub);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<PayContentBean>() {
            @Override
            public RefreshAdapter<PayContentBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new PayPubAdapter(mContext);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MallHttpUtil.getMyPayContentList(p, callback);
            }

            @Override
            public List<PayContentBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), PayContentBean.class);
            }

            @Override
            public void onRefreshSuccess(List<PayContentBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<PayContentBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        PayContentPubActivity.forward(mContext);
    }


    @Override
    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

}
