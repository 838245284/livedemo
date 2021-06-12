package com.yunbao.common.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.R;
import com.yunbao.common.utils.BitmapUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DownloadUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by cxf on 2018/11/28.
 */

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.Vh> {

    private LayoutInflater mInflater;
    private ActionListener mActionListener;
    private int mPageCount;
    private LinearLayoutManager mLayoutManager;
    private int mCurPosition;
    private View.OnLongClickListener mLongClickListener;
    private DownloadUtil mDownloadUtil;

    public ImagePreviewAdapter(final Context context, int pageCount) {
        mPageCount = pageCount;
        mInflater = LayoutInflater.from(context);
        mLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Object tag = v.getTag(R.id.img);
                if (tag == null) {
                    return false;
                }
                int position = (int) tag;
                if (mActionListener == null) {
                    return false;
                }
                final String imgUrl = mActionListener.getImageUrl(position);
                if (TextUtils.isEmpty(imgUrl)) {
                    return false;
                }
                DialogUitl.showStringArrayDialog(context, new Integer[]{R.string.save_image_album}, new DialogUitl.StringArrayDialogCallback() {
                    @Override
                    public void onItemClick(String text, int tag) {
                        if (mDownloadUtil == null) {
                            mDownloadUtil = new DownloadUtil();
                        }
                        mDownloadUtil.download("save_img", CommonAppConfig.CAMERA_IMAGE_PATH, StringUtil.generateFileName() + ".png", imgUrl, new DownloadUtil.Callback() {
                            @Override
                            public void onSuccess(File file) {
                                BitmapUtil.saveImageInfo(file);
                                ToastUtil.show(R.string.save_success);
                            }

                            @Override
                            public void onProgress(int progress) {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
                    }
                });
                return true;
            }
        };
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_preview_img, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(position);
    }

    @Override
    public int getItemCount() {
        return mPageCount;
    }


    class Vh extends RecyclerView.ViewHolder {

        ImageView mImg;

        public Vh(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView;
            mImg.setOnLongClickListener(mLongClickListener);
        }

        void setData(int position) {
            mImg.setTag(R.id.img, position);
            if (mActionListener != null) {
                mActionListener.loadImage(mImg, position);
            }
        }
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void setCurPosition(int curPosition) {
        mCurPosition = curPosition;
        if (mActionListener != null) {
            mActionListener.onPageChanged(curPosition);
        }
    }

    public int getCurPosition() {
        return mCurPosition;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int position = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (position >= 0 && mCurPosition != position) {
                    mCurPosition = position;
                    if (mActionListener != null) {
                        mActionListener.onPageChanged(position);
                    }
                }
            }
        });
    }


    public interface ActionListener {
        void onPageChanged(int position);

        void loadImage(ImageView imageView, int position);

        String getImageUrl(int position);
    }
}
