package com.myylook.common.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class VideoClassBean {
    private int mId;
    private int list_order;
    private int type; // 1短视频  2长视频
    private String mName;
    private boolean mChecked;

    public VideoClassBean() {
    }

    public VideoClassBean(int id, String name, int type, boolean checked) {
        mId = id;
        mName = name;
        mChecked = checked;
        this.type = type;
    }

    @JSONField(name = "id")
    public int getId() {
        return mId;
    }

    @JSONField(name = "id")
    public void setId(int id) {
        mId = id;
    }

    @JSONField(name = "name")
    public String getName() {
        return mName;
    }

    @JSONField(name = "name")
    public void setName(String name) {
        mName = name;
    }

    public int getList_order() {
        return list_order;
    }

    public void setList_order(int list_order) {
        this.list_order = list_order;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }
}
