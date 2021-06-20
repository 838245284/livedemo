package com.myylook.video.views;

import android.content.Context;
import android.util.AttributeSet;

import com.myylook.video.R;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

public class LVideoPlayer extends StandardGSYVideoPlayer {
    public LVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public LVideoPlayer(Context context) {
        super(context);
    }

    public LVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.video_layout_standard_l;
    }

    @Override
    protected void updateStartImage() {
        super.updateStartImage();
    }
}
