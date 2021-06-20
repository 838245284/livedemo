package com.myylook.video.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myylook.common.adapter.RefreshAdapter;
import com.myylook.common.bean.UserBean;
import com.myylook.common.glide.ImgLoader;
import com.myylook.video.R;
import com.myylook.video.bean.VideoBean;
import com.myylook.video.bean.VideoWithAds;

import java.util.List;

/**
 * Created by cxf on 2018/9/26.
 */

public class VideoRecommendAdapter extends RefreshAdapter<VideoWithAds> {


    protected View.OnClickListener mOnClickListener;
    private int mTotalY;
    private int mLastTotalY;

    public VideoRecommendAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        VideoWithAds videoBean = mList.get(position);
                        mOnItemClickListener.onItemClick(videoBean, position);
                    }
                }
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        return mList != null ? mList.get(position).itemType : 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == VideoBean.ITEM_TYPE_SHORT_VIDEO) {
//            return new Vh(mInflater.inflate(R.layout.item_main_home_video, parent, false));
//        } else if (viewType == VideoBean.ITEM_TYPE_LONG_VIDEO) {
//            return new VideoLongVh(mInflater.inflate(R.layout.item_main_home_video_long, parent, false));
//        }
//        return new Vh(mInflater.inflate(R.layout.item_main_home_video, parent, false));
        return new VideoLongVh(mInflater.inflate(R.layout.item_video_recommend, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        if (vh instanceof Vh) {
            ((Vh) vh).setData(mList.get(position).videoBean, position, payload);
        } else if (vh instanceof VideoLongVh) {
            ((VideoLongVh) vh).setData(mList.get(position).videoBean, position, payload);
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mCover;
        ImageView mAvatar;
        TextView mName;
        TextView mTitle;
        TextView mNum;

        public Vh(View itemView) {
            super(itemView);
            mCover = (ImageView) itemView.findViewById(R.id.cover);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mNum = (TextView) itemView.findViewById(R.id.num);
            itemView.setOnClickListener(mOnClickListener);
        }

        protected void setData(VideoBean bean, int position, Object payload) {
            itemView.setTag(position);
            ImgLoader.display(mContext, bean.getThumb(), mCover);
            mTitle.setText(bean.getTitle());
            mNum.setText(bean.getViewNum());
            UserBean userBean = bean.getUserBean();
            if (userBean != null) {
                ImgLoader.display(mContext, userBean.getAvatar(), mAvatar);
                mName.setText(userBean.getUserNiceName());
            }
        }
    }


    class VideoLongVh extends RecyclerView.ViewHolder {

        ImageView mCover;
        ImageView mAvatar;
        TextView mName;
        TextView mTitle;
        TextView mNum;
        private final TextView mCollectionNum;
        private final TextView mLikeNum;

        public VideoLongVh(View itemView) {
            super(itemView);
            mCover = (ImageView) itemView.findViewById(R.id.cover);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mNum = (TextView) itemView.findViewById(R.id.num);
            mLikeNum = (TextView) itemView.findViewById(R.id.like_num);
            mCollectionNum = (TextView) itemView.findViewById(R.id.collection_num);

            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(VideoBean bean, int position, Object payload) {
            itemView.setTag(position);
            ImgLoader.display(mContext, bean.getThumb(), mCover);
            mTitle.setText(bean.getTitle());
            mNum.setText(bean.getViewNum());
            UserBean userBean = bean.getUserBean();
            if (userBean != null) {
                ImgLoader.display(mContext, userBean.getAvatar(), mAvatar);
                mName.setText(userBean.getUserNiceName());

            }

            mCollectionNum.setText(bean.getSc_count());
            mLikeNum.setText(bean.getLikeNum());

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                mTotalY += dy;
                if (mLastTotalY != mTotalY && mActionListener != null) {
                    mLastTotalY = mTotalY;
                    mActionListener.onScrollYChanged(-mLastTotalY);
                }
            }
        });
    }

    public ActionListener mActionListener;

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onScrollYChanged(int scrollY);
    }
}
