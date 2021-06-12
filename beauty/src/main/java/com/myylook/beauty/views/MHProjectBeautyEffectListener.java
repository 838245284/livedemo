package com.myylook.beauty.views;

import android.graphics.Bitmap;

import com.myylook.beauty.ui.interfaces.DefaultBeautyEffectListener;


public interface MHProjectBeautyEffectListener extends DefaultBeautyEffectListener {
    public void onFilterChanged(Bitmap bitmap);
}
