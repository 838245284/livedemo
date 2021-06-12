package com.yunbao.beauty.ui.bean;


import com.yunbao.beauty.ui.enums.BeautyTypeEnum;
import com.yunbao.beauty.ui.enums.FilterEnum;

/**
 * Created by cxf on 2018/8/4.
 */

public class FilterBean extends BeautyBean {

    private int mImgSrc;
    private int mFilterSrc;
    private FilterEnum mFilterEnum;
    private boolean mChecked;
    private int mKsyFilterType;//金山自带滤镜类型

    private String mFilterName;
    private String mImgSrcPath;
    private boolean mIsProFilter;
    public FilterBean() {
    }

    public FilterBean(FilterEnum FilterEnum) {
        mFilterEnum = FilterEnum;
        this.type = BeautyTypeEnum.FILTER_TYPE_ENUM;
    }


    public FilterBean(int imgSrc, int filterSrc, FilterEnum FilterEnum) {
        mImgSrc = imgSrc;
        mFilterSrc = filterSrc;
        mFilterEnum = FilterEnum;
        this.type = BeautyTypeEnum.FILTER_TYPE_ENUM;
    }

    public FilterBean(boolean isProFilter, String filterName, String  imgSrcPath, FilterEnum filterEnum) {
        this(0,0, filterEnum);
        mFilterName = filterName;
        mIsProFilter = isProFilter;
        mImgSrcPath = imgSrcPath;
    }

    public FilterBean(int imgSrc, int filterSrc, FilterEnum FilterEnum, int ksyFilterType) {
        this(imgSrc, filterSrc, FilterEnum);
        mKsyFilterType = ksyFilterType;
    }

    public FilterBean(int imgSrc, int filterSrc, FilterEnum FilterEnum, int ksyFilterType, boolean checked) {
        this(imgSrc, filterSrc, FilterEnum, ksyFilterType);
        mChecked = checked;
    }

    public int getImgSrc() {
        return mImgSrc;
    }

    public void setImgSrc(int imgSrc) {
        this.mImgSrc = imgSrc;
    }

    public int getFilterSrc() {
        return mFilterSrc;
    }

    public void setFilterSrc(int filterSrc) {
        mFilterSrc = filterSrc;
    }

    public FilterEnum getFilterEnum() {
        return mFilterEnum;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public int getKsyFilterType() {
        return mKsyFilterType;
    }

    public void setKsyFilterType(int ksyFilterType) {
        mKsyFilterType = ksyFilterType;
    }

    public String getmFilterName() {
        return mFilterName;
    }

    public void setmFilterName(String mFilterName) {
        this.mFilterName = mFilterName;
    }

    public boolean isProFilter() {
        return mIsProFilter;
    }

    public void setProFilter(boolean proFilter) {
        mIsProFilter = proFilter;
    }

    public String getmImgSrcPath() {
        return mImgSrcPath;
    }

    public void setmImgSrcPath(String mImgSrcPath) {
        this.mImgSrcPath = mImgSrcPath;
    }
}
