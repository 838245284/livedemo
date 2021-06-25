package com.myylook.main.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.myylook.common.adapter.RefreshAdapter;
import com.myylook.common.bean.UserBean;
import com.myylook.common.glide.ImgLoader;
import com.myylook.common.utils.DensityUtils;
import com.myylook.common.utils.WordUtil;
import com.myylook.main.R;
import com.myylook.video.bean.VideoBean;

/**
 * Created by cxf on 2018/12/14.
 */

public class MyLongVideoAdapter extends RefreshAdapter<VideoBean> {

    private View.OnClickListener mOnClickListener;

    public MyLongVideoAdapter(Context context) {
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
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_my_video_long, parent, false));
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
        private final TextView mPlayCount;
        private final TextView mDate;
        ImageView mCover;
        TextView mTitle;
        private final TextView mTime;

        public Vh(View itemView) {
            super(itemView);
            mCover = (ImageView) itemView.findViewById(R.id.cover);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mPlayCount = (TextView) itemView.findViewById(R.id.tv_play);
            mDate = (TextView) itemView.findViewById(R.id.tv_date);
            itemView.findViewById(R.id.oprate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSetDialog(v);
                }
            });
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(VideoBean bean, int position) {
            itemView.setTag(position);
            ImgLoader.display(mContext, bean.getThumb(), mCover);
            mTitle.setText(bean.getTitle());
            UserBean userBean = bean.getUserBean();
            /*if (userBean != null) {
                ImgLoader.display(mContext, userBean.getAvatar(), mAvatar);

            }*/
            mPlayCount.setText(bean.getViewNum()+"播放");
            mDate.setText(bean.getDatetime()); //
            mTime.setText(bean.getVideo_time());
        }

        private void showSetDialog(View v) {
            View view = View.inflate(mContext, R.layout.dialog_jigou, null);
            final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(view);
            Window window = dialog.getWindow();
            //设置弹出位置
            window.setGravity(Gravity.BOTTOM);
            //设置弹出动画
            window.setWindowAnimations(R.style.popwindow_bottom_anim);
            window.setBackgroundDrawableResource(R.color.transparent);
            //设置对话框大小
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams lp = window.getAttributes();
            int width = DensityUtils.getScreenW(mContext);
            lp.width = width;
            window.setAttributes(lp);
            dialog.show();
            view.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }
}
