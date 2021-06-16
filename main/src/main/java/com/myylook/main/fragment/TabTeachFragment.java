package com.myylook.main.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.myylook.common.Constants;
import com.myylook.common.R;
import com.myylook.common.adapter.RefreshAdapter;
import com.myylook.common.custom.CommonRefreshView;
import com.myylook.common.custom.ItemDecoration;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.interfaces.OnItemClickListener;
import com.myylook.common.utils.DensityUtils;
import com.myylook.common.utils.DpUtil;
import com.myylook.common.utils.JsonUtil;
import com.myylook.common.utils.LogUtil;
import com.myylook.main.adapter.MainHomeVideoAdapter;
import com.myylook.video.activity.VideoPlayActivity;
import com.myylook.video.bean.VideoBean;
import com.myylook.video.bean.VideoWithAds;
import com.myylook.video.http.VideoHttpConsts;
import com.myylook.video.http.VideoHttpUtil;
import com.myylook.video.interfaces.VideoScrollDataHelper;
import com.myylook.video.utils.VideoStorge;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class TabTeachFragment extends Fragment implements OnItemClickListener<VideoWithAds> {

    @Override
    public void onItemClick(VideoWithAds bean, int position) {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tabment, container, false);
    }
}