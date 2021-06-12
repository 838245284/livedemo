package com.myylook.main.views;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.myylook.common.activity.AbsActivity;
import com.myylook.common.glide.ImgLoader;
import com.myylook.common.interfaces.ImageResultCallback;
import com.myylook.common.upload.UploadBean;
import com.myylook.common.upload.UploadCallback;
import com.myylook.common.upload.UploadQnImpl;
import com.myylook.common.utils.DialogUitl;
import com.myylook.common.utils.ProcessImageUtil;
import com.myylook.common.utils.WordUtil;
import com.myylook.common.views.AbsCommonViewHolder;
import com.myylook.main.R;
import com.myylook.mall.http.MallHttpConsts;
import com.myylook.mall.http.MallHttpUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2019/8/29.
 * 添加 淘宝商品
 */

public class GoodsAddTaoBaoViewHolder extends AbsCommonViewHolder implements View.OnClickListener {

    private EditText mLink;
    private EditText mName;
    private EditText mPriceOrigin;
    private EditText mPriceNow;
    private EditText mDes;
    private View mBtnImgDel;
    private ImageView mImg;
    private ProcessImageUtil mImageUtil;
    private File mImgFile;
    private View mBtnConfirm;
    private UploadQnImpl mUploadStrategy;
    private Dialog mLoading;


    public GoodsAddTaoBaoViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_goods_add_taobao;
    }

    @Override
    public void init() {
        mLink = (EditText) findViewById(R.id.link);
        mName = (EditText) findViewById(R.id.name);
        mPriceOrigin = (EditText) findViewById(R.id.price_origin);
        mPriceNow = (EditText) findViewById(R.id.price_now);
        mDes = (EditText) findViewById(R.id.des);
        mImg = (ImageView) findViewById(R.id.img);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);
        findViewById(R.id.btn_img_add).setOnClickListener(this);
        mBtnImgDel = findViewById(R.id.btn_img_del);
        mBtnImgDel.setOnClickListener(this);
        mImageUtil = new ProcessImageUtil((AbsActivity) mContext);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {

            }

            @Override
            public void onSuccess(File file) {
                if (file != null && file.exists()) {
                    mImgFile = file;
                    if (mImg != null) {
                        ImgLoader.display(mContext, file, mImg);
                    }
                    if (mBtnImgDel != null && mBtnImgDel.getVisibility() != View.VISIBLE) {
                        mBtnImgDel.setVisibility(View.VISIBLE);
                    }
                    setSubmitEnable();
                }
            }


            @Override
            public void onFailure() {
            }
        });
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setSubmitEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mLink.addTextChangedListener(textWatcher);
        mName.addTextChangedListener(textWatcher);
        mPriceOrigin.addTextChangedListener(textWatcher);
        mPriceNow.addTextChangedListener(textWatcher);
        mDes.addTextChangedListener(textWatcher);
    }


    /**
     * 添加图片
     */
    private void addImage() {
        if (mImgFile != null && mImgFile.exists()) {
            return;
        }
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{R.string.alumb,R.string.camera}, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.camera) {
                    mImageUtil.getImageByCamera(false);
                } else if (tag == R.string.alumb) {
                    mImageUtil.getImageByAlumb(false);
                }
            }
        });
    }

    /**
     * 删除图片
     */
    private void deleteImage() {
        if (mImg != null) {
            mImg.setImageDrawable(null);
        }
        mImgFile = null;
        if (mBtnImgDel != null && mBtnImgDel.getVisibility() == View.VISIBLE) {
            mBtnImgDel.setVisibility(View.INVISIBLE);
        }
        setSubmitEnable();
    }


    private void setSubmitEnable() {
        if (mBtnConfirm != null) {
            String link = mLink.getText().toString().trim();
            String name = mName.getText().toString().trim();
            String priceNow = mPriceNow.getText().toString().trim();
            String des = mDes.getText().toString().trim();
            mBtnConfirm.setEnabled(!TextUtils.isEmpty(link)
                    && !TextUtils.isEmpty(name)
                    && !TextUtils.isEmpty(priceNow)
                    && !TextUtils.isEmpty(des)
                    && mImgFile != null
            );
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_img_add) {
            addImage();
        } else if (i == R.id.btn_img_del) {
            deleteImage();
        } else if (i == R.id.btn_confirm) {
            submit();
        }
    }

    private void submit() {
        final String link = mLink.getText().toString().trim();
        final String name = mName.getText().toString().trim();
        final String priceOrigin = mPriceOrigin.getText().toString().trim();
        final String priceNow = mPriceNow.getText().toString().trim();
        final String des = mDes.getText().toString().trim();
        showLoading();
        if (mUploadStrategy == null) {
            mUploadStrategy = new UploadQnImpl(mContext);
        }
        List<UploadBean> list = new ArrayList<>();
        list.add(new UploadBean(mImgFile, UploadBean.IMG));
        mUploadStrategy.upload(list, true, new UploadCallback() {
            @Override
            public void onFinish(List<UploadBean> list, boolean success) {
                if (!success) {
                    hideLoading();
                    return;
                }
                if (list != null && list.size() > 0) {
                    String remoteFileName = list.get(0).getRemoteFileName();
//                    MallHttpUtil.setOutsideGoods(null,link, name, priceOrigin, priceNow, des, remoteFileName, new HttpCallback() {
//                        @Override
//                        public void onSuccess(int code, String msg, String[] info) {
//                            if (code == 0) {
//                                if (mContext != null) {
//                                    ((AbsActivity) mContext).finish();
//                                }
//                            }
//                            ToastUtil.show(msg);
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            hideLoading();
//                        }
//                    });
                }
            }
        });
    }


    private void showLoading() {
        if (mLoading == null) {
            mLoading = DialogUitl.loadingDialog(mContext, WordUtil.getString(R.string.video_pub_ing));
        }
        if (!mLoading.isShowing()) {
            mLoading.show();
        }
    }


    private void hideLoading() {
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MallHttpUtil.cancel(MallHttpConsts.SET_OUTSIDE_GOODS);
        if (mImageUtil != null) {
            mImageUtil.release();
        }
        mImageUtil = null;
        hideLoading();
    }
}
