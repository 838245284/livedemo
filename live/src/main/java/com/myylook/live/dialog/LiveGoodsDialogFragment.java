package com.myylook.live.dialog;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myylook.common.Constants;
import com.myylook.common.adapter.RefreshAdapter;
import com.myylook.common.bean.GoodsBean;
import com.myylook.common.custom.CommonRefreshView;
import com.myylook.common.dialog.AbsDialogFragment;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.interfaces.OnItemClickListener;
import com.myylook.common.utils.DpUtil;
import com.myylook.common.utils.RouteUtil;
import com.myylook.common.utils.StringUtil;
import com.myylook.common.utils.WordUtil;
import com.myylook.live.R;
import com.myylook.live.adapter.LiveGoodsAdapter;
import com.myylook.live.http.LiveHttpConsts;
import com.myylook.live.http.LiveHttpUtil;

import java.util.List;

/**
 * Created by cxf on 2019/8/29.
 */

public class LiveGoodsDialogFragment extends AbsDialogFragment implements OnItemClickListener<GoodsBean> {

    private CommonRefreshView mRefreshView;
    private LiveGoodsAdapter mAdapter;
    private TextView mTitle;
    private String mLiveUid;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_goods;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(320);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mLiveUid = bundle.getString(Constants.LIVE_UID);
        }
        mTitle = (TextView) findViewById(R.id.title);
        mRefreshView = (CommonRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setEmptyLayoutId(R.layout.view_no_data_goods);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new CommonRefreshView.DataHelper<GoodsBean>() {
            @Override
            public RefreshAdapter<GoodsBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new LiveGoodsAdapter(mContext);
                    mAdapter.setOnItemClickListener(LiveGoodsDialogFragment.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                LiveHttpUtil.getSale(p, mLiveUid, callback);
            }

            @Override
            public List<GoodsBean> processData(String[] info) {
                JSONObject obj = JSON.parseObject(info[0]);
                if (mTitle != null) {
                    mTitle.setText(StringUtil.contact(WordUtil.getString(R.string.goods_tip_17), " ", obj.getString("nums")));
                }
                return JSON.parseArray(obj.getString("list"), GoodsBean.class);
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
        mRefreshView.initData();

    }

    @Override
    public void onDestroy() {
        mContext = null;
        LiveHttpUtil.cancel(LiveHttpConsts.GET_SALE);
        super.onDestroy();

    }


    @Override
    public void onItemClick(GoodsBean bean, int position) {
        if (bean.getType() == 1) {
            RouteUtil.forwardGoodsDetailOutSide(bean.getId(), false);
        } else {
            RouteUtil.forwardGoodsDetail(bean.getId(), false,mLiveUid);
        }

    }
}