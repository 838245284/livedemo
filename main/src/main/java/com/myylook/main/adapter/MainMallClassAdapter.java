package com.myylook.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myylook.common.adapter.RefreshAdapter;
import com.myylook.common.glide.ImgLoader;
import com.myylook.main.R;
import com.myylook.main.activity.MallClassActivity;
import com.myylook.mall.bean.GoodsHomeClassBean;

import java.util.List;

public class MainMallClassAdapter extends RefreshAdapter<GoodsHomeClassBean> {

    private View.OnClickListener mOnClickListener;

    public MainMallClassAdapter(Context context, List<GoodsHomeClassBean> list) {
        super(context, list);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodsHomeClassBean bean = (GoodsHomeClassBean) v.getTag();
                MallClassActivity.forward(mContext, bean.getName(), bean.getId());
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_main_mall_class, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int i) {
        ((Vh) vh).setData(mList.get(i));
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mIcon;
        TextView mName;

        public Vh(@NonNull View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.icon);
            mName = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(GoodsHomeClassBean bean) {
            itemView.setTag(bean);
            ImgLoader.display(mContext, bean.getIcon(), mIcon);
            mName.setText(bean.getName());
        }

    }
}
