package com.myylook.live.activity;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.myylook.common.Constants;
import com.myylook.common.activity.AbsActivity;
import com.myylook.common.bean.UserBean;
import com.myylook.common.utils.WordUtil;
import com.myylook.live.R;
import com.myylook.live.views.LiveRecordViewHolder;

/**
 * Created by cxf on 2018/9/30.
 */

public class LiveRecordActivity extends AbsActivity {

    public static void forward(Context context, UserBean userBean) {
        if (userBean == null) {
            return;
        }
        Intent intent = new Intent(context, LiveRecordActivity.class);
        intent.putExtra(Constants.USER_BEAN, userBean);
        context.startActivity(intent);
    }

    private UserBean mUserBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_record;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.live_record));
        mUserBean = getIntent().getParcelableExtra(Constants.USER_BEAN);
        if (mUserBean == null) {
            return;
        }
        LiveRecordViewHolder liveRecordViewHolder = new LiveRecordViewHolder(mContext, (ViewGroup) findViewById(R.id.container),mUserBean.getId());
        liveRecordViewHolder.setActionListener(new LiveRecordViewHolder.ActionListener() {
            @Override
            public UserBean getUserBean() {
                return mUserBean;
            }
        });
        liveRecordViewHolder.addToParent();
        liveRecordViewHolder.subscribeActivityLifeCycle();
        liveRecordViewHolder.loadData();
    }
}
