package com.myylook.mall.activity;

import android.support.v7.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.myylook.common.activity.AbsActivity;
import com.myylook.common.adapter.RefreshAdapter;
import com.myylook.common.bean.GoodsBean;
import com.myylook.common.custom.CommonRefreshView;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.utils.WordUtil;
import com.myylook.mall.R;
import com.myylook.mall.adapter.GoodsCollectAdapter;
import com.myylook.mall.http.MallHttpConsts;
import com.myylook.mall.http.MallHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 商品收藏
 */
public class GoodsCollectActivity extends AbsActivity {

    private CommonRefreshView mRefreshView;
    private GoodsCollectAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_goods_collect;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.mall_394));
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_goods_collect);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<GoodsBean>() {
            @Override
            public RefreshAdapter<GoodsBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new GoodsCollectAdapter(mContext);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                MallHttpUtil.getGoodsCollect(p, callback);
            }

            @Override
            public List<GoodsBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), GoodsBean.class);
            }

            @Override
            public void onRefreshSuccess(List<GoodsBean> list, int listCount) {

            }

            @Override
            public void onRefreshFailure() {

            }

            @Override
            public void onLoadMoreSuccess(List<GoodsBean> loadItemList, int loadItemCount) {

            }

            @Override
            public void onLoadMoreFailure() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

    @Override
    protected void onDestroy() {
        MallHttpUtil.cancel(MallHttpConsts.GET_GOODS_COLLECT);
        super.onDestroy();
    }
}
