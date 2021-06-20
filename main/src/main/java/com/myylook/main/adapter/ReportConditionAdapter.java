package com.myylook.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.myylook.common.bean.VideoClassBean;
import com.myylook.main.R;

import java.util.List;

public class ReportConditionAdapter extends BaseAdapter {

    private List<VideoClassBean> videoClassList;

    public ReportConditionAdapter(List<VideoClassBean> videoClassList) {

        this.videoClassList = videoClassList;
    }

    @Override
    public int getCount() {
        return videoClassList==null?0:videoClassList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, null, false);
        }
        TextView tv = convertView.findViewById(R.id.tv);
        tv.setText(videoClassList.get(position).getName());
        return convertView;
    }
}
