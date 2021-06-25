package com.myylook.common.mob;

import android.graphics.Bitmap;

/**
 * Created by cxf on 2018/10/19.
 */

public class CoverBean {

    private Bitmap bitmap;
    private String url;
    private boolean checked;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
