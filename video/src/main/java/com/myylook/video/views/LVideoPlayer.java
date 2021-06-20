package com.myylook.video.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

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
        ImageView imageView = (ImageView) mStartButton;
        if (mCurrentState == CURRENT_STATE_PLAYING) {
            imageView.setImageResource(R.mipmap.ic_video_d_suspend);
        } else if (mCurrentState == CURRENT_STATE_ERROR) {
            imageView.setImageResource(R.drawable.video_click_error_selector);
        } else {
            imageView.setImageResource(R.mipmap.ic_video_d_play);
        }
    }
}
