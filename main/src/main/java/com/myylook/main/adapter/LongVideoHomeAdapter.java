package com.myylook.main.adapter;

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
import com.myylook.common.utils.WordUtil;
import com.myylook.main.R;
import com.myylook.video.bean.VideoBean;
import com.myylook.video.bean.VideoWithAds;

/**
 * Created by cxf on 2018/12/14.
 */

public class LongVideoHomeAdapter extends RefreshAdapter<VideoBean> {

    private View.OnClickListener mOnClickListener;
    private String mStatusShenHe;
    private String mStatusJuJue;

    public LongVideoHomeAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                VideoBean bean = mList.get(position);
                if (bean != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(bean, position);
                }
            }
        };
        mStatusShenHe = WordUtil.getString(R.string.mall_117);
        mStatusJuJue = WordUtil.getString(R.string.mall_342);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_main_home_video_long, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position), position);
    }

    /**
     * 删除视频
     */
    public void deleteVideo(String videoId) {
        if (TextUtils.isEmpty(videoId)) {
            return;
        }
//        for (int i = 0, size = mList.size(); i < size; i++) {
//            if (videoId.equals(mList.get(i).getId())) {
//                notifyItemRemoved(i);
//                break;
//            }
//        }
        notifyDataSetChanged();
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView mCover;
        ImageView mAvatar;
        TextView mName;
        TextView mTitle;
        TextView mNum;
        private final TextView mTopic;
        private final TextView mCollectionNum;
        private final TextView mLikeNum;
        private final TextView mTag;
        private final TextView mTime;

        public Vh(View itemView) {
            super(itemView);
            mCover = (ImageView) itemView.findViewById(R.id.cover);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mNum = (TextView) itemView.findViewById(R.id.num);
            mTopic = (TextView) itemView.findViewById(R.id.topic);
            mLikeNum = (TextView) itemView.findViewById(R.id.like_num);
            mCollectionNum = (TextView) itemView.findViewById(R.id.collection_num);
            mTag = (TextView) itemView.findViewById(R.id.tag);
            mTime = (TextView) itemView.findViewById(R.id.time);

            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(VideoBean bean, int position) {
            itemView.setTag(position);
            ImgLoader.display(mContext, bean.getThumb(), mCover);
            mTitle.setText(bean.getTitle());
            mNum.setText(bean.getViewNum());
            UserBean userBean = bean.getUserBean();
            if (userBean != null) {
                ImgLoader.display(mContext, userBean.getAvatar(), mAvatar);
                mName.setText(userBean.getUserNiceName());

                mTopic.setText(userBean.getSignature());
            }

            mTopic.setText(bean.getCity()); //
            mCollectionNum.setText(bean.getCommentNum()); //
            mLikeNum.setText(bean.getLikeNum());
            mTag.setText(bean.getLikeNum());  //
            mTime.setText(bean.getDatetime()); //
            mCollectionNum.setText(bean.getSc_count());
            mLikeNum.setText(bean.getLikeNum());
            mTag.setText(bean.getVideoclass());
            mTime.setText(bean.getVideo_time());
        }

    }
}
