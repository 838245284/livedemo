package com.myylook.im.activity;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.myylook.common.Constants;
import com.myylook.common.activity.AbsActivity;
import com.myylook.common.utils.RouteUtil;
import com.myylook.im.R;
import com.myylook.im.bean.ImUserBean;
import com.myylook.im.views.ChatListViewHolder;

/**
 * Created by cxf on 2018/10/24.
 */

public class ChatActivity extends AbsActivity {

    private ChatListViewHolder mChatListViewHolder;

    public static void forward(Context context) {
        context.startActivity(new Intent(context, ChatActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_list;
    }

    @Override
    protected void main() {
        mChatListViewHolder = new ChatListViewHolder(mContext, (ViewGroup) findViewById(R.id.root), ChatListViewHolder.TYPE_ACTIVITY);
        mChatListViewHolder.setActionListener(new ChatListViewHolder.ActionListener() {
            @Override
            public void onCloseClick() {
                onBackPressed();
            }

            @Override
            public void onItemClick(ImUserBean bean) {
                if (Constants.MALL_IM_ADMIN.equals(bean.getId())) {
                    RouteUtil.forward(RouteUtil.PATH_MALL_ORDER_MSG);
                } else {
                    ChatRoomActivity.forward(mContext, bean, bean.getAttent() == 1, false);
                }
            }
        });
        mChatListViewHolder.addToParent();
        mChatListViewHolder.loadData();
    }

    @Override
    protected void onDestroy() {
        if (mChatListViewHolder != null) {
            mChatListViewHolder.release();
        }
        super.onDestroy();
    }
}
