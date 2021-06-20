package com.myylook.video.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.myylook.common.bean.UserBean;

/**
 * Created by cxf on 2017/10/25.
 */

public class VideoBean implements Parcelable {





    private String id;
    private String uid;
    private String title;
    private String thumb;
    private String thumbs;
    private String href;
    private String hrefW;
    private String likeNum;
    private String viewNum;
    private String commentNum;
    private String stepNum;
    private String shareNum;
    private String addtime;
    private String lat;
    private String lng;
    private String city;
    private UserBean userBean;
    private String datetime;
    private String distance;
    private int step;//是否踩过
    private int like;//是否赞过
    private int attent;//是否关注过作者
    private int status;//视频状态 0审核中 1通过 2拒绝
    private int musicId;
    private String mGoodsId;
    private int mType;// type  绑定的内容类型 0 没绑定 1 商品 2 付费内容
    private int mGoodsType;//0站内商品 1站外商品



    private String video_time;
    private String sc_count;
    private String videoclass;
    private String isattent; // 是否关注


    public VideoBean() {

    }


    public String getIsattent() {
        return isattent;
    }

    public void setIsattent(String isattent) {
        this.isattent = isattent;
    }

    public String getVideo_time() {
        return video_time;
    }

    public void setVideo_time(String video_time) {
        this.video_time = video_time;
    }

    public String getSc_count() {
        return sc_count;
    }

    public void setSc_count(String sc_count) {
        this.sc_count = sc_count;
    }

    public String getVideoclass() {
        return videoclass;
    }

    public void setVideoclass(String videoclass) {
        this.videoclass = videoclass;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    @JSONField(name = "thumb_s")
    public String getThumbs() {
        return thumbs;
    }

    @JSONField(name = "thumb_s")
    public void setThumbs(String thumbs) {
        this.thumbs = thumbs;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @JSONField(name = "href_w")
    public String getHrefW() {
        return hrefW;
    }

    @JSONField(name = "href_w")
    public void setHrefW(String hrefW) {
        this.hrefW = hrefW;
    }


    @JSONField(name = "likes")
    public String getLikeNum() {
        return likeNum;
    }

    @JSONField(name = "likes")
    public void setLikeNum(String likeNum) {
        this.likeNum = likeNum;
    }

    @JSONField(name = "views")
    public String getViewNum() {
        return viewNum;
    }

    @JSONField(name = "views")
    public void setViewNum(String viewNum) {
        this.viewNum = viewNum;
    }

    @JSONField(name = "comments")
    public String getCommentNum() {
        return commentNum;
    }

    @JSONField(name = "comments")
    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    @JSONField(name = "steps")
    public String getStepNum() {
        return stepNum;
    }

    @JSONField(name = "steps")
    public void setStepNum(String stepNum) {
        this.stepNum = stepNum;
    }

    @JSONField(name = "shares")
    public String getShareNum() {
        return shareNum;
    }

    @JSONField(name = "shares")
    public void setShareNum(String shareNum) {
        this.shareNum = shareNum;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    @JSONField(name = "userinfo")
    public UserBean getUserBean() {
        return userBean;
    }

    @JSONField(name = "userinfo")
    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @JSONField(name = "isstep")
    public int getStep() {
        return step;
    }

    @JSONField(name = "isstep")
    public void setStep(int step) {
        this.step = step;
    }

    @JSONField(name = "islike")
    public int getLike() {
        return like;
    }

    @JSONField(name = "islike")
    public void setLike(int like) {
        this.like = like;
    }

    @JSONField(name = "isattent")
    public int getAttent() {
        return attent;
    }

    @JSONField(name = "isattent")
    public void setAttent(int attent) {
        this.attent = attent;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @JSONField(name = "music_id")
    public int getMusicId() {
        return musicId;
    }

    @JSONField(name = "music_id")
    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }

    @JSONField(name = "goodsid")
    public String getGoodsId() {
        return mGoodsId;
    }

    @JSONField(name = "goodsid")
    public void setGoodsId(String goodsId) {
        mGoodsId = goodsId;
    }

    @JSONField(name = "type")
    public int getType() {
        return mType;
    }

    @JSONField(name = "type")
    public void setType(int type) {
        mType = type;
    }

    @JSONField(name = "goods_type")
    public int getGoodsType() {
        return mGoodsType;
    }

    @JSONField(name = "goods_type")
    public void setGoodsType(int goodsType) {
        mGoodsType = goodsType;
    }


    @Override
    public String toString() {
        return "VideoBean{" +
                "title='" + title + '\'' +
                ",href='" + href + '\'' +
                ",id='" + id + '\'' +
                ",uid='" + uid + '\'' +
                ",userNiceName='" + (userBean != null ? userBean.getUserNiceName() : "null") + '\'' +
                ",thumb='" + thumb + '\'' +
                '}';
    }


    public String getTag() {
        return "VideoBean" + this.getId() + this.hashCode();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.uid);
        dest.writeString(this.title);
        dest.writeString(this.thumb);
        dest.writeString(this.thumbs);
        dest.writeString(this.href);
        dest.writeString(this.hrefW);
        dest.writeString(this.likeNum);
        dest.writeString(this.viewNum);
        dest.writeString(this.commentNum);
        dest.writeString(this.stepNum);
        dest.writeString(this.shareNum);
        dest.writeString(this.addtime);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeString(this.city);
        dest.writeParcelable(this.userBean, flags);
        dest.writeString(this.datetime);
        dest.writeString(this.distance);
        dest.writeInt(this.step);
        dest.writeInt(this.like);
        dest.writeInt(this.attent);
        dest.writeInt(this.status);
        dest.writeInt(this.musicId);
        dest.writeString(this.mGoodsId);
        dest.writeInt(this.mType);
        dest.writeInt(this.mGoodsType);
        dest.writeString(this.video_time);
        dest.writeString(this.sc_count);
        dest.writeString(this.videoclass);
        dest.writeString(this.isattent);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.uid = source.readString();
        this.title = source.readString();
        this.thumb = source.readString();
        this.thumbs = source.readString();
        this.href = source.readString();
        this.hrefW = source.readString();
        this.likeNum = source.readString();
        this.viewNum = source.readString();
        this.commentNum = source.readString();
        this.stepNum = source.readString();
        this.shareNum = source.readString();
        this.addtime = source.readString();
        this.lat = source.readString();
        this.lng = source.readString();
        this.city = source.readString();
        this.userBean = source.readParcelable(UserBean.class.getClassLoader());
        this.datetime = source.readString();
        this.distance = source.readString();
        this.step = source.readInt();
        this.like = source.readInt();
        this.attent = source.readInt();
        this.status = source.readInt();
        this.musicId = source.readInt();
        this.mGoodsId = source.readString();
        this.mType = source.readInt();
        this.mGoodsType = source.readInt();
        this.video_time = source.readString();
        this.sc_count = source.readString();
        this.videoclass = source.readString();
        this.isattent = source.readString();
    }

    protected VideoBean(Parcel in) {
        this.id = in.readString();
        this.uid = in.readString();
        this.title = in.readString();
        this.thumb = in.readString();
        this.thumbs = in.readString();
        this.href = in.readString();
        this.hrefW = in.readString();
        this.likeNum = in.readString();
        this.viewNum = in.readString();
        this.commentNum = in.readString();
        this.stepNum = in.readString();
        this.shareNum = in.readString();
        this.addtime = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
        this.city = in.readString();
        this.userBean = in.readParcelable(UserBean.class.getClassLoader());
        this.datetime = in.readString();
        this.distance = in.readString();
        this.step = in.readInt();
        this.like = in.readInt();
        this.attent = in.readInt();
        this.status = in.readInt();
        this.musicId = in.readInt();
        this.mGoodsId = in.readString();
        this.mType = in.readInt();
        this.mGoodsType = in.readInt();
        this.video_time = in.readString();
        this.sc_count = in.readString();
        this.videoclass = in.readString();
        this.isattent = in.readString();
    }

    public static final Creator<VideoBean> CREATOR = new Creator<VideoBean>() {
        @Override
        public VideoBean createFromParcel(Parcel source) {
            return new VideoBean(source);
        }

        @Override
        public VideoBean[] newArray(int size) {
            return new VideoBean[size];
        }
    };

    @Override
    public String toString() {
        return "VideoBean{" +
                "title='" + title + '\'' +
                ",href='" + href + '\'' +
                ",id='" + id + '\'' +
                ",uid='" + uid + '\'' +
//                ",userNiceName='" + userBean.getUserNiceName() + '\'' +
                ",thumb='" + thumb + '\'' +
                '}';
    }


    public String getTag() {
        return "VideoBean" + this.getId() + this.hashCode();
    }

}
