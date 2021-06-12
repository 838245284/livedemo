package com.myylook.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myylook.common.adapter.RefreshAdapter;
import com.myylook.main.R;
import com.myylook.main.activity.ActiveTopicActivity;
import com.myylook.main.bean.ActiveTopicBean;

public class ActiveSearchTopicAdapter extends RefreshAdapter<ActiveTopicBean> {

    private View.OnClickListener mOnClickListener;

    public ActiveSearchTopicAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActiveTopicBean bean = (ActiveTopicBean) v.getTag();
                if (bean != null) {
                    ActiveTopicActivity.forward(mContext, bean.getId(), bean.getName());
                }
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_active_search_topic, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int i) {
        ((Vh) vh).setData(mList.get(i));
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mName;

        public Vh(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(ActiveTopicBean bean) {
            itemView.setTag(bean);
            mName.setText(bean.getName());
        }

    }
}
