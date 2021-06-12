package com.myylook.mall.views;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myylook.common.Constants;
import com.myylook.common.HtmlConfig;
import com.myylook.common.activity.WebViewActivity;
import com.myylook.common.adapter.RefreshAdapter;
import com.myylook.common.custom.CommonRefreshView;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.utils.DialogUitl;
import com.myylook.common.utils.StringUtil;
import com.myylook.common.utils.ToastUtil;
import com.myylook.common.utils.WordUtil;
import com.myylook.common.views.AbsCommonViewHolder;
import com.myylook.im.activity.ChatRoomActivity;
import com.myylook.im.bean.ImUserBean;
import com.myylook.im.http.ImHttpUtil;
import com.myylook.mall.R;
import com.myylook.mall.activity.SellerOrderActivity;
import com.myylook.mall.activity.SellerOrderDetailActivity;
import com.myylook.mall.activity.SellerRefundDetailActivity;
import com.myylook.mall.activity.SellerSendActivity;
import com.myylook.mall.adapter.SellerOrderBaseAdapter;
import com.myylook.mall.bean.SellerOrderBean;
import com.myylook.mall.http.MallHttpUtil;

import java.util.List;

public abstract class AbsSellerOrderViewHolder extends AbsCommonViewHolder implements SellerOrderBaseAdapter.ActionListener {

    private CommonRefreshView mRefreshView;
    private SellerOrderBaseAdapter mAdapter;
    private String mNumJsonString;

    public AbsSellerOrderViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_seller_order_list;
    }

    @Override
    public void init() {
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_buyer_order);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<SellerOrderBean>() {
            @Override
            public RefreshAdapter<SellerOrderBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = getSellerOrderAdapter();
                    mAdapter.setActionListener(AbsSellerOrderViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MallHttpUtil.getSellerOrderList(getOrderType(), p, callback);
            }

            @Override
            public List<SellerOrderBean> processData(String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                mNumJsonString = obj.getString("type_list_nums");
                return JSON.parseArray(obj.getString("list"), SellerOrderBean.class);
            }

            @Override
            public void onRefreshSuccess(List<SellerOrderBean> list, int listCount) {
                if (!TextUtils.isEmpty(mNumJsonString) && mContext != null) {
                    ((SellerOrderActivity) mContext).setOrderNum(mNumJsonString);
                }
            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<SellerOrderBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }

    public abstract String getOrderType();

    public abstract SellerOrderBaseAdapter getSellerOrderAdapter();

    @Override
    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    public void refreshData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    /**
     * 点击item
     */
    @Override
    public void onItemClick(SellerOrderBean bean) {
        if (bean.getStatus() == Constants.MALL_ORDER_STATUS_REFUND) {
            SellerRefundDetailActivity.forward(mContext, bean.getId());
        } else {
            SellerOrderDetailActivity.forward(mContext, bean.getId());
        }
    }


    /**
     * 去发货
     */
    @Override
    public void onSendClick(SellerOrderBean bean) {
        SellerSendActivity.forward(mContext, bean.getId());
    }

    /**
     * 删除订单
     */
    @Override
    public void onDeleteClick(final SellerOrderBean bean) {
        new DialogUitl.Builder(mContext)
                .setContent(WordUtil.getString(R.string.mall_370))
                .setCancelable(true)
                .setBackgroundDimEnabled(true)
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        MallHttpUtil.sellerDeleteOrder(bean.getId(), new HttpCallback() {
                            @Override
                            public void onSuccess(int code, String msg, String[] info) {
                                if (code == 0) {
                                    refreshData();
                                }
                                ToastUtil.show(msg);
                            }
                        });
                    }
                })
                .build()
                .show();
    }

    /**
     * 查看物流
     */
    @Override
    public void onWuLiuClick(SellerOrderBean bean) {
        String url = StringUtil.contact(HtmlConfig.MALL_BUYER_WULIU, "orderid=", bean.getId(), "&user_type=seller");
        WebViewActivity.forward(mContext, url);
    }

    /**
     * 退款详情
     */
    @Override
    public void onRefundClick(SellerOrderBean bean) {
        SellerRefundDetailActivity.forward(mContext, bean.getId());
    }

    /**
     * 联系买家
     */
    @Override
    public void onContactBuyerClick(SellerOrderBean bean) {
        ImHttpUtil.getImUserInfo(bean.getUid(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    ImUserBean bean = JSON.parseObject(info[0], ImUserBean.class);
                    if (bean != null) {
                        ChatRoomActivity.forward(mContext, bean, bean.getAttent() == 1, false);
                    }
                }
            }
        });
    }

}
