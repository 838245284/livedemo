package com.myylook.main.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;
import com.myylook.common.CommonAppConfig;
import com.myylook.common.Constants;
import com.myylook.common.activity.AbsActivity;
import com.myylook.common.activity.WebViewActivity;
import com.myylook.common.bean.ConfigBean;
import com.myylook.common.http.CommonHttpConsts;
import com.myylook.common.http.CommonHttpUtil;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.interfaces.CommonCallback;
import com.myylook.common.interfaces.OnItemClickListener;
import com.myylook.common.utils.DialogUitl;
import com.myylook.common.utils.GlideCatchUtil;
import com.myylook.common.utils.StringUtil;
import com.myylook.common.utils.ToastUtil;
import com.myylook.common.utils.VersionUtil;
import com.myylook.common.utils.WordUtil;
import com.myylook.im.utils.ImMessageUtil;
import com.myylook.im.utils.ImPushUtil;
import com.myylook.main.R;
import com.myylook.main.adapter.SettingAdapter;
import com.myylook.main.bean.SettingBean;
import com.myylook.main.http.MainHttpConsts;
import com.myylook.main.http.MainHttpUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/30.
 */

public class SettingActivity extends AbsActivity implements OnItemClickListener<SettingBean> {

    private RecyclerView mRecyclerView;
    private Handler mHandler;
    private SettingAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.setting));
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        MainHttpUtil.getSettingList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<SettingBean> list0 = JSON.parseArray(Arrays.toString(info), SettingBean.class);
                List<SettingBean> list = new ArrayList<>();
                SettingBean bean0 = new SettingBean();
                bean0.setId(-1);
                bean0.setName(WordUtil.getString(R.string.setting_brightness));
                list.add(bean0);
                list.addAll(list0);
                SettingBean bean = new SettingBean();
                bean.setName(WordUtil.getString(R.string.setting_exit));
                bean.setLast(true);
                list.add(bean);
                mAdapter = new SettingAdapter(mContext, list, VersionUtil.getVersion(), getCacheSize());
                mAdapter.setOnItemClickListener(SettingActivity.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }


    @Override
    public void onItemClick(SettingBean bean, int position) {
        String href = bean.getHref();
        if (TextUtils.isEmpty(href)) {
            if (bean.isLast()) {//????????????
                new DialogUitl.Builder(mContext)
                        .setContent(WordUtil.getString(R.string.logout_confirm))
                        .setConfrimString(WordUtil.getString(R.string.logout_confirm_2))
                        .setCancelable(true)
                        .setIsHideTitle(true)
                        .setBackgroundDimEnabled(true)
                        .setClickCallback(new DialogUitl.SimpleCallback() {
                            @Override
                            public void onConfirmClick(Dialog dialog, String content) {
                                logout();
                            }
                        })
                        .build()
                        .show();

            } else if (bean.getId() == Constants.SETTING_MODIFY_PWD) {//????????????
                forwardModifyPwd();
            } else if (bean.getId() == Constants.SETTING_UPDATE_ID) {//????????????
                checkVersion();
            } else if (bean.getId() == Constants.SETTING_CLEAR_CACHE) {//????????????
                clearCache(position);
            }
        } else {
            if (bean.getId() == 19) {//????????????
                CancelConditionActivity.forward(mContext, href);
                return;
            }
            if (bean.getId() == 17) {//??????????????????url???????????????????????????
                if (!href.contains("?")) {
                    href = StringUtil.contact(href, "?");
                }
                href = StringUtil.contact(href, "&version=", android.os.Build.VERSION.RELEASE, "&model=", android.os.Build.MODEL);
            }
            WebViewActivity.forward(mContext, href);
        }
    }

    /**
     * ????????????
     */
    private void checkVersion() {
        CommonAppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (VersionUtil.isLatest(configBean.getVersion())) {
                        ToastUtil.show(R.string.version_latest);
                    } else {
                        VersionUtil.showDialog(mContext, configBean, configBean.getDownloadApkUrl());
                    }
                }
            }
        });

    }

    /**
     * ????????????
     */
    private void logout() {
        ImPushUtil.getInstance().logout();
        CommonHttpUtil.updatePushId("");
        CommonAppConfig.getInstance().clearLoginInfo();
        //????????????
        ImMessageUtil.getInstance().logoutImClient();
        //??????????????????
        MobclickAgent.onProfileSignOff();
        LoginActivity.forward();
    }

    /**
     * ????????????
     */
    private void forwardModifyPwd() {
        startActivity(new Intent(mContext, ModifyPwdActivity.class));
    }

    /**
     * ????????????
     */
    private String getCacheSize() {
        return GlideCatchUtil.getInstance().getCacheSize();
    }

    /**
     * ????????????
     */
    private void clearCache(final int position) {
        final Dialog dialog = DialogUitl.loadingDialog(mContext, getString(R.string.setting_clear_cache_ing));
        dialog.show();
        GlideCatchUtil.getInstance().clearImageAllCache();
        File gifGiftDir = new File(CommonAppConfig.GIF_PATH);
        if (gifGiftDir.exists() && gifGiftDir.length() > 0) {
            gifGiftDir.delete();
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (mAdapter != null) {
                    mAdapter.setCacheString(getCacheSize());
                    mAdapter.notifyItemChanged(position);
                }
                ToastUtil.show(R.string.setting_clear_cache);
            }
        }, 2000);
    }


    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        MainHttpUtil.cancel(MainHttpConsts.GET_SETTING_LIST);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_CONFIG);
        super.onDestroy();
    }

}
